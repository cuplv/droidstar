package edu.upenn.aradha.starling.droidStar.lp;

import android.content.Context;

import java.util.List;
import java.util.ArrayList;

// The whole of the MediaPlayer class except the stop() input

public class MinusStopTestLP extends MediaPlayerLP {
    public MinusStopTestLP(Context c) {
        super(c);
    }

    public String shortName() {
        return "MediaPlayer-NoStop";
    }

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList(super.uniqueInputSet());
        inputs.remove(STOP);

        return inputs;
    }


}
