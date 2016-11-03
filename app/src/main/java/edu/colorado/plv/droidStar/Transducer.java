package edu.colorado.plv.droidStar;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.concurrent.CountDownLatch;
import java.lang.Runnable;

import android.content.Context;

import static edu.colorado.plv.droidStar.Static.*;

public class Transducer implements MealyTeacher {
    private AsyncTransducer at;

    public Transducer(Context c, LearningPurpose p) {
        this.at = new AsyncTransducer(c,p);
    }

    public List<String> inputSet() { return at.inputSet(); }

    public Queue<String> membershipQuery(Queue<String> inputs) {
        Queue<String> results = new ArrayDeque();
        CountDownLatch latch = new CountDownLatch(1);
        Runnable q = new AsyncQueryR(latch);

        at.membershipQuery(q, results, inputs);

        boolean done = false;
        while (!done) {
            try {
                latch.await();
                done = true;
            } catch (Exception e) {
                log("SYNC_TRANSDUCER", "Interrupted?");
            }
        }
            
        return results;
    }

    private class AsyncQueryR implements Runnable {
        private CountDownLatch latch;

        AsyncQueryR(CountDownLatch l) {
            this.latch = l;
        }

        public void run() {
            latch.countDown();
        }
    }
}
            
