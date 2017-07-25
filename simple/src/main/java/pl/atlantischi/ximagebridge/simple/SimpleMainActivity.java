package pl.atlantischi.ximagebridge.simple;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.compat.FrescoCompat;
import pl.atlantischi.ximagebridge.interfaces.IFrescoBridge;
import pl.atlantischi.ximagebridge.interfaces.ImageBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;
import pl.atlantischi.ximagebridge.options.Size;

public class SimpleMainActivity extends AppCompatActivity {

    String jpg = "http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=75aaa91fa444ad342eea8f83e59220c2"
            + "/0bd162d9f2d3572cf556972e8f13632763d0c388.jpg";

    String png = "https://p.upyun.com/demo/webp/png/0.png";

    String webP = "https://p.upyun.com/demo/webp/webp/animated-gif-0.webp";

    String gif = "https://p.upyun.com/demo/webp/animated-gif/0.gif";

//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        View view = XImageBridge.obtain().compatFresco(name, context, attrs);
//        if (view != null) {
//            return view;
//        }
//        return super.onCreateView(name, context, attrs);
//    }

    //    @Override
    //    public View transformFrescoView(String name, Context context, AttributeSet attrs) {
    //        if ("ImageView".equals(name)) {
    //            return new SimpleDraweeView(context, attrs);
    //        }
    //        return null;
    //    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FrescoCompat.replaceToDraweeView(this, false);
        FrescoCompat.setDefaultSupportWrapContent(true);

        super.onCreate(savedInstanceState);
        setTitle(XImageBridge.obtain().toString());
        setContentView(R.layout.activity_ximage_compat);

        final ImageView iv1 = (ImageView) findViewById(R.id.imageView);
        XImageBridge.obtain().display(Uri.parse(jpg), iv1);

        final ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
        BridgeOptions options2 = new BridgeOptions();
//        options2.isCircle = true;
//        options2.roundCorner = 50;
        options2.blurRadius = 10;
        options2.size = new Size(200, 50);
        XImageBridge.obtain().display(Uri.parse(jpg), iv2, options2);

        final ImageView iv3 = (ImageView) findViewById(R.id.imageView3);
        BridgeOptions options3 = new BridgeOptions();
//        options3.isCircle = true;
//        options3.roundCorner = 50;
//        options3.blurRadius = 10;
        options3.size = new Size(800, 600);
        XImageBridge.obtain().display(Uri.parse(jpg), iv3, options3);

    }
}
