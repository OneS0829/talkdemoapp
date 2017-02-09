package com.parse.starter;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseQuery.getQuery;

public class ChatMessageActivity extends AppCompatActivity {

    EditText chatEditText;
    String opponentName = "";
    String userName = "";

    ListView messageListView;
    ArrayList<String> messageArrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    Thread messageUpdateThread;
    boolean messageUpdateActive = false;
    int beforeMessageCount = 0;

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
                     //onUpdateMessage();
                 }
             }
         });

    }

    public void onUpdateMessage()
    {
        ParseQuery<ParseObject> parseQueryCurrectUserToOpponent = new ParseQuery<ParseObject>("message");
        ParseQuery<ParseObject> parseQueryOpponentToCurrectUser = new ParseQuery<ParseObject>("message");

        parseQueryCurrectUserToOpponent.whereEqualTo("recipient", opponentName); // Currect User -> Opponent
        parseQueryCurrectUserToOpponent.whereEqualTo("sender", userName);
        parseQueryOpponentToCurrectUser.whereEqualTo("recipient", userName); //  Opponent  -> Currect User
        parseQueryOpponentToCurrectUser.whereEqualTo("sender", opponentName);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(parseQueryCurrectUserToOpponent);
        queries.add(parseQueryOpponentToCurrectUser);
        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if(objects.size() > 0) {
                        int currentMessageCount = 0;
                        for(ParseObject object : objects) {
                            currentMessageCount++;
                            if(currentMessageCount > beforeMessageCount)
                            {
                                String messageContent = object.getString("message");
                                if(object.get("sender").toString().equals(opponentName))
                                {
                                    messageContent = " > " + messageContent;
                                }
                                messageArrayList.add(messageContent);
                                beforeMessageCount++;
                            }
                        }

                        arrayAdapter.notifyDataSetChanged();
                        messageListView.setSelection(messageArrayList.size()-1);
                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(MainActivity.debugMsg == true) Log.i("Activity State","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        chatEditText = (EditText)findViewById(R.id.chatEditText);
        messageListView = (ListView)findViewById(R.id.chatContentListView);
        arrayAdapter = new ArrayAdapter(ChatMessageActivity.this,android.R.layout.simple_list_item_1,messageArrayList);

        Intent intent = getIntent();
        opponentName = intent.getStringExtra("opponentName");
        userName = ParseUser.getCurrentUser().getUsername();
        setTitle(opponentName+"     ( "+userName+" Login )");

        messageListView.setAdapter(arrayAdapter);
        messageArrayList.clear();
        beforeMessageCount = 0;

        //onUpdateMessage();

        messageUpdateThread = new MessageUpdateThread();
        messageUpdateActive = true;
        messageUpdateThread.start();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(MainActivity.debugMsg == true) Log.i("Activity State","onPause");
        super.onPause();

        if (messageUpdateThread != null) {
            messageUpdateActive = false;
        }

    }

    class MessageUpdateThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(messageUpdateActive){
                try {
                    Thread.sleep(1000);
                    //Log.i("Update","Message Updating.....");
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
