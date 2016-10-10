# DroidStar - Learning Typestates in the Android Framework #

**Current status:** Buggy Transducer proof of concept.  `L*` is left
unimplemented; hoping to adapt [`LearnLib`](http://learnlib.de/) to
fit in its place.

## Usage

Connect your phone to the computer and make sure `adb` can see and
read it.  Open a terminal and start logging with:

    adb logcat | grep DROIDSTAR

Install the app on the phone (it's been tested on Android 5.1.1), and
run it.  You will be presented with a blank white screen.  All useful
output is written on the log; you should see things now in the
terminal you opened.

The output will show a proof-of-concept query run on the
SpeechRecognizer class.  You can clean up the ouput by only listening
to the Learner object with `grep DROIDSTAR:LEARNER`.  The main class
where this experiment is defined is
[`MainActivity`](app/src/main/java/edu/colorado/plv/droidStar/MainActivity.java).

## Design

This app/library is modeled on the
[*Learning IO Automata*](http://www.mbsd.cs.ru.nl/publications/papers/fvaan/LearningIOAs/paper.pdf)
paper, with classes representing the components of the algorithm for
learning an IO Automaton.

### Target IO Automaton

The IO automaton to be learned in this case is the actual Android
Framework class.  In the proof-of-concept experiment, this is the
[`SpeechRecognzer`](https://developer.android.com/reference/android/speech/SpeechRecognizer.html)
class.

### Learning Purpose

The *learning purpose* which simplifies the target Automaton and defines
which inputs are of interest is modeled by the
[`LearningPurpose` interface](app/src/main/java/edu/colorado/plv/droidStar/LearningPurpose.java).
In the current experiment, this is implemented by the
[`SpeechRecognizerLP` class](app/src/main/java/edu/colorado/plv/droidStar/SpeechRecognizerLP.java).

Writing the `LearningPurpose` instance for a framework class includes
determining which callback outputs are to be considered real
state-changing outputs and which errors are relevant to the inputs
being queried.  For example, the `SpeechRecognizer`'s `ERROR_CLIENT`
error indicates that the class was misused by the calling app, and
thus the query which produced it is invalid.  The `ERROR_NO_MATCH`
error indicates that the recorded voice could not be recognized as
speech.  This indicates nothing about the validity of the current
query, and as such should probably just be ignored completely.

### Transducer and Teacher

The "Transducer" is the set of rules which turns a *learning purpose*
into a teacher compatible with Mealy Machine learners.  The transducer
is modeled in this library by the
[`Transducer` class](app/src/main/java/edu/colorado/plv/droidStar/Transducer.java).
When instantiated with a `LearningPurpose`, a `Transducer` object
implements the
[`MealyTeacher` interface](app/src/main/java/edu/colorado/plv/droidStar/MealyTeacher.java).

The `MealyTeacher` interface does not support the usual "equivalence"
query; it only has a "membership" query.  Learning using this setup
will require emulating the equivalence query with membership queries.

### Learner

The purpose of the transducer is to allow a learner for *Mealy
Machines* learn an Interface Automaton (the *learning purpose*).  To
work in this library, a Mealy Machine learner is written to interact
with the `MealyTeacher` interface.

For this experiment, the learner is represented by a dummy class (the
[`TrivialLearner`](app/src/main/java/edu/colorado/plv/droidStar/TrivialLearner.java))
which just asks the teacher for results on a series of inputs
corresponding to a list of hardcoded queries.  For producing actual
results, the hope is to adapt a class from
[`LearnLib`](http://learnlib.de/) to fill this role.

### Delta inputs

In *Learning IO Automata*, the input set of a *learning purpose* is
extended with a Δ (Delta) value.  To send a Δ as input means to allow
the IO Atomaton to process and return an output value (the assumption
is that if the learner wished, it could send any number of input
values before the automaton could provide any output).

This library is focused on framework classes that use callbacks to
provide their outputs rather than return values, and thus a Δ
following a normal input is implemented by registering the
*continuation of the experiment* as the input's callback.  After
providing the input, the learner idles until an output is returned.  A
β (beta) value is returned in the absence of any called-back ouput
after a set amount of time (defined by the `LearningPurpose`
implementer with respect to the usual response times of its class's
methods).

This design choice follows into the rest of the library;
the above components mainly communicate through asynchronous calls.

