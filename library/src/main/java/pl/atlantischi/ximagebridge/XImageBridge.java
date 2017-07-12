package pl.atlantischi.ximagebridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

public class XImageBridge {

    private static class InstanceHolder {
        private static XImageBridge mInstance = new XImageBridge();
    }

    public static XImageBridge getInstance() {
        return InstanceHolder.mInstance;
    }

    private XBridge mXBridge;

    private Context mAppContext;

    public void init(Context context) {
        mAppContext = context.getApplicationContext();
        setDefaultBridge();
    }

    public void setDefaultBridge() {
        Class<?> bridgeClass = null;
        try {
            bridgeClass = Class.forName("pl.atlantischi.ximagebridge.picasso.PicassoBridge");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setBridge(bridgeClass);
    }

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
        if (mXBridge != null) {
            mXBridge.display(uri, imageView);
        }
    }

    public void getBitmapFromUri(Uri uri, XBridge.BitmapLoader bitmapLoader) {
        if (mXBridge != null) {
            mXBridge.getBitmapFromUri(uri, bitmapLoader);
        }
    }

}
