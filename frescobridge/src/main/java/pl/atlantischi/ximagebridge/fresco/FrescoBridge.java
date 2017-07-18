package pl.atlantischi.ximagebridge.fresco;

import java.util.concurrent.Executor;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.fresco.processors.BlurPostprocessor;
import pl.atlantischi.ximagebridge.interfaces.IFrescoBridge;

/**
 * Created by admin on 2017/7/7.
 */

public class FrescoBridge implements IFrescoBridge<SimpleDraweeView> {

    private Context mContext;

    public FrescoBridge() {
        mContext = XImageBridge.obtain().getContext();

        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(mContext)
                .build();
        Fresco.initialize(mContext, imagePipelineConfig);
    }

//    @Override
//    public View transformFrescoView(String name, Context context, AttributeSet attrs) {
//        if ("ImageView".equals(name)) {
//            return new SimpleDraweeView(context, attrs);
//        }
//        return null;
//    }

    @Override
    public void transformFrescoView(final Activity activity) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(activity), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                if ("ImageView".equals(name)) {
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

    @Override
    public void display(Uri uri, final SimpleDraweeView draweeView, XImageBridge.Options options) {
//        if (imageView instanceof SimpleDraweeView) {
//            SimpleDraweeView draweeView = (SimpleDraweeView) imageView;
//            draweeView.setImageURI(uri);
//        }
        Preconditions.checkNotNull(uri);
        Preconditions.checkNotNull(draweeView);

        Context context = draweeView.getContext();

        GenericDraweeHierarchy draweeHierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).build();
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (options != null) {
            RoundingParams roundingParams = new RoundingParams();
            if (options.isCircle) {
                roundingParams.setRoundAsCircle(true);
            }
            if (options.roundCorner > 0) {
                roundingParams.setCornersRadius(options.roundCorner);
            }
            draweeHierarchy.setRoundingParams(roundingParams);

            if (options.blurRadius > 0) {
                imageRequestBuilder.setPostprocessor(new BlurPostprocessor(context, options.blurRadius));
            }
            if (options.size.length == 2) {
                int imageWidth = options.size[0];
                int imageHeight = options.size[1];
                imageRequestBuilder.setResizeOptions(new ResizeOptions(imageWidth, imageHeight));
            }
        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                updateViewSize(draweeView, imageInfo);
            }

            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                updateViewSize(draweeView, imageInfo);
            }

            void updateViewSize(SimpleDraweeView draweeView, @Nullable ImageInfo imageInfo) {
                if (imageInfo != null) {
                    draweeView.getLayoutParams().width = imageInfo.getWidth();
                    draweeView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                }
            }

        };

        draweeView.setHierarchy(draweeHierarchy);
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setControllerListener(controllerListener)
                .setOldController(draweeView.getController())
                .build();
        draweeView.setController(controller);

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
                //You can not assign the bitmap to any variable not in the scope of the onNewResultImpl method.
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

    public void loadOriginalImage(String url, final BitmapLoader bitmapLoader, Executor executor) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = builder.build();
        // 获取已解码的图片，返回的是Bitmap
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
                    try {
                        CloseableBitmap closeableBitmap = closeableReference.get();
                        Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                        if (bitmap != null && !bitmap.isRecycled()) {
                            // https://github.com/facebook/fresco/issues/648
                            final Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), false);
                            bitmapLoader.onBitmapLoaded(tempBitmap);
                        }
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                if (throwable != null) {
                    Log.e("ImageLoader", "onFailureImpl = " + throwable.toString());
                }
            }
        };
        dataSource.subscribe(dataSubscriber, executor);
    }


}
