package edu.colorado.plv.droidStar;

import java.util.Queue;
import java.util.ArrayDeque;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;


import android.content.Intent;
import android.content.Context;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import static edu.colorado.plv.droidStar.Static.*;

public class Transducer {

    private Context context;
    private SpeechRecognizerLP purpose;
    private Queue<String> inputs;
    private Queue<String> outputs;
    private int queryNum;

    Transducer(Context c) {
        this.context = c;
        this.purpose = new SpeechRecognizerLP(context);
        this.inputs = new ArrayDeque();
        this.outputs = new ArrayDeque();
        this.queryNum = 0;
    }

    private synchronized void advance() {
        outputs.clear();
        queryNum++;
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
            if (outputs.isEmpty()) {
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
            log("Dropped stale output \"" + o + "\" with num " + q);
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
            log("Forwarding input \"" + i + "\" to LP...");
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
            log("Got back " + output + ", continuing...");
            reportOutput(output, outerCallback, onQueryNum);
            return true;
        }
    }
}
