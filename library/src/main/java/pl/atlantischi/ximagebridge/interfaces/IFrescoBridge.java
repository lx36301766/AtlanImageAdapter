package pl.atlantischi.ximagebridge.interfaces;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2017/7/15.
 */

public interface IFrescoBridge extends Bridge {

    View transformFrescoView(String name, Context context, AttributeSet attrs);

    void transformFrescoViewForAppCompat(AppCompatActivity activity);

}
