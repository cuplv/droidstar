package edu.colorado.plv.droidStar;

import java.util.List;
import java.util.ArrayList;
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

    private static void logl(String m) {
        log("TRANSDUCER", m);
    }    

    AsyncTransducer(Context c, LearningPurpose p) {
        this.purpose = p;
        this.mainContext = c;
    }

    public List<String> inputSet() {
        List<String> inputs = new ArrayList(purpose.inputSet());
        inputs.add(DELTA);

        return inputs;
    }

    public void membershipQuery(Runnable c, Queue<String> rs, Queue<String> is) {
        Handler mainHandler = new Handler(mainContext.getMainLooper());
        mainHandler.post(new RestartMain(c, rs, is));
    }

    public void mainMembershipQuery(Runnable c, Queue<String> rs, Queue<String> is) {
        finalCallback = c;
        results = rs;
        remInputs = new ArrayDeque(is);
        inputTrace = new ArrayDeque();
        outputTrace = new ArrayDeque();
        seen = new ArrayList();

        BlockingQueue<String> outputBuff = new LinkedBlockingQueue();

        purpose.reset(new OutputCB(outputBuff));

        logl("Running query: " + query2String(remInputs));
        step(outputBuff);
    }

    private void step(BlockingQueue<String> buffer) {

        if (!remInputs.isEmpty()) {
            
            String input = remInputs.remove();

            // If the input has already been determined as disabled,
            // we just put a "-" in the outputs and move on
            if (input.equals(REJECTED)) {
                inputTrace.add(input);
                outputTrace.add(REJECTED);
                step(buffer);
            } else {
                if (!input.equals(DELTA)) purpose.giveInput(input);
                int numDeltas = 0;
                while (!remInputs.isEmpty()
                       && remInputs.element().equals(DELTA)) {
                    remInputs.remove();
                    numDeltas++;
                }

                new Thread(new OutputFetcher(buffer, input, numDeltas)).start();
            }

            
        } else {
            callback();
        }
    }

    private void callback() {
        String out = query2String(outputTrace);
        logl("Finished query with outputs: " + out);

        // Put all the outputs where the caller expects them to be
        for (String o : outputTrace) {
            results.add(o);
        }
        // Notify caller that we're finished
        new Thread(finalCallback).start();
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

    private class BetaTimer implements Runnable {
        private int timeout;
        private Queue<String> buffer;

        BetaTimer(Queue<String> b, int t) {
            this.buffer = b;
            this.timeout = t;
        }

        public void run() {
            try {
                Thread.sleep(timeout);
            } catch (Exception e) {
                logl("Couldn't sleep");
            }
            maybePutBeta(buffer);
        }
    }


    // I'm really sorry about the messiness in this class.  Bugs kept
    // popping up and they needed very quick patches. -Nick
    private class OutputFetcher implements Runnable {
        private String input;
        private int numDeltas;
        private BlockingQueue<String> buffer;

        OutputFetcher(BlockingQueue<String> b, String i, int d) {
            this.input = i;
            this.numDeltas = d;
            this.buffer = b;
        }

        public void run() {

            // Start timer for Beta outputs; if this runs out, we'll
            // stop waiting for real outputs, assign beta outputs, and
            // return.
            betaTimer(buffer, purpose.betaTimeout());

            String output = blockTakeOutput(buffer);

            if (!purpose.isError(output)) {
                if (!input.equals(DELTA)) {
                    inputTrace.add(input);
                    outputTrace.add(ACCEPTED);
                } else {
                    numDeltas++;
                }
                serveDeltas(output);
            } else {
                ArrayDeque fixedQuery = new ArrayDeque();

                // Make a new query that replaces the failed input
                // we're looking at with a REJECTED note
                fixedQuery.addAll(inputTrace);
                fixedQuery.add(REJECTED);
                for (int i = 0; i<numDeltas; i++) {
                    fixedQuery.add(DELTA);
                }
                fixedQuery.addAll(remInputs);

                // And then start the query over again
                logl("Encountered invald input, restarting query...");
                Handler mainHandler = new Handler(mainContext.getMainLooper());
                mainHandler.post(new RestartMain(finalCallback, results, fixedQuery));
            }
        }

        private void betaTimer(Queue<String> b, int t) {
            logl("Starting beta timer...");
            BetaTimer timer = new BetaTimer(b,t);
            new Thread(timer).start();
        }

        private void serveDeltas(String o) {
            String output = o;
            
            seen.add(o);
            while (numDeltas > 0) {
                if (output == BETA) {
                    // Output a beta for all waiting deltas
                    for (int i=0; i<numDeltas; i++) {
                        inputTrace.add(DELTA);
                        outputTrace.add(BETA);
                    }
                    // break loop
                    numDeltas = 0;
                } else {
                    // Record this output and get a new output for the
                    // next delta
                    outputTrace.add(output);
                    numDeltas--;

                    if (numDeltas > 0) {
                        String nextOutput = null;
                        while (nextOutput == null) {
                            nextOutput = blockTakeOutput(buffer);
                            if (seen.contains(nextOutput)
                                && (!nextOutput.equals(BETA))) {
                                logl("Dropped duplicate output \""
                                     + nextOutput
                                     + "\"");
                                nextOutput = null;
                            }
                        }
                        seen.add(nextOutput);
                        output = nextOutput; // blockTakeOutput(buffer);
                    }
                }
            }
            // if there are no deltas waiting, we don't collect any
            // outputs

            // and now we return to the main thread
            Handler mainHandler = new Handler(mainContext.getMainLooper());
            mainHandler.post(new StepMain(buffer));
        }

        private String blockTakeOutput(BlockingQueue<String> buffer) {
            String output = new String();
            boolean done = false;
            while (!done) {
                try {
                    output = buffer.take();
                    done = true;
                } catch (Exception e) {
                    logl("Output take interrupted? Continuing...");
                }
            }
            logl("BlockTake returning with " + output + "...");
            return output;
        }

    }

    private class StepMain implements Runnable {
        private BlockingQueue<String> buffer;

        StepMain(BlockingQueue<String> b) {
            this.buffer = b;
        }

        public void run() {
            step(buffer);
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
        private Queue<String> buffer;

        OutputCB(Queue<String> b) {
            this.buffer = b;
        }

        public boolean handleMessage(Message m) {
            String output = readMessage(m);
            reportOutput(buffer, output);
            return true;
        }
    }
}    
