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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class ProfileActivity extends AppCompatActivity {

    EditText idEditText;
    EditText nickNameEditText;
    EditText statusEditText;
    private Uri imageUri;
    private final static int SELECT_PIC=0x123;
    Bitmap bitmap;
    ParseUser parseUser;
    Bundle myBundle;
    ImageView profilePicImageView;
    String userName = "";

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

    public void onConfirm(View view)
    {
        if(MainActivity.debugMsg == true) Log.i("Confirm","Confirm successfully");

        final String nickName = nickNameEditText.getText().toString();
        final String status = statusEditText.getText().toString();

        if(nickName.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "Nickname field with null value.", Toast.LENGTH_LONG).show();
        }
        else{

            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserProfile");
            parseQuery.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
            parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(bitmap != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        ParseFile file = new ParseFile(idEditText.getText().toString() + "Profile.jpeg", byteArray);
                        object.put("profilepic", file);
                    }
                    object.put("nickname",nickName);
                    object.put("status",status);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null)
                            {
                                Intent intent = new Intent();
                                intent.setClass(ProfileActivity.this, UsersListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            });


        }


    }

    public void onShowProfile()
    {
        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserProfile");
        parseQuery.whereEqualTo("username", userName);
        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null)
                {
                    ParseFile file = (ParseFile) object.get("profilepic");
                    try {
                        byte[] data = file.getData();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profilePicImageView.setImageBitmap(bitmap);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                    idEditText.setText(ParseUser.getCurrentUser().getUsername());
                    idEditText.setFocusable(false);
                    nickNameEditText.setText(object.getString("nickname"));
                    if(object.getString("status").equals("")) {
                        statusEditText.setText("No status");
                    }
                    else  statusEditText.setText(object.getString("status"));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        idEditText = (EditText) findViewById(R.id.idEditText);
        profilePicImageView = (ImageView)findViewById(R.id.profilePicImageView);
        nickNameEditText = (EditText)findViewById(R.id.nickNameEditText);
        statusEditText = (EditText)findViewById(R.id.statusEditText);

        imageUri= Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "profile.jpg"));

        setTitle("Profile infomation");
        onShowProfile();
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
