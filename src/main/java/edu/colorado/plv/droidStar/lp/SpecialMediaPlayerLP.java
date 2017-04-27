package edu.colorado.plv.droidStar.lp;

import android.content.Context;

// This is a test of the mediaplayer with STOP replaced by an
// equivalent combination of other inputs (not *actually* equivalent
// unless it's called from the "playing" state though)
public class SpecialMediaPlayerLP extends MediaPlayerLP {
    public SpecialMediaPlayerLP(Context c) {
        super(c);
    }

    public String shortName() {
        return "MediaPlayer-SpecialStop";
    }

    public void giveInput(String input) throws Exception {
        if (input.equals(STOP)) {
            super.mp.reset();
            super.setDataSourceC();
        } else {
            super.giveInput(input);
        }
    }
}
