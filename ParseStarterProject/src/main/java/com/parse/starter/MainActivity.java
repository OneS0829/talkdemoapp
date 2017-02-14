/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

    static boolean debugMsg = true;

    EditText userNameEditText;
    EditText passWordEditText;
    TextView changeModeTextView;
    Button loginOrSignInButton;
    ParseUser parseUser;

  boolean loginFlag = true; // true for login, false for sign in

  public void onChangeMode(View view)
  {
       if(loginFlag == true)
       {
            loginFlag = false;
            loginOrSignInButton.setText("Sign In");
            changeModeTextView.setText("Or, Login");
       }
       else if(loginFlag == false)
       {
            loginFlag = true;
            loginOrSignInButton.setText("Login");
            changeModeTextView.setText("Or, Sign In");
       }
  }

    public void onLoginOrSignIn(View view)
    {
        final String userName = userNameEditText.getText().toString();
        final String passWord = passWordEditText.getText().toString();
        if(userName.equals("") || passWord.equals(""))
        {
            Toast.makeText(MainActivity.this, "Invalid username / password", Toast.LENGTH_SHORT).show();
        }
        else {
            if(debugMsg == true) {
                Log.i("User Info", userName);
                Log.i("Password Info", passWord);
            }
            if(loginFlag == true) {
                if(debugMsg == true) Log.i("Mode", "Login");

                parseUser.logInInBackground(userName, passWord, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e == null)
                        {
                            if(debugMsg == true) Log.i("Login Info","Welcome, "+ParseUser.getCurrentUser().getUsername());
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, UsersListActivity.class);
                            intent.putExtra("username", userName);
                            //intent.putExtra("password", passWord);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                if(debugMsg == true) Log.i("Mode", "Sign In");

                loginFlag = true;
                loginOrSignInButton.setText("Login");
                changeModeTextView.setText("Or, Sign In");

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SignInActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userName",userName);
                bundle.putString("passWord",passWord);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    loginFlag = true;
    userNameEditText = (EditText)findViewById(R.id.userNameEditText);
    passWordEditText = (EditText)findViewById(R.id.passWordEditText);
    changeModeTextView = (TextView)findViewById(R.id.changeModeTextView);
    loginOrSignInButton = (Button)findViewById(R.id.loginOrSignInButton);
    parseUser = new ParseUser();
    getSupportActionBar().hide();
    changeModeTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}