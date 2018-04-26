// TODO: Replace Query static values with LearningPurpose static
// values

package edu.upenn.aradha.starling.lstar;

import android.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.io.FileWriter;

@SuppressWarnings("WeakerAccess")
public abstract class MembershipOracle {
    public final HashMap<List<Input>, Result> cache;
    private int numQueries;
    private int numRealQueries;

    protected MembershipOracle() {
        cache = new HashMap<>();
        numQueries = 0;
        numRealQueries = 0;
    }

    public void printCache(FileWriter logFile) {
        System.out.println("==========START CACHE=============");
        for (Map.Entry<List<Input>, Result> kv : this.cache.entrySet()) {
            String logLine = kv.getKey() + " ==> " + kv.getValue();
            System.out.println(logLine);
            try{logFile.write(logLine + "\n");}
            catch (Exception e) {
                Log.e("STARLING:Q","Write failure on log file...");
                e.printStackTrace();
            }

        }
        System.out.println("============END CACHE=============");
    }

    public abstract class Result {
        public final boolean isError;

        public Result(boolean isError) {
            this.isError = isError;
        }

        @Override
        public String toString() {
            if (this.isError)
                return "ERROR";
            else
                return ((Trace)this).trace.toString();
        }
    }

    private static Error instance = null;
    private Error getErrorInstance() {
        if (MembershipOracle.instance == null)
            MembershipOracle.instance = new Error();
        return MembershipOracle.instance;
    }
    public final class Error extends Result {
        private Error() {
            super(true);
        }
    }
    public final class Trace extends Result {
        public final List<Symbol> trace;
        public final List<Input> inputs;
        public final List<Output> outputs;

        private Pair<List<Input>, List<Output>> getMMTrace(int inputLength) {
            List<Input> inputs = new ArrayList<>();
            List<Output> outputs = new ArrayList<>();
            int tracePosition = 0;
            for (int _ = 0; _ < inputLength; _++) {
                if (trace.get(tracePosition).equals(Query.Inputs.DELTA)) {
                    if (tracePosition == trace.size() - 1 || !trace.get(tracePosition+1).isOutput)
                        throw new AssertionError("Query returned non-MM trace 1: " + trace);

                    inputs.add((Input)trace.get(tracePosition));
                    outputs.add((Output)trace.get(tracePosition+1));
                    tracePosition += 2;
                } else if (trace.get(tracePosition).isInput) {
                    inputs.add((Input)trace.get(tracePosition));
                    outputs.add(Query.Outputs.EPSILON);
                    tracePosition += 1;
                } else {
                    throw new AssertionError("Query returned non-MM trace 2: " + trace);
                }
            }
            return Pair.create(inputs, outputs);
        }
        
        public Trace(List<Input> is, List<Output> os) {
            super(false);

            this.trace = new ArrayList();
            for (int i = 0; i < is.size(); i++) {
                this.trace.add(is.get(i));
                this.trace.add(os.get(i));
            }

            this.inputs = is;
            this.outputs = os;
        }
            
    }

    public int getNumQueries() {
        return numQueries;
    }

    public int getNumRealQueries() {
        return numRealQueries;
    }

    public Result query(final List<Input> word) throws Exception {
        Result cached = this.cache.get(word);
        if (cached != null)
            return cached;
        for (int i = 1; i < word.size(); i++) {
            if (cache.get(word.subList(0, i)) instanceof MembershipOracle.Error)
                return this.getErrorInstance();
        }

        Queue<Output> outputs = runQuery(word);
        numRealQueries++;

        Result result;
        if (outputs == null) {
            result = this.getErrorInstance();
        } else {

            List<Output> trace = new ArrayList<>();
            trace.addAll(outputs);

            if (trace.contains(Query.Outputs.ERROR)) {
                result = this.getErrorInstance();
            } else {
                result = new Trace(word, trace);
            }

            // Trace traceResult = new Trace(word, trace);
            // if (!traceResult.inputs.equals(word))
            //     throw new Exception();
            // result = traceResult;
        }
        this.cache.put(word, result);
        return result;
    }
    public List<Output> query(List<Input> prefix, List<Input> suffix) throws Exception {
        numQueries++;
        List<Input> word = new ArrayList<>(prefix);
        word.addAll(suffix);

        Result result = query(word);
        if (result instanceof Error) {
            List<Output> error = new ArrayList<>();
            for (int i = 0; i < suffix.size(); i++)
                error.add(Query.Outputs.ERROR);
            return error;
        } else if (result instanceof Trace) {
            List<Output> outputs = ((Trace) result).outputs;
            return outputs.subList(prefix.size(), outputs.size());
        } else {
            throw new AssertionError("Unknown subclass of MembershipOracle.Result");
        }
    }

    public abstract Queue<Output> runQuery(List<Input> word) throws AssertionError;
}
