package com.aayush.scanandtopup.preprocessingModule;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import java.io.File;

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mediaScannerConnection;
    private File imageFile;

    public SingleMediaScanner() { }

    public void beginConnection(Context context, File imageFile){
        this.imageFile = imageFile;
        mediaScannerConnection = new MediaScannerConnection(context, this);
        mediaScannerConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mediaScannerConnection.scanFile(imageFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mediaScannerConnection.disconnect();
    }
}
