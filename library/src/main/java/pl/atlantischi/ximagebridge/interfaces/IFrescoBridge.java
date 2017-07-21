package pl.atlantischi.ximagebridge.interfaces;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by admin on 2017/7/15.
 */

public interface IFrescoBridge extends ImageBridge {

    /**
     *
     * @param activity
     */
    void replaceToDraweeView(Activity activity, boolean defaultReplace);

    /**
     *
     * @param support
     */
    void setDefaultSupportWrapContent(boolean support);

    interface FrescoBitmapLoader extends BitmapLoader {

    }

}
