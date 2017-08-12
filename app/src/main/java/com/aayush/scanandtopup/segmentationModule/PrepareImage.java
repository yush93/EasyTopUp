package com.aayush.scanandtopup.segmentationModule;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class PrepareImage {
    public static Bitmap addBackgroundPixels(Bitmap img) {
        int w = img.getWidth();
        int h = img.getHeight();
        Bitmap resizedImage = Bitmap.createBitmap(w+2, h+2, Bitmap.Config.RGB_565);
        Canvas g = new Canvas();
        g.setBitmap(resizedImage);
        g.drawColor(Color.WHITE);
        g.drawBitmap(img,1,1,null);
        return resizedImage;
    }
}
