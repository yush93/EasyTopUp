package com.aayush.scanandtopup.primaryGUIModule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aayush.scanandtopup.helperModule.DatabaseHelper;
import com.aayush.scanandtopup.R;
import com.aayush.scanandtopup.classifierModule.NNMatrix;
import com.aayush.scanandtopup.classifierModule.NeuralNetwork;
import com.aayush.scanandtopup.interfaceModule.GrayScale;
import com.aayush.scanandtopup.interfaceModule.Rotate;
import com.aayush.scanandtopup.interfaceModule.SkewChecker;
import com.aayush.scanandtopup.interfaceModule.Threshold;
import com.aayush.scanandtopup.preprocessingModule.BradleyThreshold;
import com.aayush.scanandtopup.preprocessingModule.GammaCorrection;
import com.aayush.scanandtopup.preprocessingModule.HoughLineSkewChecker;
import com.aayush.scanandtopup.preprocessingModule.ITURGrayScale;
import com.aayush.scanandtopup.preprocessingModule.ImageWriter;
import com.aayush.scanandtopup.preprocessingModule.RotateNearestNeighbor;
import com.aayush.scanandtopup.segmentationModule.BinaryArray;
import com.aayush.scanandtopup.segmentationModule.CcLabeling;
import com.aayush.scanandtopup.segmentationModule.ComponentImages;
import com.aayush.scanandtopup.segmentationModule.PrepareImage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "RechargeActivity";
    DatabaseHelper mDatabaseHelper;
    private ImageButton rechargeBtn, redoButton;
    private EditText ocrResultTV;
    private ImageView rechargeImView;
    private ProgressDialog progressDialog;
    private Vibrator haptics;
    private final int HAPTICS_CONSTANT = 50;
    private final String simInfo = MainActivity.getSimInfo();
    private final int BLACK = -16777216;
    private final int WHITE = -1;
    private Bitmap croppedImage;
    private ArrayList<Bitmap> componentBitmaps = new ArrayList<>();
    private final ArrayList<NNMatrix> weightsArr = Splash.getWeights();
    private Bitmap bmResult;
    private String ocrResult;
    private ImageWriter imageWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        mDatabaseHelper = new DatabaseHelper(this);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Matrix matrix = new Matrix();
        //Edited here to rotate.........................
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            matrix.postRotate(-90);
//        }
        //..................
        croppedImage = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        instantiate();
    }

    public void AddData(String date, String carrier, String pin) {
        boolean insertData = mDatabaseHelper.addData(date, carrier, pin);
        if (insertData)
            toastMessage("Data Successfully Inserted");
        else
            toastMessage("Something went wrong");
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        haptics.vibrate(HAPTICS_CONSTANT);
        switch (v.getId()) {
            case R.id.redoFromRecharge:
                onBackPressed();
                break;

            case R.id.rechargeBtn:
                String pin = ocrResultTV.getText().toString();
                String date = DateFormat.getDateInstance().format(new Date()).toString();
                String sim = simInfo.equals("TopUp NTC") ? "NTC" : "NCell";
                AddData(date, sim, pin);
                recharge();
                break;
        }
    }

    private void recharge() {
        String prefix;
        if (simInfo == "NTC")
            prefix = "*412*";
        else
            prefix = "*102*";
        String dial = "tel:" + prefix + ocrResultTV.getText().toString().trim() + "%23";
        Toast.makeText(this, dial, Toast.LENGTH_SHORT).show();
        Intent dialIntent = new Intent(Intent.ACTION_CALL);
        dialIntent.setData(Uri.parse(dial));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(RechargeActivity.this, "Permission ERROR!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(dialIntent);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.rechargeBtn:
                Toast.makeText(this, "Crop", Toast.LENGTH_LONG).show();
                break;

            case R.id.redoFromRecharge:
                Toast.makeText(this, "Redo", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public void instantiate() {
        progressDialog = ProgressDialog.show(this, "", "Scanning", true);
        imageWriter = new ImageWriter(RechargeActivity.this);
        rechargeBtn = (ImageButton) findViewById(R.id.rechargeBtn);
        redoButton = (ImageButton) findViewById(R.id.redoFromRecharge);
        haptics = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        ocrResultTV = (EditText) findViewById(R.id.ocrResult);
        rechargeImView = (ImageView) findViewById(R.id.imageView2);
        rechargeBtn.setOnClickListener(this);
        redoButton.setOnClickListener(this);
        new MyTask().execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        private Bitmap thresholdedImage;

        @Override
        protected Void doInBackground(Void... params) {
            processImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            progressDialog.dismiss();
            ocrResultTV.setVisibility(View.VISIBLE);
            rechargeImView.setImageBitmap(thresholdedImage);
            ocrResultTV.setText(ocrResult.trim());
        }

        public void processImage() {
            bmResult = gammaCorrect(croppedImage);
            bmResult = grayScale(bmResult);
            bmResult = skewCorrect(bmResult);
            bmResult = threshold(bmResult);
            thresholdedImage = bmResult;
            componentBitmaps = getSegmentArray(bmResult);
            List<double[][]> binarySegmentList = BinaryArray.CreateBinaryArray(componentBitmaps);
            ocrResult = generateOutput(binarySegmentList);
        }

        private Bitmap gammaCorrect(Bitmap bmp) {
            GammaCorrection gc = new GammaCorrection(1.0);
            bmp = gc.correctGamma(bmp);
            return bmp;
        }

        private Bitmap grayScale(Bitmap bmp) {
            GrayScale grayScale = new ITURGrayScale(bmp);
            bmp = grayScale.grayScale();
            return bmp;
        }

        private Bitmap skewCorrect(Bitmap bmp) {
            SkewChecker skewChecker = new HoughLineSkewChecker();
            double angle = skewChecker.getSkewAngle(bmp);
            Rotate rotator = new RotateNearestNeighbor(angle);
            bmp = rotator.rotateImage(bmp);
            return bmp;
        }

        private Bitmap threshold(Bitmap bmp) {
            Threshold threshold = new BradleyThreshold();
            bmp = threshold.threshold(bmp);
            return bmp;
        }

        private ArrayList<Bitmap> getSegmentArray(Bitmap bmp) {
            ArrayList<Bitmap> segments;
            bmp = PrepareImage.addBackgroundPixels(bmp);
            int height = bmp.getHeight();
            int width = bmp.getWidth();
            int[] pixels = createPixelArray(width, height, bmp);
            boolean[] booleanImage = new boolean[width * height];
            int index = 0;
            for (int pixel : pixels) {
                if (pixel == BLACK) {
                    booleanImage[index] = true;
                }
                index++;
            }
            CcLabeling ccLabeling = new CcLabeling();
            ComponentImages componentImages = new ComponentImages(RechargeActivity.this);
            segments = componentImages.CreateComponentImages(ccLabeling.CcLabels(booleanImage, width));
            return segments;
        }

        private void generateBinarySegmentedImages() {
            List<int[]> binarySegmentList1D = BinaryArray.CreateBinaryArrayOneD(componentBitmaps);
            int counter = 0;
            for (int[] segment : binarySegmentList1D) {
                for (int i = 0; i < 256; i++) {
                    if (segment[i] == 1) segment[i] = WHITE;
                    else segment[i] = BLACK;
                }
                Bitmap bitmap = Bitmap.createBitmap(segment, 16, 16, Bitmap.Config.RGB_565);
                imageWriter.writeImage(bitmap, false, "segment" + counter++, "07_binarysegment");
            }
        }

        private int[] createPixelArray(int width, int height, Bitmap thresholdImage) {
            int[] pixels = new int[width * height];
            thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
            return pixels;
        }

        private String generateOutput(List<double[][]> binarySegmentList) {
            String ocrString = " ";
            NeuralNetwork net = new NeuralNetwork(weightsArr.get(0), weightsArr.get(1), weightsArr.get(2), weightsArr.get(3));
            List<Integer> recognizedList = new ArrayList<Integer>();
            for (double[][] binarySegment : binarySegmentList) {
                NNMatrix input = new NNMatrix(binarySegment);
                NNMatrix output = net.FeedForward(input);
                //output.showOutputArray();
                int filteredOutput = output.filterOutput();
                if (filteredOutput != -1) {
                    recognizedList.add(filteredOutput);
                }
            }
            for (int a : recognizedList) {
                ocrString = ocrString + Integer.toString(a);
            }
            return ocrString;
        }
    }
}
