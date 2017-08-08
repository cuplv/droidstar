package edu.colorado.plv.droidstar

import scala.collection.JavaConverters._

package object contrib {
  def onlyOneOf(ss: Traversable[String])(q: java.util.Queue[String]): Boolean = {
    val query: Traversable[String] = q.asScala
    val op: (String => Boolean,String) => (String => Boolean) = {
      case (a,b) => { (s: String) => a(s) || s == b }
    }
    val pr: String => Boolean = ss.foldLeft[String => Boolean](_ => false)(op)
    query.count(pr) <= 1
  }
}
