package pl.atlantischi.ximagebridge.picasso;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.XImageBridge2;
import pl.atlantischi.ximagebridge.interfaces.XBridge;
import pl.atlantischi.ximagebridge.picasso.transformation.CircleTransform;
import pl.atlantischi.ximagebridge.picasso.transformation.CropToCircleTransformation;
import pl.atlantischi.ximagebridge.picasso.transformation.RoundedCornersTransformation;

/**
 * Created by admin on 2017/7/7.
 */

public class PicassoBridge implements XBridge {

    @Override
    public void display(Uri uri, ImageView imageView, XImageBridge2.Options options) {
        if (options == null) {
            Picasso.with(imageView.getContext()).load(uri).into(imageView);
        } else {
            if (options.isCircle) {
                Picasso.with(imageView.getContext()).load(uri).transform(new CircleTransform()).into(imageView);
            } else if (options.roundCorner > 0) {
                Picasso.with(imageView.getContext()).load(uri).transform(new RoundedCornersTransformation(
                        options.roundCorner, 0)).into(imageView);
            }
        }
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
        Picasso.with(XImageBridge.getInstance().getContext()).load(uri.toString()).into(mTarget);
    }

}
