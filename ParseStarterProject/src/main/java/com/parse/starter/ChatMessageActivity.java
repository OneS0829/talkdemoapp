package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.parse.ParseQuery.getQuery;

public class ChatMessageActivity extends AppCompatActivity{

    EditText chatEditText;
    String opponentName = "";
    String userName = "";

    ListView messageListView;
    ArrayList<String> messageArrayList = new ArrayList<String>();
    private ChatArrayAdapter chatArrayAdapter;

    Thread messageUpdateThread;
    boolean messageUpdateActive = false;
    int beforeMessageCount = 0;
    private int type = 0;
    private int beforeDay = 0;
    private int beforeMonth = 0;
    private int beforeYear = 0;

    public void onSendMessage(View view)
    {
         ParseObject parseObject = new ParseObject("message");

         String message = chatEditText.getText().toString();
         String recipient = opponentName;
         String sender = userName;

         parseObject.put("recipient", recipient);
         parseObject.put("sender", sender);
         parseObject.put("message", message);

         chatEditText.setText("");

         parseObject.saveInBackground(new SaveCallback() {
             @Override
             public void done(ParseException e) {
                 if(e != null)
                 {
                     Toast.makeText(ChatMessageActivity.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                 }
                 else{
                     onUpdateMessage();
                 }
             }
         });

    }


    public void onUpdateMessage()
    {
        final Bitmap[] userBitmap = new Bitmap[1];
        final Bitmap[] friendBitmap = new Bitmap[1];
        ParseQuery<ParseObject> parseQueryCurrectUserToOpponent = new ParseQuery<ParseObject>("message");
        ParseQuery<ParseObject> parseQueryOpponentToCurrectUser = new ParseQuery<ParseObject>("message");

        parseQueryCurrectUserToOpponent.whereEqualTo("recipient", opponentName); // Currect User -> Opponent
        parseQueryCurrectUserToOpponent.whereEqualTo("sender", userName);
        parseQueryOpponentToCurrectUser.whereEqualTo("recipient", userName); //  Opponent  -> Currect User
        parseQueryOpponentToCurrectUser.whereEqualTo("sender", opponentName);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(parseQueryCurrectUserToOpponent);
        queries.add(parseQueryOpponentToCurrectUser);
        final ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserProfile");
        String[] names = {userName, opponentName};
        parseQuery.whereContainedIn("username", Arrays.asList(names));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        for(ParseObject object : objects)
                        {
                            if(object.getString("username").equals(userName))
                            {
                                ParseFile file = (ParseFile) object.get("profilepic");
                                try {
                                    byte[] data = file.getData();
                                    userBitmap[0] = BitmapFactory.decodeByteArray(data, 0, data.length);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }

                            }
                            else if(object.getString("username").equals(opponentName))
                            {
                                ParseFile file = (ParseFile) object.get("profilepic");
                                try {
                                    byte[] data = file.getData();
                                    friendBitmap[0] = BitmapFactory.decodeByteArray(data, 0, data.length);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }


                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e == null) {
                                    if(objects.size() > 0) {
                                        int currentMessageCount = 0;
                                        int currentDay = 0;
                                        int currentMonth = 0;
                                        int currentYear = 0;

                                        for(ParseObject object : objects) {

                                            currentMessageCount++;
                                            final Date date = object.getCreatedAt();
                                            SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
                                            SimpleDateFormat df2 = new SimpleDateFormat("h:mm a");
                                            String reportDate = df.format(date);
                                            final String reportDate2 = df2.format(date);
                                            currentDay = date.getDay();
                                            currentMonth = date.getMonth();
                                            currentYear = date.getYear();

                                            if(currentYear >= beforeYear && currentMonth >= beforeMonth && currentDay > beforeDay)
                                            {
                                                type = 2;
                                                chatArrayAdapter.add(new ChatMessage(type, reportDate, null, null, null));
                                                beforeDay = currentDay;
                                                beforeMonth = currentMonth;
                                                beforeYear = currentYear;
                                            }
                                            if(currentMessageCount > beforeMessageCount)
                                            {
                                                final String messageContent = object.getString("message");
                                                type = 0; //user
                                                if(object.get("sender").toString().equals(opponentName))
                                                {
                                                    type = 1;
                                                }
                                                if(type == 0) chatArrayAdapter.add(new ChatMessage(type, messageContent, reportDate2, date, userBitmap[0]));
                                                else if(type == 1) chatArrayAdapter.add(new ChatMessage(type, messageContent, reportDate2, date, friendBitmap[0]));
                                                beforeMessageCount++;
                                                messageListView.setSelection(messageArrayList.size()-1);
                                            }
                                        }
                                    }
                                }
                            }
                        });


                    }
                }
            }
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (MainActivity.debugMsg == true) Log.i("Activity State", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        chatEditText = (EditText) findViewById(R.id.chatEditText);
        chatEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    onSendMessage(v);
                    return true;
                } else {
                    return false;
                }
            }
        });


        messageListView = (ListView) findViewById(R.id.chatContentListView);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);

        Intent intent = getIntent();
        opponentName = intent.getStringExtra("opponentName");
        userName = ParseUser.getCurrentUser().getUsername();
        setTitle(opponentName);


        messageListView.setAdapter(chatArrayAdapter);
        messageArrayList.clear();
        beforeMessageCount = 0;

        messageUpdateThread = new MessageUpdateThread();
        messageUpdateActive = true;
        messageUpdateThread.start();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onPause() {
        if(MainActivity.debugMsg == true) Log.i("Activity State","onPause");
        super.onPause();

        if (messageUpdateThread != null) {
            messageUpdateActive = false;
        }
    }


    class MessageUpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(messageUpdateActive){
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    mHandler.handleMessage(message);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onUpdateMessage();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logout:

                if(MainActivity.debugMsg == true) Log.i("Menu item selected", "Logout");
                Intent intent = new Intent();
                ParseUser.logOut();
                intent.setClass(ChatMessageActivity.this, MainActivity.class);
                startActivity(intent);

                return true;

            default:
                return false;

        }
    }
}
