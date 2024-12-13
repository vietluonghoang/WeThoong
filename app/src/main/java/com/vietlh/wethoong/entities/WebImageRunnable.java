package com.vietlh.wethoong.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class WebImageRunnable implements Runnable {
    private ImageView bmImage;
    private String urlDisplay;

    public WebImageRunnable(ImageView bmImage, String urlDisplay) {
        this.bmImage = bmImage;
        this.urlDisplay = urlDisplay;
    }

    @Override
    public void run() {
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            bmImage.setImageBitmap(mIcon11);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }
}
