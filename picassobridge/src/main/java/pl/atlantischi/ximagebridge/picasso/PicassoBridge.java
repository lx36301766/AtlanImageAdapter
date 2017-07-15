package pl.atlantischi.ximagebridge.picasso;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.interfaces.IPicassoBridge;
import pl.atlantischi.ximagebridge.picasso.transformation.CircleTransform;
import pl.atlantischi.ximagebridge.picasso.transformation.RoundedCornersTransformation;

/**
 * Created by admin on 2017/7/7.
 */

public class PicassoBridge implements IPicassoBridge {

    @Override
    public void display(Uri uri, ImageView imageView, XImageBridge.Options options) {
        RequestCreator requestCreator = Picasso.with(imageView.getContext()).load(uri);
        Transformation transformation = null;
        if (options != null) {
            if (options.isCircle) {
                transformation = new CircleTransform();
            } else if (options.roundCorner > 0) {
                transformation = new RoundedCornersTransformation(options.roundCorner, 0);
            }
        }
        if (transformation != null) {
            requestCreator.transform(transformation);
        }
        requestCreator.into(imageView);
    }


    //Picasso的坑，target在内部是个弱引用，外部使用时必须保持强引用，否则没有回调
    private Target mTarget;

    @Override
    public void getBitmapFromUri(Uri uri, final BitmapLoader bitmapLoader) {
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmapLoader.onBitmapLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(XImageBridge.obtain().getContext()).load(uri.toString()).into(mTarget);
    }

}
