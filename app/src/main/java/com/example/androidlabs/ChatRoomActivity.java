package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity {
    MyOwnAdapter myAdapter;
    ArrayList<Message> messageList = new ArrayList<>();
    private static int ACTIVITY_VIEW_MESSAGE = 33;
    int positionClicked = 0;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = findViewById(R.id.theListView);
        Button sendButton = findViewById(R.id.sendButton);
        Button receiveButton = findViewById(R.id.receiveButton);
        EditText userType = findViewById(R.id.typeHere);

        //get any previously saved Message objects
        loadDataFromDatabase();

        //create an adapter object and send it to the listVIew
        myAdapter = new MyOwnAdapter();
        myList.setAdapter(myAdapter);

        myList.setOnItemClickListener(((parent, view, position, id) -> {
            showMessage(position);
        }));

        sendButton.setOnClickListener(click -> {
            //get the message that were typed
            String message = userType.getText().toString();

            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();

            //Now provide a value for every database column defined in MyOpener.java:
            //put string message in the message column:
            newRowValues.put(MyOpener.COL_MESSAGE, message);
            newRowValues.put(MyOpener.COL_SEND, 1);

            //Now insert in the database:
            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

            //now you have the newId, you can create the Message object

            Message newMe = new Message(newId, message, true);

            //add the new contact to the list:
            messageList.add(newMe);
            //update the listView:
            myAdapter.notifyDataSetChanged();

            userType.setText("");

            //Show the id of the inserted item:
            Toast.makeText(this, "IsSended item id:" + newId, Toast.LENGTH_LONG).show();
        });


        receiveButton.setOnClickListener(click -> {
            //get the message that were typed
            String message = userType.getText().toString();

            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();

            //Now provide a value for every database column defined in MyOpener.java:

            //put string message in the EMAIL column:
            newRowValues.put(MyOpener.COL_MESSAGE, message);
            newRowValues.put(MyOpener.COL_SEND, 0);


            //Now insert in the database:
            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

            //now you have the newId, you can create the Message object
            Message newMe = new Message(newId, message, false);

            //add the new contact to the list:
            messageList.add(newMe);
            //update the listView:
            myAdapter.notifyDataSetChanged();
            userType.setText("");

            //Show the id of the inserted item:
            Toast.makeText(this, "Inreceived item id:" + newId, Toast.LENGTH_LONG).show();
        });
    }

        private void loadDataFromDatabase() {

            //get a database connection:
            MyOpener dbOpener = new MyOpener(this);
            db = dbOpener.getWritableDatabase();

            //we want to get all of the columns. Look at MyOpener.java for the definitions:
            String[] columns = {MyOpener.COL_ID, MyOpener.COL_MESSAGE, MyOpener.COL_SEND};

            //query all the results from the database:
            Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

            //Now the results object has rows of results that math the query.
            //find the column indices:
            int messageColumnIndex = results.getColumnIndex(MyOpener.COL_MESSAGE);
            int idColIndex = results.getColumnIndex(MyOpener.COL_ID);
            int sendColIndex = results.getColumnIndex(MyOpener.COL_SEND);

            //iterate over the results, return true if there is a next item:
            while (results.moveToNext()) {
                String message = results.getString(messageColumnIndex);
                long id = results.getLong(idColIndex);
                boolean isSend = results.getInt(sendColIndex)==1;

                //add the new message to the array list
                messageList.add(new Message(id, message,  isSend));
            }
            printCursor(results, db.getVersion());
            }


        protected void showMessage(int position) {
            Message selectedMessage = messageList.get(position);

            View message_view = getLayoutInflater().inflate(R.layout.message_edit, null);

            //get the TextViews
            EditText rowMessage = message_view.findViewById(R.id.edit_msg);
            TextView rowId = message_view.findViewById(R.id.msgId);

            //set the fields for the alert dialog
            rowMessage.setText(selectedMessage.getMessage());
            rowId.setText("id:" + selectedMessage.getId());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You clicked on item #" + position)
                    .setMessage("You can update the fields and then click update to save in the database")
                    .setView(message_view) //add the 1 edit text showing the message information
                    .setPositiveButton("Update", (click, b) -> {
                        selectedMessage.update(rowMessage.getText().toString());
                        updateMessage(selectedMessage);
                        myAdapter.notifyDataSetChanged(); //the message have changed so rebuild the list
                    })
                    .setNegativeButton("Delete", (click, b) -> {
                        deleteMessage(selectedMessage); //remove the contact from database
                        messageList.remove(position); //remove the contact from contact list
                        myAdapter.notifyDataSetChanged(); //there is one less item so update the list
                    })
                    .setNeutralButton("dismiss", (click, b) -> {
                    })
                    .create().show();
    }

        protected void updateMessage(Message m) {
        //Create a ContentValues object to represent a database row:
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MyOpener.COL_MESSAGE, m.getMessage());

        //now call the update function:
        db.update(MyOpener.TABLE_NAME, updatedValues, MyOpener.COL_ID + "= ?", new String[]{Long.toString(m.getId())});
    }

        protected void deleteMessage(Message m) {
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[]{Long.toString(m.getId())});

    }

    class MyOwnAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Message getItem(int position) {
            return messageList.get(position);
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if (messageList.get(position).isSend()) {
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

        public long getItemId(int position) {
            return getItem(position).getId();
        }
    }


        protected void printCursor(Cursor c, int version) {

            Log.d("The Version = ", Integer.toString(version));
            Log.d("Number of columns = ", Integer.toString(c.getColumnCount()));
            Log.d("Column names = ", Arrays.toString(c.getColumnNames()));
            Log.d("Number of results = ", Integer.toString(c.getCount()));

            c.moveToFirst();
            int colIndex = c.getColumnIndex(MyOpener.COL_MESSAGE);
            for (int i = 0; i < c.getCount(); i++) {
                Log.d("Results =  ", c.getString(0) +
                        "/" + c.getString(1) +
                        "/" + c.getString(2));
                c.moveToNext();
            }
        }
    }