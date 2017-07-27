package edu.colorado.plv.droidstar.experiments

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import scala.collection.JavaConverters._

import android.os.Environment

import edu.upenn.aradha.starling.Experiment
import edu.colorado.plv.droidStar.LearningPurpose

class MainActivity extends AppCompatActivity {

  def weCanWrite(): Boolean =
    Environment
      .getExternalStorageState()
      .equals(Environment.MEDIA_MOUNTED)

  var activityLP: LearningPurpose = new lp.ActivityLP(this)
  var asyncTaskLP: LearningPurpose = new lp.AsyncTaskLP(this)

  override def onStart(): Unit = {
    super.onStart()
    weCanWrite() match {
      case true => Experiment.experiment(
        this, Seq(asyncTaskLP).asJava)
      case false => println("Seems we can't report results...")
    }
  }

}
