package pl.atlantischi.ximagebridge.picasso.transformation;

import com.squareup.picasso.Transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;

/**
 * Created on 13/07/2017.
 *
 * @author lx
 */

public class CropToCircleTransformation implements Transformation {

    private Integer mRadius = null;

    public CropToCircleTransformation() {
    }

    public CropToCircleTransformation(int radius) {
        if (radius > 0) {
            mRadius = radius;
        }
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.max(source.getWidth(), source.getHeight());

        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(result);

        BitmapShader shader;
        shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        RectF rect = new RectF(0, 0, size, size);
        int radius = size / 2;
        if (mRadius != null) {
            radius = mRadius;
        }
        canvas.drawRoundRect(rect, radius, radius, paint);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @NonNull
    @Override
    public String key() {
        return "CropToCircle";
    }

}
