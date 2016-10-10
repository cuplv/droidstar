package edu.colorado.plv.droidStar;

import java.util.Queue;

import android.os.Message;
import android.os.Handler.Callback;

import static edu.colorado.plv.droidStar.Static.*;

public class TrivialLearner {
    private Queue<Queue<String>> queries;
    private Queue<String> current;
    private MealyTeacher teacher;
    private int inputNum;

    private static void logl(String m) {
        log("LEARNER", m);
    }

    TrivialLearner(Queue<Queue<String>> qs, MealyTeacher t) {
        this.queries = qs;
        this.teacher = t;
        this.inputNum = 0;
    }

    public void learn() {
        logl("Starting learn process...");
        current = queries.poll();
        if (current != null) {
            teacher.reset();
            nextInput();
        } else {
            logl("Empty queue, nothing to learn?");
        }
    }

    private synchronized void nextInput() {
        String i = current.poll();
        if (i != null) {
            teacher.query(new TrivialLearnerCB(i, inputNum), i);
        } else {
            logl("Inputs exhausted; query complete.");
            if (!queries.isEmpty()) {
                learn();
            }
        }
    }

    private synchronized void reportFact(String i, String o) {        
        logl("Received output \"" + o + "\" on input \"" + i + "\"");
        inputNum++;
    }

    private class TrivialLearnerCB implements Callback {
        private String input;
        private int cbInputNum;

        TrivialLearnerCB(String i, int n) {
            this.input = i;
            this.cbInputNum = n;
        }

        public boolean handleMessage(Message m) {
            if (cbInputNum == inputNum) {
                String output = readMessage(m);
                reportFact(input, output);
                nextInput();
            } else {
                logl("Dup teacher response dropped.");
            }
            return true;
        }
    }
    
}
