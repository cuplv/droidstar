package edu.colorado.plv.droidStar;

import java.util.Queue;

import android.os.Message;
import android.os.Handler.Callback;

import static edu.colorado.plv.droidStar.Static.*;

public class TrivialLearner {
    private Queue<String> query;
    private MealyTeacher teacher;

    private static void logl(String m) {
        log("LEARNER", m);
    }

    TrivialLearner(Queue<String> q, MealyTeacher t) {
        this.query = q;
        this.teacher = t;
    }

    public void learn() {
        logl("Starting learn process...");
        nextInput();
    }

    private synchronized void nextInput() {
        String i = query.poll();
        if (i != null) {
            teacher.query(new TrivialLearnerCB(i), i);
        } else {
            logl("Inputs exhausted; query complete!");
        }
    }

    private synchronized void reportFact(String i, String o) {
        logl("Received output \"" + o + "\" on input \"" + i + "\"");
    }

    private class TrivialLearnerCB implements Callback {
        private String input;

        TrivialLearnerCB(String i) {
            this.input = i;
        }

        public boolean handleMessage(Message m) {
            String output = readMessage(m);
            reportFact(input, output);
            nextInput();
            return true;
        }
    }
    
}
