package pl.atlantischi.ximagebridge.simple;

import android.app.Application;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.simple.impl.PicassoBridge;

/**
 * Created on 12/07/2017.
 *
 * @author lx
 */

public class SimpleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XImageBridge.getInstance().init(this);
        XImageBridge.getInstance().setBridge(new PicassoBridge());
    }
}
