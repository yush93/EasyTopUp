package com.aayush.scanandtopup.extras;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.aayush.scanandtopup.R;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        this.setTitle("About Developers");
    }
}
