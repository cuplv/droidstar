package edu.colorado.plv.droidstar
package experiments.lp

import android.content.{Context, Intent}
import android.os.Handler.Callback
import android.os.Bundle
import android.app.Activity

import scala.collection.JavaConverters._

import edu.colorado.plv.droidstar.LearningPurpose

/* This experiment is fundamentally broken; see interior comment */
class ActivityLP(c: Context) extends LearningPurpose(c) {
  import edu.colorado.plv.droidstar.Static._

  var activity: Activity = null

  override def betaTimeout(): Int = 500
  override def giveInput(i: String): Unit = i match {
    case "finish" => activity.finish()
    case _ => ()
  }
  override def isError(o: String): Boolean = false

  /* This can't actually work, because InstActivity is required to be
   * static, which would make it impossible to use the `respond`
   * method of reporting output symbols :(
   */
  override def resetActions(c: Context, b: Callback): String = {
    val cls: Class[_] = classOf[InstActivity]
    val intent: Intent = new Intent(c, cls)
    c.startActivity(intent)
    null
  }
  override def shortName(): String = "Activity"
  override def uniqueInputSet(): java.util.List[String] =
    List("start","stop","finish").asJava

  class InstActivity extends Activity {
    override def onCreate(b: Bundle): Unit = respond("onCreate")
  }
}
