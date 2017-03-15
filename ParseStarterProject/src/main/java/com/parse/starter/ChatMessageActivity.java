package com.parse.starter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.parse.Parse;
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
import java.util.Locale;
import java.util.TimeZone;

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

    Thread messageLoadingThread;
    boolean messageLoadingActive = false;

    int beforeMessageCount = 0;
    private int type = 0;
    private int beforeDay = 0;
    private int beforeMonth = 0;
    private int beforeYear = 0;

    Dialog dialog;

    public void onSendMessage(View view)
    {
         ParseObject parseObject = new ParseObject("message");

         String message = chatEditText.getText().toString();
         String recipient = opponentName;
         String sender = userName;

         parseObject.put("recipient", recipient);
         parseObject.put("sender", sender);
         parseObject.put("message", message);
         parseObject.put("status", false);  // false: Unread, true: Read

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

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
         int w = bitmap.getWidth();
         int h = bitmap.getHeight();
         Matrix matrix = new Matrix();
         float scaleWidth = ((float) width / w);
         float scaleHeight = ((float) height / h);
         matrix.postScale(scaleWidth, scaleHeight);
         Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
         return newbmp;
    }

    public void onUpdateMessage()
    {
        final Bitmap[] userBitmap = new Bitmap[1];
        final Bitmap[] friendBitmap = new Bitmap[1];
        final Bitmap[] resizeUserBitmap = new Bitmap[1];
        final Bitmap[] resizeFriendBitmap = new Bitmap[1];
        final boolean[] friendBitmapFlag = {false};
        final boolean[] appUserBitmapFlag = {false};

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
            public void done(List<ParseObject> objects, ParseException e)
            {

                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        for(ParseObject object : objects)
                        {
                            if(object.getString("username").equals(userName) && appUserBitmapFlag[0] == false)
                            {
                                /*
                                                                                ParseFile file = (ParseFile) object.get("profilepic");
                                                                                try {
                                                                                    byte[] data = file.getData();
                                                                                    userBitmap[0] = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                                                    resizeUserBitmap[0] = zoomBitmap(userBitmap[0],100,100);
                                                                                    appUserBitmapFlag[0] = true;
                                                                                } catch (ParseException e1) {
                                                                                    e1.printStackTrace();
                                                                                }
                                                                 */
                                final ParseFile file = (ParseFile) object.get("profilepic");
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if(e == null)
                                        {
                                            try {
                                                data = file.getData();
                                                userBitmap[0] = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                resizeUserBitmap[0] = zoomBitmap(userBitmap[0],100,100);
                                                appUserBitmapFlag[0] = true;
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                            else if(object.getString("username").equals(opponentName) && friendBitmapFlag[0] == false)
                            {
                                /*
                                                                        ParseFile file = (ParseFile) object.get("profilepic");
                                                                        try {
                                                                            byte[] data = file.getData();
                                                                            friendBitmap[0] = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                                            resizeFriendBitmap[0] = zoomBitmap(friendBitmap[0], 100, 100);
                                                                            friendBitmapFlag[0] = true;
                                                                        } catch (ParseException e1) {
                                                                            e1.printStackTrace();
                                                                        }
                                                                 */
                                final ParseFile file = (ParseFile) object.get("profilepic");
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        try {
                                            data = file.getData();
                                            friendBitmap[0] = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            resizeFriendBitmap[0] = zoomBitmap(friendBitmap[0], 100, 100);
                                            friendBitmapFlag[0] = true;
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                });
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

                                            SimpleDateFormat df_day = new SimpleDateFormat("dd");
                                            SimpleDateFormat df_month = new SimpleDateFormat("MM");
                                            SimpleDateFormat df_year = new SimpleDateFormat("yyyy");


                                            df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                                            df2.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                                            df_day.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                                            df_month.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                                            df_year.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

                                            String reportDate = df.format(date);
                                            final String reportDate2 = df2.format(date);

                                            currentDay = Integer.parseInt(df_day.format(date));
                                            currentMonth = Integer.parseInt(df_month.format(date));
                                            currentYear = Integer.parseInt(df_year.format(date));

                                            if((currentYear > beforeYear) || (currentYear == beforeYear && currentMonth > beforeMonth) || (currentYear == beforeYear && currentMonth == beforeMonth && currentDay > beforeDay))
                                            {
                                                type = 2;
                                                chatArrayAdapter.add(new ChatMessage(type, reportDate, null, null, null, null, null));
                                                beforeDay = currentDay;
                                                beforeMonth = currentMonth;
                                                beforeYear = currentYear;
                                            }
                                            if(currentMessageCount > beforeMessageCount)
                                            {
                                                final String messageContent = object.getString("message");
                                                final Boolean messageStatus = object.getBoolean("status");
                                                final String messageId = object.getObjectId();

                                                type = 0; //user
                                                if(object.get("sender").toString().equals(opponentName))
                                                {
                                                    type = 1;
                                                }
                                                if(type == 0) {
                                                    //Log.i("Bitmap size width",String.valueOf(userBitmap[0].getWidth()));
                                                    //Log.i("Bitmap size height",String.valueOf(userBitmap[0].getHeight()));

                                                    //Log.i("Bitmap size width",String.valueOf(resizeBitmap.getWidth()));
                                                    //Log.i("Bitmap size height",String.valueOf(resizeBitmap.getHeight()));
                                                    chatArrayAdapter.add(new ChatMessage(type, messageContent, reportDate2, date, resizeUserBitmap[0], messageStatus, messageId));
                                                }
                                                else if(type == 1) {
                                                    chatArrayAdapter.add(new ChatMessage(type, messageContent, reportDate2, date, resizeFriendBitmap[0], messageStatus, messageId));
                                                }
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

                dialog.dismiss();

            }
        });

        chatArrayAdapter.checkHasUnReadMessage();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (MainActivity.debugMsg == true) Log.i("Activity State", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        Intent intent = getIntent();
        opponentName = intent.getStringExtra("opponentName");

        userName = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserProfile");
        parseQuery.whereEqualTo("username",opponentName);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null) {
                    setTitle(object.getString("nickname"));
                }
            }
        });


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
        chatArrayAdapter.setFriend(opponentName);

        messageListView.setAdapter(chatArrayAdapter);
        messageArrayList.clear();
        beforeMessageCount = 0;

        dialog = ProgressDialog.show(ChatMessageActivity.this,
                "讀取中", "Wait...",true);

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
                    mHandler.sendMessage(message);

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
            //chatArrayAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.logout:

                if(MainActivity.debugMsg == true) Log.i("Menu item selected", "Logout");
                ParseUser.logOut();
                intent.setClass(ChatMessageActivity.this, MainActivity.class);
                startActivity(intent);

                return true;

            case R.id.profile:

                if(MainActivity.debugMsg == true) Log.i("Menu item selected", "Profile");
                intent.setClass(ChatMessageActivity.this, ProfileActivity.class);
                intent.putExtra("username",userName);
                startActivity(intent);

                return true;

            case R.id.userList:

                intent.setClass(ChatMessageActivity.this, UsersListActivity.class);
                intent.putExtra("username",userName);
                startActivity(intent);

                return true;

            default:
                return false;

        }
    }
}
