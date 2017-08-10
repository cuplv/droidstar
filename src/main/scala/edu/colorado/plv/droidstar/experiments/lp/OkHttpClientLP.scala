package edu.colorado.plv.droidstar
package experiments.lp

import android.os.Handler.Callback
import android.content.{Context, BroadcastReceiver, IntentFilter, Intent}
import android.net.Uri
import scala.collection.JavaConverters._

import okhttp3.OkHttpClient

class OkHttpClientLP(c: Context) {
  val asdf: OkHttpClient = new OkHttpClient()
}
