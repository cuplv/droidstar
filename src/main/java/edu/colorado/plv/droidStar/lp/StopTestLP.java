package edu.colorado.plv.droidStar.lp;

import android.content.Context;

import java.util.List;
import java.util.ArrayList;

// This is a short test of specifically the stop() input, which was
// giving us problems.
public class StopTestLP extends MediaPlayerLP {
    public StopTestLP(Context c) {
        super(c);
    }

    public String shortName() {
        return "MediaPlayer-StopTest";
    }


    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(PREPARE_ASYNC);
        inputs.add(STOP);
        inputs.add(SET_DATA_SOURCE);
        inputs.add(START);

        return inputs;
    }


}
