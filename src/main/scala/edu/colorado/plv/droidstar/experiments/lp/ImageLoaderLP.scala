package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._

import android.graphics.Bitmap
import android.view.View

import com.nostra13.universalimageloader.core._
import assist.FailReason
import listener.ImageLoadingListener

import Static._

class ImageLoaderLP(c: Context) extends LearningPurpose(c) {

  var il: ImageLoader = ImageLoader.getInstance()
  il.init(ImageLoaderConfiguration.createDefault(c))
  var gen: Int = 0

  // inputs
  val loadGood: String = "loadGood"
  val loadBad: String = "loadBad"
  val cancel: String = "cancel"

  // outputs
  val onLoadStart: String = "onLoadStart"
  val onLoadComplete: String = "onLoadComplete"
  val onLoadCancelled: String = "onLoadCancelled"
  val onError: String = "onError"

  val goodUrl: String = "https://xkcd.com/s/0b7742.png"
  val badUrl: String = "https://www.example.com/nothere.png"

  override def shortName(): String = "ImageLoaderLP"
  override def isError(o: String): Boolean = o match {
    case `onError` => true
    case _ => false
  }
  override def betaTimeout(): Int = 1000
  override def validQuery(q: java.util.Queue[String]): Boolean =
    onlyOneOf(Seq(loadGood, loadBad))(q)

  def mkListener(g: Int): ImageLoadingListener = new ImageLoadingListener() {
    override def onLoadingStarted(url: String, v: View) {
      if (g == gen) respond(onLoadStart)
    }
    override def onLoadingComplete(url: String, v: View, i: Bitmap) {
      if (g == gen) respond(onLoadComplete)
    }
    override def onLoadingCancelled(url: String, v: View) {
      if (g == gen) respond(onLoadCancelled)
    }
    override def onLoadingFailed(url: String, v: View, f: FailReason) {
      log("FAIL",f.getType().toString())
      log("FAIL",f.getCause().toString())
      if (g == gen) respond(onError)
    }
  }

  override def uniqueInputSet(): java.util.List[String] = Seq(
    loadGood,
    loadBad,
    cancel
  ).asJava
  override def giveInput(input: String): Unit = input match {
    case `loadGood` => il.loadImage(goodUrl, mkListener(gen))
    case `loadBad` => il.loadImage(badUrl, mkListener(gen))
    case `cancel` => il.stop()
  }
  override def resetActions(c: Context, b: Callback): String = {
    if (il.isInited()) il.stop()
    gen += 1
    null
  }
}
