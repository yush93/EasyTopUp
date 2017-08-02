package com.aayush.scanandtopup.maininterfaces;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.aayush.scanandtopup.classifier.JsonContentReader;
import com.aayush.scanandtopup.classifier.NNMatrix;
import com.aayush.scanandtopup.classifier.WeightReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Splash extends AppCompatActivity {

    //My code begins from here

    private static NNMatrix weights_at_layer2, weights_at_layer3;
    private static NNMatrix bias_at_layer2, bias_at_layer3;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


    @TargetApi(Build.VERSION_CODES.M)
    private boolean permissionRequest() {
        List<String> permissionNeeded = new ArrayList<String>();
        final List<String> permissionList = new ArrayList<String>();

        if (!addPermission(permissionList, android.Manifest.permission.CALL_PHONE)) {
            permissionNeeded.add("CALL");
        }
        if (!addPermission(permissionList, Manifest.permission.CAMERA)) {
            permissionNeeded.add("CAMERA");
        }
        if (!addPermission(permissionList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionNeeded.add("MOUNT FOLDER");
        }
        if (!addPermission(permissionList, Manifest.permission.READ_PHONE_STATE)) {
            permissionNeeded.add("SIN");
        }
        if (!addPermission(permissionList, Manifest.permission.VIBRATE)) {
            permissionNeeded.add("HAPTICS");
        }


        if (permissionList.size() > 0) {
            requestPermissions(permissionList.toArray(new String[permissionList.size()]),REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return true;
        } else
            return false;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.VIBRATE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Toast.makeText(this, "ALll Permission granted", Toast.LENGTH_SHORT).show();
                    loadData();
                    changeIntent();
                } else {
                    Toast.makeText(this, "Grant permissions from settings", Toast.LENGTH_LONG).show();
                }
            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //siminfo = (TextView) findViewById(R.id.splashSimInfo);

        if (!permissionRequest()) {
            loadData();
            changeIntent();
        }
    }

    private void changeIntent()
    {
        /* Create an Intent that will start the Camera Activity. */
        Intent mainIntent = new Intent(Splash.this, MainActivity.class);
        Splash.this.startActivity(mainIntent);
        Splash.this.finish();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 3000);

    }


    private void loadData() {
        JsonContentReader jsonContentReader = new JsonContentReader();
        String jsonContent = jsonContentReader.getJsonString(getApplicationContext());

        WeightReader weightReader = new WeightReader();
        bias_at_layer2 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_1_bias"));
        weights_at_layer2 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_1_weight"));
        weights_at_layer3 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_2_weight"));
        bias_at_layer3 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_2_bias"));

    }

    public static ArrayList<NNMatrix> getWeights()
    {
        ArrayList<NNMatrix>  weightArr = new ArrayList<NNMatrix>();
        weightArr.add(bias_at_layer2);
        weightArr.add(bias_at_layer3);
        weightArr.add(weights_at_layer2);
        weightArr.add(weights_at_layer3);
        return weightArr;
    }

}
