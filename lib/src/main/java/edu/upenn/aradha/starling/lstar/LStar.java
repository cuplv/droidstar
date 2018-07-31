package edu.upenn.aradha.starling.lstar;

import android.util.Pair;
import android.util.Log;

import java.lang.Integer;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
abstract class Signature {
    public static Signature emptySignature() {
        return new RealSignature();
    }
    public static Signature errorSignature() {
        return ErrorSignature.instance;
    }
    abstract public Signature clone();
}
class RealSignature extends Signature {
    public final List<List<Output>> results;
    protected RealSignature() {
        results = new ArrayList<List<Output>>();
    }
    private RealSignature(List<List<Output>> results) {
        this.results = results;
    }
    @Override
    public int hashCode() {
        return results.hashCode();
    }
    @Override
    public boolean equals(Object that) {
        if (that instanceof RealSignature)
            return this.results.equals(((RealSignature)that).results);
        return false;
    }
    public void add(List<Output> output) {
        this.results.add(output);
    }
    public int size() {
        return this.results.size();
    }
    @Override
    public Signature clone() {
        List<List<Output>> newResults = new ArrayList<>();
        newResults.addAll(this.results);
        return new RealSignature(newResults);
    }
    @Override
    public String toString() {
        return results.toString();
    }
}
class ErrorSignature extends Signature {
    public static ErrorSignature instance = new ErrorSignature();
    private ErrorSignature() {}
    @Override
    public Signature clone() {
        return this;
    }
    @Override
    public String toString() {
        return "ERROR";
    }
}

@SuppressWarnings("WeakerAccess")
class State {
    public final List<Input> representativeWord;
    public final Signature signature;
    private final Map<Input, Signature> successorSignatures;
    public final LStar parent;
    private final int id;
    public final boolean isErrorState;

    public State(
            List<Input> representativeWord,
            Signature signature,
            LStar parent,
            int id
    ) throws Exception {
        System.out.println("Initializing state " + id);
        this.id = id;
        this.representativeWord = representativeWord;
        this.parent = parent;
        this.signature = signature;
        this.isErrorState = this.signature instanceof ErrorSignature;
        if (this.isErrorState)
            System.out.println("Is error state: " + id);
        this.successorSignatures = new LinkedHashMap<>();
        initializeSuccessorSignatures();
        updateSignatures();
        System.out.println("Finished initializing state " + id);
        System.out.flush();
    }

    @Override
    public String toString() {
        return "STATE " + this.id;
    }

    public void initializeSuccessorSignatures() throws Exception {
        for (Input input : this.parent.inputs) {
            List<Input> word = new ArrayList<>();
            word.addAll(this.representativeWord);
            word.add(input);
            if (this.parent.mOracle.query(word).isError) {
                System.out.println("\tNo transition on " + input);
                this.successorSignatures.put(input, Signature.errorSignature());
            } else {
                System.out.println("\tTransition on " + input);
                this.successorSignatures.put(input, Signature.emptySignature());
            }
        }
    }
    public void updateSignatures() throws Exception {
        parent.completeSignature(this.representativeWord, this.signature);
        updateSuccessorSignatures();
    }
    private void updateSuccessorSignatures() throws Exception {
        for (Map.Entry<Input, Signature> kv : this.successorSignatures.entrySet()) {
            Input input = kv.getKey();
            Signature successorSignature = kv.getValue();
            List<Input> prefix = new ArrayList<>(this.representativeWord);
            prefix.add(input);
            parent.completeSignature(prefix, successorSignature);
        }
    }
    public boolean isEquivalent(Signature otherSignature) {
        return this.signature.equals(otherSignature);
    }
    public Iterable<Map.Entry<Input, Signature>> getTransitions() {
        return this.successorSignatures.entrySet();
    }
    public Signature transition(Input input) {
        return this.successorSignatures.get(input);
    }

    public Output getOutputOn(Input input, List<List<Input>> experiments) {
        if (this.signature instanceof ErrorSignature)
            return Query.Outputs.ERROR;

        List<Input> inputExpt = new ArrayList<>(); inputExpt.add(input);
        int i = experiments.indexOf(inputExpt);
        if (i == -1)
            return Query.Outputs.ERROR;
        List<Output> res = ((RealSignature)this.signature).results.get(i);
        assert (res.size() == 1);
        return res.get(0);
    }
}

@SuppressWarnings("WeakerAccess")
public class LStar {
    public final MembershipOracle mOracle;
    public final EquivalenceOracle eOracle;

    public final List<Input> inputs;
    public final List<List<Input>> experiments;
    public final List<State> states;

    private int numMQueries;
    private int numEQueries;
    private int numEMQueries;

    private FileWriter diagramFile;
    private FileWriter dataFile;
    private FileWriter logFile;

