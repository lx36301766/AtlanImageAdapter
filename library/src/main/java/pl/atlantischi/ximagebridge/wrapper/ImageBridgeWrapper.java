package pl.atlantischi.ximagebridge.wrapper;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.ImageBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;

/**
 * Created on 19/07/2017.
 *
 * @author lx
 */

public class ImageBridgeWrapper implements ImageBridge {

    private ImageBridge mBase;

    public ImageBridgeWrapper(ImageBridge imageBridge) {
        this.mBase = imageBridge;
    }

    @Override
    public void initialize(Context context) {
        if (mBase != null) {
            mBase.initialize(context);
        }
    }

    @Override
    public void display(Uri uri, ImageView imageView) {
        display(uri, imageView, null);
    }

    public void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions) {
        if (mBase != null) {
            mBase.display(uri, imageView, bridgeOptions);
        }
    }

    public void getBitmapFromUri(Uri uri, ImageBridge.BitmapLoader bitmapLoader) {
        if (mBase != null) {
            mBase.getBitmapFromUri(uri, bitmapLoader);
        }
    }

    public ImageBridge getBaseBridge() {
        return mBase;
    }

    @Override
    public String toString() {
        if (mBase != null) {
            return mBase.getClass().getSimpleName();
        }
        return "ImageBridgeWrapper--->mBase is null";
    }

}
