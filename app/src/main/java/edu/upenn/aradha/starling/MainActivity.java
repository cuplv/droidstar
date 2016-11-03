package edu.upenn.aradha.starling;

import android.support.v7.app.AppCompatActivity;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.plv.droidStar.LearningPurpose;
import edu.upenn.aradha.starling.droidStar.lp.SpeechRecognizerLP;
import edu.upenn.aradha.starling.droidStar.lp.CountDownTimerLP;
import edu.upenn.aradha.starling.droidStar.lp.AsyncTaskNoGetLP;
import edu.upenn.aradha.starling.droidStar.lp.SpellCheckerSessionLP;
import edu.upenn.aradha.starling.droidStar.lp.SQLiteOpenHelperLP;
import edu.upenn.aradha.starling.droidStar.lp.FileObserverLP;

public class MainActivity extends AppCompatActivity {
    public static boolean started = false;

    @Override
    public void onStart() {
        super.onStart();
        if (started)
            return;
        started = true;

        if (weCanWrite()) {
            List<LearningPurpose> purposes = new ArrayList();
            purposes.add(new SpeechRecognizerLP(this));
            purposes.add(new CountDownTimerLP(this));
            purposes.add(new AsyncTaskNoGetLP(this));
            purposes.add(new SpellCheckerSessionLP(this));
            purposes.add(new SQLiteOpenHelperLP(this));
            purposes.add(new FileObserverLP(this));

            Experiment.experiment(this, purposes);
        } else {
            System.out.println("No storage, can't report results");
        }
    }

    private boolean weCanWrite() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
}
