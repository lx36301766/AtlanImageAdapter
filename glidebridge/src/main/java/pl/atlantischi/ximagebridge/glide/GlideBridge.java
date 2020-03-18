package pl.atlantischi.ximagebridge.glide;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.auto.service.AutoService;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.glide.transformation.BlurTransformation;
import pl.atlantischi.ximagebridge.interfaces.IGlideBridge;
import pl.atlantischi.ximagebridge.interfaces.ImageBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;

import static com.bumptech.glide.util.Preconditions.*;

/**
 * Created by admin on 2017/7/22.
 */

@AutoService(ImageBridge.class)
public class GlideBridge implements IGlideBridge {

    private Context mContext;

    @Override
    public void initialize(Context context) {
        mContext = context;
    }

    @Override
    public void display(Uri uri, ImageView imageView) {
    }

    @Override
    public void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions) {
        checkNotNull(uri);
        checkNotNull(imageView);
        final Context context = imageView.getContext();
        RequestManager requestManager = Glide.with(imageView);
        requestManager.downloadOnly();
        RequestBuilder<?> builder = requestManager.asBitmap();
        if (bridgeOptions != null) {
            if (bridgeOptions.showAsGif) {
                builder = requestManager.asGif();
            }
            RequestOptions options = buildRequestOptions(bridgeOptions, context);
            builder.apply(options);
        }
        builder.load(uri).into(imageView);
    }

    protected RequestOptions buildRequestOptions(BridgeOptions bridgeOptions, Context context) {
        RequestOptions options = new RequestOptions();
        if (bridgeOptions.size != null && bridgeOptions.size.isValid()) {
            int imageWidth = bridgeOptions.size.width;
            int imageHeight = bridgeOptions.size.height;
            options.override(imageWidth, imageHeight);
        }
        Transformation<Bitmap> transformation = buildTransformation(bridgeOptions, context);
        if (transformation != null) {
            options.transform(transformation);
        }
        if (bridgeOptions.placeHolderDrawable != null) {
            options.placeholder(bridgeOptions.placeHolderDrawable);
        } else if (bridgeOptions.placeHolderResId > 0) {
            options.placeholder(bridgeOptions.placeHolderResId);
        }
        return options;
    }

    @SuppressWarnings("unchecked")
    protected Transformation<Bitmap> buildTransformation(BridgeOptions bridgeOptions, Context context) {
        List<Transformation<Bitmap>> transformationList = new ArrayList<>();
        if (bridgeOptions.blurRadius > 0) {
            transformationList.add(new BlurTransformation(context, bridgeOptions.blurRadius));
        }
        if (bridgeOptions.isCircle) {
            transformationList.add(new CircleCrop());
        } else if (bridgeOptions.roundCorner > 0) {
            transformationList.add(new RoundedCorners(bridgeOptions.roundCorner));
        }
        if (transformationList.size() > 0) {
            Transformation<Bitmap>[] transformations = transformationList.toArray(new Transformation[0]);
            return new MultiTransformation(transformations);
        }
        return null;
    }

    @Override
    public void getBitmapFromUri(Uri uri, final BitmapCallback bitmapCallback) {
        checkNotNull(mContext, "mContext is null, please call initialize(context) before");
        checkNotNull(uri);
        Glide.with(mContext).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (bitmapCallback == null) {
                    return;
                }
                bitmapCallback.onBitmapLoaded(resource);
            }
        });
    }

}
