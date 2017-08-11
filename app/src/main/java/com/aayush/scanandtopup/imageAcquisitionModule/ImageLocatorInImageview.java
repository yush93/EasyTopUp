package com.aayush.scanandtopup.imageAcquisitionModule;

import android.graphics.Rect;
import android.widget.ImageView;
import com.aayush.scanandtopup.interfaceModule.Coordinates;

public class ImageLocatorInImageview implements Coordinates {
    Rect imageViewCoordinates = new Rect();

    public ImageLocatorInImageview(ImageView imageView){
        ImageParametersInImageview imageParametersInImageview = new ImageParametersInImageview(imageView);
        final int scaledWidth = Math.round(imageParametersInImageview.getImageWidth() * imageParametersInImageview.getScaleX()); //Needs to make
        final int scaledHeight = Math.round(imageParametersInImageview.getImageHeight() * imageParametersInImageview.getScaleY());
        imageViewCoordinates.left = (int) Math.max(imageParametersInImageview.getTransX(), 0);
        imageViewCoordinates.top = (int) Math.max(imageParametersInImageview.getTransY(), 0);
        imageViewCoordinates.right = Math.min(imageViewCoordinates.left + scaledWidth, imageView.getWidth());
        imageViewCoordinates.bottom= Math.min(imageViewCoordinates.top + scaledHeight, imageView.getHeight());
    }

    @Override
    public Rect getCoordinates() {return imageViewCoordinates;}
}
