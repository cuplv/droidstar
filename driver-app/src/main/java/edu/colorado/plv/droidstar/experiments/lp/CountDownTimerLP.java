package edu.colorado.plv.droidstar.experiments.lp;
import java.util.List;
import java.util.ArrayList;
import android.os.CountDownTimer;
import android.os.Handler.Callback;
import android.content.Context;
import edu.colorado.plv.droidstar.LearningPurpose;
import static edu.colorado.plv.droidstar.Static.*;

public class CountDownTimerLP extends LearningPurpose {


    // * State and experiment setup
    //
    // In this section, we define the state that will be tested during
    // the experiment and the way to reset the state between tests in
    // order to isolate their effects.

    protected CountDownTimer timer;

    public CountDownTimerLP(Context c) {
        super(c);
        // The timer will be initialized by resetActions()
        this.timer = null;
    }

    // The resetActions destroy and recreate the state, isolating the
    // new state from any effects (or pending callbacks) the old state
    // accumulated.
    protected String resetActions(Context context, Callback callback) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CTimer(1100);
        return null;
    }


    // * Inputs
    //
    // In this section, we declare and define the list of inputs that
    // DroidStar should investigate.  DroidStar's goal is to learn
    // exactly what stateful effects each input has on the
    // CountDownTimer object, including in what order they are allowed
    // to be invoked.

    // Convenient definitions so we don't mistype the strings later
    public static String START = "start";
    public static String CANCEL = "cancel";

    // The list of inputs DroidStar is responsible for learning about.
    // DroidStar will use this list to create the necessary tests.
    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(START);
        inputs.add(CANCEL);

        return inputs;
    }

    // The "definition" of each input, meaning the actual code
    // invocations DroidStar should perform in order to run an input.
    //
    // Here, as in most cases, each input corresponds to a single
    // method call.
    public void giveInput(String input, int altKey) throws Exception {
        if (input.equals(START)) {
            timer.start();
        } else if (input.equals(CANCEL)) {
            timer.cancel();
        } else {
            logl("Unknown command to CountDownTimer");
            throw new IllegalArgumentException("Unknown command to CountDownTimer");
        }
    }


    // * Outputs
    //
    // Here we set up the mechanism by which DroidStar records
    // callbacks executed by the object.

    public static String FINISHED = "finished";

    // Callbacks are recorded by instrumenting them with a "respond"
    // call.  Generally, output tracking is performed simply by
    // creating a subclass which adds a "respond(callback)" to each
    // callback method we care about.
    public class CTimer extends CountDownTimer {
        public CTimer(long s) {
            super(s, 1000);
        }
        public void onTick(long s) {

        }
        public void onFinish() {
            respond(FINISHED);
        }
    }


    // * Settings
    //
    // Here we set various settings necessary to the experiment

    // Some classes send a callback upon error instead of throwing an
    // exception.  We use this setting to declare callbacks that
    // should be considered as errors.
    //
    // CountDownTimer does not have any of these.
    public boolean isError(String output) {
        return false;
    }

    // Experiment name for logging purposes
    public String shortName() {
        return "CountDownTimer";
    }

    // How long DroidStar should wait for a callback before assuming
    // there will be none.  This value is very dependent on the class
    // being studied.  Large values are sometimes necessary, will
    // significantly extend the learning time.  (Time is in
    // milliseconds)
    public int betaTimeout() {
        return 2000;
    }

    // DroidStar is a tool for learing *regular* behavior.  Some
    // classes, like CountDownTimer, are non-regular.  This means that
    // calling an input N times will produce N callbacks.
    //
    // In order to learn a *regular subset* of its behavior, we can
    // restrict certain inputs to only be called once in any
    // particular test.
    public List<String> singleInputs() {
        List<String> inputs = new ArrayList();
        inputs.add(START);
        return inputs;
    }
}
