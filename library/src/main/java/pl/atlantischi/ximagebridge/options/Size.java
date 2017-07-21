package pl.atlantischi.ximagebridge.options;

/**
 * Created on 19/07/2017.
 *
 * @author lx
 */

public class Size {

    public int width;
    public int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isValid() {
        return width > 0 && height > 0;
    }

}
