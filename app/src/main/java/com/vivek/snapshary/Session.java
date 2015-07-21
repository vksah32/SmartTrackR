package com.vivek.snapshary;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by vivek on 7/17/15.
 */
public class Session {
    private ParseObject mUser;

    private ParseObject[] mFriends;



    public Session(String userPhone){
        mUser = new ParseObject("User");
        mUser.put("phone", userPhone);
        mUser.saveInBackground();


    }

    public void setFriends(ParseObject[] mFriends) {
        this.mFriends = mFriends;
    }



}
