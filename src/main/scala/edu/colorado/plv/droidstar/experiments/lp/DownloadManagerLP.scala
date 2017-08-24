package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._

import android.app.DownloadManager
import android.app.DownloadManager.Request

import collection.mutable.Queue

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

  var dm: DownloadManager = c.getSystemService(Context.DOWNLOAD_SERVICE).asInstanceOf[DownloadManager]
  // var dm: DownloadManager = c.getSystemService(classOf[DownloadManager])

  val complete: String = DownloadManager.ACTION_DOWNLOAD_COMPLETE

  var filter: IntentFilter = new IntentFilter()
  filter.addAction(complete)
  c.registerReceiver(receiver,filter)

  val validUri: Request = new Request(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/7/77/Bourg-la-Reine_%28la_mairie%29.JPG"))
  // val invalidUri: Request = new Request(Uri.parse("invalid"))
  val unavailableUri: Request = new Request(Uri.parse("https://www.octalsrc.org/not_here.html"))

  val enqueueValid = "enque_valid"
  val enqueueInvalid = "enque_invalid"
  val enqueueUnavailable = "enque_unavailable"
  val remove = "remove"

  val onCompleted = "onCompleted"

  var ids: Queue[Long] = new Queue()

  override def betaTimeout(): Int = 2000
  override def safetyTimeout(): Int = 2000

  override def uniqueInputSet(): java.util.List[String] =
    List(enqueueValid, enqueueUnavailable, remove).asJava

  override def validQuery(q: java.util.Queue[String]): Boolean =
    onlyOneOf(Seq(enqueueValid, enqueueUnavailable))(q)

  @throws(classOf[Exception])
  override def giveInput(i: String): Unit = i match {
    case `enqueueValid` => {
      val id = dm.enqueue(validUri)
      ids.enqueue(id)
    }
    case `enqueueUnavailable` => {
      val id = dm.enqueue(unavailableUri)
      ids.enqueue(id)
    }
    case `remove` => dm.remove(ids.dequeue())
    case _ => {
      logl("Unknown command to DownloadManager")
      throw new IllegalArgumentException("Unknown command to DownloadManager")
    }
  }

  override def isError(o: String): Boolean = false

  override def resetActions(c: Context, b: Callback): String = {
    ids.clear()
    null
  }

  override def shortName(): String = "DownloadManager"

}
