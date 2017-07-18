package pl.atlantischi.ximagebridge.fresco;

import com.facebook.common.internal.Preconditions;
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
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.fresco.listeners.WrapContentSupportControllerListener;
import pl.atlantischi.ximagebridge.fresco.processors.BlurPostprocessor;
import pl.atlantischi.ximagebridge.interfaces.IFrescoBridge;
import timber.log.Timber;

/**
 * Created by admin on 2017/7/7.
 */

public class FrescoBridge implements IFrescoBridge {

    private Context mContext;

    private boolean mSupportWrapContent;

    public FrescoBridge() {
        mContext = XImageBridge.obtain().getContext();
        Preconditions.checkNotNull(mContext);
        Fresco.initialize(mContext, buildImagePipelineConfig());
    }

//    @Override
//    public View transformFrescoView(String name, Context context, AttributeSet attrs) {
//        if ("ImageView".equals(name)) {
//            return new SimpleDraweeView(context, attrs);
//        }
//        return null;
//    }

    @Override
    public void replaceToDraweeView(final Activity activity, final boolean defaultReplace) {
        Preconditions.checkNotNull(activity);
        LayoutInflaterCompat.setFactory(LayoutInflater.from(activity), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FrescoBridge);
                boolean replace = a.getBoolean(R.styleable.FrescoBridge_replaceToDraweeView, defaultReplace);
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

    private ImagePipelineConfig buildImagePipelineConfig() {
        return ImagePipelineConfig.newBuilder(mContext)
                .setDownsampleEnabled(true) //support png/webp/jpg with ResizeOptions
                .setResizeAndRotateEnabledForNetwork(true)
                .build();
    }

    @Override
    public void setDefaultSupportWrapContent(boolean support) {
        mSupportWrapContent = support;
    }

    @Override
    public void display(Uri uri, final ImageView imageView, XImageBridge.Options options) {
        Preconditions.checkNotNull(imageView);
        Preconditions.checkNotNull(uri);
        if (imageView instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) imageView;
            draweeView.setHierarchy(buildGenericDraweeHierarchy(options));
            draweeView.setController(buildDraweeController(uri, draweeView, options));
        }
    }

    private GenericDraweeHierarchy buildGenericDraweeHierarchy(XImageBridge.Options options) {
        GenericDraweeHierarchy draweeHierarchy = new GenericDraweeHierarchyBuilder(mContext.getResources()).build();
        if (options != null) {
            RoundingParams roundingParams = new RoundingParams();
            if (options.isCircle) {
                roundingParams.setRoundAsCircle(true);
            }
            if (options.roundCorner > 0) {
                roundingParams.setCornersRadius(options.roundCorner);
            }
            draweeHierarchy.setRoundingParams(roundingParams);
        }
        return draweeHierarchy;
    }

    private DraweeController buildDraweeController(Uri uri, SimpleDraweeView draweeView, XImageBridge.Options options) {
        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(buildImageRequest(uri, options))
                .setOldController(draweeView.getController());
        if (mSupportWrapContent) {
            controllerBuilder.setControllerListener(new WrapContentSupportControllerListener(draweeView));
        }
        return controllerBuilder.build();
    }

    private ImageRequest buildImageRequest(Uri uri, XImageBridge.Options options) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (options != null) {
            if (options.blurRadius > 0) {
                imageRequestBuilder.setPostprocessor(new BlurPostprocessor(mContext, options.blurRadius));
            }
            if (options.size != null && options.size.isValid()) {
                int imageWidth = options.size.width;
                int imageHeight = options.size.height;
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
    public void getBitmapFromUri(Uri uri, final BitmapLoader bitmapLoader) {
        Preconditions.checkNotNull(uri);
        Preconditions.checkNotNull(bitmapLoader);

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                // You can not assign the bitmap to any variable not in the scope of the onNewResultImpl method.
                // The reason is, as already explained in the above examples that, after the subscriber has finished
                // executing, the image pipeline will recycle the bitmap and free its memory. If you try to draw the
                // bitmap after that, your app will crash with an IllegalStateException.
                bitmapLoader.onBitmapLoaded(bitmap);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, null);

    }

//    public void loadOriginalImage(String url, final BitmapLoader bitmapLoader, Executor executor) {
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