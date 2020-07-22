package com.example.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {
    private AppCompatActivity parentActivity;
    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;
    private boolean checked;

        public void setTablet(boolean tablet) {
            isTablet = tablet;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(ChatRoomActivity.ITEM_ID);
        checked = dataFromActivity.getBoolean(ChatRoomActivity.ITEM_ISSEND);

        //Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_details, container, false);

        //show the message
        TextView message = (TextView) result.findViewById(R.id.message);
        message.setText(dataFromActivity.getString(ChatRoomActivity.ITEM_SELECTED));

        //show the id
        TextView idView = (TextView) result.findViewById(R.id.idText);
        idView.setText("ID=" + id);

        CheckBox checkBox = (CheckBox) result.findViewById(R.id.checkBox);
        checkBox.setChecked(checked);

        //get the hideButton, and add a click listener:
        Button hideButton = (Button) result.findViewById(R.id.hideButton);
        hideButton.setOnClickListener(clk -> {
             //Tell parent activity to remove
                parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
             });
            return result;
        }
        @Override //need this method to make the hideButton work
            public void onAttach(Context context){
            super.onAttach(context);
            //Context will either be DetailsFragment for a tablet, or EmptyActivity for a phone
            parentActivity = (AppCompatActivity)context;
        }
}