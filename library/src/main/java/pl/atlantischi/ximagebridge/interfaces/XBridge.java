package pl.atlantischi.ximagebridge.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by admin on 2017/7/7.
 */

public interface XBridge {

    void display(Uri uri, ImageView imageView);

    void getBitmapFromUri(Uri uri, BitmapLoader bitmapLoader);

    interface BitmapLoader {
        void onBitmapLoaded(Bitmap bitmap);
    }

}
