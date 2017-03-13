package com.parse.starter;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Date;

/**
 * Created by OneS on 2017/2/9.
 */

public class ChatMessage {

    public int type;   // appUser: 0, friend: 1, date: 2
    public String message;
    public String msgTime;
    public Date dateMsgTime;
    public Bitmap profilePic;
    public Boolean msgStatus;
    public String messageId;

    public ChatMessage(int type, String message, String msgTime, Date dataMsgTime, Bitmap profilePic, Boolean msgStatus, String messageId) {
        super();

        this.type = type;
        this.message = message;
        this.msgTime = msgTime;
        this.profilePic = profilePic;
        this.dateMsgTime = dataMsgTime;
        this.msgStatus = msgStatus;
        this.messageId = messageId;
    }


}
