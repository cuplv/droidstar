package edu.colorado.plv.droidstar.experiments.lp;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import static edu.colorado.plv.droidstar.Static.*;

public class ExtraMediaPlayerLP extends MediaPlayerLP {

    public static String BAD_DATA_SOURCE = "BadDataSource";

    public static String BAD_URL = "https://www.octalsrc.org/missing_song.mp3";

    public ExtraMediaPlayerLP(Context c) {
        super(c);
    }

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList(super.uniqueInputSet());
        inputs.add(BAD_DATA_SOURCE);

        return inputs;
    }

    protected void badDataSourceC() throws Exception {
        super.mp.setDataSource(BAD_URL);
    }

    public void giveInput(String input, int altKey) throws Exception {
        if (input.equals(BAD_DATA_SOURCE)) {
            badDataSourceC();
        } else {
            super.giveInput(input, altKey);
        }
    }
}
