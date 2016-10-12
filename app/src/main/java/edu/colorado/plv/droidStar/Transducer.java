package edu.colorado.plv.droidStar;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;
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
    private Callback finalCallback;
    private Queue<String> remInputs;
    private Queue<String> inputTrace;
    private Queue<String> outputTrace;
    private BlockingQueue<String> outputBuff;

    private static void logl(String m) {
        log("TRANSDUCER", m);
    }    

    Transducer(LearningPurpose p) {
        this.purpose = p;
    }

    public List<String> inputs() {
        List<String> inputs = new ArrayList(purpose.inputs());
        inputs.add(DELTA);

        return inputs;
    }

    public void membershipQuery(Callback c, Queue<String> is) {
        purpose.reset();
        finalCallback = c;
        remInputs = new ArrayDeque(is);
        inputTrace = new ArrayDeque();
        outputTrace = new ArrayDeque();
        outputBuff = new LinkedBlockingQueue();

        logl("Running query: " + query2String(remInputs));
        step();
    }

    private void step() {
        
        
        if (!remInputs.isEmpty()) {
            
            String input = remInputs.remove();

            // If the input has already been determined as disabled,
            // we just put a "-" in the outputs and move on
            if (input.equals(REJECTED)) {
                inputTrace.add(input);
                outputTrace.add(REJECTED);
                step();
            } else {
                purpose.giveInput(new OutputCB(outputBuff), input);
                int numDeltas = 0;
                while (!remInputs.isEmpty()
                       && remInputs.element().equals(DELTA)) {
                    numDeltas++;
                }

                new Thread(new OutputFetcher(input, numDeltas)).start();
            }

            
        } else {
            callback();
        }
    }

    // TODO: Call back to sender with ouput queue
    private void callback() {
        String o = query2String(outputTrace);
        logl("Finished query with outputs: " + o);
        finalCallback.handleMessage(new Message());
    }

    private synchronized void reportOutput(Queue<String> b, String o) {
        String output = new String(o);
        logl("Purpose returned " + output);
        b.add(output);
    }

    private synchronized void maybePutBeta(Queue<String> b) {
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


    private class OutputFetcher implements Runnable {
        private String input;
        private int numDeltas;

        OutputFetcher(String i, int d) {
            this.input = i;
            this.numDeltas = d;
        }

        public void run() {

            // Start timer for Beta outputs; if this runs out, we'll
            // stop waiting for real outputs, assign beta outputs, and
            // return.
            betaTimer(outputBuff, purpose.betaTimeout());

            String output = blockTakeOutput(outputBuff);

            if (!purpose.isError(output)) {
                inputTrace.add(input);
                outputTrace.add(ACCEPTED);
                serveDeltas(output);
            } else {
                ArrayDeque fixedQuery = new ArrayDeque();

                // Make a new query that replaces the failed input
                // we're looking at with a REJECTED note
                fixedQuery.addAll(inputTrace);
                fixedQuery.add(REJECTED);
                fixedQuery.addAll(remInputs);

                // And then start the query over again
                logl("Encountered invald input, restarting query...");
                membershipQuery(finalCallback, fixedQuery);
            }
        }

        private void betaTimer(Queue<String> b, int t) {
            logl("Starting beta timer...");
            BetaTimer timer = new BetaTimer(b,t);
            new Thread(timer).start();
        }

        private void serveDeltas(String o) {
            String output = o;
            while (numDeltas > 0) {
                if (output == BETA) {
                    // Output a beta for all waiting deltas
                    for (int i=0; i<numDeltas; i++) {
                        outputTrace.add(BETA);
                    }
                    // break loop
                    numDeltas = 0;
                } else {
                    // Record this output and get a new output for the
                    // next delta
                    outputTrace.add(output);
                    numDeltas--;
                    output = blockTakeOutput(outputBuff);
                }
            }
            // if there are no deltas waiting, we don't collect any
            // outputs
        }

        private String blockTakeOutput(BlockingQueue<String> outputBuff) {
            String output = new String();
            boolean done = false;
            while (!done) {
                try {
                    output = outputBuff.take();
                    done = true;
                } catch (Exception e) {
                    logl("Output take interrupted? Continuing...");
                }
            }
            return output;
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

// public class Transducer implements MealyTeacher {

//     private LearningPurpose purpose;
//     private BlockingQueue<String> outputsSeen;
//     private boolean responded;

//     private static void logl(String m) {
//         log("TRANSDUCER", m);
//     }

//     Transducer(LearningPurpose p) {
//         this.purpose = p;
//         this.outputsSeen = new LinkedBlockingQueue();
//         this.responded = false;
//     }

//     public List<String> inputs() {
//         List<String> is = new ArrayList(purpose.inputs());
//         is.add(DELTA);
//         return is;
//     }

//     private synchronized void advance() {
//         outputsSeen = new LinkedBlockingQueue<String>();
//         responded = false;
//     }

//     public synchronized void reset() {
//         advance();
//         purpose.reset();
//     }

//     private void rollback() {
//         purpose.reset();
//         playAll(inputTrace);
//         advance();
//     }

//     // TODO
//     private void playAll(BlockingQueue<String> is) {
//         // perform inputTrace in sequence, discarding outputs
//     }

//     private synchronized void reportOutput(String o, Callback c, int q) {
//         if (q == queryNum) {
//             if (!responded) {
//                 responded = true;
//                 if (purpose.isError(o)) {
//                     rollback();
    
//                     c.handleMessage(quickMessage(REJECTED));
//                 } else {
//                     outputsSeen.add(o);
//                     c.handleMessage(quickMessage(ACCEPTED));
//                 }
//             } else {
//                 outputsSeen.add(o);
//             }
//         } else {
//             logl("Dropped stale output \"" + o + "\" with num " + q + ", not " + queryNum);
//         }

//     }

//     public synchronized void query(Callback c, String i) {
//         if (i == DELTA) {
//             new Thread(new BetaTimer(purpose.betaTimeout())).start();
//             new Thread(new DeltaFetcher(c)).start();
//         } else {
//             logl("Forwarding input \"" + i + "\" to LP...");
//             advance();
//             new Thread(new BetaTimer(purpose.betaTimeout())).start();
//             purpose.giveInput(new TransducerCB(c, outputsSeen), i);
//         }
//     }

//     private synchronized void maybePutBeta() {
//         if (outputsSeen.isEmpty()) {
//             outputsSeen.add(BETA);
//         }
//     }

//     private class BetaTimer implements Runnable {
//         private int timeout;

//         BetaTimer(int t) {
//             this.timeout = t;
//         }

//         public void run() {
//             try {
//                 Thread.sleep(timeout);
//             } catch (Exception e) {
//                 logl("Couldn't sleep");
//             }
//             maybePutBeta();
//         }
//     }

//     private class TransducerCB implements Callback {
//         private Callback finalCallback;
//         private Queue<String> remInputs;
//         private Queue<String> outputs;
//         private BlockingQueue<String> outputBuff;

//         TransducerCB(Callback c, Queue<String> is) {
//             this.finalCallback = c;
//             this.remInputs = new ArrayDeque(is);
//             this.outputs = new ArrayDeque();
//             this.outputBuff = new LinkedBlockingQueue<String>();
//         }

//         TransducerCB(TransducerCB prev, String o) {
//             this.finalCallback = prev.finalCallback;
//             this.remInputs = 


//     private class TransducerCB implements Callback {
//         private Queue<String> outputLog;
//         private Callback outerCallback;

//         TransducerCB(Callback c, Queue<String> q) {
//             this.outputLog = q;
//             this.outerCallback = c;
//         }

//         public boolean handleMessage(Message m) {
//             String output = readMessage(m);
//             logl("Got back " + output + ", continuing...");
//             reportOutput(output, outputLog, outerCallback);
//             return true;
//         }
//     }
// }
