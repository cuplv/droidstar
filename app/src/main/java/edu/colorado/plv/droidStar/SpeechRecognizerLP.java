package edu.colorado.plv.droidStar;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.content.Context;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import static edu.colorado.plv.droidStar.Static.TAG;
import static edu.colorado.plv.droidStar.Static.log;
import static edu.colorado.plv.droidStar.Static.logcb;

public class SpeechRecognizerLP {

    private SpeechRecognizer sr;

    private static Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                  "edu.colorado.plv.droidStar")
        .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5)
        .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

    SpeechRecognizerLP(Activity a) {
        sr = SpeechRecognizer.createSpeechRecognizer(a);
    }

    public void query(Alphabet input) {
        sr.setRecognitionListener(new Listener());
        handleInput(input);
    }

    public void handleInput(Alphabet input) {
        switch(input) {
        case START:
            log("Invoking \"startListening()\"...");
            sr.startListening(intent);
            break;
        case STOP:
            log("Invoking \"stopListening()\"...");            
            sr.stopListening();
            break;
        case CANCEL:
            log("Invoking \"cancel()\"...");            
            sr.cancel();
            break;
        }
    }

    public enum Alphabet { START, STOP, CANCEL }

    public class Listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
            logcb("onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            logcb("onBeginningOfSpeech");
        }

        public void onEndOfSpeech() {
            logcb("onEndOfSpeech");
        }

        public void onError(int error) {
            logcb("onError");
        }

        public void onResults(Bundle results) {
            logcb("results!");
        }

        public void onPartialResults(Bundle partialResults) {
            logcb("some results...");
        }

        public void onEvent(int eventType, Bundle params) {
            logcb("event?");
        }

        public void onRmsChanged(float rmsdB) {
            // Too noisy!
            // logcb("seems the rms has changed.");
        }

        public void onBufferReceived(byte[] buffer) {
            logcb("buff aquired");
        }

    }
}
