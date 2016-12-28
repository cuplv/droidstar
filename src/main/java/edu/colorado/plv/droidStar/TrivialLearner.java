package edu.colorado.plv.droidStar;

import java.util.ArrayDeque;
import java.util.Queue;

import java.lang.Runnable;

import static edu.colorado.plv.droidStar.Static.*;

public class TrivialLearner {
    private Queue<Queue<String>> queries;
    private Queue<String> current;
    private AsyncMealyTeacher teacher;

    private static void logl(String m) {
        log("LEARNER", m);
    }

    TrivialLearner(Queue<Queue<String>> qs, AsyncMealyTeacher t) {
        this.queries = qs;
        this.teacher = t;
    }

    public void learn() {
        logl("---- Starting learn process...");
        nextQuery();
    }

    private synchronized void nextQuery() {
        Queue<String> q = queries.poll();
        Queue<String> res = new ArrayDeque();
        Runnable r = new TrivialLearnerR(res);
        if (q != null) {
            logl("Asking query: " + query2String(q));
            teacher.membershipQuery(r, res, q);
        } else {
            logl("Queries exhausted; learning complete.");
        }
    }

    private class TrivialLearnerR implements Runnable {
        private Queue<String> results;

        TrivialLearnerR(Queue<String> r) {
            this.results = r;
        }

        public void run() {
            logl("Teacher received notification of response.");
            logl("Response was " + query2String(results));
            logl("Sending next query...");
            nextQuery();
        }
    }
    
}
