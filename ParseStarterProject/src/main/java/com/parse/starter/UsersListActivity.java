package com.parse.starter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.parse.ParseQuery.getQuery;

public class UsersListActivity extends AppCompatActivity {

    ArrayList<String> usersArrayList = new ArrayList<String>();
    ArrayList<String> userProfileArrayList = new ArrayList<String>();
    //ArrayAdapter arrayAdapter;
    private UserListArrayAdapter userListArrayAdapter;
    ListView usersListView;
    String userName;
    static ProgressDialog dialog;

    Thread userListUpdateThread;
    boolean userListUpdateActive = false;
    Thread userListLoadingThread;
    boolean userListLoadingActive = false;

    public void updateUserListView()
    {
        userListArrayAdapter.updateUserList();
    }

    public void onShowUserView()
    {
        if(MainActivity.debugMsg == true) Log.i("onShowUserView","Entry");

        final Bitmap[] resizeBitmap = new Bitmap[1];
        final int[] unreadMsgNumber = {0};

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserProfile");
        parseQuery.whereNotEqualTo("username",userName);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    for (final ParseObject object : objects) {
                        final ParseFile file = (ParseFile) object.get("profilepic");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                resizeBitmap[0] = zoomBitmap(bitmap, 100, 100);
                                userListArrayAdapter.add(new UserInfomation(object.getString("username"), object.getString("nickname"), object.getString("status"), resizeBitmap[0], "No message...", "", 0));

                            }
                        });
                    }

                    userListUpdateThread.start();
                    userListLoadingThread.start();
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


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MainActivity.debugMsg == true) Log.i("Activity State","onCreate");

        setContentView(R.layout.activity_users_list);
        Intent intent = getIntent();
        userName = ParseUser.getCurrentUser().getUsername();

        //String passWord = intent.getStringExtra("password");
        usersListView = (ListView)findViewById(R.id.usersListView);
        usersArrayList.clear();
        userProfileArrayList.clear();

        dialog = ProgressDialog.show(UsersListActivity.this,
                "讀取中", "Wait...",true);

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

        userListUpdateThread = new UsersListActivity.userListUpdateThread();
        userListUpdateActive = true;
        userListLoadingThread = new UsersListActivity.userListLoadingThread();


        onShowUserView();

        Log.i("Test","Out Loop");
    }

    class userListUpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(true){
                try {
                    Message message = new Message();
                    if(userListUpdateActive == true) {
                        mHandler.sendMessage(message);
                        //Log.i("Thread","Going...");
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class userListLoadingThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2500);
                dialog.dismiss();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUserListView();
        }
    };

    @Override
    protected void onPause() {
        if(MainActivity.debugMsg == true) Log.i("Activity State","onPause");
        super.onPause();

        if (userListUpdateThread != null) {
            userListUpdateActive = false;
        }
    }

    @Override
    protected void onRestart() {
        if(MainActivity.debugMsg == true) Log.i("Activity State","onRestart");
        super.onRestart();

        if (userListUpdateThread != null) {
            userListUpdateActive = true;
            //userListLoadingThread.run();
        }
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
