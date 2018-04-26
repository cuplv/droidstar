package edu.upenn.aradha.starling.droidstar;

import java.util.List;
import java.util.ArrayList;

import edu.colorado.plv.droidstar.LearningPurpose;

import edu.upenn.aradha.starling.lstar.Input;
import edu.upenn.aradha.starling.lstar.Output;

public class Static {

    public static String DELTA = edu.colorado.plv.droidstar.Static.DELTA;

    public static List<Input> makeInputs(List<String> is) {
        List<Input> inputs = new ArrayList();

        for (String input : is) {
            inputs.add(new Input(input));
        }

        return inputs;
    }

    public static List<Input> inputSet(LearningPurpose p) {
        List<String> strings = p.inputSet();
        strings.add(DELTA);

        return makeInputs(strings);
    }
    
}
