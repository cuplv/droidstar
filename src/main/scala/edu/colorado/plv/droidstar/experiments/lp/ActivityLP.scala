package edu.colorado.plv.droidstar
package experiments.lp

import android.content.Context
import android.os.Handler.Callback
import android.os.Bundle
import android.app.Activity

import scala.collection.JavaConverters._

import edu.colorado.plv.droidStar.LearningPurpose

class ActivityLP(c: Context) extends LearningPurpose(c) {
  import edu.colorado.plv.droidStar.Static._

  var activity: Activity = null

  override def betaTimeout(): Int = 500
  override def giveInput(i: String): Unit = i match {
    case "finish" => activity.finish()
    case _ => ()
  }
  override def isError(o: String): Boolean = false
  override def resetActions(c: Context, b: Callback): String = {
    activity = new Activity()
    null
  }
  override def shortName(): String = "Activity"
  override def uniqueInputSet(): java.util.List[String] =
    List("start","stop","finish").asJava

  class InstActivity() extends Activity() {
    override def onCreate(b: Bundle): Unit = respond("onCreate")
  }
}
