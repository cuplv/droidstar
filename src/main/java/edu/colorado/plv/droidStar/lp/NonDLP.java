package edu.colorado.plv.droidStar.lp;

import java.util.List;
import java.util.ArrayList;

import android.os.Handler.Callback;
import android.content.Context;

import edu.colorado.plv.droidStar.LearningPurpose;
import static edu.colorado.plv.droidStar.Static.*;

public class NonDLP extends LearningPurpose {
    protected NonD nond;

    // INPUTS
    public static String A = "a";
    public static String B = "b";
    public static String C = "c";

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(A);
        inputs.add(B);
        inputs.add(C);
        return inputs;
    }

    //OUTPUTS
    public static String X = "x";
    public static String Y = "y";

    public boolean isError(String output) {
        return false;
    }

    public int betaTimeout() {
        return 100;
    }

    public String shortName() {
        return "NonD";
    }

    public NonDLP(Context c) {
        super(c);
        this.nond = null;
    }

    // The purpose of this class is that it does not properly reset,
    // thus it will force non-deterministic results 
    protected void resetActions(Context context, Callback callback) {
        if (nond == null) {
            nond = new NonD(1);
        }
    }

    public class NonD {
        int max;
        int counter;

        public NonD(int c) {
            this.max = c;
            this.counter = 0;
        }

        private void runA() {
            respond(X);
        }
        private void runB() {
            respond(X);
        }
        private void runC() {
            if (counter < max) {
                counter ++;
                respond(X);
            } else {
                counter = 0;
                respond(Y);
            }
        }
    }

    public void giveInput(String input) {
        if (input.equals(A)) {
            nond.runA();
        } else if (input.equals(B)) {
            nond.runB();
        } else if (input.equals(C)) {
            nond.runC();
        } else {
            logl("Unknown command to NonD");
            throw new IllegalArgumentException("Unknown command to NonD");
        }
    }
}

            
