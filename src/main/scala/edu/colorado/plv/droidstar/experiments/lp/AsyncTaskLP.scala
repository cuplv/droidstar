package edu.colorado.plv.droidstar
package experiments.lp

import android.content.Context
import android.os.Handler.Callback

import android.os.AsyncTask


import scala.collection.JavaConverters._

class AsyncTaskLP(c: Context) extends LearningPurpose(c) {
  var task: AsyncTask[AnyRef,AnyRef,AnyRef] = null
  var counter: Int = 0

  val param = "asdf"

  val execute = "exec"
  val cancel = "cancel"
  val cancelled = "on_cancelled"
  val postexec = "on_postexec"

  override def betaTimeout(): Int = 500

  @throws(classOf[Exception])
  override def giveInput(i: String, altKey: Int): Unit = i match {
    case `execute` => task.execute(param)
    case `cancel` => task.cancel(false)
    case _ => {
      logl("Unknown command to AsyncTask")
      throw new IllegalArgumentException("Unknown command to AsyncTask")
    }
  }

  override def inputAlts(): java.util.Map[String,java.lang.Integer] = {
    val m = super.inputAlts()
    m.put(execute,1)
    m
  }

  override def isError(o: String): Boolean = false

  override def resetActions(c: Context, b: Callback): String = {
    if (task != null) {
      task.cancel(true)
      counter += 1
    }
    task = new SimpleTask(counter)
    null
  }
  override def shortName(): String = "AsyncTask"
  override def uniqueInputSet(): java.util.List[String] =
    List(execute,cancel).asJava

  class SimpleTask(localCounter: Int) extends AsyncTask[AnyRef,AnyRef,AnyRef] {

    /* Using `String` instead of `AnyRef` gives AbstractMethodErrors...
     *
     * (see https://stackoverflow.com/questions/24934022/asynctask-doinbackground-abstract-method-not-implemented-error-in-android-scal)
     */
    override def doInBackground(ss: AnyRef*): AnyRef = {
      try {Thread.sleep(200)}
      catch {
        case _ : Throwable => logl("Sleep problem?")
      }
      param
    }
    override def onCancelled(s: AnyRef): Unit = {
      if (localCounter == counter) {
        respond(cancelled)
      }
    }
    override def onPostExecute(s: AnyRef): Unit = {
      respond(postexec)
    }
  }

}
