package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    ParseObject parseObject = new ParseObject("message");
    EditText chatEditText;
    String opponentName = "";
    String userName = "";

    ListView messageListView;
    ArrayList<String> messageArrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    public void onSendMessage(View view)
    {
         String message = chatEditText.getText().toString();
         String recipient = opponentName;
         String sender = userName;

         parseObject.put("recipient", recipient);
         parseObject.put("sender", sender);
         parseObject.put("message", message);

         parseObject.saveInBackground(new SaveCallback() {
             @Override
             public void done(ParseException e) {
                 if(e != null)
                 {
                     Toast.makeText(ChatMessageActivity.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();

                 }
                 else if(e == null)
                 {
                     onUpdateMessage();
                 }
             }
         });

    }

    public void onUpdateMessage()
    {
        messageArrayList.clear();
        //messageArrayList.add("test message");

        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("message");

        parseQuery.whereEqualTo("recipient", opponentName);
        parseQuery.orderByAscending("createdAt");
        parseQuery.setLimit(20);

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    for(ParseObject object : objects)
                    {
                        String recipient = object.get("recipient").toString();
                        String sender = object.get("sender").toString();
                        String message = object.get("message").toString();
                        messageArrayList.add(sender+" -> "+recipient+" : "+message);
                    }

                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        onUpdateMessage();
    }

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
