package edu.colorado.plv.droidStar;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Thread;
import java.lang.Runnable;

import android.os.Message;
import android.os.Handler;
import android.os.Handler.Callback;
import android.content.Context;

import static edu.colorado.plv.droidStar.Static.*;

public class AsyncTransducer implements AsyncMealyTeacher {
    private LearningPurpose purpose;
    private Runnable finalCallback;
    private Queue<String> results;
    private Queue<String> remInputs;
    private Queue<String> inputTrace;
    private Queue<String> outputTrace;
    private Context mainContext;
    List<String> seen;
    private int querySize;
    BlockingQueue<String> outputBuff;

    private Map<List<String>,List<String>> ndcache;

    private static void logl(String m) {
        log("TRANSDUCER", m);
    }

    private static void logq(String m) {
        log("TRANSDUCER:Q", m);
    }

    AsyncTransducer(Context c, LearningPurpose p) {
        this.purpose = p;
        this.mainContext = c;
        this.ndcache = new Hashtable();
    }

    public List<String> inputSet() {
        return purpose.inputSet();
    }

    public void membershipQuery(Runnable c, Queue<String> rs, Queue<String> is) {
        Handler mainHandler = new Handler(mainContext.getMainLooper());
        mainHandler.post(new RestartMain(c, rs, is));
        querySize = is.size();
        logq("---");
        logq("IN  >>> " + query2String(is));
    }

    private void timeout(int t) {
        if (t > 0) {
            try {Thread.sleep(t);}
            catch (Exception e) {logq("Interrupted during timeout!");}
        }
    }

    public void mainMembershipQuery(Runnable c, Queue<String> rs, Queue<String> is) {
        finalCallback = c;
        results = rs;
        remInputs = new ArrayDeque(is);
        inputTrace = new ArrayDeque();
        outputTrace = new ArrayDeque();
        seen = new ArrayList();

        if (purpose.validQuery(remInputs)) {
            purpose.reset(new OutputCB());
            timeout(purpose.postResetTimeout());
            logl("Running query: " + query2String(remInputs));
            step();
        } else {
            returnWithError();
        }
    }

    // After checking that there are still inputs to go and that we
    // haven't missed an error that should be attributed to a previous
    // input, we continue with the next input.
    private void step() {
        if (!remInputs.isEmpty()) {
            nextInput();
        } else {
            returnSuccessfully();
        }
    }

    // Pop the next input + deltas from the query and try to run it
    private void nextInput() {

        // First flush outputs produced by the previous block of
        // inputs
        outputBuff = new LinkedBlockingQueue();

        // Now gather the inputs and deltas in this block
        Queue<String> inputs = popInputs();
        int numDeltas = popDeltas();

        try {
            performInputs(inputs);
            fetchOutputs(numDeltas);
        } catch (Exception e) {
            logq("Input Exception; error");
            returnWithError();
        }
    }

    private void performInputs(Queue<String> inputs) throws Exception {
        for (String input : inputs) {
            inputTrace.add(input);
            outputTrace.add(ACCEPTED);
        }
        for (String input : inputs) {
            purpose.giveInput(input);
        }
    }

    private void fetchOutputs(int numDeltas) {
        new Thread(new OutputFetcher(numDeltas)).start();
    }

    private Queue<String> popInputs() {
        Queue<String> inputs = new ArrayDeque();
        while (!remInputs.isEmpty()
               && !remInputs.element().equals(DELTA)) {
            inputs.add(remInputs.remove());
        }
        return inputs;
    }

    private int popDeltas() {
        int ds = 0;
        while (!remInputs.isEmpty()
               && remInputs.element().equals(DELTA)) {
            remInputs.remove();
            ds++;
        }
        return ds;
    }

    private void returnSuccessfully() {
        logq("OUT >>> " + query2String(outputTrace));
        for (String o : outputTrace) {
            results.add(o);
        }
        returnToCaller();
    }

    private void returnWithError() {
        logq("OUT >>> " + query2String(outputTrace) + " (error)");
        for (String o : outputTrace) {
            results.add(REJECTED);
        }
        returnToCaller();
    }

    private void returnToCaller() {
        // Notify caller that we're finished

        // Also check the query and result for non-determinism
        checkND();

        new Thread(finalCallback).start();
    }

