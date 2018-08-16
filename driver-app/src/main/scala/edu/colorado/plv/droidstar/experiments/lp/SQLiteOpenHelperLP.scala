package edu.colorado.plv.droidstar
package experiments.lp

import java.io.File;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;

import android.os.Handler.Callback;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


import scala.collection.JavaConverters._

class SQLiteOpenHelperLP(c: Context) extends LearningPurpose(c) {

  def dbfile(c: Context): File =
    new File(c.getExternalFilesDir(null), "testDB.sqlite3")

  var helper: MyDBHelper = null
  var testDB: File = dbfile(c)

  def initdb(v: Int): Unit = {
    var db: SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(testDB, null)
    db.setVersion(v)
    db.close()
  }

  // INPUTS
  val OPENNEW = "openNew"
  val OPENHV = "openHV" // higher ver
  val OPENLV = "openLV" // lower ver
  val OPENSV = "openSV" // same ver
  val CLOSE = "close"

  val THISV: Int = 5
  val HV: Int = 6
  val LV: Int = 4

  // OUTPUTS
  val CONFIGURED = "confd"
  val CREATED = "created"
  val OPENED = "opened"
  val UPGRADED = "upgrd"

  override def betaTimeout(): Int = 500

  def openinput(v: Int): Unit = {
    initdb(v)
    helper.getWritableDatabase()
  }

  @throws(classOf[Exception])
  override def giveInput(i: String, k: Int): Unit = i match {
    case `OPENNEW` => helper.getWritableDatabase()
    case `OPENSV` => openinput(THISV)
    case `OPENHV` => openinput(HV)
    case `OPENLV` => openinput(LV)
    case `CLOSE` => helper.close()
    case _ => {
      logl("Unknown command to SQLiteOpenHelper")
      throw new IllegalArgumentException("Unknown command to AsyncTask")
    }
  }

  // All queries must start with an OPEN input, and then can't have
  // any more OPEN inputs.  This is a very limited experiment...
  override def validQuery(q: Queue[String]): Boolean = {
    var query: Queue[String] = new ArrayDeque(q)
    val first: String = query.poll()

    if (first == null) {
      return true
    }
    if (first.equals(OPENHV)
        || first.equals(OPENNEW)
        || first.equals(OPENLV)
        || first.equals(OPENSV)) {

        for (input <- query.asScala) {
            if (input.equals(OPENHV)
                || input.equals(OPENNEW)
                || input.equals(OPENLV)
                || input.equals(OPENSV)) {
                return false
            }
        }
        return super.validQuery(query)
    }
    return false
  }

  override def isError(o: String): Boolean = false

  override def resetActions(c: Context, b: Callback): String = {
    if (helper != null) {
      helper.close()
    }
    testDB.delete()
    helper = new MyDBHelper(c, testDB.getAbsolutePath(), null, 5)
    null
  }

  class MyDBHelper(
    c: Context,
    n: String,
    f: SQLiteDatabase.CursorFactory,
    v: Int
  ) extends SQLiteOpenHelper(c,n,f,v) {
    override def onConfigure(db: SQLiteDatabase): Unit =
      respond(CONFIGURED)
    override def onCreate(db: SQLiteDatabase): Unit =
      respond(CREATED)
    override def onOpen(db: SQLiteDatabase): Unit =
      respond(OPENED)
    override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit =
      respond(UPGRADED)
  }

  override def shortName(): String = "SQLiteOpenHelper"

  override def uniqueInputSet(): java.util.List[String] =
    List(OPENNEW, OPENHV, OPENLV, OPENSV, CLOSE).asJava

}
