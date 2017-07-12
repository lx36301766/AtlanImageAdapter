package pl.atlantischi.ximagebridge;

import android.content.Context;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.XBridge;

/**
 * Created on 12/07/2017.
 *
 * @author lx
 */

public class XImageBridge {




    private static class InstanceHolder {
        private static XImageBridge mInstance = new XImageBridge();
    }

    public static XImageBridge getInstance() {
        return InstanceHolder.mInstance;
    }

    private XBridge mXBridge;
    private Context mContext;

    public void init(Context context) {
        mContext = context;
    }

    public void setBridge(XBridge bridge) {
        mXBridge = bridge;
    }

    public void display(String url, ImageView imageView) {
        if (mXBridge != null) {
            mXBridge.display(url, imageView);
        }
    }

}
