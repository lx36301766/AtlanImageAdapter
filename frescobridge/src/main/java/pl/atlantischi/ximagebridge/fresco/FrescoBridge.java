package pl.atlantischi.ximagebridge.fresco;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.fresco.listeners.WrapContentSupportControllerListener;
import pl.atlantischi.ximagebridge.fresco.processors.BlurPostprocessor;
import pl.atlantischi.ximagebridge.interfaces.IFrescoBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;

import static com.facebook.common.internal.Preconditions.*;

/**
 * Created by admin on 2017/7/7.
 */

public class FrescoBridge implements IFrescoBridge {

    private Context mContext;

    private boolean mSupportWrapContent;

    @Override
    public void initialize(Context context) {
        checkNotNull(context);
        mContext = context;
        Fresco.initialize(context, buildImagePipelineConfig(context));
    }

    @Override
    public void replaceToDraweeView(final Activity activity, final boolean defaultReplace) {
        checkNotNull(activity);
        LayoutInflaterCompat.setFactory(LayoutInflater.from(activity), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.frescoBridge);
                boolean replace = a.getBoolean(R.styleable.frescoBridge_replaceToDraweeView, defaultReplace);
                a.recycle();
                if ("ImageView".equals(name) && replace) {
                    return new SimpleDraweeView(context, attrs);
                }
                if (activity instanceof AppCompatActivity) {
                    AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                    return appCompatActivity.getDelegate().createView(parent, name, context, attrs);
                }
                return null;
            }
        });
    }

    private ImagePipelineConfig buildImagePipelineConfig(Context context) {
        return ImagePipelineConfig.newBuilder(context)
                .setDownsampleEnabled(true) //support png/webp/jpg with ResizeOptions
                .setResizeAndRotateEnabledForNetwork(true)
                .build();
    }

    @Override
    public void setDefaultSupportWrapContent(boolean support) {
        mSupportWrapContent = support;
    }

    @Override
    public void display(Uri uri, ImageView imageView) {
    }

    @Override
    public void display(Uri uri, final ImageView imageView, BridgeOptions bridgeOptions) {
        checkNotNull(imageView);
        checkNotNull(uri);
        if (imageView instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) imageView;
            draweeView.setHierarchy(buildGenericDraweeHierarchy(bridgeOptions));
            draweeView.setController(buildDraweeController(uri, draweeView, bridgeOptions));
        }
    }

    protected GenericDraweeHierarchy buildGenericDraweeHierarchy(BridgeOptions bridgeOptions) {
        GenericDraweeHierarchy draweeHierarchy = new GenericDraweeHierarchyBuilder(mContext.getResources()).build();
        if (bridgeOptions != null) {
            RoundingParams roundingParams = new RoundingParams();
            if (bridgeOptions.isCircle) {
                roundingParams.setRoundAsCircle(true);
            }
            if (bridgeOptions.roundCorner > 0) {
                roundingParams.setCornersRadius(bridgeOptions.roundCorner);
            }
            draweeHierarchy.setRoundingParams(roundingParams);
            if (bridgeOptions.placeHolderDrawable != null) {
                draweeHierarchy.setPlaceholderImage(bridgeOptions.placeHolderDrawable);
            } else if (bridgeOptions.placeHolderResId > 0) {
                draweeHierarchy.setPlaceholderImage(bridgeOptions.placeHolderResId);
            }
        }
        return draweeHierarchy;
    }

    protected DraweeController buildDraweeController(Uri uri, SimpleDraweeView draweeView, BridgeOptions bridgeOptions) {
        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(buildImageRequest(uri, bridgeOptions))
                .setOldController(draweeView.getController());
        if (mSupportWrapContent) {
            controllerBuilder.setControllerListener(new WrapContentSupportControllerListener(draweeView));
        }
        if (bridgeOptions != null && bridgeOptions.showAsGif) {
            controllerBuilder.setAutoPlayAnimations(true);
        }
        return controllerBuilder.build();
    }

    protected ImageRequest buildImageRequest(Uri uri, BridgeOptions bridgeOptions) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (bridgeOptions != null) {
            if (bridgeOptions.blurRadius > 0) {
                imageRequestBuilder.setPostprocessor(new BlurPostprocessor(mContext, bridgeOptions.blurRadius));
            }
            if (bridgeOptions.size != null && bridgeOptions.size.isValid()) {
                int imageWidth = bridgeOptions.size.width;
                int imageHeight = bridgeOptions.size.height;
                //Resizing has some limitations:
                //  it only supports JPEG files
                //  the actual resize is carried out to the nearest 1/8 of the original size
                //  it cannot make your image bigger, only smaller (not a real limitation though)
                imageRequestBuilder.setResizeOptions(ResizeOptions.forDimensions(imageWidth, imageHeight));
            }
        }
        return imageRequestBuilder.build();
    }

    @Override
    public void getBitmapFromUri(Uri uri, final BitmapCallback bitmapCallback) {
        checkNotNull(mContext, "mContext is null, please call initialize(context) before");
        checkNotNull(uri);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                if (bitmapCallback == null) {
                    return;
                }
                Bitmap destBmp;
                if (bitmapCallback instanceof FrescoBitmapCallback) {
                    destBmp = bitmap;
                } else {
                    // You can not assign the bitmap to any variable not in the scope of the onNewResultImpl method.
                    // The reason is, as already explained in the above examples that, after the subscriber has finished
                    // executing, the image pipeline will recycle the bitmap and free its memory. If you try to draw the
                    // bitmap after that, your app will crash with an IllegalStateException.

                    // https://github.com/facebook/fresco/issues/648
                    destBmp = bitmap.copy(bitmap.getConfig(), false);
                }
                bitmapCallback.onBitmapLoaded(destBmp);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, UiThreadImmediateExecutorService.getInstance());
    }

//    public void loadOriginalImage(String url, final BitmapCallback bitmapLoader, Executor executor) {
//        if (TextUtils.isEmpty(url)) {
//            return;
//        }
//        Uri uri = Uri.parse(url);
//        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
//        ImageRequest imageRequest = builder.build();
//        // 获取已解码的图片，返回的是Bitmap
//        DataSource<CloseableReference<CloseableImage>>
//                dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);
//        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
//            @Override
//            public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
//                if (!dataSource.isFinished()) {
//                    return;
//                }
//                CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
//                if (imageReference != null) {
//                    final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
//                    try {
//                        CloseableBitmap closeableBitmap = closeableReference.get();
//                        Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
//                        if (bitmap != null && !bitmap.isRecycled()) {
//                            // https://github.com/facebook/fresco/issues/648
//                            final Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), false);
//                            bitmapLoader.onBitmapLoaded(tempBitmap);
//                        }
//                    } finally {
//                        imageReference.close();
//                        closeableReference.close();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailureImpl(DataSource dataSource) {
//                Throwable throwable = dataSource.getFailureCause();
//                if (throwable != null) {
//                    Log.e("ImageLoader", "onFailureImpl = " + throwable.toString());
//                }
//            }
//        };
//        dataSource.subscribe(dataSubscriber, executor);
//    }


}
