package pl.atlantischi.ximagebridge.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;

/**
 * Created by admin on 2017/7/7.
 */

public interface ImageBridge<I extends ImageView> {

    void display(Uri uri, I imageView, XImageBridge.Options options);

    void getBitmapFromUri(Uri uri, BitmapLoader bitmapLoader);

    interface BitmapLoader {
        void onBitmapLoaded(Bitmap bitmap);
    }

}
