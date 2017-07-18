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

    /**
     *
     */
    public static int MAX_RADIUS = 25;

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

    public static class FrescoCompat {

        public static void replaceToDraweeView(Activity activity, boolean defaultReplace) {
            IFrescoBridge frescoBridge = getFrescoBridge();
            if (frescoBridge != null) {
                frescoBridge.replaceToDraweeView(activity, defaultReplace);
            }
        }

        public static void setDefaultSupportWrapContent(boolean support) {
            IFrescoBridge frescoBridge = getFrescoBridge();
            if (frescoBridge != null) {
                frescoBridge.setDefaultSupportWrapContent(support);
            }
        }

        private static IFrescoBridge getFrescoBridge() {
            ImageBridge imageBridge = XImageBridge.obtain().getImageBridge();
            if (imageBridge instanceof IFrescoBridge) {
                return (IFrescoBridge) imageBridge;
            }
            return null;
        }

    }

    public static class Size {

        public int width;
        public int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public boolean isValid() {
            return width > 0 && height > 0;
        }

    }

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
        public Size size;

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

        public Builder setSize(Size size) {
            if (mOptions != null) {
                mOptions.size = size;
            }
            return this;
        }

        public Builder reset() {
            mOptions = new Options();
            mOptions.isCircle = false;
            mOptions.roundCorner = -1;
            mOptions.blurRadius = -1;
            mOptions.size = new Size(0, 0);
            return this;
        }

        public XImageBridge build() {
            XImageBridge xImageBridge = new XImageBridge();
            xImageBridge.mOptions = mOptions;
            return xImageBridge;
        }

    }

    public ImageBridge getImageBridge() {
        return mImageBridge;
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
