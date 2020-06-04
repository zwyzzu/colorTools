package com.zhangwy.colortools;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seekBar;
    private TextView seekLeft;
    private TextView seekRight;
    private ImageView plateLeft;
    private ImageView plateRight;
    private TextView plateLeftRGB;
    private TextView plateRightRGB;
    private ImageView plateRequest;
    private TextView plateRequestRGB;
    private final int SEEK_MAX = 10;
    private final int SEEK_DEFAULT = 5;
    private int progress = SEEK_DEFAULT;
    private int plateRadius = 100;
    private int plateRequestRadius = 200;
    private int strokeWidth = 2;
    private int plateLeftColor = Color.YELLOW;
    private int plateRightColor = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.seekLeft = this.findViewById(R.id.mainColorSeekLeft);
        this.seekRight = this.findViewById(R.id.mainColorSeekRight);
        this.seekBar = this.findViewById(R.id.mainColorSeek);
        this.plateLeft = this.findViewById(R.id.mainColorPlateLeft);
        this.plateRight = this.findViewById(R.id.mainColorPlateRight);
        this.plateLeftRGB = this.findViewById(R.id.mainColorPlateLeftRGB);
        this.plateRightRGB = this.findViewById(R.id.mainColorPlateRightRGB);
        this.plateRequest = this.findViewById(R.id.mainColorPlateRequest);
        this.plateRequestRGB = this.findViewById(R.id.mainColorPlateRequestRGB);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int plateLength = (int) (screenWidth - metrics.density * (8 * 4 + 48)) / 2;
        this.plateLeft.getLayoutParams().width = plateLength;
        this.plateLeft.getLayoutParams().height = plateLength;
        this.plateRight.getLayoutParams().width = plateLength;
        this.plateRight.getLayoutParams().height = plateLength;
        this.plateRequest.getLayoutParams().width = plateLength * 2;
        this.plateRequest.getLayoutParams().height = plateLength * 2;
        this.plateRadius = plateLength / 2;
        this.plateRequestRadius = plateLength;
        this.strokeWidth = (int) (metrics.density * this.strokeWidth);
        this.initSeekBar();
    }

    private void initSeekBar() {
        this.seekBar.setOnSeekBarChangeListener(this);
        this.seekBar.setMax(this.SEEK_MAX);
        this.seekBar.setProgress(this.SEEK_DEFAULT);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.refresh(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void refresh(int progress) {
        this.progress = progress;
        progress = progress * 10;
        this.seekLeft.setText(getString(R.string.color_percent, String.valueOf(progress)));
        this.seekRight.setText(getString(R.string.color_percent, String.valueOf(100 - progress)));
        this.plateLeft.setImageDrawable(this.createRoundRectDrawable(this.plateRadius, this.plateLeftColor, this.strokeWidth));
        this.plateRight.setImageDrawable(this.createRoundRectDrawable(this.plateRadius, this.plateRightColor, this.strokeWidth));
        int plateRequestColor = getRequestColor(this.plateLeftColor, this.plateRightColor, this.progress);
        this.plateRequest.setImageDrawable(this.createRoundRectDrawable(this.plateRequestRadius, plateRequestColor, this.strokeWidth));
        this.plateLeftRGB.setText(new UIColor(this.plateLeftColor).toString());
        this.plateRightRGB.setText(new UIColor(this.plateRightColor).toString());
        this.plateRequestRGB.setText(new UIColor(plateRequestColor).toString());
    }

    public GradientDrawable createRoundRectDrawable(int radius, @ColorInt int color, int strokeWidth) {
        //左上、右上、右下、左下的圆角半径
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        drawable.setColor(color);
        UIColor uiColor = new UIColor(color);
        drawable.setStroke(strokeWidth, uiColor.rollback(false).toColor());
        return drawable;
    }

    @ColorInt
    private int getRequestColor(@ColorInt int left, @ColorInt int right, int progress) {
        UIColor uiColorLeft = new UIColor(left);
        UIColor uiColorRight = new UIColor(right);
        float pgs = 1f - (progress * 1f / this.SEEK_MAX);
        return uiColorLeft.add(uiColorRight, pgs).toColor();
    }

    private static class UIColor {
        private int alpha;
        private int red;
        private int green;
        private int blue;

        private UIColor(int alpha, int red, int green, int blue) {
            this.alpha = alpha;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        private UIColor(@ColorInt int color) {
            alpha = Color.alpha(color);//(color >> 24) & 0xff);
            red = Color.red(color);//(color >> 16) & 0xff);
            green = Color.green(color);//(color >> 8) & 0xff);
            blue = Color.blue(color);//(color) & 0xff);
        }

        UIColor rollback(boolean rollbackAlpha) {
            int cAlpha = rollbackAlpha ? (255 - this.alpha) : this.alpha;
            int cRed = 255 - this.red;
            int cGreen = 255 - this.green;
            int cBlue = 255 - this.blue;
            return new UIColor(cAlpha, cRed, cGreen, cBlue);
        }

        UIColor add(UIColor uiColor, float progress) {
            int iProgress = (int) (progress * 100);
            int cAlpha = ((this.alpha * (100 - iProgress)) + (uiColor.alpha * iProgress)) / 100;
            int cRed = ((this.red * (100 - iProgress)) + (uiColor.red * iProgress)) / 100;
            int cGreen = ((this.green * (100 - iProgress)) + (uiColor.green * iProgress)) / 100;
            int cBlue = ((this.blue * (100 - iProgress)) + (uiColor.blue * iProgress)) / 100;
            return new UIColor(cAlpha, cRed, cGreen, cBlue);
        }

        @ColorInt
        int toColor() {
            return (alpha << 24) | (red << 16) | (green << 8) | blue;
        }

        @NonNull
        @Override
        public String toString() {
            return "#" + hexString(this.alpha) + hexString(this.red) + hexString(this.green) + hexString(this.blue);
        }

        private String hexString(float value) {
            String hex = Integer.toHexString((int) (value + 0.5f));
            return hex.length() == 1 ? "0" + hex : hex;
        }
    }
}