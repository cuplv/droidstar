package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._

import java.io.IOException
import okhttp3.{Call, Callback => OKCallback, OkHttpClient, Request, Response}

class OkHttpClientLP(c: Context) extends LearningPurpose(c) {
  var client: OkHttpClient = new OkHttpClient()
  var call: Call = null

  val urlV: String = "https://www.octalsrc.org/index.html"
  val urlU: String = "https://www.octalsrc.org/nope.html"

  // Inputs
  val enqueueValid = "enqueue_valid"
  val enqueueUnavailable = "enqueue_unavailable"
  val cancel = "cancel"

  // Outputs
  val onCompleted = "onCompleted"
  val on404 = "on404"
  val onError = "onError"

  override def shortName(): String = "OkHttpClientLP"
  override def isError(o: String): Boolean = o match {
    case `onError` => true
    case _ => false
  }
  override def betaTimeout(): Int = 2000
  override def safetyTimeout(): Int = 2000
  override def validQuery(q: java.util.Queue[String]): Boolean =
    onlyOneOf(Seq(enqueueValid, enqueueUnavailable))(q)

  override def resetActions(c: Context, b: Callback): String = {
    null
  }

  override def uniqueInputSet(): java.util.List[String] =
    List(enqueueValid, enqueueUnavailable, cancel).asJava

  def mkRequest(url: String): Request = ???

  def mkCallback: OKCallback = new OKCallback() {
    override def onFailure(call: Call, e: IOException): Unit = {
      respond(onError)
    }

    override def onResponse(call: Call, response: Response): Unit = {
      respond(onCompleted)
    }
  }

  override def giveInput(i: String): Unit = i match {
    case `enqueueValid` => client.newCall(mkRequest(urlV)).enqueue(mkCallback)
    case `enqueueUnavailable` => client.newCall(mkRequest(urlU)).enqueue(mkCallback)
    case `cancel` => call.cancel()
  }
}
