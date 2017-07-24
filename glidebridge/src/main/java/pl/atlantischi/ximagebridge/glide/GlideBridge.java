package pl.atlantischi.ximagebridge.glide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.transition.TransitionFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.glide.annotation.GlideApp;
import pl.atlantischi.ximagebridge.glide.transformation.RoundedCornersTransformation;
import pl.atlantischi.ximagebridge.interfaces.IGlideBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;

/**
 * Created by admin on 2017/7/22.
 */

public class GlideBridge implements IGlideBridge {

    @Override
    public void initialize(Context context) {

    }

    @Override
    public void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions) {
        final Context context = imageView.getContext();
        RequestBuilder<Bitmap> builder = Glide.with(imageView).asBitmap().load(uri);
        if (bridgeOptions != null) {
            if (bridgeOptions.isCircle) {
                builder.apply(RequestOptions.circleCropTransform());
            } else if (bridgeOptions.roundCorner > 0) {
                BitmapTransitionOptions options = BitmapTransitionOptions.with(new TransitionFactory<Bitmap>() {
                    @Override
                    public Transition<Bitmap> build(DataSource dataSource, boolean isFirstResource) {
//                        return new RoundedCornersTransformation(context, 10, 0);
                        return null;
                    }
                });
                builder.transition(options);
                builder.apply(RequestOptions.circleCropTransform());
            }
        }
        builder.into(imageView);

        GlideApp.with(context).load(uri).into(imageView);
    }

    @Override
    public void getBitmapFromUri(Uri uri, BitmapLoader bitmapLoader) {

    }

}
