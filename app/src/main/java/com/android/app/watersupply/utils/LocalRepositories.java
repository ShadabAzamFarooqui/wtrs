package com.android.app.watersupply.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;


public class LocalRepositories {

    public synchronized static void saveAppUser(Context ctx, AppUser user) {

        String jsonString = new Gson().toJson(user);
        PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putString("Pref", jsonString)
                .commit();


    }

    public synchronized static AppUser getAppUser(Context ctx) {

        String jsonString = PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString("Pref", "");

        return "".equals(jsonString) ?
                null : new Gson().fromJson(jsonString, AppUser.class);
    }
}
