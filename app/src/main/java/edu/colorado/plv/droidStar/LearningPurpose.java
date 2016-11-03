package edu.colorado.plv.droidStar;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;

import android.os.Handler.Callback;
import android.content.Context;

import static edu.colorado.plv.droidStar.Static.*;

public abstract class LearningPurpose {
    private Context context;
    private Callback forOutput;

    protected LearningPurpose(Context c) {
        this.context = c;
        this.forOutput = null;
    }

    protected Context getContext() {
        return context;
    }

    protected void logl(String m) {
        log("PURPOSE", m);
    }

    protected void logcb(String output) {
        logl("Callback reported: " + output);
    }

    protected void respond(String output) {
        if (forOutput != null) {
            logcb(output);
            forOutput.handleMessage(quickMessage(output));
        } else {
            throw new AssertionError("LP's callback was not initialized.  You must call reset() before you use it.");
        }
    }

    public void reset(Callback c) {
        this.forOutput = c;
        resetActions(this.context, c);
        logl("LP has been reset.");
    }

    public List<String> inputSet() {
        List<String> is = new ArrayList(uniqueInputSet());
        is.add(DELTA);

        return is;
    }

    // The things that need to be done between queries to go back to
    // an initialized state
    protected abstract void resetActions(Context context, Callback callback);

    // Identifies whether a given output should be considered an error
    public abstract boolean isError(String output);

    // Drop an output if you've already seen it in the course of one
    // delta-block.  Maybe useful if a class is buggy and repeats
    // callbacks (looking at you, SpeechRecognizer...)
    public boolean dropDoubleOutput() {
        return false;
    }

    // The name that will be used for this class in logs and printed
    // result files
    public abstract String shortName();

    // Use this in case there are issues with noise in the queue after
    // a block of inputs.  The transducer will wait this amount of
    // time (in milliseconds) after each block is finished.
    public int safetyTimeout() {
        return 0;
    }

    // Inputs that should only be allowed once, in order to reduce
    // more complicated classes to regular languages that we can learn
    // correctly
    //
    // It's empty by default
    public List<String> singleInputs() {
        return new ArrayList();
    }

    // All queries are checked against this before being run.  They
    // will be counted as errors if this fails.
    //
    // The default implementation asserts that any input in the
    // singleInputs() for this class only appears once.
    public boolean validQuery(Queue<String> q) {
        Queue<String> query = new ArrayDeque(q);
        List<String> seen = new ArrayList();
        
        for (String input : query) {
            if (seen.contains(input)
                && singleInputs().contains(input)) {
                return false;
            } else {
                seen.add(input);
            }
        }
        return true;
    }

    // Use this to wait after resetting, in case resetting causes some
    // callback to be reported which you need to be flushed before
    // starting the query.
    public int postResetTimeout() {
        return 0;
    }

    // Time reasonable to wait before reporting "beta" for a delta
    // input (in milliseconds)
    public abstract int betaTimeout();

    // Variable to the equivalence query; must be longer for some
    // classes that need many inputs before they produce any outputs
    // (motivated by the FileObserverLP)
    public int eqLength() {
        return 2;
    }

    // Input set for this class, not including delta
    protected abstract List<String> uniqueInputSet();

    // Take an input symbol and interact with the object accordingly
    public abstract void giveInput(String input) throws Exception;
}
