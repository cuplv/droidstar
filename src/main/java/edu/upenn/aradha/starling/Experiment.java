package edu.upenn.aradha.starling;

import java.io.File;
import java.io.FileWriter;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import edu.colorado.plv.droidStar.LearningPurpose;

import edu.upenn.aradha.starling.lstar.DistinguisherBoundedEquivalenceOracle;
import edu.upenn.aradha.starling.lstar.EquivalenceOracle;
import edu.upenn.aradha.starling.lstar.Input;
import edu.upenn.aradha.starling.lstar.LStar;
import edu.upenn.aradha.starling.lstar.MembershipOracle;
import edu.upenn.aradha.starling.droidStar.TransducerAdapter;

import edu.upenn.aradha.starling.droidStar.Static;

public class Experiment implements Runnable {
    List<LearningPurpose> purposes;
    Activity activity;

    public static void experiment(Activity c, List<LearningPurpose> ps) {
        new Thread(new Experiment(c,ps)).start();
    }

    public Experiment(Activity c, List<LearningPurpose> ps) {
        this.purposes = ps;
        this.activity = c;
    }

    public void run() {
        File dir = getClearDir();
        Log.d("STARLING:Q","");
        Log.d("STARLING:Q", "Today we are learning the following classes:");
        for (LearningPurpose purpose : purposes) {
            Log.d("STARLING:Q", "  - " + (purpose.shortName()));
        }
        Log.d("STARLING:Q", "---");
        for (LearningPurpose purpose : purposes) {
            try {
                Log.d("STARLING:Q", "Now learning: " + purpose.shortName());
                lstar(purpose, dir);
            } catch (Exception e) {
                Log.e("STARLING:Q", "Learning failed for " + purpose.shortName());
                e.printStackTrace();
            }
        }
        Log.d("STARLING:Q", "---");
        Log.d("STARLING:Q", "Finished all experiments :)");

        // should close the app, for convenience
        activity.finish();
    }

    protected void lstar(LearningPurpose purpose, File dir) throws Exception {
        FileWriter dataFile = new FileWriter(mkfile("data.txt", dir, purpose));
        FileWriter diagramFile = new FileWriter(mkfile("diagram.gv", dir, purpose));
        FileWriter logFile = new FileWriter(mkfile("log.txt", dir, purpose));
        List<Input> inputs = Static.inputSet(purpose);
        MembershipOracle mo = new TransducerAdapter(activity, purpose);
        EquivalenceOracle eo = new DistinguisherBoundedEquivalenceOracle(mo, inputs, purpose.eqLength());

        dataFile.write("- class: " + purpose.shortName() + "\n");
        LStar ls = new LStar(mo, eo, inputs, diagramFile, dataFile, logFile);
        ls.run();
        Log.d("STARLING:Q", "Completed learning " + purpose.shortName());

        // IF YOU ADD NEW FILES, MAKE SURE TO CLOSE THEM HERE!!!!
        dataFile.close();
        diagramFile.close();
        logFile.close();
    }

    protected File getClearDir() {
        File dir = new File(activity.getExternalFilesDir(null), "results");
        if (dir.exists()) {
            for (File child : dir.listFiles()) {
                child.delete();
            }
        }
        return dir;
    }

    protected File mkfile(String name, File dir, LearningPurpose p) {
        dir.mkdirs();
        File child = new File(dir, p.shortName() + "-" + name);
        if (child.exists()) {
            Log.d("STARLING:Q", "Deleting previous " + name + " for this class...");
            child.delete();
        }

        return child;
    }
}
