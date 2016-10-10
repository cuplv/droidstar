package edu.colorado.plv.droidStar;

import java.util.List;

import android.os.Message;
import android.os.Handler.Callback;

public interface MealyTeacher {
    public List<String> inputs();
    public void query(Callback c, String input);
    public void reset();
}
