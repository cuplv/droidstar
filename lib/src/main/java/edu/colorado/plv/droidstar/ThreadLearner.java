package edu.colorado.plv.droidstar;

import java.util.Queue;
import java.lang.Runnable;

import static edu.colorado.plv.droidstar.Static.*;

public class ThreadLearner implements Runnable {
    private Queue<Queue<String>> queries;
    private Queue<String> current;
    private MealyTeacher teacher;

    private static void logl(String m) {
        log("LEARNER", m);
    }

    ThreadLearner(Queue<Queue<String>> qs, MealyTeacher t) {
        this.queries = qs;
        this.teacher = t;
    }

    public void run() {
        logl("---- Starting learn process...");
        
        for (Queue<String> query : queries) {
            Queue<String> result = teacher.membershipQuery(query);
            logl("Asked: " + query);
            logl("Answered: " + result);
        }
    }
    
}
