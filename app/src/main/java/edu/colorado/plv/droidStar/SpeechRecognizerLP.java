package edu.colorado.plv.droidStar;

import java.util.List;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler.Callback;
import android.content.Intent;
import android.content.Context;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import static edu.colorado.plv.droidStar.Static.*;

public class SpeechRecognizerLP implements LearningPurpose {

    private SpeechRecognizer sr;
    private Context context;
    private Callback forOutput;

    private static Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                  "edu.colorado.plv.droidStar")
        .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5)
        .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

    public static String START = "start";
    public static String STOP = "stop";
    public static String CANCEL = "cancel";

    public List<String> inputSet() {
        List<String> is = new ArrayList();
        is.add(START);
        is.add(STOP);
        is.add(CANCEL);
        return is;
    }

    private static void logl(String m) {
        log("PURPOSE", m);
    }

    public boolean isError(String o) {
        if (o == "onError:8") {
            return true;
        } else if (o == "onError:5") {
            return true;
        } else {
            return false;
        }       
    }

    public int betaTimeout() {
        return 9000;
    }

    SpeechRecognizerLP(Context c) {
        this.context = c;
    }

    public void reset(Callback c) {
        this.sr = SpeechRecognizer.createSpeechRecognizer(this.context);
        sr.setRecognitionListener(new Listener(c));
        logl("LP has been reset.");
    }

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

    public class Listener implements RecognitionListener {
        private Callback forOutput;

        private void logcb(String callbackName) {
            logl("Callback received: " + callbackName);
        }

        private void logcf(String callbackName) {
            logl("Callback reported: " + callbackName);
        }

        Listener(Callback c) {
            this.forOutput = c;
            logl("STARTED A PURPOSE LISTENER!!!");
        }

        private void respond(String output) {
            logcb(output);
            // forOutput.handleMessage(quickMessage(output));
            logcf(output);
        }

        public void onReadyForSpeech(Bundle params) {
            respond("onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            respond("onBeginningOfSpeech");
        }

        public void onEndOfSpeech() {
            respond("onEndOfSpeech");
        }

        public void onError(int error) {
            respond("onError:" + error);
        }

        public void onResults(Bundle results) {
            respond("results!");
        }

        public void onPartialResults(Bundle partialResults) {
            // Doesn't state-change (I assume?), not important
            // logcb("some results...");
            // respond("some results...");
        }

        public void onEvent(int eventType, Bundle params) {
            logcb("event?");
            respond("event?");
        }

        public void onRmsChanged(float rmsdB) {
            // Too noisy!
            // logcb("seems the rms has changed.");
        }

        public void onBufferReceived(byte[] buffer) {
            logcb("buff aquired");
            respond("buff aquired");
        }

    }
}
