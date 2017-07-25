package pl.atlantischi.ximagebridge.interfaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;

/**
 * Created by admin on 2017/7/7.
 */

public interface ImageBridge {

    void initialize(Context context);

    void display(Uri uri, ImageView imageView);

    void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions);

    void getBitmapFromUri(Uri uri, final BitmapLoader bitmapLoader);

    interface BitmapLoader {
        void onBitmapLoaded(Bitmap bitmap);
    }

}
