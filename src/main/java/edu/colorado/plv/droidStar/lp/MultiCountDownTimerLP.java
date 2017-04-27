package edu.colorado.plv.droidStar.lp;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import static edu.colorado.plv.droidStar.Static.*;

public class MultiCountDownTimerLP extends CountDownTimerLP {

    public MultiCountDownTimerLP(Context c) {
        super(c);
    }

    // EXTRA INPUTS
    public static String INIT0 = "init0";
    public static String INIT2 = "init2";
    public static String INIT5 = "init5";

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList(super.uniqueInputSet());
        inputs.add(INIT0);
        inputs.add(INIT2);
        inputs.add(INIT5);

        return inputs;
    }

    public String shortName() {
        return "CountDownTimer-MultiInit";
    }

    protected void doReset() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void giveInput(String input) throws Exception {
        if (input.equals(INIT0)) {
            doReset();
            timer = new CTimer(0);
        } else if (input.equals(INIT2)) {
            doReset();
            timer = new CTimer(2000);
        } else if (input.equals(INIT5)) {
            doReset();
            timer = new CTimer(5000);
        } else {
            super.giveInput(input);
        }
    }
}
