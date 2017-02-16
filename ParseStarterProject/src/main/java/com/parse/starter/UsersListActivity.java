package com.parse.starter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseQuery.getQuery;

public class UsersListActivity extends AppCompatActivity {

    ArrayList<String> usersArrayList = new ArrayList<String>();
    ArrayList<String> userProfileArrayList = new ArrayList<String>();
    //ArrayAdapter arrayAdapter;
    private UserListArrayAdapter userListArrayAdapter;
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
                          //usersArrayList.add(object.getString("username"));
                          //if(MainActivity.debugMsg == true) Log.i("onShowUserView",object.getString("username"));
                          ParseQuery<ParseObject> parseQuery2 = ParseQuery.getQuery("UserProfile");
                          parseQuery2.whereEqualTo("username",object.getString("username"));
                          parseQuery2.getFirstInBackground(new GetCallback<ParseObject>() {
                              @Override
                              public void done(final ParseObject object, ParseException e) {
                                  if(e == null) {
                                      final ParseFile file = (ParseFile) object.get("profilepic");
                                      file.getDataInBackground(new GetDataCallback() {
                                          @Override
                                          public void done(byte[] data, ParseException e) {
                                              if(e == null)
                                              {
                                                  Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                  userListArrayAdapter.add(new UserInfomation(object.getString("username"), object.getString("nickname"), object.getString("status"), bitmap));
                                              }
                                          }
                                      });
                                  }
                              }
                          });
                      }
                      userListArrayAdapter.notifyDataSetChanged();
                  }
                  else{
                      if(MainActivity.debugMsg == true) Log.i("Error", e.toString());
                  }
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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

        //arrayAdapter = new ArrayAdapter(UsersListActivity.this, android.R.layout.simple_list_item_1, usersArrayList);
        //usersListView.setAdapter(arrayAdapter);

        userListArrayAdapter = new UserListArrayAdapter(getApplicationContext(), R.layout.user_list);
        usersListView.setAdapter(userListArrayAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.debugMsg == true) Log.i("onItemClick",String.valueOf(position));
                if(MainActivity.debugMsg == true) Log.i("onItemClick",view.getTag(R.id.userlist_layout).toString());

                Intent intent = new Intent();
                intent.setClass(UsersListActivity.this, ChatMessageActivity.class);
                intent.putExtra("opponentName", view.getTag(R.id.userlist_layout).toString());
                startActivity(intent);

            }
        });

        setTitle("");
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        final LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserProfile");
        parseQuery.whereEqualTo("username",userName);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null)
                {
                    final String user_nickName = object.getString("nickname");
                    ParseFile file = (ParseFile) object.get("profilepic");

                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                            ImageView profilePicImageView;
                            TextView nicknameTextView;
                            View v = inflator.inflate(R.layout.custom_imageview, null);
                            nicknameTextView = (TextView) v.findViewById(R.id.user_name);
                            nicknameTextView.setText(user_nickName);
                            profilePicImageView = (ImageView)v.findViewById(R.id.user_profilepic);
                            profilePicImageView.setImageBitmap(bitmap);
                            actionBar.setCustomView(v);
                        }
                    });
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

            case R.id.userList:
                   //do-notthing
                return true;

            default:
                return false;

        }
    }

}
