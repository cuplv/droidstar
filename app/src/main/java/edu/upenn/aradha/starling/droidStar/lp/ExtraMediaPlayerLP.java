package edu.upenn.aradha.starling.droidStar.lp;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import static edu.colorado.plv.droidStar.Static.*;

public class ExtraMediaPlayerLP extends MediaPlayerLP {

    public static String BAD_DATA_SOURCE = "BadDataSource";

    public static String BAD_URL = "You know, that one song?";

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

    public void giveInput(String input) throws Exception {
        if (input.equals(BAD_DATA_SOURCE)) {
            badDataSourceC();
        } else {
            super.giveInput(input);
        }
    }
}
