package com.parse.starter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class UserListArrayAdapter extends ArrayAdapter<UserInfomation> {

    private TextView nicknameText;
    private TextView statusText;
    private ImageView profilePicImageView;
    private List<UserInfomation> userList = new ArrayList<UserInfomation>();
    private Context context;

    @Override
    public void add(UserInfomation object) {
        userList.add(object);
        super.add(object);
    }

    public UserListArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.userList.size();
    }

    public UserInfomation getItem(int index) {
        return this.userList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        UserInfomation userInfomationObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.user_list, parent, false);

            nicknameText = (TextView) row.findViewById(R.id.userlist_nickname);
            nicknameText.setText(userInfomationObj.nickname);
            statusText = (TextView) row.findViewById(R.id.userlist_status);
            statusText.setText(userInfomationObj.status);
            profilePicImageView = (ImageView)row.findViewById(R.id.userlist_image);
            profilePicImageView.setImageBitmap(userInfomationObj.profilePic);

        row.setTag(R.id.userlist_layout, userInfomationObj.username);

        return row;
    }
}