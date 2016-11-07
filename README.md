# Starling #

## Building and installing the application ##

This depends largely on your setup.  The simplest method is to make
sure that the environment variable `ANDROID_HOME` is pointing to the
root of your installed Android SDK, and then, in the root directory of
the application (where this README is located), run:

    ./gradlew build

The application can be installed with the `adb` tool.  Your phone must
be connected by USB and have USB debugging enabled.

    adb install -r app/build/outputs/apk/app-debug.apk

## Running the experiments ##

An [`Experiment`][2] tests a list of `LearningPurpose`s in sequence,
allowing you to fetch the results all at once at the end.

1. Edit [`MainActivity.java`][3], adding the classes you'd like to
   test the the `purposes` list.
2. Build and install the app (as described above).
3. Tap the app on the phone to run it; you should just get a white
   screen.
4. Observe the experiments' progress by running `adb logcat | grep
   'STARLING:Q\|TRANSDUCER:Q'`

The classes you can choose from for experiments are found in the
[lp][4] source code directory.  If you would like to experiment on a
new class, you can use these as examples to write a new
`LearningPurpose` for it (this is described in more detail below).

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

## Writing a `LearningPurpose` instance ##

The "Learning Purpose" for a class is an abstraction of that class and
its methods that the learning algorithm uses to learn a particular
subset of its interface.  The concept (and the funny name) is adapted
from [*Learning I/O Automata*][1], a paper which forms much of the
theoretical background for this tool.

The learning algorithm which will synthesize the class's interface
operates by making "membership queries".  It asks a question
consisting of an ordered list of **input symbols**.  These inputs are
interpreted and executed, and a trace of **ouput symbols** is
returned.  These traces are used to build an automaton that simulates
the interface.

The Learning Purpose enumerates these symbols and defines their
meaning (in terms of concrete method calls).  By choosing these
symbols and meanings you can control which parts of the class's
interface you are interested in.

To write a Learning Purpose, you extend the
[`LearningPurpose` abstract class][5].  Examples of these
implementations are found in the [`lp` package][4].  The important
methods to override are described here.

### `public abstract String shortName()` ###

This is the string that will be used to refer to the class in logs and
the final results.  It will usually just be the name of the target
class.  For example, in the `AsyncTaskLP` instance:

    public String shortName() {
        return "AsyncTask";
    }

This **must** be unique to this class.  If two classes have the same
`shortName()`, they will overwrite each others' results!

### `protected abstract List<String> uniqueInputSet()` ###

This is an unordered set of `String` symbols representing the possible
inputs to the class you are learning.  This method instance, combined
with the `giveInput()` method described below, make up the interface
that the learning algorithm will used to make queries.  Here is an
example implementation from the `SpeechRecognizerLP` instance:

    public static String START = "start";
    public static String STOP = "stop";
    public static String CANCEL = "cancel";

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(START);
        inputs.add(STOP);
        inputs.add(CANCEL);

        return inputs;
    }

(defining the static strings first is just a convenience)

In the simplest case, these symbols will correspond directly to method
calls on the target class (in this case the `startListening()`,
`stopListening()`, and `cancel()` methods of the `SpeechRecognizer`
class), but they can also represent a group of calls.  The actual
meaning of the symbols is defined in the `giveInput()` implementation.

For all classes, the actual input set visible to the learning
algorithm will also include a "delta" input, which means "wait for a
callback output".

### `public abstract void giveInput(String input) throws Exception` ###

This method receives inputs from the learning algorithm, and should
do something specific for every input listed in the `uniqueInputSet()`
(it will not receive the previously mentioned "delta" input; that is
handled automatically).  Here's the example that corresponds to the
input set defined for `SpeechRecognizer`:

    public void giveInput(String input) {
        logl("LP received input \"" + input + "\"...");
        
        if (input.equals(START)) {
            logl("Invoking \"startListening()\"...");
            sr.startListening(intent);
        } else if (input.equals(STOP)) {
            logl("Invoking \"stopListening()\"...");
            sr.stopListening();
        } else if (input.equals(CANCEL)) {
            logl("Invoking \"cancel()\"...");
            sr.cancel();
        } else {
            logl("Unrecognized input received, doing nothing...");
        }
    }

(`logl(String message)` is a method of the `LearningPurpose` abstract
class, and can be used for convenient debugging messages)

### `public abstract int betaTimeout()` ###

This is the amount of time that should be allowed for a callback
output to arrive when running a "delta" input (in milliseconds).  A
half second (`500`) is usually sufficient.  In some cases (such as the
`SpeechRecognizer`), more time is necessary to allow for complicated
processes such as recording audio that take a while to call back.  The
proper amount of time can be determined by running methods in a
trivial app setup and observing how quick their responses are.

### `public abstract boolean isError(String output)` ###

The output symbols for your class don't need to be defined as
explicitly as the inputs, but it is necessary to state which should be
interpreted as errors (if any).  Here is the implementation for
`SpeechRecognizer`.

    public static String CLIENT_ERROR = "error";
    public static String ENV_ERROR = "environment_error";

    public boolean isError(String o) {
        if (o.equals(CLIENT_ERROR)) {
            return true;
        } else if (o.equals(ENV_ERROR)) {
            return true;
        } else {
            return false;
        }
    }

### `protected abstract void resetActions(Context cx, Callback cb)` ###

These actions will be executed before a new membership query, so that
each string of input symbols is starting from a common initial state.
Usually, this method will destroy the existing instance of the class
that the last query was run on and initialize a new instance for the
next query.

The `Context` passed in is that of the app's main activity, which is
used in the constructors of many Android Framework classes.

(the `Callback` is provided for legacy reasons and should not be used)

Here is an example from `CountDownTimerLP`:

    // OUTPUTS
    public static String FINISHED = "finished";
    public static String TICK = "tick";

    public class CTimer extends CountDownTimer {
        public CTimer(long s) {
            super(s, 1000);
        }
        public void onTick(long s) {
            respond(TICK);
        }
        public void onFinish() {
            respond(FINISHED);
        }
    }

    protected void resetActions(Context context, Callback callback) {
        doReset();
    }

    protected void doReset() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CTimer(1100);
    }

In this example, the `Context` was not needed.

The instance being initialized is in this case an extension of the
CountDownTimer class, with its callbacks instrumented with the
`respond(String output)`, which is used to report output symbols for
the trace that the learning algorithm receives.

### Reporting output symbols with `respond(String output)` ###

The meaning of a particular non-error output symbol `OUTPUT` is
defined by instrumenting appropriate callbacks with calls to
`respond(OUTPUT)`.  All classes that provide a callback interface
also provide an interface or abstract class for you to implement or
extend in order to use the callbacks.

In the above example for `CountDownTimer`, the `CountDownTimer` class
itself is an abstract class with callback methods `onTick()` and
`onFinish()`.  For our purposes, we just call `respond()` with the
appropriate output symbol (string).

In the following example from `SpeechRecognizerLP`, an implementation
of `RecognitionListener` provides the callbacks and is registered with
the `SpeechRecognizer` instance.  Calls to `respond()` are used in the
same way, but some callbacks which we consider equivalent for the
purposes of the interface (decisions which are tested by the learning
process) are made to call `respond()` with the same output symbol.

    //OUTPUTS
    public static String RECORDING_STARTING = "starting";
    public static String RECORDING_FINISHED = "finished";
    public static String CLIENT_ERROR = "error";
    public static String ENV_ERROR = "environment_error";

    public class Listener implements RecognitionListener {
        private Callback forOutput;

        Listener(Callback c) {
            this.forOutput = c;
            // logl("STARTED A PURPOSE LISTENER!!!");
        }

        public void onReadyForSpeech(Bundle params) {
            respond(RECORDING_STARTING);
        }

        public void onError(int error) {
            switch (error) {
            case 5: // client error
                logl("Error: Client");
                respond(CLIENT_ERROR);
                break;
            case 8: // recognizer busy
                logl("Error: Busy");
                respond(CLIENT_ERROR);
                break;
            case 3: // audio failure
            case 9: // permissions not set right
                respond(ENV_ERROR);
                break;
            case 7: // no matches
            case 6: // speech timeout
            case 1: // network timeout
            case 2: // server error
                respond(RECORDING_FINISHED);
                break;
            default: // don't acknowledge others
                break;
            }
        }

        public void onResults(Bundle results) {
            respond(RECORDING_FINISHED);
        }

        // Callbacks we don't pay attention to...
        public void onPartialResults(Bundle partialResults) {}
        public void onEvent(int eventType, Bundle params) {}
        public void onRmsChanged(float rmsdB) {}
        public void onBufferReceived(byte[] buffer) {}
        public void onBeginningOfSpeech() {}
        public void onEndOfSpeech() {}
    }

An instance of this `Listener` is then registered with the instance of
the `SpeechRecognizer` during the reset process:

    public void resetActions(Context context, Callback callback) {
        if (sr != null) sr.destroy();
        sr = SpeechRecognizer.createSpeechRecognizer(context);
        sr.setRecognitionListener(new Listener(callback));
    }

Note that in this example, the `Context` is necessary for initializing
the `SpeechRecognizer` instance.

[1]: http://www.sws.cs.ru.nl/publications/papers/fvaan/LearningIOAs/paper.pdf "Learing I/O Automata"
[2]: app/src/main/java/edu/upenn/aradha/starling/Experiment.java "Experiment.java"
[3]: app/src/main/java/edu/upenn/aradha/starling/MainActivity.java "MainActivity.java"
[4]: app/src/main/java/edu/upenn/aradha/starling/droidStar/lp "lp"
[5]: app/src/main/java/edu/colorado/plv/droidStar/LearningPurpose.java "LearningPurpose"
