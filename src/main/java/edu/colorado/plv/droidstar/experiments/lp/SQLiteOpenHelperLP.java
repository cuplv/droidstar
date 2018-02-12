package edu.colorado.plv.droidstar.experiments.lp;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;

import android.os.Handler.Callback;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import edu.colorado.plv.droidstar.LearningPurpose;
import static edu.colorado.plv.droidstar.Static.*;

public class SQLiteOpenHelperLP extends LearningPurpose {
    protected MyDBHelper helper;
    protected File testDB;

    public String shortName() {return "SQLiteOpenHelper";}

    protected File dbfile(Context c) {
        return new File(c.getExternalFilesDir(null), "testDB.sqlite3");
    }

    // INPUTS
    public static String OPENNEW = "openNew";
    public static String OPENHV = "openHV"; // higher ver
    public static String OPENLV = "openLV"; // lower ver
    public static String OPENSV = "openSV"; // same ver
    public static String CLOSE = "close";

    public static int THISV = 5;
    public static int HV = 6;
    public static int LV = 4;

    public List<String> uniqueInputSet() {
        List<String> is = new ArrayList();
        is.add(OPENNEW);
        is.add(OPENHV);
        is.add(OPENLV);
        is.add(OPENSV);
        is.add(CLOSE);

        return is;
    }

    // All queries must start with an OPEN input, and then can't have
    // any more OPEN inputs.  This is a very limited experiment...
    public boolean validQuery(Queue<String> q) {
        Queue<String> query = new ArrayDeque(q);

        String first = query.poll();

        if (first == null) return true;

        if (first.equals(OPENHV)
            || first.equals(OPENNEW)
            || first.equals(OPENLV)
            || first.equals(OPENSV)) {

            for (String input : query) {
                if (input.equals(OPENHV)
                    || input.equals(OPENNEW)
                    || input.equals(OPENLV)
                    || input.equals(OPENSV)) {
                    return false;
                }
            }
            return super.validQuery(query);
        }
        
        return false;
        
    }

    // OUTPUTS
    public static String CONFIGURED = "confd";
    public static String CREATED = "created";
    public static String OPENED = "opened";
    public static String UPGRADED = "upgrd";

    public SQLiteOpenHelperLP(Context c) {
        super(c);
        this.testDB = dbfile(c);
    }

    public boolean isError(String output) {return false;}

    public int betaTimeout() {
        return 500;
    }

    public void initdb(int v) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(testDB, null);
        db.setVersion(v);
        db.close();
    }

    public void openinput(int v) {
        initdb(v);
        helper.getWritableDatabase();
    }

    public void giveInput(String input, int altKey) throws Exception {
        if (input.equals(OPENSV)) {
            openinput(THISV);
        } else if (input.equals(OPENNEW)) {
            helper.getWritableDatabase();
        } else if (input.equals(OPENHV)) {
            openinput(HV);
        } else if (input.equals(OPENLV)) {
            openinput(LV);
        } else if (input.equals(CLOSE)) {
            helper.close();
        }
    }

    protected String resetActions(Context context, Callback callback) {
        if (helper != null) helper.close();
        testDB.delete();
        helper = new MyDBHelper(context, testDB.getAbsolutePath(), null, 5);
        return null;
    }

    protected class MyDBHelper extends SQLiteOpenHelper {
        public MyDBHelper(Context c, String n, SQLiteDatabase.CursorFactory f, int v) {
            super(c,n,f,v);
        }

        public void onConfigure(SQLiteDatabase db) {respond(CONFIGURED);}
        public void onCreate(SQLiteDatabase db) {respond(CREATED);}
        public void onOpen(SQLiteDatabase db) {respond(OPENED);}
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            respond(UPGRADED);
        }
    }
}
