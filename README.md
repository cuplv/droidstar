# DroidStar #

DroidStar is an Android testing tool that generates behavioral
specifications for Android classes that explain how and when their
callbacks occur.

The most important interface for using this tool is the
[`LearningPurpose` abstract class][1] which defines test harnesses for
classes you are interested in.  Examples can be found in the
[`DroidStar Experiments`][2] repository.

DroidStar is a research implementation of the callback typestate
learning technique described in the upcoming ICSE'18 paper
[*DroidStar: Callback Typestates for Android Classes*][3].

## Building the library ##

You will need the [`sbt`](http://www.scala-sbt.org/) scala/java build
tool installed on your system.  Please follow [the installation
directions on the `sbt` site](http://www.scala-sbt.org/download.html).

If you have the Android SDK installed on your system, make sure the
environment variable `ANDROID_HOME` in pointing to the SDK root
directory.  If you don't have it, `sbt` will fetch it for you when you
package the library with `sbt package`.


[1]: https://github.com/cuplv/droidstar/tree/master/src/main/java/edu/colorado/plv/droidstar/LearningPurpose.java
[2]: https://github.com/cuplv/droidstar-experiments
[3]: https://arxiv.org/abs/1701.07842
