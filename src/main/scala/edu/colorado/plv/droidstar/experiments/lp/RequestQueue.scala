package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._
import edu.colorado.plv.droidstar.LearningPurpose
import edu.colorado.plv.droidstar.Static._

import com.android.volley.{RequestQueue, Request, Response}
import com.android.volley.toolbox.{Volley, StringRequest}

class RequestQueueLP(c: Context) extends LearningPurpose(c) {
  var rq: RequestQueue = null

  val url: String = "https://www.octalsrc.org/index.html"

  val enqueueValid = "enqueue_valid"
  val enqueueUnavailable = "enqueue_unavailable"
  val remove = "remove"

  val onCompleted = "onCompleted"
  val onError = "onError"

  override def resetActions(c: Context, b: Callback): String = {
    rq = Volley.newRequestQueue(c);
  }

  override def uniqueInputSet(): java.util.list[String] =
    List(enqueueValid).asJava

  override def giveInput(i: String): Unit = i match {
    case `enqueueValid` => {
      val stringRequest: StringRequest = new StringRequest(Request.Method.GET, url,
        new Response.Listener[String]() {
          override def onResponse(response: String): Unit = {
            reponse(onCompleted)
          }
        },
        new Response.ErrorListener() {
          override def onErrorResponse(error: VolleyError): Unit = {
            response(onError)
          }
        }
      )
    }
    case _ => {
      logl("Unknown command to DownloadManager")
      throw new IllegalArgumentException("Unknown command to DownloadManager")
    }
  }

  override def shortName(): String = "RequestQueueLP"
  override def isError(o: String): Boolean = false
  override def betaTimeout(): Int = 2000
  override def safetyTimeout(): Int = 2000
}
