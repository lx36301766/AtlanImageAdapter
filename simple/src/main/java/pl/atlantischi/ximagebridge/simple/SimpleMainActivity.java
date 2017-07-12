package pl.atlantischi.ximagebridge.simple;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.interfaces.XBridge;

public class SimpleMainActivity extends AppCompatActivity {

    String url = "http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=75aaa91fa444ad342eea8f83e59220c2"
            + "/0bd162d9f2d3572cf556972e8f13632763d0c388.jpg";

    XImageBridge mXImageBridge = XImageBridge.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ximage_compat);

        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        mXImageBridge.display(Uri.parse(url), iv);

//        mXImageBridge.getBitmapFromUri(Uri.parse(url), new XBridge.BitmapLoader() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap) {
//                iv.setImageBitmap(bitmap);
//            }
//        });

    }
}
