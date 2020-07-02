package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {
    private ChatAdapter myAdapter;
    private ArrayList<Message> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = findViewById(R.id.theListView);
        Button sendButton = findViewById(R.id.sendButton);
        Button receiveButton = findViewById(R.id.receiveButton);
        EditText userType = findViewById(R.id.typeHere);

        myList.setAdapter(myAdapter = new ChatAdapter());

        sendButton.setOnClickListener(click -> {
            Message newMessage = new Message(1, userType.getText().toString(), false);
            list.add(newMessage);
            myAdapter.notifyDataSetChanged();
            userType.setText("");
        });

        receiveButton.setOnClickListener(click -> {
            Message receiveMessage = new Message(2, userType.getText().toString(), true);
            list.add(receiveMessage);
            myAdapter.notifyDataSetChanged();
            userType.setText("");
        });

        myList.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("A title")

                    .setMessage("Do you want to delete this? " +
                            "\nThe selected row is: " + pos +
                            "\nThe database id is: " + id)


                    .setPositiveButton("Yes", (click, arg) -> {
                        list.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })

                    .setNegativeButton("No", (click, arg) -> {
                    })

                    .setView(getLayoutInflater().inflate(R.layout.row_layout, null))

                    //Show the dialog
                    .create().show();
            return true;
        });

    }

    class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if (list.get(position).isSend()) {
                newView = inflater.inflate(R.layout.send_layout, parent, false);
            } else {
                newView = inflater.inflate(R.layout.receive_layout, parent, false);
            }
            //return it to be put in the table

            //set what the text should be for this row:
            TextView tView = (TextView) newView.findViewById(R.id.msgText);
            tView.setText(((Message) getItem(position)).getMessage());
            return newView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

}