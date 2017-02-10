package com.parse.starter;

import java.util.Date;

/**
 * Created by OneS on 2017/2/9.
 */

public class ChatMessage {
    //public boolean left;
    public int type;   // appUser: 0, friend: 1, date: 2
    public String message;
    public String msgTime;

    public ChatMessage(int type, String message, String msgTime) {
        super();
        //this.left = left;
        this.type = type;
        this.message = message;
        this.msgTime = msgTime;
    }


}
