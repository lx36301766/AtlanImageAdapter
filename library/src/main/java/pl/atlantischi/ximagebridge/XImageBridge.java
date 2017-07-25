package pl.atlantischi.ximagebridge;

import java.lang.reflect.Constructor;

import android.annotation.SuppressLint;
import android.content.Context;
import pl.atlantischi.ximagebridge.interfaces.ImageBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;
import pl.atlantischi.ximagebridge.wrapper.ImageBridgeWrapper;

/**
 * Created on 12/07/2017.
 *
 * @author lx
 */

public class XImageBridge {

    /**
     *
     */
    public static int MAX_RADIUS = 25;

    private static ImageBridgeWrapper mImageBridgeWrapper;

    private static String[] mDefaultBridges = new String[] {
            "pl.atlantischi.ximagebridge.glide.GlideBridge",
            "pl.atlantischi.ximagebridge.picasso.PicassoBridge",
            "pl.atlantischi.ximagebridge.fresco.FrescoBridge",
    };

    public static void initialize(Context context) {
        obtain().initialize(context.getApplicationContext());
    }

    public static ImageBridge obtain() {
        if (mImageBridgeWrapper == null) {
            synchronized(XImageBridge.class) {
                if (mImageBridgeWrapper == null) {
                    ImageBridge baseBridge = findBridge();
                    setBridge(baseBridge);
                }
            }
        }
        return mImageBridgeWrapper;
    }

    private static ImageBridge findBridge() {
        ImageBridge imageBridge = null;
        for (String bridgeStr : mDefaultBridges) {
            try {
                Class<?> bridgeClass = Class.forName(bridgeStr);
                if (ImageBridge.class.isAssignableFrom(bridgeClass)) {
                    Constructor<?> constructor = bridgeClass.getConstructor();
                    imageBridge = (ImageBridge) constructor.newInstance();
                    break;
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return imageBridge;
    }

    public static void setBridge(ImageBridge imageBridge) {
        synchronized(XImageBridge.class) {
            mImageBridgeWrapper = new ImageBridgeWrapper(imageBridge);
        }
    }

    public static ImageBridge getBaseBridge() {
        if (mImageBridgeWrapper != null) {
            return mImageBridgeWrapper.getBaseBridge();
        }
        return null;
    }

}
