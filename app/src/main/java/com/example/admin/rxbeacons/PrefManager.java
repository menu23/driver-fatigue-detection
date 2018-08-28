package com.example.admin.rxbeacons;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Artemis on 19-May-2018.
 */

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "user-data";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String USER_NAME = "UserName";
    private static final String CONTACT_NAME = "ContactName";
    private static final String CONTACT_NUMBER = "Number";
    private static final String GENDER = "Gender";
    private static final String REMOTE = "Remote";
    private static final String LOCATION = "Location";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setName(String name) {
        editor.putString(USER_NAME, name);
        editor.commit();
    }

    public String getName() {
        return pref.getString(USER_NAME, "");
    }

    public void setContactName(String name) {
        editor.putString(CONTACT_NAME, name);
        editor.commit();
    }

    public String getContactName() {
        return pref.getString(CONTACT_NAME, null);
    }

    public void setContactNumber(String number) {
        editor.putString(CONTACT_NUMBER, number);
        editor.commit();
    }

    public String getContactNumber() {
        return pref.getString(CONTACT_NUMBER, null);
    }

    public void setGender(int gender) {
        editor.putInt(GENDER, gender);
        editor.commit();
    }

    public int getGender() {
        return pref.getInt(GENDER, -1);
    }

    public void setRemote(boolean remote) {
        editor.putBoolean(REMOTE, remote);
        editor.commit();
    }

    public boolean getRemote() {
        return pref.getBoolean(REMOTE, true);
    }

    public void setLocation(boolean location) {
        editor.putBoolean(LOCATION, location);
        editor.commit();
    }

    public boolean getLocation() {
        return pref.getBoolean(LOCATION, true);
    }
}
