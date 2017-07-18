package pl.atlantischi.ximagebridge.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;

/**
 * Created by admin on 2017/7/7.
 */

public interface ImageBridge {

    /**
     *
     * @param uri
     * @param imageView
     * @param options
     */
    void display(Uri uri, ImageView imageView, XImageBridge.Options options);

    /**
     *
     * @param uri
     * @param bitmapLoader
     */
    void getBitmapFromUri(Uri uri, BitmapLoader bitmapLoader);

    interface BitmapLoader {
        void onBitmapLoaded(Bitmap bitmap);
    }

}
