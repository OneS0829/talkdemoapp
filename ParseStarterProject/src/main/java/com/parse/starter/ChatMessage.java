package com.parse.starter;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Date;

/**
 * Created by OneS on 2017/2/9.
 */

public class ChatMessage {
    //public boolean left;
    public int type;   // appUser: 0, friend: 1, date: 2
    public String message;
    public String msgTime;
    public Date dateMsgTime;
    public Bitmap profilePic;

    public ChatMessage(int type, String message, String msgTime, Date dataMsgTime, Bitmap profilePic) {
        super();
        //this.left = left;
        this.type = type;
        this.message = message;
        this.msgTime = msgTime;
        this.profilePic = profilePic;
        this.dateMsgTime = dataMsgTime;
        //if(dateMsgTime != null) Log.i("DateMsgTime",dateMsgTime.toString());
    }


}
