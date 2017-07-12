package pl.atlantischi.ximagebridge.simple.impl;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.XBridge;

/**
 * Created by admin on 2017/7/9.
 */

public class UniversalImageLoaderBridge implements XBridge {

    @Override
    public void display(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }

}
