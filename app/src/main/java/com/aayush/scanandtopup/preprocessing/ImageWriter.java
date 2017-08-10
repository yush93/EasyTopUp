package com.aayush.scanandtopup.preprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageWriter {
    private final int SCALED_HEIGHT = 16;
    private final int SCALED_WIDTH = 16;
    private final int COMPRESS_QUALITY=100;
    Context context;

    public ImageWriter(Context context) {
        this.context = context;
    }

    private String getFolderPath(String folderName) {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String folderPath = Environment.getExternalStorageDirectory() + File.separator + folderName;
        return folderPath;
    }

    private ByteArrayOutputStream getScaledImage(Bitmap printImage, ByteArrayOutputStream byteArrayOutputStream) {
        Bitmap scaled = Bitmap.createScaledBitmap(printImage, SCALED_WIDTH, SCALED_HEIGHT, true);
        scaled.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, byteArrayOutputStream);
        return byteArrayOutputStream;
    }

    public void writeImage(Bitmap printImage, boolean scale, String imageName, String folderName) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (scale) {
            byteArrayOutputStream = getScaledImage(printImage, byteArrayOutputStream);
        } else {
            printImage.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, byteArrayOutputStream);
        }
        File imageFile = new File(getFolderPath(folderName) + File.separator + imageName + "_" + System.currentTimeMillis() / 1000 + ".jpg");
        writeStream(imageFile, byteArrayOutputStream, imageName);
    }

    private boolean writeStream(File imageFile, ByteArrayOutputStream byteArrayOutputStream, String imageName) {
        try {
            if (imageFile.createNewFile()) {
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.close();
                SingleMediaScanner scanNewFolders = new SingleMediaScanner();
                scanNewFolders.beginConnection(context, imageFile);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
