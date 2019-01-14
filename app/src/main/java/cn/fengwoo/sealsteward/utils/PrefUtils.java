package cn.fengwoo.sealsteward.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils  {
    /**
     * SharePreference封装
     *
     */
    public static final String PREF_NAME = "config";

    public static boolean getBoolean(Context context, String key,
                                     boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }
}
