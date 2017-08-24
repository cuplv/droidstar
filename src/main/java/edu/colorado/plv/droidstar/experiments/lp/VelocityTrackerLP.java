package edu.colorado.plv.droidstar.experiments.lp;

import java.util.List;
import java.util.ArrayList;

import java.lang.AssertionError;
import java.lang.Thread;
import java.util.concurrent.TimeUnit;

import android.os.Handler.Callback;
import android.content.Context;
import android.view.VelocityTracker;
import android.view.MotionEvent;
import android.os.SystemClock;

import edu.colorado.plv.droidstar.LearningPurpose;
import static edu.colorado.plv.droidstar.Static.*;

public class VelocityTrackerLP extends LearningPurpose {
        protected VelocityTracker vt;

    protected String resetActions(Context ctx, Callback cb) {
        this.vt = VelocityTracker.obtain();
        return null;
    }
    protected List<String> uniqueInputSet() {
        List<String>is = new ArrayList();
        is.add("move");
        is.add("clear");
        is.add("compute");
        is.add("get");
        is.add("recycle");
        return is;
    }
    public int betaTimeout() {
        return 200;
    }
    public boolean isError(String o) {return false;}
    public String shortName() {
        return "VelocityTracker";
    }
    public VelocityTrackerLP(Context c) {
        super(c);
    }
    public void giveInput(String input) throws Exception {
        if (input.equals("move")) {
            MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(),
                                               SystemClock.uptimeMillis(),
                                               MotionEvent.ACTION_DOWN,
                                               0,0,0);
            vt.addMovement(e);
        } else if (input.equals("clear")) {
            vt.clear();
        } else if (input.equals("compute")) {
            vt.computeCurrentVelocity(1);
        } else if (input.equals("get")) {
            vt.getXVelocity();
        } else if (input.equals("recycle")) {
            vt.recycle();
        }
    }
}
