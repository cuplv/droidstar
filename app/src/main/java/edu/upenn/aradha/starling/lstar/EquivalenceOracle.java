package edu.upenn.aradha.starling.lstar;

import android.util.Pair;

import java.lang.Integer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EquivalenceOracle {
    public final Map<String, Object> additionalInfo = new HashMap<>();
    public Pair<List<Input>, Integer> check(List<State> states, Map<Pair<State, Input>, State> transitions)
    throws Exception {
        additionalInfo.clear();
        return doCheck(states, transitions);
    }

    public abstract int getNumRealMQueries();
    public abstract int getNumMQueries();
    public abstract int getNumEQueries();

    public abstract Pair<List<Input>, Integer> doCheck(List<State> states, Map<Pair<State, Input>, State> transitions) throws Exception;
}
