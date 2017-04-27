package edu.colorado.plv.droidStar.lp;

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


import edu.colorado.plv.droidStar.LearningPurpose;
import static edu.colorado.plv.droidStar.Static.*;

public class DoubleSpellCheckerSessionLP extends SpellCheckerSessionLP {

    // the deprecated suggest method
    public static String SUGGESTDEP = "suggestdep";

    public static String RESULTSDEP = "resultsdep";

    public static TextInfo TEXT_ARG2 = new TextInfo("Climb a mountain");

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList(super.uniqueInputSet());
        inputs.add(SUGGESTDEP);

        return inputs;
    }

    public void giveInput(String input) throws Exception {
        if (input.equals(SUGGESTDEP)) {
            sp.getSuggestions(TEXT_ARG2, LIMIT_ARG);
        } else {
            super.giveInput(input);
        }
    }

    public DoubleSpellCheckerSessionLP(Context c) {
        super(c);
    }

    public boolean isError(String output) {
        // there are no errors for this class?
        return false;
    }

    public String shortName() {
        return "SpellCheckerSession-Double";
    }

    protected void respondToResultsDep() {
        respond(RESULTSDEP);
    }

}
