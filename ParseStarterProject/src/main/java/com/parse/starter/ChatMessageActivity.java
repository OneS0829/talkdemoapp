package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ChatMessageActivity extends AppCompatActivity {
    ParseObject parseObject = new ParseObject("message");

    ListView messageListView;
    ArrayList<String> messageArrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    public void onUpdateMessage()
    {
        messageArrayList.clear();
        messageArrayList.add("test message");

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        messageListView = (ListView)findViewById(R.id.chatContentListView);
        arrayAdapter = new ArrayAdapter(ChatMessageActivity.this,android.R.layout.simple_list_item_1,messageArrayList);

        Intent intent = getIntent();
        String opponentName = intent.getStringExtra("opponentName");
        String userName = ParseUser.getCurrentUser().getUsername();
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
