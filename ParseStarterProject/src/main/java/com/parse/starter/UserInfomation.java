package com.parse.starter;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by OneS on 2017/2/9.
 */

public class UserInfomation {

    public String username;
    public String nickname;
    public String status;
    public Bitmap profilePic;
    public String lastMsgDate;
    public String lastMsg;
    public int unreadMsgNumber;

    public UserInfomation(String username, String nickname, String status, Bitmap profilePic, String lastMsg, String lastMsgDate, int unreadMsgNumber) {
        super();

        this.username = username;
        this.nickname = nickname;
        this.status = status;
        this.profilePic = profilePic;
        this.lastMsg = lastMsg;
        this.lastMsgDate = lastMsgDate;
        this.unreadMsgNumber = unreadMsgNumber;
        //if(dateMsgTime != null) Log.i("DateMsgTime",dateMsgTime.toString());
    }


}
