package edu.colorado.plv.droidStar;

import java.util.Queue;
import java.util.ArrayDeque;
import android.os.Message;

import android.os.Handler.Callback;

import static edu.colorado.plv.droidStar.Static.*;

public class TrivialLearner {
    private Queue<String> query;
    private Transducer teacher;

    TrivialLearner(Queue<String> q, Transducer t) {
        this.query = q;
        this.teacher = t;
    }

    public void learn() {
        log("Starting learn process...");
        nextInput();
    }

    private synchronized void nextInput() {
        String i = query.poll();
        if (i != null) {
            teacher.query(new TrivialLearnerCB(i), i);
        } else {
            log("Inputs exhausted; query complete!");
        }
    }

    private synchronized void reportFact(String i, String o) {
        log("Learner received O: \"" + o + "\" on I: \"" + i + "\"");
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
