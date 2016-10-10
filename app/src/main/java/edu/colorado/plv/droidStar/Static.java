package edu.colorado.plv.droidStar;

import android.util.Log;

import android.os.Message;
import android.os.Bundle;

public class Static {

    public static String TAG = "DROIDSTAR";
    public static String ACCEPTED = "+";
    public static String REJECTED = "-";
    public static String DELTA = "delta";
    public static String BETA = "beta";

    public static void log(String context, String message) {
        Log.d(TAG + ":" + context, message);
    }


    public static Message quickMessage(String val) {
        Message m = new Message();
        Bundle b = new Bundle();
        b.putString("quickMessage", val);
        m.setData(b);
        return m;
    }

    public static String readMessage(Message m) {
        return m.getData().getString("quickMessage");
    }

}
