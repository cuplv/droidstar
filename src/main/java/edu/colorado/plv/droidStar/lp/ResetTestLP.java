package edu.colorado.plv.droidStar.lp;

import android.content.Context;

import java.util.List;
import java.util.ArrayList;


public class ResetTestLP extends MediaPlayerLP {
    public ResetTestLP(Context c) {
        super(c);
    }

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(PREPARE_ASYNC);
        inputs.add(SET_DATA_SOURCE);
        inputs.add(START);
        inputs.add(RESET);

        return inputs;
    }

    public String shortName() {
        return "MediaPlayer-ResetTest";
    }
}
