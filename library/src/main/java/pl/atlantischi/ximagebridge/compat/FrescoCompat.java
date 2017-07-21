package pl.atlantischi.ximagebridge.compat;

import android.app.Activity;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.interfaces.IFrescoBridge;
import pl.atlantischi.ximagebridge.interfaces.ImageBridge;

/**
 * Created on 19/07/2017.
 *
 * @author lx
 */

public class FrescoCompat {

    public static void replaceToDraweeView(Activity activity, boolean defaultReplace) {
        IFrescoBridge frescoBridge = getFrescoBridge();
        if (frescoBridge != null) {
            frescoBridge.replaceToDraweeView(activity, defaultReplace);
        }
    }

    public static void setDefaultSupportWrapContent(boolean support) {
        IFrescoBridge frescoBridge = getFrescoBridge();
        if (frescoBridge != null) {
            frescoBridge.setDefaultSupportWrapContent(support);
        }
    }

    private static IFrescoBridge getFrescoBridge() {
        ImageBridge imageBridge = XImageBridge.getBaseBridge();
        if (imageBridge instanceof IFrescoBridge) {
            return (IFrescoBridge) imageBridge;
        }
        return null;
    }

}
