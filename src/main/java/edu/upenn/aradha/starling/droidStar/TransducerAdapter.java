package edu.upenn.aradha.starling.droidStar;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;

import android.content.Context;

import edu.upenn.aradha.starling.lstar.Input;
import edu.upenn.aradha.starling.lstar.Output;
import edu.upenn.aradha.starling.lstar.Symbol;
import edu.upenn.aradha.starling.lstar.MembershipOracle;


import edu.colorado.plv.droidStar.Transducer;
import edu.colorado.plv.droidStar.LearningPurpose;

import static edu.colorado.plv.droidStar.Static.*;

public class TransducerAdapter extends MembershipOracle {
    private Transducer transducer;

    public TransducerAdapter(Context c, LearningPurpose p) {
        super();
        this.transducer = new Transducer(c,p);
    }

    public Queue<Output> runQuery(List<Input> word) {
        Queue<String> inputs = new ArrayDeque();
        Queue<String> outputs = new ArrayDeque();
        Queue<Output> trace = new ArrayDeque();
        
        for (Input input : word) {
            inputs.add(input.toString());
        }

        outputs = transducer.membershipQuery(inputs);

        if (outputs != null) {
            for (int i = 0; i < inputs.size(); i++) {
                if (outputs.peek() != null) {
                    trace.add(new Output(outputs.remove()));
                } else {
                    trace.add(new Output(REJECTED));
                }
            }
            return trace;
        } else {
            // We are reporting an error
            return null;
        }
    }

}
