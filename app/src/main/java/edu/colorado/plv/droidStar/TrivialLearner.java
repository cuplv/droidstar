package edu.colorado.plv.droidStar;

import java.util.Queue;

import android.os.Message;
import android.os.Handler.Callback;

import static edu.colorado.plv.droidStar.Static.*;

public class TrivialLearner {
    private Queue<Queue<String>> queries;
    private Queue<String> current;
    private MealyTeacher teacher;

    private static void logl(String m) {
        log("LEARNER", m);
    }

    TrivialLearner(Queue<Queue<String>> qs, MealyTeacher t) {
        this.queries = qs;
        this.teacher = t;
    }

    public void learn() {
        logl("Starting learn process...");
        nextQuery();
    }

    private synchronized void nextQuery() {
        Queue<String> q = queries.poll();
        Callback c = new TrivialLearnerCB();
        if (q != null) {
            teacher.membershipQuery(c, q);
        } else {
            logl("Queries exhausted; learning complete.");
        }
    }

    private class TrivialLearnerCB implements Callback {
        TrivialLearnerCB() {
        }

        public boolean handleMessage(Message m) {
            logl("Teacher received notification of response.");
            logl("Sending next query...");
            return true;
        }
    }
    
}
