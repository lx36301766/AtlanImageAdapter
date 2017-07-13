package pl.atlantischi.ximagebridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.XBridge;
import timber.log.Timber;

/**
 * Created on 12/07/2017.
 *
 * @author lx
 */

public class XImageBridge2 {

    @SuppressLint("StaticFieldLeak")
    private static XImageBridge2 singleton;

    private Context mAppContext;

    private XBridge mXBridge;

    private Options mOptions;

    public static XImageBridge2 get(Context context) {
        if (singleton == null) {
            synchronized (XImageBridge2.class) {
                if (singleton == null) {
                    singleton = new XImageBridge2.Builder(context).build();
                }
            }
        }
        return singleton;
    }

    private XImageBridge2(Context context) {
        init(context);
    }

    private void init(Context context) {
        mAppContext = context.getApplicationContext();
        setDefaultBridge();
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

    }

    /**
     *
     */
    public static class Builder {

        Context context;
        public boolean mIsCircle;
        public int mRoundCorner;

        public Builder(Context context) {
            this.context = context;
            reset();
        }

        public Builder setShowAsCircle(boolean isCircle) {
            mIsCircle = isCircle;
            return this;
        }

        public Builder setRoundCorner(int corner) {
            mRoundCorner = corner;
            return this;
        }

        public Builder reset() {
            mIsCircle = false;
            mRoundCorner = -1;
            return this;
        }

        public XImageBridge2 build() {
            XImageBridge2 xImageBridge2 = new XImageBridge2(context);
            xImageBridge2.mOptions = new Options();
            xImageBridge2.mOptions.isCircle = mIsCircle;
            xImageBridge2.mOptions.roundCorner = mRoundCorner;
            return xImageBridge2;
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
            e.printStackTrace();
        }
        setBridge(bridgeClass);
    }

    /**
     *
     * @param bridgeClass
     */
    public void setBridge(Class bridgeClass) {
        if (bridgeClass != null && XBridge.class.isAssignableFrom(bridgeClass)) {
            try {
                Constructor<?> constructor = bridgeClass.getConstructor();
                mXBridge = (XBridge) constructor.newInstance();
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
        if (mXBridge == null) {
            Timber.e("cannot find instance implements XBridge");
        }
    }

    public Context getContext() {
        return mAppContext;
    }

    public void display(Uri uri, ImageView imageView) {
        display(uri, imageView, null);
    }

    public void display(Uri uri, ImageView imageView, Options options) {
        if (mXBridge != null) {
            mXBridge.display(uri, imageView, options);
        }
    }

    public void getBitmapFromUri(Uri uri, XBridge.BitmapLoader bitmapLoader) {
        if (mXBridge != null) {
            mXBridge.getBitmapFromUri(uri, bitmapLoader);
        }
    }

}
