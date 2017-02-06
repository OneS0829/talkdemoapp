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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    ParseUser parseUser;
    ArrayList usersArrayList = new ArrayList();
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
        userName = intent.getStringExtra("username");
        //String passWord = intent.getStringExtra("password");
        setTitle("Users List ( "+ userName +" Login )");
        usersListView = (ListView)findViewById(R.id.usersListView);
        usersArrayList.clear();
        arrayAdapter = new ArrayAdapter(UsersListActivity.this, android.R.layout.simple_list_item_1, usersArrayList);
        usersListView.setAdapter(arrayAdapter);

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

        switch (item.getItemId()) {
            case R.id.logout:

                if(MainActivity.debugMsg == true) Log.i("Menu item selected", "Logout");
                Intent intent = new Intent();
                parseUser.logOut();
                intent.setClass(UsersListActivity.this, MainActivity.class);
                startActivity(intent);

                return true;

            default:
                return false;

        }
    }

}
