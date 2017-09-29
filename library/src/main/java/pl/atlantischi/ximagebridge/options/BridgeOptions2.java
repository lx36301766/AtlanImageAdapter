package pl.atlantischi.ximagebridge.options;

import android.graphics.drawable.Drawable;

/**
 * Created on 19/07/2017.
 *
 * @author lx
 */

public class BridgeOptions2 {

    private boolean isCircle;
    private int roundCorner;

    private int blurRadius;

    private Size size;

    private Drawable placeHolderDrawable;
    private int placeHolderResId;

    public BridgeOptions2(Builder builder) {
        isCircle = builder.isCircle;
        roundCorner = builder.roundCorner;
        blurRadius = builder.blurRadius;
        size = builder.size;
        placeHolderDrawable = builder.placeHolderDrawable;
        placeHolderResId = builder.placeHolderResId;
    }

    public static class Builder {

        private boolean isCircle;
        private int roundCorner;
        private int blurRadius;
        private Size size;
        private Drawable placeHolderDrawable;
        private int placeHolderResId;

        public Builder displayAsCircle(boolean circle) {
            this.isCircle = circle;
            return this;
        }

        public Builder setRoundCorner(int roundCorner) {
            this.roundCorner = roundCorner;
            return this;
        }

        public Builder setBlurRadius(int blurRadius) {
            this.blurRadius = blurRadius;
            return this;
        }

        public Builder setImageSize(int width, int height) {
            this.size = new Size(width, height);
            return this;
        }

        public Builder setPlaceHolder(Drawable placeHolderDrawable) {
            this.placeHolderDrawable = placeHolderDrawable;
            return this;
        }

        public Builder setPlaceHolder(int placeHolderResId) {
            this.placeHolderResId = placeHolderResId;
            return this;
        }

        public Builder clone(BridgeOptions2 bridgeOptions2) {
            isCircle = bridgeOptions2.isCircle;
            roundCorner = bridgeOptions2.roundCorner;
            blurRadius = bridgeOptions2.blurRadius;
            size = bridgeOptions2.size;
            placeHolderDrawable = bridgeOptions2.placeHolderDrawable;
            placeHolderResId = bridgeOptions2.placeHolderResId;
            return this;
        }

        public BridgeOptions2 build() {
            return new BridgeOptions2(this);
        }

    }

}
