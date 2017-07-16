package pl.atlantischi.ximagebridge.interfaces;

import android.app.Activity;
import android.widget.ImageView;

/**
 * Created by admin on 2017/7/15.
 */

public interface IFrescoBridge<I extends ImageView> extends ImageBridge<I> {

    void transformFrescoView(Activity activity);

}
