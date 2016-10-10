package edu.colorado.plv.droidStar;

import java.util.Queue;
import java.util.ArrayDeque;

import android.os.Handler.Callback;
import android.os.Message;
import android.content.Context;

import static edu.colorado.plv.droidStar.Static.*;

public class Transducer {

    private Context context;
    private SpeechRecognizerLP purpose;
    private Queue<String> inputs;
    private Queue<String> outputs;
    private int queryNum;
    private boolean responded;

    private static void logl(String m) {
        log("TRANSDUCER", m);
    }

    Transducer(Context c) {
        this.context = c;
        this.purpose = new SpeechRecognizerLP(context);
        this.inputs = new ArrayDeque();
        this.outputs = new ArrayDeque();
        this.queryNum = 0;
        this.responded = false;
    }

    private synchronized void advance() {
        outputs.clear();
        queryNum++;
        responded = false;
    }

    public synchronized void reset() {
        purpose.reset();
        inputs.clear();
        advance();
    }

    private void rollback() {
        purpose.reset();
        playAll(inputs);
        advance();
    }

    // TODO
    private void playAll(Queue<String> is) {
        // perform all inputs in sequence, discarding outputs
    }

    private synchronized void reportOutput(String o, Callback c, int q) {
        if (q == queryNum) {
            if (!responded) {
                responded = true;
                if (purpose.isError(o)) {
                    rollback();
    
                    c.handleMessage(quickMessage(REJECTED));
                } else {
                    outputs.add(o);
                    c.handleMessage(quickMessage(ACCEPTED));
                }
            } else {
                outputs.add(o);
            }
        } else {
            logl("Dropped stale output \"" + o + "\" with num " + q);
        }

    }

    public synchronized void query(Callback c, String i) {
        if (i == DELTA) {
            String output = outputs.poll();
            if (output == null) {
                c.handleMessage(quickMessage(BETA));
            } else {
                c.handleMessage(quickMessage(output));
            }
        } else {
            logl("Forwarding input \"" + i + "\" to LP...");
            advance();
            purpose.giveInput(new TransducerCB(c, queryNum), i);
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
