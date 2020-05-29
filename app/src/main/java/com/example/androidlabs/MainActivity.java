package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        Button btn = findViewById(R.id.checkBox2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_message) , Toast.LENGTH_LONG).show();
            }
        });

        //Snackbar snackbar = (Button) findViewById(R.id.snkbtn);
        final Snackbar snackbar;
        final Switch swi1 = findViewById(R.id.switch1);
        swi1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Snackbar snackbar;
                boolean flag = swi1.isChecked();
                String output;
                if(flag) {
                    output = getString(R.string.Switch_on);
                }
                else{
                    output = getString(R.string.Switch_off);
                }

                snackbar = Snackbar.make(view,output, Snackbar.LENGTH_LONG).setAction("Action", null);
                snackbar.setAction(R.string.undo_string, new MyUndoListener());
                snackbar.show();
            }


             class MyUndoListener implements View.OnClickListener{
                @Override

                public void onClick(View v){
                    final Switch swi1 = findViewById(R.id.switch1);
                    swi1.setChecked(false);
                }

            }
        });

        EditText et = findViewById(R.id.myEditText);
        et.setHint(getString(R.string.type_message));

    }
}
