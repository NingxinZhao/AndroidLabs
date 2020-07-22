package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.androidlabs.MyOpener.COL_ID;
import static com.example.androidlabs.MyOpener.TABLE_NAME;

public class ChatRoomActivity extends AppCompatActivity {
    MyOwnAdapter myAdapter;
    ArrayList<Message> messageList = new ArrayList<>();
    private static int ACTIVITY_VIEW_MESSAGE = 33;
    int positionClicked = 0;
    SQLiteDatabase db;

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final String ITEM_ISSEND = "ITEM_ISSEND";
    private static final int EMPTY_ACTIVITY = 345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = findViewById(R.id.theListView);
        Button sendButton = findViewById(R.id.sendButton);
        Button receiveButton = findViewById(R.id.receiveButton);
        EditText userType = findViewById(R.id.typeHere);
        FrameLayout frameLayout = findViewById(R.id.fragmentLocation);
        boolean isTablet =  frameLayout != null; //check if the Fragment is loaded

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
            long newId = db.insert(TABLE_NAME, null, newRowValues);

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
            long newId = db.insert(TABLE_NAME, null, newRowValues);

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

        myList.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("A title")

                    //What is the message:
                    .setMessage("Do you want to delete this? " +
                            "The selected row is: "+id+
                            "The database id is: " +id)

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        messageList.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> {
                    })

                    //You can add extra layout elements:
                    .setView(getLayoutInflater().inflate(R.layout.row_layout, null))

                    //Show the dialog
                    .create().show();
            return true;
        });

        myList.setOnItemClickListener((list,view,position, id) ->{
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, messageList.get(position).toString());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID,messageList.get(position).getId());
            dataToPass.putBoolean(ITEM_ISSEND, messageList.get(position).isSend());

            if(isTablet){
                DetailsFragment dFragment = new DetailsFragment();//add a DetailFrament
                dFragment.setArguments(dataToPass);//Pass it a bundle for information
                dFragment.setTablet(true);//tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment)//Add the fragment in FrameLayout
                        .commit();
            }else//isPhone
                {
                    Intent nextActivity = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                    nextActivity.putExtras(dataToPass);//send data to next activity
                    startActivityForResult(nextActivity, EMPTY_ACTIVITY);//make the transition
                }
        });
    }

        private void loadDataFromDatabase() {

            //get a database connection:
            MyOpener dbOpener = new MyOpener(this);
            db = dbOpener.getWritableDatabase();

            //we want to get all of the columns. Look at MyOpener.java for the definitions:
            String[] columns = {COL_ID, MyOpener.COL_MESSAGE, MyOpener.COL_SEND};

            //query all the results from the database:
            Cursor results = db.query(false, TABLE_NAME, columns, null, null, null, null, null, null);

            //Now the results object has rows of results that math the query.
            //find the column indices:
            int messageColumnIndex = results.getColumnIndex(MyOpener.COL_MESSAGE);
            int idColIndex = results.getColumnIndex(COL_ID);
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
            FrameLayout frameLayout = findViewById(R.id.fragmentLocation);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You clicked on item #" + position)
                    .setMessage("You can update the fields and then click update to save in the database")
                    .setView(message_view) //add the 1 edit text showing the message information
                    .setPositiveButton("Yes", (click, b) -> {

                       deleteMessage(selectedMessage); //remove the contact from database
                        if(frameLayout != null){
                            for(Fragment fragment : getSupportFragmentManager().getFragments()){
                                if(fragment.getArguments().getLong(ITEM_ID) == Long.valueOf(myAdapter.getItemId(position))) {
                                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                    break;
                                }
                            }
                        }
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
        db.update(TABLE_NAME, updatedValues, COL_ID + "= ?", new String[]{Long.toString(m.getId())});
    }

        protected void deleteMessage(Message m) {
        db.delete(TABLE_NAME, COL_ID + "= ?", new String[]{Long.toString(m.getId())});

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMPTY_ACTIVITY) {
            if (resultCode == RESULT_OK) //if you hit the hide button
            {
                long id = data.getLongExtra(ITEM_ID, 0);
                deleteMessageId((int) id);
            }
        }
    }

    public void deleteMessageId(int id) {
        String where = COL_ID + "=" + id;
//        int result = db.delete(MyDatabaseOpenHelper.TABLE_NAME, where, null);
        int result = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        if (result > 0) {
            myAdapter.deleteItem(id);
        }
        Log.i("Delete this message", " id=" + id);
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

        public void deleteItem(long id) {
            for (Message msg : messageList) {
                if (msg.getId() == id) {
                    messageList.remove(msg);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
            }
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