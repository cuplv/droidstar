package edu.colorado.plv.droidstar.experiments

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import scala.collection.JavaConverters._

import android.os.Environment

import edu.upenn.aradha.starling.Experiment
import edu.colorado.plv.droidstar.LearningPurpose

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

  override def onCreate(s: Bundle): Unit = {
    super.onCreate(s)
    weCanWrite() match {
      case true => Experiment.experiment(
        this, Seq(requestQueueLP).asJava
      )
      case false => println("Seems we can't report results...")
    }
  }

}
