package edu.colorado.plv.droidStar;

import java.util.List;

import android.os.Handler.Callback;

import static edu.colorado.plv.droidStar.Static.*;

public interface LearningPurpose {
    public boolean isError(String output);
    public int betaTimeout();
    public List<String> inputSet();
    
    public void reset(Callback c);
    public void giveInput(String input);
}
