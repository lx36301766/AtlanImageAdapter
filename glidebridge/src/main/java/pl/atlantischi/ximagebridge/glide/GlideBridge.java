package pl.atlantischi.ximagebridge.glide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
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
        RequestBuilder builder = Glide.with(imageView).load(uri);
        if (bridgeOptions != null) {
            if (bridgeOptions.isCircle) {
                builder.apply(RequestOptions.circleCropTransform());
            } else if (bridgeOptions.roundCorner > 0) {
//                builder.transition()
            }
        }
        builder.into(imageView);
    }

    @Override
    public void getBitmapFromUri(Uri uri, BitmapLoader bitmapLoader) {

    }

}
