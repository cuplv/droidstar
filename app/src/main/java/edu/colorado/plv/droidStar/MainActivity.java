package edu.colorado.plv.droidStar;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.content.ContextWrapper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.content.Intent;
import android.content.Context;

import android.util.Log;

// debugging only!
import static java.lang.Thread.sleep;

public class MainActivity extends Activity {

    public SpeechRecognizer sr;

    public static final String TAG = "DROIDSTAR";

    public static final void log(String message) {
        Log.d(TAG, message);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            log("------------------------------");

            log("Testing speech rec...");
            this.sr = SpeechRecognizer.createSpeechRecognizer(this);
            this.sr.setRecognitionListener(new Listener());
            log("Recognizer initialized.");

            log("Starting recognizer...");
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                            "edu.colorado.plv.droidStar");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            this.sr.startListening(intent);

        } else {
            
            log("Speech recognition not available on this system?");
            
        }

    }

    // public void onStart() {

    @Override
    public void onStop() {
        super.onStop();
    }

    public class Listener implements RecognitionListener {

        public void logcb(String callbackName) {
            log("CALLBACK: " + callbackName);
        }

        public void onReadyForSpeech(Bundle params) {
            logcb("onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            logcb("onBeginningOfSpeech");
            // log("Stopping recognizer...");
            // sr.stopListening();
        }

        public void onEndOfSpeech() {
            logcb("onEndOfSpeech");
        }

        public void onError(int error) {
            logcb("onError");
        }

        public void onResults(Bundle results) {
            logcb("results!");
            log("Cleaning up...");
            sr.destroy();
    
            log("Experiment complete.");
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
