package pl.atlantischi.ximagebridge.fresco;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.interfaces.IFrescoBridge;
import timber.log.Timber;

/**
 * Created by admin on 2017/7/7.
 */

public class FrescoBridge implements IFrescoBridge {

    private Context mContext;

    public FrescoBridge() {
        mContext = XImageBridge.obtain().getContext();
        Fresco.initialize(mContext);
    }

    @Override
    public View transformFrescoView(String name, Context context, AttributeSet attrs) {
        if ("ImageView".equals(name)) {
            return new SimpleDraweeView(context, attrs);
        }
        return null;
    }

    @Override
    public void transformFrescoViewForAppCompat(final AppCompatActivity activity) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(activity), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                View view = transformFrescoView(name, context, attrs);
                if (view != null) {
                    return view;
                }
                return activity.getDelegate().createView(parent, name, context, attrs);
            }
        });
    }

    @Override
    public void display(Uri uri, ImageView imageView, XImageBridge.Options options) {
        if (imageView instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) imageView;
            draweeView.setImageURI(uri);
        }
    }


    @Override
    public void getBitmapFromUri(Uri uri, final BitmapLoader bitmapLoader) {

    }

}
