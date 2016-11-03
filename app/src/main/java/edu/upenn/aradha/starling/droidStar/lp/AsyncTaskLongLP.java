package edu.upenn.aradha.starling.droidStar.lp;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import edu.colorado.plv.droidStar.LearningPurpose;
import static edu.colorado.plv.droidStar.Static.*;

public class AsyncTaskLongLP extends AsyncTaskLP {
    public String shortName() {
        return "AsyncTask-Long";
    }

    public AsyncTaskLongLP(Context c) {
        super(c);
    }

    public int betaTimeout() {return 4000;}
}
