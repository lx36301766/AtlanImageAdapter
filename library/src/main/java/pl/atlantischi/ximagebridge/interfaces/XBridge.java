package pl.atlantischi.ximagebridge.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge2;

/**
 * Created by admin on 2017/7/7.
 */

public interface XBridge {

    void display(Uri uri, ImageView imageView, XImageBridge2.Options options);

    void getBitmapFromUri(Uri uri, BitmapLoader bitmapLoader);

    interface BitmapLoader {
        void onBitmapLoaded(Bitmap bitmap);
    }

}
