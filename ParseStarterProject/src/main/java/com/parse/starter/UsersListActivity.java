package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseQuery.getQuery;

public class UsersListActivity extends AppCompatActivity {

    ArrayList<String> usersArrayList = new ArrayList<String>();
    ArrayList<String> userProfileArrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ListView usersListView;
    String userName;

    public void onShowUserView()
    {
        if(MainActivity.debugMsg == true) Log.i("onShowUserView","Entry");

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo("username",userName);
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                  if(e == null)
                  {
                      for(ParseUser object : objects)
                      {
                          usersArrayList.add(object.getString("username"));
                          if(MainActivity.debugMsg == true) Log.i("onShowUserView",object.getString("username"));
                      }
                      arrayAdapter.notifyDataSetChanged();
                  }
                  else{
                      if(MainActivity.debugMsg == true) Log.i("Error", e.toString());
                  }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Intent intent = getIntent();
        userName = ParseUser.getCurrentUser().getUsername();

        //String passWord = intent.getStringExtra("password");
        usersListView = (ListView)findViewById(R.id.usersListView);
        usersArrayList.clear();
        userProfileArrayList.clear();

        arrayAdapter = new ArrayAdapter(UsersListActivity.this, android.R.layout.simple_list_item_1, usersArrayList);
        usersListView.setAdapter(arrayAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.debugMsg == true) Log.i("onItemClick",usersArrayList.get(position));

                Intent intent = new Intent();
                intent.setClass(UsersListActivity.this, ChatMessageActivity.class);
                intent.putExtra("opponentName", usersArrayList.get(position));
                startActivity(intent);
            }
        });

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserProfile");
        parseQuery.whereEqualTo("username",userName);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null)
                {
                    String user_nickName = object.getString("nickname");
                    setTitle(user_nickName +"(Login)");
                }
            }
        });

        onShowUserView();
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
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.logout:

                if(MainActivity.debugMsg == true) Log.i("Menu item selected", "Logout");
                ParseUser.logOut();
                intent.setClass(UsersListActivity.this, MainActivity.class);
                startActivity(intent);

                return true;

            case R.id.profile:

                if(MainActivity.debugMsg == true) Log.i("Menu item selected", "Profile");
                intent.setClass(UsersListActivity.this, ProfileActivity.class);
                intent.putExtra("username",userName);
                startActivity(intent);

                return true;

            default:
                return false;

        }
    }

}
