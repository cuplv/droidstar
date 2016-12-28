package edu.upenn.aradha.starling.droidStar.lp;

import java.util.List;
import java.util.ArrayList;
 
import android.os.CountDownTimer;
import android.os.Handler.Callback;
import android.content.Context;


import edu.colorado.plv.droidStar.LearningPurpose;
import static edu.colorado.plv.droidStar.Static.*;

public class CountDownTimerLP extends LearningPurpose {
    protected CountDownTimer timer;

    // INPUTS
    public static String START = "start";
    public static String CANCEL = "cancel";

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(START);
        inputs.add(CANCEL);

        return inputs;
    }

    // OUTPUTS
    public static String FINISHED = "finished";
    public static String TICK = "tick";

    public boolean isError(String output) {
        // there are no errors for this class?
        return false;
    }

    public String shortName() {
        return "CountDownTimer";
    }

    public int betaTimeout() {
        // Ticks come every second, so timeout should be more than a
        // second
        return 2000;
    }

    public List<String> singleInputs() {
        List<String> inputs = new ArrayList();
        inputs.add(START);
        return inputs;
    }

    public CountDownTimerLP(Context c) {
        super(c);
        this.timer = null;
    }

    protected void resetActions(Context context, Callback callback) {
        doReset();
    }

    protected void doReset() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CTimer(1100);
    }

    public void giveInput(String input) throws Exception {
        if (input.equals(START)) {
            timer.start();
        } else if (input.equals(CANCEL)) {
            timer.cancel();
        } else {
            logl("Unknown command to CountDownTimer");
            throw new IllegalArgumentException("Unknown command to CountDownTimer");
        }
    }

    public class CTimer extends CountDownTimer {
        public CTimer(long s) {
            super(s, 1000);
        }
        public void onTick(long s) {
            // respond(TICK);
        }
        public void onFinish() {
            respond(FINISHED);
        }
    }
}
