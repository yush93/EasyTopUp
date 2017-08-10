package com.aayush.scanandtopup.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.widget.ImageView;
import com.aayush.scanandtopup.interfaces.Coordinates;

public class CoordinateLocatorInBitmap implements Coordinates {
    private Rect bitmapCoordinates = new Rect();

    public CoordinateLocatorInBitmap(ImageView imageView, Rect clippingWindowCoordinates) {
        ImageParametersInImageview imageParametersInImageview = new ImageParametersInImageview(imageView);
        bitmapCoordinates.left = (int) Math.max((clippingWindowCoordinates.left - imageParametersInImageview.getTransX()) / imageParametersInImageview.getScaleX(), 0);  //Since Image is translated and scaled in Imageview
        bitmapCoordinates.right = (int) Math.min((clippingWindowCoordinates.right - imageParametersInImageview.getTransX()) / imageParametersInImageview.getScaleX(), imageParametersInImageview.getImageWidth());
        bitmapCoordinates.top = (int) Math.max((clippingWindowCoordinates.top - imageParametersInImageview.getTransY()) / imageParametersInImageview.getScaleY(), 0);
        bitmapCoordinates.bottom = (int) Math.min((clippingWindowCoordinates.bottom - imageParametersInImageview.getTransY()) / imageParametersInImageview.getScaleY(), imageParametersInImageview.getImageHeight());
    }

    public CoordinateLocatorInBitmap(Bitmap image, Rect cameraOverlayWindow) {
        bitmapCoordinates.left = (image.getWidth() * cameraOverlayWindow.left) / CameraOverlay.getParentWidth();
        bitmapCoordinates.right = (image.getWidth() * cameraOverlayWindow.right) / CameraOverlay.getParentWidth();
        bitmapCoordinates.top = (image.getHeight() * cameraOverlayWindow.top) / CameraOverlay.getParentHeight();
        bitmapCoordinates.bottom = (image.getHeight() * cameraOverlayWindow.bottom) / CameraOverlay.getParentHeight();
    }

    @Override
    public Rect getCoordinates() {return bitmapCoordinates;}
}
