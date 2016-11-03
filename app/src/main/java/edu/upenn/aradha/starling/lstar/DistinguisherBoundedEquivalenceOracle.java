package edu.upenn.aradha.starling.lstar;

import android.util.Pair;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class DistinguisherBoundedEquivalenceOracle extends EquivalenceOracle {
    public final MembershipOracle mOracle;
    public final List<Input> inputs;
    public final int bound;
    public final List<List<Input>> distinguishers;
    int numMQueries;
    int numRealMQueries;
    int numEQueries;

    public int getNumRealMQueries() {
        return numRealMQueries;
    }

    public int getNumMQueries() {
        return numMQueries;
    }

    public int getNumEQueries() {
        return numEQueries;
    }

    public DistinguisherBoundedEquivalenceOracle(
            MembershipOracle mOracle,
            List<Input> inputs,
            int bound) {
        this.mOracle = mOracle;
        this.numMQueries = 0;
        this.numRealMQueries = 0;
        this.numEQueries = 0;
        this.bound = bound;
        this.inputs = inputs;
        this.distinguishers = new ArrayList<>();
        for (int length = 1; length <= bound; length++)
            this.distinguishers.addAll(Utils.wordsOfLength(this.inputs, length));
        System.out.println("Found " + this.distinguishers.size() + " distinguishers!");

    }

    private Pair<List<Input>, Pair<List<Output>, List<Output>>>
    distinguish(List<Input> rep1, List<Input> rep2) throws Exception {
        // For each string w of length up to bound
        // Check that rep1.w has same output as rep2.w
        for (List<Input> suffix : this.distinguishers) {
            List<Output> out1 = mOracle.query(rep1, suffix);
            numMQueries++;
            List<Output> out2 = mOracle.query(rep2, suffix);
            numMQueries++;
            if (!out1.equals(out2))
                return Pair.create(suffix, Pair.create(out1, out2));
        }
        return null;
    }

    // This method should probably be generic based on State type
    @Override
    public Pair<List<Input>, Integer> doCheck(List<State> states, Map<Pair<State, Input>, State> transitions)
    throws Exception {
        int currFreshMQueries = mOracle.cache.size();
        StringBuilder timeLog = new StringBuilder();
        for (Map.Entry<Pair<State, Input>, State> transition: transitions.entrySet()) {
            long startTime = System.currentTimeMillis();
            State from = transition.getKey().first;
            Input symbol = transition.getKey().second;
            State to = transition.getValue();

            List<Input> rep1 = new ArrayList<>();
            rep1.addAll(from.representativeWord);
            rep1.add(symbol);

            List<Input> rep2 = new ArrayList<>();
            rep2.addAll(to.representativeWord);

            // These are definitely cached by mOracle and shouldn't be expensive
            MembershipOracle.Result out1 = mOracle.query(rep1);
            numMQueries++;
            MembershipOracle.Result out2 = mOracle.query(rep2);
            numMQueries++;

            if (out1.isError && out2.isError) {
                System.out.println("Error Testing " + from + " --" + symbol + " -> " + to);
            } else if (rep1.equals(rep2)) {
                System.out.println("Equal Testing " + from + " --" + symbol + " -> " + to);
            } else if (out2.isError && symbol.equals(Query.Inputs.DELTA)) {
                System.out.println("Delta Error Testing " + from + " --" + symbol + " -> " + to);
            } else {
                System.out.println("Full Testing " + from + " --" + symbol + " -> " + to);
                Pair<List<Input>, Pair<List<Output>, List<Output>>>distinguisher =
                        distinguish(rep1, rep2);
                if (distinguisher != null) {
                    List<Input> distinguishingSuffix = distinguisher.first;
                    this.additionalInfo.put("Distinguisher", distinguishingSuffix);
                    this.additionalInfo.put("FromState", from);
                    this.additionalInfo.put("ToState", to);
                    this.additionalInfo.put("FromRep.Input", rep1);
                    this.additionalInfo.put("ToRep", rep2);
                    this.additionalInfo.put("Input", symbol);
                    this.additionalInfo.put("Query(FromRep.Input.Dist)", distinguisher.second.first);
                    this.additionalInfo.put("Query(ToRep.Dist)", distinguisher.second.second);
                    List<Input> result = new ArrayList<>();
                    result.addAll(rep1);
                    result.addAll(distinguishingSuffix);
                    numRealMQueries += (mOracle.cache.size() - currFreshMQueries);
                    return Pair.create(result,new Integer(numMQueries));
                }
            }
            long endTime = System.currentTimeMillis();
            timeLog.append(from);
            timeLog.append(" --");
            timeLog.append(symbol);
            timeLog.append("-> ");
            timeLog.append(to);
            timeLog.append(" took ");
            timeLog.append(endTime - startTime);
            timeLog.append(" ms\n");
        }
        System.out.println(timeLog.toString());
        return Pair.create(null,new Integer(numMQueries));
    }
}
