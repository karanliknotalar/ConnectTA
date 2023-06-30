package com.aturan.connectta;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveLoginInfo {
    private Context context;
    private String fileName;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SaveLoginInfo(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public void writeString(String key, String value) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeBoolean(String key, Boolean value) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String readString(String key, String defValue) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    public Boolean readBoolean(String key, Boolean defValue) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defValue);
    }

    public void Clear() {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void RemoveKey(String key) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
