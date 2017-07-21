package pl.atlantischi.ximagebridge;

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

    private ImageBridge mBridge;

    public ImageBridgeWrapper(ImageBridge imageBridge) {
        this.mBridge = imageBridge;
    }

    public void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions) {
        if (mBridge != null) {
            mBridge.display(uri, imageView, bridgeOptions);
        }
    }

    public void getBitmapFromUri(Uri uri, ImageBridge.BitmapLoader bitmapLoader) {
        if (mBridge != null) {
            mBridge.getBitmapFromUri(uri, bitmapLoader);
        }
    }

    public ImageBridge getImageBridge() {
        return mBridge;
    }

}
