package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample
        /* So far the screen is blank */
        //This is copied directly from FragmentExample.java lines 47-54
        DetailsFragment dFragment = new DetailsFragment();//add a DetailsFragment
        dFragment.setArguments(dataToPass);//Pass it a bundle for information
        //dFragment.setTablet(true);//tell the fragment if it's running on a tablet or not
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, dFragment)//Add the fragment in FrameLayout
                .commit();
    }
}