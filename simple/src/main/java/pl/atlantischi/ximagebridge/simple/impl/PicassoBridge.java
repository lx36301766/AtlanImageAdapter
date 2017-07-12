package pl.atlantischi.ximagebridge.simple.impl;

import com.squareup.picasso.Picasso;

import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.XBridge;

/**
 * Created by admin on 2017/7/7.
 */

public class PicassoBridge implements XBridge {

    @Override
    public void display(String url, ImageView imageView) {
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }

}
