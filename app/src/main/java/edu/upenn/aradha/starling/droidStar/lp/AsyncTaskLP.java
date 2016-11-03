package edu.upenn.aradha.starling.droidStar.lp;

import java.util.List;
import java.util.ArrayList;

import java.lang.Thread;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;
import android.os.Handler.Callback;
import android.content.Context;


import edu.colorado.plv.droidStar.LearningPurpose;
import static edu.colorado.plv.droidStar.Static.*;

public class AsyncTaskLP extends LearningPurpose {
    protected SimpleTask task;
    public int counter;

    // INPUTS
    public static String EXECUTE = "exec";
    public static String CANCEL = "cancel";
    public static String GET = "get";

    public static String PARAM = "asdf";

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(EXECUTE);
        inputs.add(CANCEL);
        inputs.add(GET);

        return inputs;
    }

    // OUTPUTS
    public static String CANCELLED = "cancelled";
    public static String POSTEXEC = "postexec";
    public static String PREEXEC = "preexec";

    public boolean isError(String output) {
        // there are no errors for this class?
        return false;
    }

    public int betaTimeout() {
        return 500;
    }

    // public int safetyTimeout() {
    //     // Maybe this will fix the onCancelled issues
    //     return 2000;
    // }

    public String shortName() {
        return "AsyncTask";
    }

    // public int postResetTimeout() {
    //     // The task could take up to 200ms to complete, so we need to
    //     // wait longer than that!!!
    //     return 500;
    // };

    public AsyncTaskLP(Context c) {
        super(c);
        this.task = null;
        this.counter = 0;
    }

    protected void resetActions(Context context, Callback callback) {
        doReset();
    }

    protected void doReset() {
        if (task != null) {
            task.cancel(true);
            counter++;
        }
        task = new SimpleTask(counter);
    }

    public void giveInput(String input) throws Exception {
        if (input.equals(EXECUTE)) {
            task.execute(PARAM);
        } else if (input.equals(CANCEL)) {
            task.cancel(false);
        } else if (input.equals(GET)) {
            task.get(0, TimeUnit.MILLISECONDS);
        } else {
            logl("Unknown command to AsyncTask");
            throw new IllegalArgumentException("Unknown command to AsyncTask");
        }
    }

    public class SimpleTask extends AsyncTask<String, String, String> {
        int localCounter;

        public SimpleTask(int c) {
            super();
            this.localCounter = c;
        }
        protected String doInBackground(String... ss) {
            try {Thread.sleep(200);}
            catch (Exception e) {logl("Sleep problem?");}
            return PARAM;
        }

        protected void onPreExecute() {
            // respond(PREEXEC);
        }

        protected void onCancelled(String s) {
            if (localCounter == counter) {
                respond(CANCELLED);
            }
        }

        protected void onPostExecute(String s) {
            if (s.equals(PARAM)) {
                respond(POSTEXEC);
            }
        }
    }

}
