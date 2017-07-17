package pl.atlantischi.ximagebridge.fresco;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
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
        Fresco.initialize(mContext);
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

    }

}
