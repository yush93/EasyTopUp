package com.aayush.scanandtopup.maininterfaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.design.widget.TabLayout;
import android.widget.Button;
import android.widget.Toast;

import com.aayush.scanandtopup.R;
import com.aayush.scanandtopup.WelcomeActivity;
import com.aayush.scanandtopup.camera.CameraAccess;


public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static String SIM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_sim_card_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_sim_card_black_24dp);


    }

        /////////////////////Fragments or tabs are displayed from here

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                View rootView = inflater.inflate(R.layout.fragment_ntc, container, false);
                return rootView;
            } else {
                View rootView = inflater.inflate(R.layout.fragment_ncell, container, false);
                return rootView;
            }


        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "NTC";
                case 1:
                    return "NCell";
            }
            return null;
        }
    }

    /////////For Click Events///////////////////
    public void btn_clicked(View v) {

        ////////////////////////For Recharge//////////////////////
        if (v.getId() == R.id.ntcRechargeLayout) {
            Intent nav = new Intent(this, CameraAccess.class);
            SIM = "TopUp NTC";
            startActivity(nav);
            Toast.makeText(this, "NTC Recharge", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ncellRechargeLayout) {
            Intent nav = new Intent(this, CameraAccess.class);
            SIM = "TopUp NCell";
            startActivity(nav);
            Toast.makeText(this, "NCell Recharge Pressed", Toast.LENGTH_LONG).show();
        }

        ////////////////////For Balance inquiry//////////////////////////
        else if (v.getId() == R.id.ntcBalanceLayout) {
            Intent ussdIntent = new Intent(Intent.ACTION_CALL);
            ussdIntent.setData(Uri.parse("tel:*400%23"));
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission ERROR!!!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(ussdIntent);
        } else if(v.getId() == R.id.ncellBalanceLayout){
            Intent ussdIntent = new Intent(Intent.ACTION_CALL);
            ussdIntent.setData(Uri.parse("tel:*101%23"));
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission ERROR!!!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(ussdIntent);

        }

        ///////////////////////////For Balance Transfer/////////////////////////////
        else if(v.getId() == R.id.ntcBalanceTransferLayout){
            Intent tNav = new Intent(this, BalanceTransferActivity.class);
            SIM = "NTC Balance Transfer";
            startActivity(tNav);
            Toast.makeText(this, "Transfer", Toast.LENGTH_LONG).show();
        } else {
            Intent tNav = new Intent(this, BalanceTransferActivity.class);
            SIM = "NCell Balance Transfer";
            startActivity(tNav);
            Toast.makeText(this, "Transfer", Toast.LENGTH_LONG).show();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap back button once more to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static String getSimInfo()
    {
        return SIM;
    }

}
