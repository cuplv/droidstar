package edu.colorado.plv.droidStar;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Thread;
import java.lang.Runnable;

import android.os.Handler.Callback;
import android.os.Message;
import android.content.Context;

import static edu.colorado.plv.droidStar.Static.*;

public class Transducer implements MealyTeacher {

    private LearningPurpose purpose;
    private BlockingQueue<String> inputTrace;
    private BlockingQueue<String> outputsSeen;
    private int queryNum;
    private boolean responded;

    private static void logl(String m) {
        log("TRANSDUCER", m);
    }

    Transducer(LearningPurpose p) {
        this.purpose = p;
        this.inputTrace = new LinkedBlockingQueue();
        this.outputsSeen = new LinkedBlockingQueue();
        this.queryNum = 0;
        this.responded = false;
    }

    public List<String> inputs() {
        List<String> is = new ArrayList(purpose.inputs());
        is.add(DELTA);
        return is;
    }

    private synchronized void advance() {
        outputsSeen.clear();
        queryNum++;
        responded = false;
    }

    public synchronized void reset() {
        purpose.reset();
        inputTrace.clear();
        advance();
    }

    private void rollback() {
        purpose.reset();
        playAll(inputTrace);
        advance();
    }

    // TODO
    private void playAll(BlockingQueue<String> is) {
        // perform inputTrace in sequence, discarding outputs
    }

    private synchronized void reportOutput(String o, Callback c, int q) {
        if (q == queryNum) {
            if (!responded) {
                responded = true;
                if (purpose.isError(o)) {
                    rollback();
    
                    c.handleMessage(quickMessage(REJECTED));
                } else {
                    outputsSeen.add(o);
                    c.handleMessage(quickMessage(ACCEPTED));
                }
            } else {
                outputsSeen.add(o);
            }
        } else {
            logl("Dropped stale output \"" + o + "\" with num " + q + ", not " + queryNum);
        }

    }

    public synchronized void query(Callback c, String i) {
        if (i == DELTA) {
            new Thread(new BetaTimer(purpose.betaTimeout())).start();
            new Thread(new DeltaFetcher(c)).start();
        } else {
            logl("Forwarding input \"" + i + "\" to LP...");
            advance();
            new Thread(new BetaTimer(purpose.betaTimeout())).start();
            purpose.giveInput(new TransducerCB(c, queryNum), i);
        }
    }

    private synchronized void maybePutBeta() {
        if (outputsSeen.isEmpty()) {
            outputsSeen.add(BETA);
        }
    }

    private class BetaTimer implements Runnable {
        private int timeout;

        BetaTimer(int t) {
            this.timeout = t;
        }

        public void run() {
            try {
                Thread.sleep(timeout);
            } catch (Exception e) {
                logl("Couldn't sleep");
            }
            maybePutBeta();
        }
    }

    private class DeltaFetcher implements Runnable {
        private Callback outerCallback;

        DeltaFetcher(Callback c) {
            this.outerCallback = c;
        }

        public void run() {
            boolean done = false;
            while (!done) {
                try {
                    outerCallback.handleMessage
                        (quickMessage(outputsSeen.take()));
                    Thread.sleep(100);
                    done = true;
                } catch (Exception e) {
                    logl("Couldn't block?");
                }
            }
        }
    }

    private class TransducerCB implements Callback {
        private int onQueryNum;
        private Callback outerCallback;

        TransducerCB(Callback c, int n) {
            this.onQueryNum = n;
            this.outerCallback = c;
        }

        public boolean handleMessage(Message m) {
            String output = readMessage(m);
            logl("Got back " + output + ", continuing...");
            reportOutput(output, outerCallback, onQueryNum);
            return true;
        }
    }
}
