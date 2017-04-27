package edu.colorado.plv.droidStar.lp;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import edu.colorado.plv.droidStar.LearningPurpose;
import static edu.colorado.plv.droidStar.Static.*;

public class AsyncTaskNoGetLP extends AsyncTaskLP {
    public String shortName() {
        return "AsyncTask-NoGet";
    }

    public AsyncTaskNoGetLP(Context c) {
        super(c);
    }

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList(super.uniqueInputSet());
        inputs.remove(GET);

        return inputs;
    }
}
