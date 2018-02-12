package edu.colorado.plv.droidstar.experiments.lp;

import java.util.List;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler.Callback;
import android.content.Intent;
import android.content.Context;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import edu.colorado.plv.droidstar.LearningPurpose;

import static edu.colorado.plv.droidstar.Static.*;

public class SpeechRecognizerLP extends LearningPurpose {

    private SpeechRecognizer sr;

    private static Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                  "edu.colorado.plv.droidstar.experiments")
        .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5)
        .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

    // INPUTS
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

    //OUTPUTS
    public static String RECORDING_STARTING = "starting";
    public static String RECORDING_FINISHED = "finished";
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

    // public boolean dropDoubleOutput() {
    //     return true;
    // }

    public int postResetTimeout() {
        return 500;
    }

    // public int safetyTimeout() {
    //     return 2000;
    // }

    public String shortName() {
        return "SpeechRecognizer";
    }

    public int betaTimeout() {
        return 8000;
    }

    public SpeechRecognizerLP(Context c) {
        super(c);
        this.sr = null;
    }

    public String resetActions(Context context, Callback callback) {
        if (sr != null) sr.destroy();
        sr = SpeechRecognizer.createSpeechRecognizer(context);
        sr.setRecognitionListener(new Listener(callback));
        return null;
    }

    public void giveInput(String input, int altKey) {
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

    public class Listener implements RecognitionListener {
        private Callback forOutput;

        Listener(Callback c) {
            this.forOutput = c;
            // logl("STARTED A PURPOSE LISTENER!!!");
        }

        public void onReadyForSpeech(Bundle params) {
            respond(RECORDING_STARTING);
        }

        public void onBeginningOfSpeech() {
            // Non-deterministic
            // respond(BEGIN_SPEECH);
        }

        public void onEndOfSpeech() {
            // Non-deterministic
            // respond(END_SPEECH);
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
    }
}
