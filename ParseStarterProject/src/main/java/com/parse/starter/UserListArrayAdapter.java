package com.parse.starter;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

class UserListArrayAdapter extends ArrayAdapter<UserInfomation> {

    private TextView nicknameText;
    private TextView statusText;
    private TextView lastMessageText;
    private ImageView profilePicImageView;
    private TextView lastMessageDateText;
    private TextView unreadMsgNumberText;
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

    public void clear(){
        userList.clear();
    }

    public void updateUserList(){

        final String appUser = ParseUser.getCurrentUser().getUsername();
        Log.i("AppUser",appUser.toString());

        for(int i=0; i<userList.size(); i++)
        {
            final String friend = userList.get(i).username;
            Log.i("Friend",friend.toString());

            final ParseQuery<ParseObject> parseQueryCurrectUserToOpponent = new ParseQuery<ParseObject>("message");
            final ParseQuery<ParseObject> parseQueryOpponentToCurrectUser = new ParseQuery<ParseObject>("message");

            parseQueryCurrectUserToOpponent.whereEqualTo("recipient", friend); // Currect User -> Opponent
            parseQueryCurrectUserToOpponent.whereEqualTo("sender", appUser);
            parseQueryOpponentToCurrectUser.whereEqualTo("recipient", appUser); //  Opponent  -> Currect User
            parseQueryOpponentToCurrectUser.whereEqualTo("sender", friend);

            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            queries.add(parseQueryCurrectUserToOpponent);
            queries.add(parseQueryOpponentToCurrectUser);
            final ParseQuery<ParseObject> query = ParseQuery.or(queries);
            query.orderByDescending("createdAt");
            //query.setLimit(1);

            final int finalI = i;
            final int[] countUnread = {0};
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null) {
                        int i = 0;
                        for(ParseObject object : objects)
                        {
                            if(i == 0) {

                                if(object.getString("message") != null)
                                {
                                    int currentDay = 0;
                                    int lastMsgDay = 0;
                                    int currentMonth = 0;
                                    int lastMsgMonth = 0;
                                    int currentYear = 0;
                                    int lastMsgYear = 0;

                                    final Date date = object.getCreatedAt();
                                    final Date CurrentData = Calendar.getInstance().getTime();
                                    SimpleDateFormat df_day = new SimpleDateFormat("dd");
                                    SimpleDateFormat df_month = new SimpleDateFormat("MM");
                                    SimpleDateFormat df_year = new SimpleDateFormat("yyyy");
                                    SimpleDateFormat df_time = new SimpleDateFormat("h:mm a");

                                    df_time.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                                    df_day.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                                    df_month.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                                    df_year.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

                                    String lastMsgTime = df_time.format(date);
                                    lastMsgDay = Integer.parseInt(df_day.format(date));
                                    lastMsgMonth = Integer.parseInt(df_month.format(date));
                                    lastMsgYear = Integer.parseInt(df_year.format(date));

                                    currentDay = Integer.parseInt(df_day.format(CurrentData));
                                    currentMonth = Integer.parseInt(df_month.format(CurrentData));
                                    currentYear = Integer.parseInt(df_year.format(CurrentData));

                                    String tmp_Date = "";
                                    if (lastMsgYear < currentYear) {
                                        //userList.get(finalI).lastMsgDate = lastMsgYear + "/" + lastMsgMonth + "/" + lastMsgDay;
                                        tmp_Date = lastMsgYear + "/" + lastMsgMonth + "/" + lastMsgDay;
                                    } else if (lastMsgYear == currentYear) {
                                        if (lastMsgDay == currentDay && lastMsgMonth == currentMonth) {
                                            //userList.get(finalI).lastMsgDate = lastMsgTime;
                                            tmp_Date =  lastMsgTime;
                                        } else {
                                            //userList.get(finalI).lastMsgDate = lastMsgMonth + "/" + lastMsgDay;
                                            tmp_Date = lastMsgMonth + "/" + lastMsgDay;
                                        }
                                    }

                                   if(object.getString("message").equals(userList.get(finalI).lastMsg) && tmp_Date.equals(userList.get(finalI).lastMsgDate)) {
                                        userList.get(finalI).lastMsg = object.getString("message");
                                        userList.get(finalI).lastMsgDate = tmp_Date;
                                   }

                                    if(object.getBoolean("status") == false
                                            && object.getString("recipient").equals(appUser)
                                            && object.getString("sender").equals(friend)) {

                                        countUnread[0]++;

                                    }
                                    Log.i("info", object.getString("recipient")+" : "+object.getString("sender")+" : "+object.getBoolean("status"));

                                }

                            }
                            else{
                                Log.i("info", object.getString("recipient")+" : "+object.getString("sender")+" : "+object.getBoolean("status"));

                                if(object.getBoolean("status") == false
                                        && object.getString("recipient").equals(appUser)
                                        && object.getString("sender").equals(friend)) {
                                    countUnread[0]++;
                                }

                            }

                            i++;
                        }
                        userList.get(finalI).unreadMsgNumber = countUnread[0];
                    }
                }
            });

        }

        this.notifyDataSetChanged();
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
            String statusText = userInfomationObj.status;
            nicknameText.setText(userInfomationObj.nickname);
            lastMessageText = (TextView) row.findViewById(R.id.userlist_last_message);
            lastMessageText.setText(userInfomationObj.lastMsg);
            lastMessageDateText = (TextView) row.findViewById(R.id.userlist_last_message_time);
            lastMessageDateText.setText(userInfomationObj.lastMsgDate);
            unreadMsgNumberText = (TextView) row.findViewById(R.id.userlist_unread_message_number);
            if (userInfomationObj.unreadMsgNumber == 0)
                unreadMsgNumberText.setVisibility(View.INVISIBLE);
            else {
                unreadMsgNumberText.setText(String.valueOf(userInfomationObj.unreadMsgNumber));
                unreadMsgNumberText.setVisibility(View.VISIBLE);
            }
            //statusText = (TextView) row.findViewById(R.id.userlist_status);
            //statusText.setText(userInfomationObj.status);
            profilePicImageView = (ImageView) row.findViewById(R.id.userlist_image);
            profilePicImageView.setImageBitmap(userInfomationObj.profilePic);

            row.setTag(R.id.userlist_layout, userInfomationObj.username);

            return row;
    }
}