    public LStar(MembershipOracle mOracle, EquivalenceOracle eOracle, List<Input> inputs, FileWriter diagram, FileWriter data, FileWriter log) {
        this.mOracle = mOracle;
        this.eOracle = eOracle;
        this.inputs = inputs;
        this.experiments = new ArrayList<>();
        this.states = new ArrayList<>();
        this.diagramFile = diagram;
        this.dataFile = data;
        this.logFile = log;

        this.numMQueries = 0;
        this.numEQueries = 0;
        this.numEMQueries = 0;
    }

    public void run() throws Exception {
        long startTime = System.currentTimeMillis();
        // Initialize experiments
        for (Input input : this.inputs) {
            List<Input> word = new ArrayList<>();
            word.add(input);
            this.experiments.add(word);
        }

        // Initialize with epsilon state
        List<Input> epsilon = new ArrayList<>();
        this.states.add(new State(epsilon, Signature.emptySignature(), this, 0));

        while (true) {
            makeConsistent();
            try {
                this.printAutomaton(false);
                this.printAutomaton2("CHECK");
            } catch (Exception e) {
                Log.e("STARLING:Q", "what? :(");
                e.printStackTrace();
            }
            printTable();
            mOracle.printCache(logFile);
            List<Input> cex = checkEquivalence();
            if (cex == null) {
                System.out.println("Automaton is correct!");
                Log.d("STARLING:Q", "Automaton is correct!");
                try {
                    this.printAutomaton(true);
                    this.printAutomaton2("RESULT");
                } catch (Exception e) {
                    Log.e("STARLING:Q", "Error writing result diagram :(");
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                System.out.println("Run took " + (endTime - startTime) + " ms");
                try {
                    recordData(startTime, endTime);
                } catch (Exception e) {
                    Log.e("STARLING:Q", "Error writing result data :(");
                    e.printStackTrace();
                }
                return;
            }

            System.out.println("Got cex input: " + Utils.stringJoin(" ", cex));
            Log.d("DROIDSTAR:NG:CEX", Utils.stringJoin(",", cex));
            System.out.println("**************************************************");
            for (Map.Entry<String, Object> kv : eOracle.additionalInfo.entrySet()) {
                System.out.println("Equivalence INFO: " + kv.getKey() + " " + kv.getValue());
            }
            System.out.println("**************************************************");

            List<Input> newExperiment = analyzeCex(cex);
            System.out.println("Got distinguishing experiment: " + Utils.stringJoin(" ", newExperiment));

            this.addExperiment(newExperiment);
        }
    }

    private void recordData(long start, long end) throws Exception {
        dataFile.write("  date: " + start + "\n");
        dataFile.write("  time: " + (end - start) + "\n");
        dataFile.write("  mqueries: " + mOracle.getNumQueries() + "\n");
        dataFile.write("  rmqueries: " + mOracle.getNumRealQueries() + "\n");
        dataFile.write("  equeries: " + numEQueries + "\n");    
        dataFile.write("  emqueries: " + eOracle.getNumMQueries() + "\n");
        dataFile.write("  ermqueries: " + eOracle.getNumRealMQueries() + "\n");
    }

    private void printTable() {
        System.out.println("==========START TABLE=============");
        System.out.println(this.experiments);
        for (State state: this.states) {
            System.out.println(state);
            System.out.println(state.representativeWord + "|" + state.signature);
            for (Map.Entry<Input, Signature> kv : state.getTransitions()) {
                System.out.println(state + "." + kv.getKey() + " | " + kv.getValue());
            }
        }
        System.out.println("==========END TABLE===============");
    }

    private void printAutomaton(boolean correct) throws Exception {
        System.out.println("==========START AUTOMATON=============");
        System.out.println("digraph {\n");
        if (correct) diagramFile.write("digraph {\n");
        for (int from = 0; from < this.states.size(); from++) {
            // System.out.println(from);
            for (Map.Entry<Input, Signature> kv : this.states.get(from).getTransitions()) {
                Input input = kv.getKey();
                Signature signature = kv.getValue();
                int to = findState(signature);
                String label = input.toString();
                if (input.equals(Query.Inputs.DELTA))
                    label += "_" + this.states.get(from).getOutputOn(input, experiments);
                if (!this.states.get(to).isErrorState) {
                    System.out.println(from + " -> " + findState(signature) + "[label=" + label + "];");
                    if (correct) diagramFile.write("  " + from + " -> " + findState(signature) + "[label=" + label + "];\n");
                }
            }
        }
        System.out.println("}\n");
        if (correct) diagramFile.write("}\n");
        System.out.println("==========END AUTOMATON===============");
    }
    private void printAutomaton2(String kind) throws Exception {
        String dbgr = new String();
        // System.out.println("==========START AUTOMATON=============");
        // System.out.println("digraph {\n");
        // if (correct) diagramFile.write("digraph {\n");
        dbgr = dbgr + "digraph {";
        for (int from = 0; from < this.states.size(); from++) {
            // System.out.println(from);
            for (Map.Entry<Input, Signature> kv : this.states.get(from).getTransitions()) {
                Input input = kv.getKey();
                Signature signature = kv.getValue();
                int to = findState(signature);
                String label = input.toString();
                if (input.equals(Query.Inputs.DELTA))
                    label += "_" + this.states.get(from).getOutputOn(input, experiments);
                if ((!this.states.get(to).isErrorState)
                    && (!label.equals("delta_beta"))) {
                    // System.out.println(from + " -> " + findState(signature) + "[label=" + label + "];");
                    // if (correct) diagramFile.write("  " + from + " -> " + findState(signature) + "[label=" + label + "];\n");
                    String label2 = new String(label).replaceAll("delta_","cb_");
                    dbgr = dbgr + (from + " -> " + findState(signature) + "[label=" + label2 + "];");
                }
            }
        }
        // System.out.println("}\n");
        // if (correct) diagramFile.write("}\n");
        dbgr = dbgr + "}";
        Log.d("DROIDSTAR:NG:" + kind, dbgr);
        // System.out.println("==========END AUTOMATON===============");
    }

    public List<Input> checkEquivalence() throws Exception {
        Map<Pair<State, Input>, State> transitions =  this.buildTransitions();
        numEQueries++;
        Log.d("STARLING:Q", "Performing equivalence query...");
        Pair<List<Input>, Integer> info = eOracle.check(states, transitions);
        int ms = info.second.intValue();
        numEMQueries += ms;
        Log.d("STARLING:Q", "Finished equivalence query (costing " + ms + " mqueries)");
        return info.first;
    }
    public Map<Pair<State, Input>, State> buildTransitions() {
        Map<Pair<State, Input>, State> transitions = new LinkedHashMap<>();
        for (State fromState: this.states) {
            for (Map.Entry<Input, Signature> kv : fromState.getTransitions()) {
                Input input = kv.getKey();
                Signature successorSignature = kv.getValue();
                for (State toState : this.states)
                    if (toState.isEquivalent(successorSignature))
                        transitions.put(Pair.create(fromState, input), toState);
            }
        }
        return transitions;
    }
    public List<Input> analyzeCex(List<Input> cex) {
        if (eOracle.additionalInfo.containsKey("Distinguisher")) {
            Object distinguisher = eOracle.additionalInfo.get("Distinguisher");
            return (List<Input>)distinguisher;
        }
        throw new UnsupportedOperationException();
    }
    public void addExperiment(List<Input> newExperiment) throws Exception {
        if (this.experiments.contains(newExperiment)) {
            System.out.println("New experiment: " + newExperiment);
            System.out.println("Current experiments: " + this.experiments);
            throw new AssertionError("Adding duplicate experiment!");
        }
        this.experiments.add(newExperiment);
        for (State state: this.states)
            state.updateSignatures();
    }
    public void makeConsistent() throws Exception {
        Pair<State, Input> inconsistency;
        while ((inconsistency = maybeInconsistency()) != null) {
            List<Input> repWord = new ArrayList<>();
            repWord.addAll(inconsistency.first.representativeWord);
            repWord.add(inconsistency.second);

            Signature newSignature = inconsistency.first.transition(inconsistency.second).clone();
            System.out.println("Adding new state with id " + this.states.size());
            State newState = new State(repWord, newSignature, this, this.states.size());
            this.states.add(newState);
        }
    }

    // This should be optional.
    public Pair<State, Input> maybeInconsistency() {
        for (State state : this.states) {
            for (Map.Entry<Input, Signature> kv : state.getTransitions()) {
                Input input = kv.getKey();
                Signature signature = kv.getValue();
                if (!this.containsState(signature))
                    return Pair.create(state, input);
            }
        }
        return null;
    }
    public int findState(Signature signature) {
        for (int i = 0; i < this.states.size(); i++) {
            if (this.states.get(i).isEquivalent(signature))
                return i;
        }
        return -1;
    }
    public boolean containsState(Signature signature) {
        return findState(signature) != -1;
    }
    public void completeSignature(List<Input> prefix, Signature signature) throws Exception {
        if (signature instanceof ErrorSignature)
            return;
        RealSignature rSignature = (RealSignature) signature;
        int current = rSignature.size();
        int all = this.experiments.size();
        for (int i = current; i < all; i++) {
            List<Output> output = this.mOracle.query(prefix, this.experiments.get(i));
            numMQueries++;
            rSignature.add(output);
        }
    }
}
