package pl.atlantischi.ximagebridge.universalimageloader.processor;

import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.RSRuntimeException;
import pl.atlantischi.ximagebridge.util.FastBlur;
import pl.atlantischi.ximagebridge.util.RSBlur;

/**
 * Created by admin on 2017/7/27.
 */

public class BlurProcessor implements BitmapProcessor {

    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private Context mContext;

    private int mRadius;
    private int mSampling;

    public BlurProcessor(Context context) {
        this(context, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurProcessor(Context context, int radius) {
        this(context, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurProcessor(Context context, int radius, int sampling) {
        mContext = context.getApplicationContext();
        mRadius = radius;
        mSampling = sampling;
    }

    @Override
    public Bitmap process(Bitmap source) {
        int scaledWidth = source.getWidth() / mSampling;
        int scaledHeight = source.getHeight() / mSampling;

        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) mSampling, 1 / (float) mSampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                bitmap = RSBlur.blur(mContext, bitmap, mRadius);
            } catch (RSRuntimeException e) {
                bitmap = FastBlur.blur(bitmap, mRadius, true);
            }
        } else {
            bitmap = FastBlur.blur(bitmap, mRadius, true);
        }
        source.recycle();
        return bitmap;
    }

}
