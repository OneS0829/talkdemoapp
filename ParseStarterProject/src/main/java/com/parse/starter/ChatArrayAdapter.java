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

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView dateText;
    private TextView timeText;
    private ImageView profilePicImageView;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
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