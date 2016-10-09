package edu.colorado.plv.droidStar;

import android.util.Log;

public class Static {

    public static String TAG = "DROIDSTAR";

    public static void log(String message) {
        Log.d(TAG, message);
    }

    public static void logcb(String callbackName) {
        log("CALLBACK: " + callbackName);
    }
    
}
