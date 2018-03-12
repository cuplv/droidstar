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



[1]: https://github.com/cuplv/droidstar/tree/master/src/main/scala/edu/colorado/plv/droidstar/experiments/MainActivity.scala
[2]: https://github.com/cuplv/droidstar/tree/master/src/main/scala/edu/colorado/plv/droidstar/experiments/lp
[3]: https://github.com/cuplv/droidstar/tree/master/src/main/java/edu/colorado/plv/droidstar/experiments/lp
[4]: https://github.com/cuplv/droidstar-lib/tree/master/src/main/java/edu/colorado/plv/droidstar/LearningPurpose.java
[5]: https://github.com/cuplv/droidstar-lib
