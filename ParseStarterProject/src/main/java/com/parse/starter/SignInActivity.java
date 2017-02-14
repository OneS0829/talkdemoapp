package com.parse.starter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class SignInActivity extends AppCompatActivity {

    EditText idEditText;
    EditText passWordEditText;
    EditText nickNameEditText;
    EditText confirmPasswordEditText;
    EditText statusEditText;
    private Uri imageUri;
    private final static int SELECT_PIC=0x123;
    Bitmap bitmap;
    ParseUser parseUser;
    Bundle myBundle;

    ImageView profilePicImageView;
    public void onSignIn(View view)
    {
        String confirmPW = confirmPasswordEditText.getText().toString();
        String PW = passWordEditText.getText().toString();
        String nickName = nickNameEditText.getText().toString();
        parseUser = new ParseUser();

        if(!confirmPW.equals(PW))
        {
            Toast.makeText(SignInActivity.this, "Password confirm error!!", Toast.LENGTH_LONG).show();
        }
        else if(nickName.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please enter your nickname.", Toast.LENGTH_LONG).show();
        }
        else{
            ParseUser.logOut();

            String userName = myBundle.getString("userName");
            String passWord = myBundle.getString("passWord");

            Log.i("Username",myBundle.getString("userName"));
            Log.i("Password",myBundle.getString("passWord"));

            parseUser.setUsername(userName);
            parseUser.setPassword(passWord);

            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                    {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        if(bitmap == null)
                        {
                            Resources res=getResources();
                            bitmap=BitmapFactory.decodeResource(res, R.drawable.profilepic);
                        }

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        ParseFile file = new ParseFile(idEditText.getText().toString()+"Profile.jpeg", byteArray);

                        ParseObject object = new ParseObject("UserProfile");
                        object.put("profilepic", file);
                        object.put("username",idEditText.getText().toString());
                        object.put("nickname",nickNameEditText.getText().toString());
                        object.put("status",statusEditText.getText().toString());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                {
                                    Toast.makeText(SignInActivity.this, "Sign in successfully!! Please enter your username & password.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.setClass(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(SignInActivity.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(SignInActivity.this, "Sign in error, Fail: "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void onGetPhotoAndCrop() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从所有图片中进行选择
        intent.putExtra("crop", "true");//设置为裁切
        intent.putExtra("aspectX", 1);//裁切的宽比例
        intent.putExtra("aspectY", 1);//裁切的高比例
        intent.putExtra("outputX", 512);//裁切的宽度
        intent.putExtra("outputY", 512);//裁切的高度
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将裁切的结果输出到指定的Uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//裁切成的图片的格式
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, SELECT_PIC);
    }

    public void onUpdateProfilePic(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {

                onGetPhotoAndCrop();

            }

        } else {

            onGetPhotoAndCrop();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        idEditText = (EditText) findViewById(R.id.idEditText);
        passWordEditText = (EditText) findViewById(R.id.passWordEditText);
        profilePicImageView = (ImageView)findViewById(R.id.profilePicImageView);
        nickNameEditText = (EditText)findViewById(R.id.nickNameEditText);
        confirmPasswordEditText = (EditText)findViewById(R.id.confirmPasswordEditText);
        statusEditText = (EditText)findViewById(R.id.statusEditText);

        imageUri=Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "profile.jpg"));


        myBundle = this.getIntent().getExtras();
        idEditText.setText(myBundle.getString("userName"));
        passWordEditText.setText(myBundle.getString("passWord"));
        idEditText.setFocusable(false);
        passWordEditText.setFocusable(false);

        setTitle("Sign In infomation");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case SELECT_PIC:
                if (resultCode==RESULT_OK) {
                    try {
                        bitmap=BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));//将imageUri对象的图片加载到内存
                        profilePicImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}