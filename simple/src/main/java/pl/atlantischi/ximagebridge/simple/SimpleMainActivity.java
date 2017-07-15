package pl.atlantischi.ximagebridge.simple;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;

public class SimpleMainActivity extends AppCompatActivity {

    String url = "http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=75aaa91fa444ad342eea8f83e59220c2"
            + "/0bd162d9f2d3572cf556972e8f13632763d0c388.jpg";

//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        View view = XImageBridge.obtain().compatFresco(name, context, attrs);
//        if (view != null) {
//            return view;
//        }
//        return super.onCreateView(name, context, attrs);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XImageBridge.obtain().compatFrescoWithAppCompat(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ximage_compat);

        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        XImageBridge.obtain().display(Uri.parse(url), iv);

//        mXImageBridge.getBitmapFromUri(Uri.parse(url), new Bridge.BitmapLoader() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap) {
//                iv.setImageBitmap(bitmap);
//            }
//        });

    }
}
