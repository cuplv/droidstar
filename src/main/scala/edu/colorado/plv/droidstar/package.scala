package edu.colorado.plv

import android.app.Activity;

import scala.collection.JavaConverters._

package object droidstar {

  val experiment: (Activity,java.util.List[LearningPurpose]) => Unit =
    edu.upenn.aradha.starling.Experiment.experiment _

  def onlyOneOf(ss: Traversable[String])(q: java.util.Queue[String]): Boolean = {
    val query: Traversable[String] = q.asScala
    val op: (String => Boolean,String) => (String => Boolean) = {
      case (a,b) => { (s: String) => a(s) || s == b }
    }
    val pr: String => Boolean = ss.foldLeft[String => Boolean](_ => false)(op)
    query.count(pr) <= 1
  }
}
