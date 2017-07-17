package pl.atlantischi.ximagebridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.ImageBridge;
import pl.atlantischi.ximagebridge.interfaces.IFrescoBridge;
import timber.log.Timber;

/**
 * Created on 12/07/2017.
 *
 * @author lx
 */

public class XImageBridge {

    @SuppressLint("StaticFieldLeak")
    private static XImageBridge singleton;

    private Context mAppContext;

    private ImageBridge mImageBridge;

    private Options mOptions;

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
        setDefaultBridge();
    }

//    public View compatFresco(String name, Context context, AttributeSet attrs) {
//        if (mImageBridge instanceof IFrescoBridge) {
//            return ((IFrescoBridge) mImageBridge).transformFrescoView(name, context, attrs);
//        }
//        return null;
//    }

    public void compatFresco(Activity activity) {
        if (mImageBridge instanceof IFrescoBridge) {
            ((IFrescoBridge) mImageBridge).transformFrescoView(activity);
        }
    }

    public ImageBridge getBridge() {
        return mImageBridge;
    }

    /**
     *
     */
    public static int MAX_RADIUS = 25;

    public static class Options {

        /**
         *
         */
        public boolean isCircle;

        /**
         *
         */
        public int roundCorner;

        /**
         *
         */
        public int blurRadius;

        /**
         *
         */
        public int[] size;

    }

    /**
     *
     */
    public static class Builder {

        Options mOptions;

        public Builder() {
            reset();
        }

        public Builder setShowAsCircle(boolean isCircle) {
            if (mOptions != null) {
                mOptions.isCircle = isCircle;
            }
            return this;
        }

        public Builder setRoundCorner(int corner) {
            if (mOptions != null) {
                mOptions.roundCorner = corner;
            }
            return this;
        }

        public Builder setBlurRadius(int radius) {
            if (mOptions != null) {
                mOptions.blurRadius = radius;
            }
            return this;
        }

        public Builder setSize(int[] size) {
            if (mOptions != null) {
                mOptions.size = new int[size.length];
                System.arraycopy(size, 0, mOptions.size, 0, size.length);
            }
            return this;
        }

        public Builder reset() {
            mOptions = new Options();
            mOptions.isCircle = false;
            mOptions.roundCorner = -1;
            mOptions.blurRadius = -1;
            mOptions.size = new int[] {};
            return this;
        }

        public XImageBridge build() {
            XImageBridge xImageBridge = new XImageBridge();
            xImageBridge.mOptions = mOptions;
            return xImageBridge;
        }

    }

    /**
     *
     */
    public void setDefaultBridge() {
        Class<?> bridgeClass = null;
        try {
            bridgeClass = Class.forName("pl.atlantischi.ximagebridge.picasso.PicassoBridge");
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        if (bridgeClass == null) {
            try {
                bridgeClass = Class.forName("pl.atlantischi.ximagebridge.fresco.FrescoBridge");
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
            }
        }
        setBridge(bridgeClass);
    }

    /**
     *
     * @param bridgeClass
     */
    public void setBridge(Class bridgeClass) {
        if (bridgeClass != null && ImageBridge.class.isAssignableFrom(bridgeClass)) {
            try {
                Constructor<?> constructor = bridgeClass.getConstructor();
                mImageBridge = (ImageBridge) constructor.newInstance();
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
        if (mImageBridge == null) {
            Timber.e("cannot find Object implements ImageBridge interface");
        }
    }

    public Context getContext() {
        return mAppContext;
    }

    public void display(Uri uri, ImageView imageView) {
        display(uri, imageView, null);
    }

    public void display(Uri uri, ImageView imageView, Options options) {
        if (mImageBridge != null) {
            mImageBridge.display(uri, imageView, options);
        }
    }

    public void getBitmapFromUri(Uri uri, ImageBridge.BitmapLoader bitmapLoader) {
        if (mImageBridge != null) {
            mImageBridge.getBitmapFromUri(uri, bitmapLoader);
        }
    }

}
