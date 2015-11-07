package com.josiahgaskin.kyo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class History {
    private static History sInstance;
    private SharedPreferences prefs;

    public static void init(Context c) {
        sInstance = new History();
        sInstance.prefs = c.getSharedPreferences("History", Context.MODE_PRIVATE);
    }

    public static void add(String s) {
        Set<String> history = sInstance.prefs.getStringSet("raw", new HashSet<String>());
        history.add(s);
        sInstance.prefs.edit().putStringSet("raw", history).commit();
    }

    public static Set<String> getAll() {
        return sInstance.prefs.getStringSet("raw", new HashSet<String>());
    }
}
