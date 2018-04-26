package edu.colorado.plv.droidstar.experiments.lp;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.TextServicesManager;
import android.view.textservice.TextInfo;

import android.os.Handler.Callback;
import android.content.Context;


import edu.colorado.plv.droidstar.LearningPurpose;
import static edu.colorado.plv.droidstar.Static.*;

public class SpellCheckerSessionLP extends LearningPurpose {
    protected SpellCheckerSession sp;

    public static String CANCEL = "cancel";
    public static String CLOSE = "close";
    public static String SUGGEST = "suggest";

    public static String RESULTS = "results";

    public static TextInfo[] TEXT_ARG = new TextInfo[]{
        new TextInfo("Climb a mountain")
    };

    public static int LIMIT_ARG = 3;

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(CLOSE);
        inputs.add(CANCEL);
        inputs.add(SUGGEST);

        return inputs;
    }

    public void giveInput(String input, int altKey) throws Exception {
        if (input.equals(CLOSE)) {
            sp.close();
        } else if (input.equals(CANCEL)) {
            sp.cancel();
        } else if (input.equals(SUGGEST)) {
            sp.getSentenceSuggestions(TEXT_ARG, LIMIT_ARG);
        } else {
            logl("Unknown command");
            throw new IllegalArgumentException("Unknown command");
        }
    }

    public boolean isError(String output) {
        // there are no errors for this class?
        return false;
    }

    public String shortName() {
        return "SpellCheckerSession";
    }

    public int betaTimeout() {
        return 500;
    }

    public SpellCheckerSessionLP(Context c) {
        super(c);
        this.sp = null;
    }

    public List<String> singleInputs() {
        List<String> inputs = new ArrayList();
        inputs.add(SUGGEST);
        return inputs;
    }

    protected String resetActions(Context context, Callback callback) {
        doReset(context);
        return null;
    }

    protected void doReset(Context context) {
        if (sp != null) {
            sp.cancel();
        }
        TextServicesManager tsm = (TextServicesManager) context.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        sp = tsm.newSpellCheckerSession(null, Locale.ENGLISH, new Listener(), true);
    }

    protected void respondToResultsDep() {}

    protected class Listener implements SpellCheckerSessionListener {
        public Listener() {}

        public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
            respond(RESULTS);
        }

        public void onGetSuggestions(SuggestionsInfo[] s) {
            respondToResultsDep();
        }
    }
}
