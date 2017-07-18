package pl.atlantischi.ximagebridge.picasso;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.interfaces.IPicassoBridge;
import pl.atlantischi.ximagebridge.picasso.transformation.BlurTransformation;
import pl.atlantischi.ximagebridge.picasso.transformation.CircleTransform;
import pl.atlantischi.ximagebridge.picasso.transformation.RoundedCornersTransformation;

/**
 * Created by admin on 2017/7/7.
 */

public class PicassoBridge implements IPicassoBridge {

    private Context mContext;

    public PicassoBridge() {
        mContext = XImageBridge.obtain().getContext();
    }

    @Override
    public void display(Uri uri, ImageView imageView, XImageBridge.Options options) {
        RequestCreator requestCreator = Picasso.with(mContext).load(uri);
        if (options != null) {
            if (options.size != null && options.size.isValid()) {
                int imageWidth = options.size.width;
                int imageHeight = options.size.height;
                requestCreator.resize(imageWidth, imageHeight);
            }
            requestCreator.transform(buildTransformations(options));
        }
        requestCreator.into(imageView);
    }

    private List<Transformation> buildTransformations(XImageBridge.Options options) {
        List<Transformation> transformationList = new ArrayList<>();
        if (options.isCircle) {
            transformationList.add(new CircleTransform());
        } else if (options.roundCorner > 0) {
            transformationList.add(new RoundedCornersTransformation(options.roundCorner, 0));
        }
        if (options.blurRadius > 0) {
            transformationList.add(new BlurTransformation(mContext, options.blurRadius));
        }
        return transformationList;
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
        Picasso.with(mContext).load(uri.toString()).into(mTarget);
    }

}