    /* check for non-determinism 
       
       This will replace the results list with a null value if the 
       output is found to be non-deterministic.  The details of the
       error are printed to the log.
    */
    private void checkND() {
        List<List<String>> ips = getPrefixes(new ArrayList(inputTrace));
        List<List<String>> ops = getPrefixes(new ArrayList(ouputTrace));

        for (List<String> p : ips) {
            List<String> o = ndcache.get(p);
            if (o == null) {
                ndcache.put(p,o);
            } else if (! o.equals(new ArrayList(outputTrace))) {
                logq("!!!! Non-determinism detected, terminating");
                logq("ND Prefix: " + query2String(new ArrayDeque(p)));
                logq("First result: " + query2String(new ArrayDeque(o)));
                logq("Last result: " + query2String(outputTrace));
                results = null;
            }
        }
                
    }

    /* return all prefixes of a list */
    private List<List<String>> getPrefixes(List<String> trace) {
        List<List<String>> prefixes = new ArrayList();

        for (int i=0; i < trace.size(); i++) {
            prefixes.add(trace.subList(0, i+1));
        }

        return prefixes;
    }

    private void reportOutput(Queue<String> b, String o) {
        String output = new String(o);
        b.add(output);
    }

    private void maybePutBeta(Queue<String> b) {
        if (b.isEmpty()) {
            logl("Time is up, reporting beta.");
            b.add(BETA);
        }
    }

    // I'm really sorry about the messiness in this class.  Bugs kept
    // popping up and they needed very quick patches. -Nick
    private class OutputFetcher implements Runnable {
        private int numDeltas;

        OutputFetcher(int d) {
            this.numDeltas = d;
        }


        public void run() {
            serveDeltas();
        }

        private void deltaRecord(List<String> seen, String o) {
            if (!purpose.dropDoubleOutput()
                || o.equals(BETA)
                || !seen.contains(o)) {

                inputTrace.add(DELTA);
                outputTrace.add(o);
                seen.add(o);
                numDeltas--;
            } else {
                logq("        (Dropped duplicate output \"" + o + "\")");
            }
        }

        private void betaShort(List<String> seen) {
            while (numDeltas > 0) {
                deltaRecord(seen, BETA);
            }
        }

        private void serveDeltas() {
            List<String> seen = new ArrayList();
            boolean done = false;
            boolean returned = false;
            String output;

            if (numDeltas > 0) {
                while (numDeltas > 0 && !returned) {
                    output = blockTakeOutput(outputBuff);
                    
                    if (output.equals(BETA)) {
                        betaShort(seen);
                    } else if (purpose.isError(output)) {
                        logq("delta caught error");
                        returnWithError();
                        returned = true;
                    } else {
                        deltaRecord(seen, output);
                    }
                }
                
                // go back to the main thread for the next step, if we
                // haven't already exited
                if (!returned) stepAgain();
            } else {
                if (noMissedErrors()) {
                    stepAgain();
                } else {
                    logq("post-delta caught error");
                    returnWithError();
                }
            }
                
        }

        private String blockTakeOutput(BlockingQueue<String> buffer) {
            String output = new String();
            boolean done = false;
            while (!done) {
                try {
                    output = buffer.poll(purpose.betaTimeout(),
                                         TimeUnit.MILLISECONDS);
                    done = true;
                } catch (Exception e) {
                    logl("Output take interrupted? Continuing...");
                }
            }
            if (output == null) output = BETA;
            
            logl("BlockTake returning with " + output + "...");
            return output;
        }

        // If the outputBuff has been previously instantiated, check to
        // see if it contains any "-" outputs
        private boolean noMissedErrors() {
            timeout(purpose.safetyTimeout());
            timeout(purpose.betaTimeout());

            boolean errors = false;
            if (outputBuff != null) {
                for (String output : outputBuff) {
                    errors = errors || purpose.isError(output);
                }
            }
            return !errors;
        }

    }

    private void stepAgain() {
        Handler mainHandler = new Handler(mainContext.getMainLooper());
        mainHandler.post(new StepMain());
    }

    private class StepMain implements Runnable {
        public void run() {
            step();
        }
    }

    private class RestartMain implements Runnable {
        private Runnable callback;
        private Queue<String> results;
        private Queue<String> newQuery;

        RestartMain(Runnable c, Queue<String> r, Queue<String> q) {
            this.callback = c;
            this.results = r;
            this.newQuery = q;
        }

        public void run() {
            mainMembershipQuery(callback, results, newQuery);
        }
    }

    private class OutputCB implements Callback {
        public boolean handleMessage(Message m) {
            String output = readMessage(m);
            reportOutput(outputBuff, output);
            return true;
        }
    }
}    
