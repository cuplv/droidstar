# DroidStar #

DroidStar is an active learning tool that synthesizes behavioral specifications for event-driven framework classes that explain how and when their callbacks occur.

In Android application programming, understanding when the application is allowed to call into the framework (callins) and when the framework may call back to the application (callbacks) is difficult. The object-oriented type of Android classes does not provide this extra “callback typestate” information, and writing manual tests for different sequences of asynchronous events is extremely tedious. DroidStar automatically chooses and executes test sequences in order to fully explore the possible states of an Android class, using the results of these tests to generate a specification of all possible callin and callback orderings. This specification can be used by the developer of the class to check that their implementation does what they intend, and it can serve as documentation to quickly explain to users of the class how to interact with it.

This repository holds a growing set of specification
experiments which can be quickly run with a template Android
application.

## Quick start ##

*Your environment will need: `git`, `dot` (a graph-drawing program
provided by the [graphviz](https://www.graphviz.org/) package), and
[`sbt`](https://www.scala-sbt.org/).*

Clone the repository and connect your Android device.

    $ git clone --recurse-submodules https://github.com/cuplv/droidstar
    $ sudo adb start-server
    $ # Connect your device
    $ adb devices

If `adb devices` lists your device, you are good to go.

Now give a command to `sbt` that will build, install, and run the
experiment application on your device.

    $ sbt android:run

You should see an empty screen appear on your device.  You can now
follow the experiment's progress.

    $ ./track-progress

By default, the experiment application will learn the callback
typestate for the `AsyncTask` class.  The tracker will tell you when
the experiment is complete (and your device will return to its
home-screen).  At this point you can fetch the results from the
device.

    $ ./fetch-results
    
This command will place the results of the experiment in a `./results`
directory.  This directory will contain a `.png` image that shows the
learned callback typestate for the `AsyncTask` class.


## Interpreting the results ##

When you fetch the results of an experiment from the phone, you get
back three files.

- `${Class}-data.txt`: Metric data, to go in a table
    - `date`: Time-stamp when experiment completed, maybe useful to
      keep you from mixing up current and previous results
    - `time`: Length of time (in milliseconds) that the experiment
      took
    - `mqueries`: Number of membership queries that were asked
      (including ones answered by the cache)
    - `rmqueries`: Number of unique membership queries that were run
      (not answered by the cache)
    - `equeries`: Number of equivalence queries that were made
    - `emqueries`: Number of membership queries that were made
      inside the equivalence queries
    - `ermqueries`: Number of unique membership queries that were made
      inside the equivalence queries
      
- `${Class}-diagram.gv`: A diagram of the learned automaton that can
  be rendered with `dot`
  
- `${Class}-log.txt`: A list of all membership queries that were run
  during the experiment, in order(?).  If running an experiment twice
  produces different results, comparing the log files may help explain
  why.

## Running other experiments ##

An experiment tests a list of `LearningPurpose` instances in sequence,
allowing you to fetch the results all at once at the end.  You can
edit [`MainActivity.scala`][1], to change the classes you'd like to
test by adding them to the `purposes` list.

The classes you can choose from for experiments are found in the
[lp][2] source code directory (and also in the older [java lp][3]
directory; they inmplement the same interface).  If you would like to
experiment on a new class, you can use these as examples to write a
new `LearningPurpose` for it.  Also, check out the [`LearningPurpose`
class source file][4] for some useful comments on what its various
methods and options are for.


## Writing an experiment ##

In order to perform your own experiment on a class you are interested
in, you must write an instance of the `LearningPurpose` abstract class
that tells `droidstar` how to explore its behavior.

We begin with the boiler-plate imports.  Add any imports you need
here, and replace the name `AsyncTaskLP` with whatever your experiment
should be called.  By convention, we name them `$(class under
study)LP`.

    package edu.colorado.plv.droidstar
    package experiments.lp
    
    import android.content.Context
    import android.os.Handler.Callback
    import android.os.AsyncTask
    import scala.collection.JavaConverters._
    
    class AsyncTaskLP(c: Context) extends LearningPurpose(c) {


Now that we are defining our subclass, it is helpful to start by
defining the various identifiers you will use up front, so that they
are not mis-typed later.  These `String` identifiers will be
associated with code snippets and used to mark results in the
automaton that is produced.

You will need one for each distinct input and output that you are
studying.

      // inputs
      val execute = "exec"
      val cancel = "cancel"

      // outputs
      val cancelled = "on_cancelled"
      val postexec = "on_postexec"
      val preexec = "on_preexec"

    
Next, establish the mutable state that `droidstar` will work on.  If
the focus of your experiment is a singe class, such as the `AsyncTask`
class in this example, this state will simply be an object of the
class.  The object does not need to be instantiated at this point; it
will be re-initialized at the beginning of each testing round.

      var task: AsyncTask[AnyRef,AnyRef,AnyRef] = null


In most cases, you will need to extend the class you are studying in
order to instrument its callbacks with reports that `droidstar` can
see.  Here, we define a simple `AsyncTask` instance that waits a
little while as its task and reports callback identifiers (that we
defined in the previous step) using the `repsond()` method that
`LearningPurpose` provides.

      class SimpleTask(localCounter: Int) extends AsyncTask[AnyRef,AnyRef,AnyRef] {
    
        override def doInBackground(ss: AnyRef*): AnyRef = {
          try {Thread.sleep(200)}
          catch {
            case _ : Throwable => logl("Sleep problem?")
          }
          param
        }

        override def onPostExecute(s: AnyRef): Unit = respond(postexec)

        override def onPreExecute(): Unit = respond(preexec)
      }


We now define the steps `droistar` takes to set up a test.  Usually
this is a code snippet that initializes the mutable state we have
established.  Here we initialize the `task` variable we previously
declared with an instance of `SimpleTask`, disabling and discarding
any `task` left over from a previous test.

      override def resetActions(c: Context, b: Callback): String = {
        if (task != null) {
          task.cancel(true)
        }
        task = new SimpleTask(0)
        null
      }


Now define the `LearningPurpose.uniqueInputSet()`, a list of `String`
values, as the list of your input identifiers.
    
      override def uniqueInputSet(): java.util.List[String] =
        List(execute,cancel,isCancelled).asJava


This list will be used to generate test sequences.  So that `droistar`
can actually execute each test, you must associate each input
identifier in the list with a code snippet that acts on the mutable
state you have established.

      @throws(classOf[Exception])
      override def giveInput(i: String, altKey: Int): Unit = i match {
        case `execute` => task.execute("asdf")
        case `cancel` => task.cancel(false)
    
        case _ => {
          logl("Unknown command to AsyncTask")
          throw new IllegalArgumentException("Unknown command to AsyncTask")
        }
      }


Almost finished!  All that remains is a handful of optional settings
that you may need to adjust for your experiment to be useful.  The
few most important ones appear here; the full list of modifiable
settings can be found in the [`LearningPurpose` source file][4].

The `betaTimeout` is an integer representing the number of
milliseconds `droidstar` should wait to receive a callback.  This
timeout is very important; it should be greater than the amount of
time you think any callback you are tracking should take so that none
are missed.

      override def betaTimeout(): Int = 500


The `isError` function takes an output identifier (as reported by a
callback using the `respond` method) and states whether it should be
considered an error.  This is used to make an input as "not enabled"
even if it didn't throw a synchronous error.  Some callins report that
they failed via callback.

      override def isError(o: String): Boolean = false
    
We don't have any of these for `AsyncTask`.


The last option is a name that `droistar` will use to title the
results it produces.  It is important to make sure if you are running
several experiments together that each of their `LearningPurpose`s has
a different `shortName` value.

      override def shortName(): String = "AsyncTask"
    }


[1]: https://github.com/cuplv/droidstar/tree/master/src/main/scala/edu/colorado/plv/droidstar/experiments/MainActivity.scala
[2]: https://github.com/cuplv/droidstar/tree/master/src/main/scala/edu/colorado/plv/droidstar/experiments/lp
[3]: https://github.com/cuplv/droidstar/tree/master/src/main/java/edu/colorado/plv/droidstar/experiments/lp
[4]: https://github.com/cuplv/droidstar-lib/tree/master/src/main/java/edu/colorado/plv/droidstar/LearningPurpose.java
[5]: https://github.com/cuplv/droidstar-lib
