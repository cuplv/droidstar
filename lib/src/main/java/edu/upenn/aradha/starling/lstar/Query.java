package edu.upenn.aradha.starling.lstar;

import android.os.Looper;
import android.util.StringBuilderPrinter;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

import edu.colorado.plv.droidstar.Static;

public abstract class Query {
    public static class Inputs {
        public static final Input DELTA = new Input(Static.DELTA);
    }
    public static class Outputs {
        public static final Output ERROR = new Output(Static.REJECTED);
        public static final Output EPSILON = new Output(Static.BETA);
    }

    protected final Queue<Symbol> trace;
    private final List<Input> inputs;
    private boolean error;

    boolean isError() {
        return error;
    }

    public Query(List<Input> word) {
        this.trace = new ConcurrentLinkedQueue<>();
        this.error = false;
        this.inputs = Collections.unmodifiableList(word);
    }
    public abstract void runCommand(Input command) throws Exception;
    public abstract void finish();

    public static String looperDump() {
        StringBuilder s = new StringBuilder();
        StringBuilderPrinter sp = new StringBuilderPrinter(s);
        Looper.getMainLooper().dump(sp, "LOOPER DUMP: ");
        return s.toString();
    }

    public Queue<Symbol> run() {
        for (Input command: this.inputs) {
            try {
                this.trace.add(command);
                if (command.equals(Inputs.DELTA)) {
                    int size = this.trace.size();
                    sleep(3000);
                    int newSize = this.trace.size();
                    int numOutputs = newSize - size;
                    if (numOutputs != 1)
                        throw new Exception("Delta failure");
                } else {
                    runCommand(command);
                }
            } catch(InterruptedException e) {
                if (command.equals(Inputs.DELTA)) {
                    // So, waiting failed for some stupid reason
                    // Probably some call back returned.
                    // Screw it!
                    System.err.println("Interrupted while DELTA");
                }
                this.error = true;
                break;
            } catch(Exception e) {
                // System.out.println(e.getMessage());
                this.error = true;
                break;
            }
        }
        try { sleep(1000); } catch (InterruptedException e) {}

        if (this.trace.contains(Outputs.ERROR))
            this.error = true;

        this.finish();

        return this.trace;
    }
}
