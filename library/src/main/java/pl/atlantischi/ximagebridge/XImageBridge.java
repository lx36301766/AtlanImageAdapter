package pl.atlantischi.ximagebridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.Bridge;
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

    private Bridge mBridge;

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

    public void init(Context context) {
        mAppContext = context.getApplicationContext();
        setDefaultBridge();
    }

    public View compatFresco(String name, Context context, AttributeSet attrs) {
        if (mBridge instanceof IFrescoBridge) {
            return ((IFrescoBridge) mBridge).transformFrescoView(name, context, attrs);
        }
        return null;
    }

    public void compatFrescoWithAppCompat(AppCompatActivity activity) {
        if (mBridge instanceof IFrescoBridge) {
            ((IFrescoBridge) mBridge).transformFrescoViewForAppCompat(activity);
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

    }

    /**
     *
     */
    public static class Builder {

//        Context context;
        public boolean mIsCircle;
        public int mRoundCorner;

        public Builder() {
//            this.context = context;
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

        public XImageBridge build() {
            XImageBridge xImageBridge = new XImageBridge();
            xImageBridge.mOptions = new Options();
            xImageBridge.mOptions.isCircle = mIsCircle;
            xImageBridge.mOptions.roundCorner = mRoundCorner;
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
        if (bridgeClass != null && Bridge.class.isAssignableFrom(bridgeClass)) {
            try {
                Constructor<?> constructor = bridgeClass.getConstructor();
                mBridge = (Bridge) constructor.newInstance();
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
        if (mBridge == null) {
            Timber.e("cannot find instance implements Bridge");
        }
    }

    public Context getContext() {
        return mAppContext;
    }

    public void display(Uri uri, ImageView imageView) {
        display(uri, imageView, null);
    }

    public void display(Uri uri, ImageView imageView, Options options) {
        if (mBridge != null) {
            mBridge.display(uri, imageView, options);
        }
    }

    public void getBitmapFromUri(Uri uri, Bridge.BitmapLoader bitmapLoader) {
        if (mBridge != null) {
            mBridge.getBitmapFromUri(uri, bitmapLoader);
        }
    }

}
