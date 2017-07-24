package pl.atlantischi.ximagebridge.glide;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.glide.transformation.BlurTransformation;
import pl.atlantischi.ximagebridge.glide.transformation.CropCircleTransformation;
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
    public void display(Uri uri, ImageView imageView) {
        display(uri, imageView, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions) {
        final Context context = imageView.getContext();
        RequestBuilder<Bitmap> builder = Glide.with(imageView).asBitmap().load(uri);
        if (bridgeOptions != null) {
            RequestOptions options = new RequestOptions();

            List<Transformation<Bitmap>> transformationList = new ArrayList<>();
            if (bridgeOptions.blurRadius > 0) {
//                options.optionalTransform(new BlurTransformation(context, bridgeOptions.blurRadius));
                transformationList.add(new BlurTransformation(context, bridgeOptions.blurRadius));
            }
            if (bridgeOptions.isCircle) {
//                builder.apply(RequestOptions.circleCropTransform());
                transformationList.add(new CircleCrop());
            } else if (bridgeOptions.roundCorner > 0) {
//                options.optionalTransform(new RoundedCornersTransformation(context, bridgeOptions.roundCorner, 0));
                transformationList.add(new RoundedCorners(bridgeOptions.roundCorner));
//                transformationList.add(new RoundedCornersTransformation(context, bridgeOptions.roundCorner, 0));
            }
            Transformation<Bitmap>[] transformations = transformationList.toArray(
                    (Transformation<Bitmap>[]) Array.newInstance(Transformation.class, transformationList.size()));
            MultiTransformation<Bitmap> multiTransformation = new MultiTransformation(transformations);
            options.transform(multiTransformation);

            if (bridgeOptions.size != null && bridgeOptions.size.isValid()) {
                int imageWidth = bridgeOptions.size.width;
                int imageHeight = bridgeOptions.size.height;
                options.override(imageWidth, imageHeight);
            }

            builder.apply(options);
        }
        builder.into(imageView);
    }

    @Override
    public void getBitmapFromUri(Uri uri, BitmapLoader bitmapLoader) {

    }

}
