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
    private Callback finalCallback;
    private Queue<String> remInputs;
    private Queue<String> inputTrace;
    private Queue<String> outputTrace;
    private BlockingQueue<String> outputBuff;

    Transducer(LearningPurpose p) {
        this.purpose = p;
    }

    public void query(Callback c, Queue<String> is) {
        purpose.reset();
        finalCallback = c;
        remInputs = new ArrayDeque(is);
        inputTrace = new ArrayDeque();
        outputTrace = new ArrayDeque();

        step();
    }

    private void step() {
        Queue<String> unresolvedInputs;
        int numDeltas;
        if (!remInputs.isEmpty()) {
            
            while (!remInputs.isEmpty()
                   && !remInputs.element().equals(DELTA)) {
                String input = remInputs.remove();
                unresolvedInputs.add(input);
                purpose.giveInput(outputBuff, input);
            }
            
            while (!remInputs.isEmpty()
                   && remInputs.element().equals(DELTA)) {
                numDeltas++;
            }

            new Thread(new OutputFetcher(unresolvedInputs, numDeltas)).start();
        } else {
            callback();
        }
    }

    // TODO: Call back to sender with ouput queue
    private void callback() {
        logl("Finished query with outputs:");
        for (String output : outputTrace) {
            logl(output);
        }
    }

    // TODO: Undo a bad input by reseting purpose and re-running all
    // successful inputs
    private void rollback() {
        
    }

    private class OutputFetcher implements Runnable {
        private Queue unresolvedInputs;
        private int numDeltas;

        OutputFetcher(Queue is, int d) {
            this.unresolvedInputs = is;
            this.numDeltas = d;
        }

        public void run() {

            // Start timer for Beta outputs; if this runs out, we'll
            // stop waiting for real outputs, assign beta outputs, and
            // return.
            new Thread(new BetaTimer(outputBuff)).start();

            String output = blockTakeOutput(outputBuff);

            if (!purpose.isError(output)) {
                
                // Record inputs as successful
                for (String input : unresolvedInputs) {
                    inputTrace.add(input);
                    outputTrace.add(ACCEPTED);
                }

                serveDeltas(output);
            } else {
                rollback();
                // TODO: might need to start a new BetaTimer?
                serveDeltas(blockTakeOutput(outputBuff));
            }
        }

        private void serveDeltas(String ouput) {
            if (numDeltas > 0) {
                if (output == BETA) {
                    // Output a beta for all waiting deltas
                    for (int i=0; i<numDeltas; i++) {
                        outputTrace.add(BETA);
                    }
                } else {
                    // Record this output and recurse for remaining
                    // deltas
                    outputTrace.add(output);
                    numDeltas--;
                    serveDeltas(blockTakeOutput(outputBuff));
                }
            }
            // if there are no deltas waiting, we don't collect any
            // outputs
        }

        private static String blockTakeOutput(BlockingQueue outputBuff) {
            String output;
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
