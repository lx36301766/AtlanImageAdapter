package pl.atlantischi.ximagebridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.content.Context;
import pl.atlantischi.ximagebridge.interfaces.ImageBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;
import pl.atlantischi.ximagebridge.options.Size;
import timber.log.Timber;

/**
 * Created on 12/07/2017.
 *
 * @author lx
 */

public class XImageBridge {

    @SuppressLint("StaticFieldLeak")
    private static XImageBridge singleton;

    private static Context mAppContext;
    private static ImageBridgeWrapper mImageBridgeWrapper;
    private BridgeOptions mBridgeOptions;

    /**
     *
     */
    public static int MAX_RADIUS = 25;

    private static String[] mDefaultBridges = new String[] {
            "pl.atlantischi.ximagebridge.picasso.PicassoBridge",
            "pl.atlantischi.ximagebridge.fresco.FrescoBridge",
    };

    public static ImageBridge get() {
        if (mImageBridgeWrapper == null) {
            synchronized(XImageBridge.class) {
                if (mImageBridgeWrapper == null) {
                    ImageBridge imageBridge = null;
                    for (String bridge : mDefaultBridges) {
                        try {
                            Class<?>  bridgeClass = Class.forName(bridge);
                            if (ImageBridge.class.isAssignableFrom(bridgeClass)) {
                                Constructor<?> constructor = bridgeClass.getConstructor();
                                imageBridge = (ImageBridge) constructor.newInstance();
                                break;
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                    mImageBridgeWrapper = new ImageBridgeWrapper(imageBridge);
                }
            }
        }
        return mImageBridgeWrapper;
    }

    public static XImageBridge obtain() {
        if (singleton == null) {
            synchronized (XImageBridge.class) {
                if (singleton == null) {
                    singleton = new XImageBridge.Builder().build();
                }
            }
        }
        return singleton;
    }

    private XImageBridge() {
    }

    public void initialize(Context context) {
        mAppContext = context.getApplicationContext();
        linkDefaultBridge();
    }

    /**
     *
     */
    public void linkDefaultBridge() {
        Class<?> bridgeClass = null;
        for (String bridge : mDefaultBridges) {
            try {
                if (bridgeClass == null) {
                    bridgeClass = Class.forName(bridge);
                }
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
            }
        }
        linkBridge(bridgeClass);
    }

    /**
     *
     * @param bridgeClass
     */
    public void linkBridge(Class bridgeClass) {
        if (bridgeClass != null && ImageBridge.class.isAssignableFrom(bridgeClass)) {
            try {
                Constructor<?> constructor = bridgeClass.getConstructor();
                ImageBridge imageBridge = (ImageBridge) constructor.newInstance();
                mImageBridgeWrapper = new ImageBridgeWrapper(imageBridge);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (mImageBridgeWrapper == null) {
            Timber.e("cannot find Object implements ImageBridge interface");
        }
    }

    /**
     *
     */
    public static class Builder {

        BridgeOptions mBridgeOptions;

        public Builder() {
            reset();
        }

        public Builder setShowAsCircle(boolean isCircle) {
            if (mBridgeOptions != null) {
                mBridgeOptions.isCircle = isCircle;
            }
            return this;
        }

        public Builder setRoundCorner(int corner) {
            if (mBridgeOptions != null) {
                mBridgeOptions.roundCorner = corner;
            }
            return this;
        }

        public Builder setBlurRadius(int radius) {
            if (mBridgeOptions != null) {
                mBridgeOptions.blurRadius = radius;
            }
            return this;
        }

        public Builder setSize(Size size) {
            if (mBridgeOptions != null) {
                mBridgeOptions.size = size;
            }
            return this;
        }

        public Builder reset() {
            mBridgeOptions = new BridgeOptions();
            mBridgeOptions.isCircle = false;
            mBridgeOptions.roundCorner = -1;
            mBridgeOptions.blurRadius = -1;
            mBridgeOptions.size = new Size(0, 0);
            return this;
        }

        public XImageBridge build() {
            XImageBridge xImageBridge = new XImageBridge();
            xImageBridge.mBridgeOptions = mBridgeOptions;
            return xImageBridge;
        }

    }

    public Context getContext() {
        return mAppContext;
    }

    public ImageBridge getImageBridge() {
        if (mImageBridgeWrapper != null) {
            return mImageBridgeWrapper.getImageBridge();
        }
        return null;
    }

}
