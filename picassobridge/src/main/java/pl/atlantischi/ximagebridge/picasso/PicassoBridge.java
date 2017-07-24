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
import pl.atlantischi.ximagebridge.interfaces.IPicassoBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;
import pl.atlantischi.ximagebridge.picasso.transformation.BlurTransformation;
import pl.atlantischi.ximagebridge.picasso.transformation.CircleTransform;
import pl.atlantischi.ximagebridge.picasso.transformation.RoundedCornersTransformation;

import static pl.atlantischi.ximagebridge.util.Preconditions.*;

/**
 * Created by admin on 2017/7/7.
 */

public class PicassoBridge implements IPicassoBridge {

    private Context mContext;

    @Override
    public void initialize(Context context) {
        mContext = context;
    }

    @Override
    public void display(Uri uri, ImageView imageView) {
        display(uri, imageView, null);
    }

    @Override
    public void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions) {
        if (imageView == null) {
            return;
        }
        Context context = imageView.getContext();
        RequestCreator requestCreator = Picasso.with(context).load(uri);
        if (bridgeOptions != null) {
            if (bridgeOptions.size != null && bridgeOptions.size.isValid()) {
                int imageWidth = bridgeOptions.size.width;
                int imageHeight = bridgeOptions.size.height;
                requestCreator.resize(imageWidth, imageHeight);
            }
            requestCreator.transform(buildTransformations(context, bridgeOptions));
        }
        requestCreator.into(imageView);
    }

    private List<Transformation> buildTransformations(Context context, BridgeOptions bridgeOptions) {
        List<Transformation> transformationList = new ArrayList<>();
        if (bridgeOptions.isCircle) {
            transformationList.add(new CircleTransform());
        } else if (bridgeOptions.roundCorner > 0) {
            transformationList.add(new RoundedCornersTransformation(bridgeOptions.roundCorner, 0));
        }
        if (bridgeOptions.blurRadius > 0) {
            transformationList.add(new BlurTransformation(context, bridgeOptions.blurRadius));
        }
        return transformationList;
    }

    //Picasso的坑，target在内部是个弱引用，外部使用时必须保持强引用，否则没有回调
    private Target mTarget;

    @Override
    public void getBitmapFromUri(Uri uri, final BitmapLoader bitmapLoader) {
        checkNotNull(mContext, "mContext is null, please call initialize(context) before");
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
        Picasso.with(mContext).load(uri).into(mTarget);
    }

}
