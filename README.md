# DroidStar Experiments #

## Running the experiments ##

An experiment tests a list of `LearningPurpose` instances in sequence,
allowing you to fetch the results all at once at the end.

1. Edit [`MainActivity.scala`][1], adding the classes you'd like to
   test the the `purposes` list.
2. Build and install the app (as described above).
3. Tap the app on the phone to run it; you should just get a white
   screen.
4. Observe the experiments' progress by running `adb logcat | grep
   'STARLING:Q\|TRANSDUCER:Q'`

The classes you can choose from for experiments are found in the
[lp][2] source code directory (and also in the older [java lp][3]
directory; they inmplement the same interface).  If you would like to
experiment on a new class, you can use these as examples to write a
new `LearningPurpose` for it.  Also, check out the [`LearningPurpose`
class source file][4] for some useful comments on what its various
methods and options are for.


## Fetching the results ##

Make sure you have `adb` and `dot` (graphviz) in your path, and that
your phone is connected by USB and in USB Debugging Mode.  From the
root directory of the application:

1. `./fetch-results --fetch DEVICE_ID` (the device id is only
   necessary if you have multiple devices connected to your computer)
2. This should pull the data and diagrams from the phone and put them
   in the `./results` directory, building the diagram pngs with `dot`.
3. Note that this will overwrite results with the same name in the
   `results` directory!  Move or rename results you wish to keep
   before running the experiment again.

**Also note: The phone deletes all previously stored results each time
the app is run!  Make sure to fetch results between runs.**


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


[1]: https://github.com/cuplv/droidstar-experiments/tree/master/src/main/scala/edu/colorado/plv/droidstar/experiments/MainActivity.scala
[2]: https://github.com/cuplv/droidstar-experiments/tree/master/src/main/scala/edu/colorado/plv/droidstar/experiments/lp
[3]: https://github.com/cuplv/droidstar-experiments/tree/master/src/main/java/edu/colorado/plv/droidstar/experiments/lp
[4]: https://github.com/cuplv/droidstar/tree/master/src/main/java/edu/colorado/plv/droidstar/LearningPurpose.java
