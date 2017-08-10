package com.aayush.scanandtopup.maininterfaces;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.aayush.scanandtopup.R;
import com.aayush.scanandtopup.extras.DatabaseHelper;
import com.aayush.scanandtopup.extras.HistoryAdapter;
import com.aayush.scanandtopup.extras.HistoryData;
import java.util.ArrayList;

public class History extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";
    DatabaseHelper mDatabaseHelper;
    private ListView mListView;
    private TextView textView1, textView2, textView3;
    FloatingActionButton clear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        this.setTitle("TopUp History");
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        mListView = (ListView) findViewById(R.id.listView);
        clear = (FloatingActionButton) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
                finish();
                startActivity(getIntent());
            }
        });
        mDatabaseHelper = new DatabaseHelper(this);
        populateListView();
    }

    private void populateListView(){
        Log.d(TAG, "populateListView: Display data in the ListView");
        Cursor data = mDatabaseHelper.getData();
        ArrayList<HistoryData> listData = new ArrayList<>();
        if(data.getCount() == 0)
            Toast.makeText(this, "Table Empty", Toast.LENGTH_SHORT).show();
        else{
            while(data.moveToNext()){
                String date = data.getString(1);
                String carrier = data.getString(2);
                String pin = data.getString(3);
                HistoryData historyData = new HistoryData(date, carrier, pin);
                listData.add(historyData);
            }
            HistoryAdapter adapter = new HistoryAdapter(this, R.layout.adapter_view, listData);
            mListView.setAdapter(adapter);
        }
    }

    private void clearHistory() {mDatabaseHelper.clearData();}
}
