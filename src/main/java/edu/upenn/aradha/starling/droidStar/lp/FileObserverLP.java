package edu.upenn.aradha.starling.droidStar.lp;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;

import android.os.Handler.Callback;
import android.content.Context;
import android.util.Log;

import edu.colorado.plv.droidStar.LearningPurpose;
import static edu.colorado.plv.droidStar.Static.*;

import android.os.FileObserver;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class FileObserverLP extends LearningPurpose {
    protected TestObserver obs;
    protected File testfile;

    public String shortName() {return "FileObserver";}

    public FileObserverLP(Context c) {
        super(c);
        obs = null;
        testfile = new File(c.getFilesDir(), "file-observer-testfile");
    }

    // INPUTS
    public static String START = "start";
    public static String STOP = "stop";
    public static String MODIFY = "modify";
    public static String DELETE = "delete";

    // OUTPUTS
    public static String MODIFIED = "modified";
    public static String DELETED = "deleted";

    protected void resetActions(Context context, Callback callback) {
        if (obs != null) obs.stopWatching();
        testfile.delete();
        try {
            testfile.createNewFile();
            Log.d("STARLING:Q", "File created");
        }
        catch (Exception e) {e.printStackTrace();}
        
        obs = new TestObserver();
    }

    public boolean isError(String output) {return false;}

    public int betaTimeout() {return 500;}

    protected List<String> uniqueInputSet() {
        List<String> is = new ArrayList();
        is.add(START);
        is.add(STOP);
        is.add(MODIFY);
        is.add(DELETE);
        return is;
    }

    public int postResetTimeout() {return 100;}

    public int eqLength() {return 3;}

    public void giveInput(String i) throws Exception {
        if (i.equals(DELETE)) {
            testfile.delete();
            Log.d("STARLING:Q", "File deleted");
            
        } else if (i.equals(MODIFY)) {
            writeToFile(testfile);
            Log.d("STARLING:Q", "Wrote to file");
            
        } else if (i.equals(START)) {
            obs.startWatching();
            Log.d("STARLING:Q", "Started watching");
            
        } else if (i.equals(STOP)) {
            obs.stopWatching();
            Log.d("STARLING:Q", "Stopped watching");
        }
    }

    public boolean validQuery(Queue<String> q) {
        Queue<String> query = new ArrayDeque(q);
    
        while (!query.isEmpty()) {
            String i1 = query.remove();
            String i2 = query.peek();
            if ((i1.equals(MODIFY) || i1.equals(DELETE))
                && !(i2 == null || i2.equals(DELTA))) {
                    return false;
            }
        }
        return true;
    }

    public class TestObserver extends FileObserver {
        public TestObserver() {
            super(testfile.getPath(), FileObserver.ALL_EVENTS);
        }
        public void onEvent(int event, String path) {
            Log.d("STARLING:Q", "We got an event");
            switch (event) {
            case FileObserver.DELETE_SELF:
                respond(DELETED);
                break;
            case FileObserver.MODIFY:
                respond(MODIFIED);
                break;
            default:
                break;
            }
        }
    }

    protected void writeToFile(File f) throws Exception {
        FileOutputStream fo = new FileOutputStream(f);
        PrintWriter p = new PrintWriter(fo);
        p.append("hi");
        p.flush();
        p.close();
    }
}
