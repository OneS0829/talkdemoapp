package com.parse.starter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView statusText;
    private TextView dateText;
    private TextView timeText;
    private ImageView profilePicImageView;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private String friend;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public void clear() {
        chatMessageList.clear();
    }

    public void checkHasUnReadMessage(){

        //Log.i("checkHasReadMessage","Entry");

        String appUser = ParseUser.getCurrentUser().getUsername();

        //For User
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("message");
        parseQuery.whereEqualTo("recipient",appUser);
        parseQuery.whereEqualTo("sender",friend);
        parseQuery.whereEqualTo("status", false);

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                 if(objects.size() > 0) {
                      for(int i=0; i<objects.size(); i++)
                      {
                          //Log.i("test info",String.valueOf(objects.get(i).getBoolean("status")));
                          objects.get(i).put("status",true);
                          try {
                              objects.get(i).save();
                          } catch (ParseException e1) {
                              e1.printStackTrace();
                          }
                      }
                 }
            }
        });

        //For Friend
        //boolean hasUnReadinView = false;
/*
        for(int i=0; i<chatMessageList.size(); i++)
        {
             if(chatMessageList.get(i).type == 0)
             {
                 if(chatMessageList.get(i).msgStatus == false) {
                     //hasUnReadinView = true;
                     ParseQuery<ParseObject> parseQuery2 = new ParseQuery<ParseObject>("message");
                     parseQuery2.whereEqualTo("objectId", chatMessageList.get(i).messageId);
                     parseQuery2.whereEqualTo("status", true);

                     final int finalI = i;
                     parseQuery2.findInBackground(new FindCallback<ParseObject>() {
                         @Override
                         public void done(List<ParseObject> objects, ParseException e) {
                             if (objects.size() > 0) {
                                 chatMessageList.get(finalI).msgStatus = true;
                             }
                         }
                     });

                 }
             }


        }
*/
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public void setFriend(String friend)
    {
        this.friend = friend;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.type == 0) {
            //Log.i("Who","User"+chatMessageObj.msgTime);
            row = inflater.inflate(R.layout.right, parent, false);
            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(chatMessageObj.message);
            timeText = (TextView) row.findViewById(R.id.msg_time);
            timeText.setText(chatMessageObj.msgTime);
            statusText = (TextView) row.findViewById(R.id.msg_status);
            //if(!chatMessageObj.msgStatus) statusText.setText("");
            //else statusText.setText("Read");
            profilePicImageView = (ImageView)row.findViewById(R.id.avatar_chat_right);
            profilePicImageView.setImageBitmap(chatMessageObj.profilePic);
        }else if(chatMessageObj.type == 1){
            //Log.i("Who","Friend"+chatMessageObj.msgTime);
            row = inflater.inflate(R.layout.left, parent, false);
            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(chatMessageObj.message);
            timeText = (TextView) row.findViewById(R.id.msg_time);
            timeText.setText(chatMessageObj.msgTime);
            profilePicImageView = (ImageView)row.findViewById(R.id.avatar_chat_left);
            profilePicImageView.setImageBitmap(chatMessageObj.profilePic);
        }
        else if(chatMessageObj.type == 2){
            row = inflater.inflate(R.layout.date, parent, false);
            dateText = (TextView) row.findViewById(R.id.chatdate);
            dateText.setText(chatMessageObj.message);
        }
        return row;
    }
}