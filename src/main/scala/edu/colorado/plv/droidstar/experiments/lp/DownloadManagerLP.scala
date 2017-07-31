package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._
import edu.colorado.plv.droidstar.LearningPurpose
import edu.colorado.plv.droidstar.Static._

import android.app.DownloadManager
import android.app.DownloadManager.Request

class DownloadManagerLP(c: Context) extends LearningPurpose(c) {

  val receiver: BroadcastReceiver = new BroadcastReceiver {
    override def onReceive(c: Context,i: Intent) {
      val action: String = i.getAction()
      i.getAction() match {
        case `complete` => respond(onCompleted)
        case _ => ()
      }
    }
  }

  // var dm: DownloadManager = c.getSystemService(Context.DOWNLOAD_SERVICE)
  var dm: DownloadManager = c.getSystemService(classOf[DownloadManager])

  val complete: String = DownloadManager.ACTION_DOWNLOAD_COMPLETE

  var filter: IntentFilter = new IntentFilter()
  filter.addAction(complete)
  c.registerReceiver(receiver,filter)

  val validUri: Request = ???
  val invalidUri: Request = ???
  val unavailableUri: Request = ???

  val enqueueValid = "enque_valid"
  val enqueueInvalid = "enque_invalid"
  val enqueueUnavailable = "enque_unavailable"

  val onCompleted = "onCompleted"

  override def betaTimeout(): Int = 500

  @throws(classOf[Exception])
  override def giveInput(i: String): Unit = i match {
    case `enqueueValid` => dm.enqueue(validUri)
    case _ => {
      logl("Unknown command to DownloadManager")
      throw new IllegalArgumentException("Unknown command to DownloadManager")
    }
  }

  override def isError(o: String): Boolean = false

  override def resetActions(c: Context, b: Callback): String = {
    null
  }

  override def shortName(): String = "DownloadManager"

  override def uniqueInputSet(): java.util.List[String] =
    List(enqueueValid).asJava

}
