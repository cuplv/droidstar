package edu.colorado.plv.droidstar
package experiments.lp

import android.content.Context
import android.os.Handler.Callback

import android.os.AsyncTask


import scala.collection.JavaConverters._

import edu.colorado.plv.droidStar.LearningPurpose
import edu.colorado.plv.droidStar.Static._

class AsyncTaskLP(c: Context) extends LearningPurpose(c) {
  var task: SimpleTask = null
  var counter: Int = 0

  val param = "asdf"

  val execute = "exec"
  val cancel = "cancel"
  val cancelled = "on_cancelled"
  val postexec = "on_postexec"

  override def betaTimeout(): Int = 500
  override def giveInput(i: String): Unit = i match {
    case `execute` => task.execute(param)
    case `cancel` => task.cancel(false)
    case _ => {
      logl("Unknown command to AsyncTask")
      throw new IllegalArgumentException("Unknown command to AsyncTask")
    }
  }

  override def isError(o: String): Boolean = false

  override def resetActions(c: Context, b: Callback): String = {
    if (task != null) {
      task.cancel(true)
      counter += 1
    }
    null
  }
  override def shortName(): String = "AsyncTask"
  override def uniqueInputSet(): java.util.List[String] =
    List(execute,cancel).asJava

  class SimpleTask(localCounter: Int) extends AsyncTask[String,String,String] {
    override def doInBackground(ss: String*): String = {
      try {Thread.sleep(200)}
      catch {
        case _ => logl("Sleep problem?")
      }
      param
    }
    override def onCancelled(s: String): Unit = {
      if (localCounter == counter) {
        respond(cancelled)
      }
    }
    override def onPostExecute(s: String): Unit = {
      respond(postexec)
    }
  }

}
