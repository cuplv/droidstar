package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._
import edu.colorado.plv.droidstar.LearningPurpose
import edu.colorado.plv.droidstar.Static._

import com.android.volley.{RequestQueue, Request, Response, VolleyError}
import com.android.volley.toolbox.{Volley, StringRequest}

class RequestQueueLP(c: Context) extends LearningPurpose(c) {
  var rq: RequestQueue = null

  val urlV: String = "https://www.octalsrc.org/index.html"
  val urlU: String = "https://www.octalsrc.org/nope.html"

  val enqueueValid = "enqueue_valid"
  val enqueueUnavailable = "enqueue_unavailable"
  val cancel = "cancel"

  val onCompleted = "onCompleted"
  val onError = "onError"

  val tagV = "TagV"
  val tagU = "TagU"

  override def resetActions(c: Context, b: Callback): String = {
    rq = Volley.newRequestQueue(c);
    null
  }

  override def uniqueInputSet(): java.util.List[String] =
    List(enqueueValid, enqueueUnavailable, cancel).asJava

  def mkRequest(url: String, tag: String): StringRequest = {
    var request: StringRequest = new StringRequest(Request.Method.GET, url,
      new Response.Listener[String]() {
        override def onResponse(response: String): Unit = {
          respond(onCompleted)
        }
      },
      new Response.ErrorListener() {
        override def onErrorResponse(error: VolleyError): Unit = {
          respond(onError)
        }
      }
    )
    request.setTag(tag)
    request
  }

  override def giveInput(i: String): Unit = i match {
    case `enqueueValid` => {
      var request: StringRequest = new StringRequest(Request.Method.GET, urlV,
        new Response.Listener[String]() {
          override def onResponse(response: String): Unit = {
            respond(onCompleted)
          }
        },
        new Response.ErrorListener() {
          override def onErrorResponse(error: VolleyError): Unit = {
            respond(onError)
          }
        }
      )
      request.setTag(tagV)
      rq.add(request)
    }
    case `cancel` => rq.cancelAll(tagV)
    case _ => {
      logl("Unknown command to DownloadManager")
      throw new IllegalArgumentException("Unknown command to DownloadManager")
    }
  }

  override def shortName(): String = "RequestQueueLP"
  override def isError(o: String): Boolean = o match {
    case `onError` => true
    case _ => false
  }
  override def betaTimeout(): Int = 2000
  override def safetyTimeout(): Int = 2000
  override def singleInputs(): java.util.List[String] =
    List(enqueueValid, enqueueUnavailable).asJava
}
