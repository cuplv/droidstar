package edu.colorado.plv.droidstar
package experiments

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import scala.collection.JavaConverters._

import android.os.Environment

class MainActivity extends AppCompatActivity {

  def weCanWrite(): Boolean =
    Environment
      .getExternalStorageState()
      .equals(Environment.MEDIA_MOUNTED)

  // These MUST be lazy, so that they are created after the onCreate
  // method is invoked.
  lazy val activityLP: LearningPurpose = new lp.ActivityLP(this)
  lazy val asyncTaskLP: LearningPurpose = new lp.AsyncTaskLP(this)
  lazy val downloadLP: LearningPurpose = new lp.DownloadManagerLP(this)
  lazy val requestQueueLP: LearningPurpose = new lp.RequestQueueLP(this)
  lazy val okhttpcallLP: LearningPurpose = new lp.OkHttpCallLP(this)
  lazy val imageloaderLP: LearningPurpose = new lp.ImageLoaderLP(this)
  lazy val velocitytrackerLP: LearningPurpose = new lp.VelocityTrackerLP(this)
  lazy val mediaplayerLP: LearningPurpose = new lp.ExtraMediaPlayerLP(this)

  override def onCreate(s: Bundle): Unit = {
    super.onCreate(s)
    weCanWrite() match {
      case true => experiment(
        this, Seq(asyncTaskLP).asJava
      )
      case false => println("Seems we can't report results...")
    }
  }

}
