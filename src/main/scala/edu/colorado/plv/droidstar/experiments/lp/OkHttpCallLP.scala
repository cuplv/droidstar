package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._

import java.io.IOException
import okhttp3.{Call, Callback => OKCallback, OkHttpClient, Request, Response}

class OkHttpCallLP(c: Context) extends LearningPurpose(c) {
  var client: OkHttpClient = new OkHttpClient()
  var call: Call = null

  val urlV: String = "https://www.octalsrc.org/index.html"
  val urlU: String = "https://www.octalsrc.org/nope.html"

  // Inputs
  val buildValid = "build_valid"
  val buildUnavailable = "build_unavailable"
  val enqueue = "enqueue"
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
  override def betaTimeout(): Int = 1000
  override def safetyTimeout(): Int = 2000
  override def validQuery(q: java.util.Queue[String]): Boolean =
    onlyOneOf(Seq(buildValid, buildUnavailable))(q)

  override def resetActions(c: Context, b: Callback): String = {
    call match {
      case null => ()
      case _ => { call.cancel(); call = null }
    }
    null
  }

  override def uniqueInputSet(): java.util.List[String] =
    List(
      buildValid,
      buildUnavailable,
      enqueue,
      cancel
    ).asJava

  def mkRequest(url: String): Request =
    new Request.Builder().url(url).build()

  def mkCallback: OKCallback = new OKCallback() {
    override def onFailure(call: Call, e: IOException): Unit = {
      respond(onError)
    }

    override def onResponse(call: Call, response: Response): Unit = {
      response.code() match {
        case 404 => respond(on404)
        case _ if response.isSuccessful() => respond(onCompleted)
      }
    }
  }

  override def giveInput(i: String): Unit = i match {
    case `buildValid` => { call = client.newCall(mkRequest(urlV)) }
    case `buildUnavailable` => { call = client.newCall(mkRequest(urlU)) }
    case `enqueue` => call.enqueue(mkCallback)
    case `cancel` => call.cancel()
  }
}
