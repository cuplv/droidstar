package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._

import com.nostra13.universalimageloader.core.ImageLoader

class ImageLoaderLP(c: Context) extends LearningPurpose(c) {

  val onError: String = "onError"

  override def shortName(): String = "ImageLoaderLP"
  override def isError(o: String): Boolean = o match {
    case `onError` => true
    case _ => false
  }
  override def betaTimeout(): Int = 1000
  override def safetyTimeout(): Int = 2000
  // override def validQuery(q: java.util.Queue[String]): Boolean =
  //   onlyOneOf(Seq(buildValid, buildUnavailable))(q)

  override def uniqueInputSet(): java.util.List[String] = ???
  override def giveInput(input: String): Unit = ???
  override def resetActions(c: Context, b: Callback): String = ???
}


