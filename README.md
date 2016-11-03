# Starling #

## Running the experiments ##

An
[`Experiment`](app/src/main/java/edu/upenn/aradha/starling/Experiment.java)
tests a list of `LearningPurpose`s in sequence, allowing you to fetch
the results all at once at the end.

1. Edit
   [MainActivity.java](app/src/main/java/edu/upenn/aradha/starling/MainActivity.java),
   adding the classes you'd like to test the the `purposes` list.
2. Build and install the app (how you do this depends on your setup).
3. Tap the app on the phone to run it; you should just get a white
   screen.
4. Observe the experiments' progress by running `adb logcat | grep
   'STARLING:Q\|TRANSDUCER:Q'`

The classes you can choose from for experiments are found in the
[lp](app/src/main/java/edu/upenn/aradha/starling/droidStar/lp) source
code directory.

### Running a certain class multiple times ###

If you are suspicious as to the determinism of a particular learing
purpose, you might want to test it two or three times and compare the
results.

If you run the exact same class twice in a single experiment, the
second run will overwrite the results of the first before you get a
chance to see them.  To get around this, extend the class you want
with a unique `shortName()`; you'll be able to get back the results
for each run.  You can make this reasonably concise with anonymous
classes:

    purposes.add(new AsyncTaskLP(this){
            public String shortName() {return "AsyncTask-Run1";}
        });
    purposes.add(new AsyncTaskLP(this){
            public String shortName() {return "AsyncTask-Run2";}
        });
    purposes.add(new AsyncTaskLP(this){
            public String shortName() {return "AsyncTask-Run3";}
        });
    
    Experiment.experiment(this, purposes);

## Fetching the results ##

Make sure you have the latest master of `Patchr`, and that you have
`adb` and `dot` (graphviz) in your path.

1. `cd Patchr`
2. `./fetch-results --fetch DEVICE_ID` (the device id is only
   necessary if you have multiple devices connected to your computer)
3. This should pull the data and diagrams from the phone and put them
   in the `Patchr/results` directory, building the diagram pngs with
   `dot`.
4. Note that this will overwrite results with the same name in the
   `results` directory; review your changes before git-committing to
   make sure you want the new ones over the old ones.

**Also note: The phone deletes previous all previously stored results
each time the app is run!  Make sure to fetch results between runs.**

## Interpreting the results ##

When you fetch the results of an experiment from the phone, you get
back three files.

- `${Class}-data.txt`: Metric data, to go in a table
    - `data`: Time-stamp when experiment completed, maybe useful to
      keep you from mixing up current and previous results
    - `time`: Length of time (in milliseconds) that the experiment
      took
    - `mqueries`: Number of membership queries that were run
    - `equeries`: Number of equivalence queries that were made
    - `emqueries`: Number of membership queries that were made
      inside the equivalence queries (mqueries does not include these)
      
- `${Class}-diagram.gv`: A diagram of the learned automaton that can
  be rendered with `dot`
  
- `${Class}-log.txt`: A list of all membership queries that were run
  during the experiment, in order(?).  If running an experiment twice
  produces different results, comparing the log files may help explain
  why.
