package pl.atlantischi.ximagebridge.simple;

import android.app.Application;
import pl.atlantischi.ximagebridge.XImageBridge;
import timber.log.Timber;

/**
 * Created on 12/07/2017.
 *
 * @author lx
 */

public class SimpleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        XImageBridge.obtain().initialize(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //Timber.plant(new CrashReportingTree());
        }
    }
}
