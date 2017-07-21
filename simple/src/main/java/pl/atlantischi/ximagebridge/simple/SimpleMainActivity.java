package pl.atlantischi.ximagebridge.simple;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.XImageBridge;
import pl.atlantischi.ximagebridge.fresco.FrescoCompat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FrescoCompat.replaceToDraweeView(this, false);
        FrescoCompat.setDefaultSupportWrapContent(true);

        super.onCreate(savedInstanceState);
        setTitle(XImageBridge.obtain().getImageBridge().getClass().getSimpleName());
        setContentView(R.layout.activity_ximage_compat);

        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        BridgeOptions options = new BridgeOptions();
//        options.isCircle = true;
//        options.roundCorner = 50;
//        options.blurRadius = 10;
        options.size = new Size(800, 700);
        XImageBridge.obtain().display(Uri.parse(png), iv, options);

//        mXImageBridge.getBitmapFromUri(Uri.parse(url), new ImageBridge.BitmapLoader() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap) {
//                iv.setImageBitmap(bitmap);
//            }
//        });

    }
}
