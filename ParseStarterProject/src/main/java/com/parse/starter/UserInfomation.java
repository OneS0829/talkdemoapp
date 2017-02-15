package com.parse.starter;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by OneS on 2017/2/9.
 */

public class UserInfomation {
    //public boolean left;
    public String username;
    public String nickname;
    public String status;
    public Bitmap profilePic;

    public UserInfomation(String username, String nickname, String status, Bitmap profilePic) {
        super();

        this.username = username;
        this.nickname = nickname;
        this.status = status;
        this.profilePic = profilePic;
        //if(dateMsgTime != null) Log.i("DateMsgTime",dateMsgTime.toString());
    }


}
