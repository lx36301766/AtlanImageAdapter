package pl.atlantischi.ximagebridge.simple.impl;

import com.bumptech.glide.Glide;

import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.XBridge;

/**
 * Created by admin on 2017/7/7.
 */

public class GlideBridge implements XBridge {

    @Override
    public void display(String url, ImageView imageView) {
        Glide.with(imageView).load(url).into(imageView);
    }

}
