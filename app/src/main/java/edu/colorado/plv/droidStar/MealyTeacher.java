package edu.colorado.plv.droidStar;

import java.util.List;
import java.util.Queue;

import android.os.Message;
import android.os.Handler.Callback;

public interface MealyTeacher {
    public List<String> inputSet();
    public void membershipQuery(Callback c, Queue<String> query);
}
