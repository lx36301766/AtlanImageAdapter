package pl.atlantischi.ximagebridge.fresco.listeners;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

/**
 * Created by admin on 2017/7/18.
 */

public class WrapContentSupportControllerListener extends BaseControllerListener<ImageInfo> {

    private DraweeView mDraweeView;

    public WrapContentSupportControllerListener(DraweeView draweeView) {
        this.mDraweeView = draweeView;
    }

    @Override
    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
        updateViewSize(imageInfo);
    }

    @Override
    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
        updateViewSize(imageInfo);
    }

    private void updateViewSize(@Nullable ImageInfo imageInfo) {
        if (mDraweeView != null && imageInfo != null) {
            mDraweeView.getLayoutParams().width = imageInfo.getWidth();
            mDraweeView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mDraweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
        }
    }

}
