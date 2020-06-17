package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        email = findViewById(R.id.typed_email);
        email.setHint(getString(R.string.email_here));

        prefs = getSharedPreferences("com.example.androidlabs", Context.MODE_PRIVATE);
        String savedString = prefs.getString("email", "");
        email.setText(savedString);

        Button loginButton = findViewById(R.id.loginButton);
        //loginButton.setOnClickListener(bt -> onPause());
        loginButton.setOnClickListener(click -> {
            Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
            goToProfile.putExtra("email", email.getText().toString());
            startActivity(goToProfile);
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        prefs = getSharedPreferences("com.example.androidlabs", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("email", email.getText().toString());
        ed.commit();
    }

}
