package edu.upenn.aradha.starling;

import android.support.v7.app.AppCompatActivity;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.plv.droidStar.LearningPurpose;
import edu.upenn.aradha.starling.droidStar.lp.SpeechRecognizerLP;
import edu.upenn.aradha.starling.droidStar.lp.MediaPlayerLP;
import edu.upenn.aradha.starling.droidStar.lp.MinusStopTestLP;
import edu.upenn.aradha.starling.droidStar.lp.CountDownTimerLP;
import edu.upenn.aradha.starling.droidStar.lp.AsyncTaskLP;
import edu.upenn.aradha.starling.droidStar.lp.AsyncTaskNoGetLP;
import edu.upenn.aradha.starling.droidStar.lp.AsyncTaskLongLP;
import edu.upenn.aradha.starling.droidStar.lp.SpellCheckerSessionLP;
import edu.upenn.aradha.starling.droidStar.lp.DoubleSpellCheckerSessionLP;
import edu.upenn.aradha.starling.droidStar.lp.SQLiteOpenHelperLP;
import edu.upenn.aradha.starling.droidStar.lp.FileObserverLP;

import edu.upenn.aradha.starling.lstar.DistinguisherBoundedEquivalenceOracle;
import edu.upenn.aradha.starling.lstar.EquivalenceOracle;
import edu.upenn.aradha.starling.lstar.Input;
import edu.upenn.aradha.starling.lstar.LStar;
import edu.upenn.aradha.starling.lstar.MembershipOracle;
import edu.upenn.aradha.starling.droidStar.TransducerAdapter;

import edu.upenn.aradha.starling.droidStar.Static;

public class MainActivity extends AppCompatActivity {

    public static boolean started = false;

    public static String OUTDIR = Environment.DIRECTORY_DOCUMENTS;

    @Override
    public void onStart() {
        super.onStart();
        if (started)
            return;
        started = true;

        if (weCanWrite()) {
            List<LearningPurpose> purposes = new ArrayList();
            purposes.add(new FileObserverLP(this));
            // purposes.add(new SpeechRecognizerLP(this));

            Experiment.experiment(this, purposes);
        } else {
            System.out.println("No storage, can't report results");
        }
    }

    private boolean weCanWrite() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
}
