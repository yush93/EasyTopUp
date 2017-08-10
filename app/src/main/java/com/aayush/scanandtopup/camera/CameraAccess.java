package com.aayush.scanandtopup.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aayush.scanandtopup.maininterfaces.MainActivity;
import com.aayush.scanandtopup.R;

import com.aayush.scanandtopup.interfaces.Coordinates;


public class CameraAccess extends Activity implements SurfaceHolder.Callback, View.OnClickListener, View.OnLongClickListener {
    private Vibrator heptics;

    private final int HEPTICS_CONSTANT = 50;

    private Camera camera;

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    private final String simInfo = MainActivity.getSimInfo();

    private boolean isPreviewing = false;


    private RelativeLayout animateView;
    private android.support.design.widget.FloatingActionButton takePicture;
    private TextView simInfoView;
    private int cameraId;

    private static Bitmap cameraImage;

    private PictureCallback jpegPictureCallBack;
    private Camera.AutoFocusCallback autoFocusCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        instantiate();


    }

    private void instantiate() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        simInfoView = (TextView) findViewById(R.id.simInfo);

        simInfoView.setText(simInfo);

        takePicture = (android.support.design.widget.FloatingActionButton) findViewById(R.id.takepicture);
        heptics = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        takePicture.setOnClickListener(this);
        takePicture.setOnLongClickListener(this);

        cameraId = findRearFacingCamera();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                takePicture.setEnabled(true);
            }
        };

        CameraOverlay previewBackground = (CameraOverlay) findViewById(R.id.overlay);
        previewBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                takePicture.setEnabled(false);
                if (event.getAction() == event.ACTION_DOWN) {
                    try {
                        camera.autoFocus(autoFocusCallback);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;

            }


        });

        jpegPictureCallBack = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                try {
                    if (bytes != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        setBitmapImage(bitmap);

                        Intent intent = new Intent(getApplicationContext(), CropActivity.class);
                        startActivity(intent);
                        finish();

                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        };
    }


    @Override
    public void onClick(View v) {
        heptics.vibrate(HEPTICS_CONSTANT);
        capturePicture();

    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.takepicture:
                Toast.makeText(this, "Capture Image", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    private void capturePicture() {
        try {
            camera.takePicture(null, null, null, jpegPictureCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap getBitmapImage() {
        //Mapping the overlay Coordinates with Bitmap Coordinates Window to ViewPort Transformation
        cameraImage = getRotatedImage(cameraImage);
        Coordinates coordinates = new CoordinateLocatorInBitmap(cameraImage, CameraOverlay.getRectangleCoordinates());
        Rect imageCoordinates = coordinates.getCoordinates();
        Bitmap bmp = Bitmap.createBitmap(cameraImage, imageCoordinates.left, imageCoordinates.top, imageCoordinates.right - imageCoordinates.left, imageCoordinates.bottom - imageCoordinates.top);
        return bmp;

    }

    private static Bitmap getRotatedImage(Bitmap bmp) {
        Matrix returnImage = new Matrix();
        returnImage.postRotate(90);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), returnImage, true);

    }

    private void setBitmapImage(Bitmap bitmap) {
        this.cameraImage = bitmap;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            camera = Camera.open(cameraId);
            camera.setDisplayOrientation(90);
            camera.startPreview();
            isPreviewing = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("camera", "not found");
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (isPreviewing) {
            camera.stopPreview();
            isPreviewing = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
//                camera.enableShutterSound(true);
                camera.setDisplayOrientation(90);
                camera.startPreview();
                isPreviewing = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            isPreviewing = false;

            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check if a rear camera is available
    private int findRearFacingCamera() {
        int cameraId = 0;
//        Searching for the rear facing camera
        int noOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < noOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}