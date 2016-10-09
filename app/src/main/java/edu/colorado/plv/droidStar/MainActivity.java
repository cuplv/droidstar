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

import static edu.colorado.plv.droidStar.Static.TAG;
import static edu.colorado.plv.droidStar.Static.log;
import static edu.colorado.plv.droidStar.Static.logcb;

// debugging only!
import static java.lang.Thread.sleep;

public class MainActivity extends Activity {

    public SpeechRecognizerLP purpose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            log("------------------------------");

            log("Testing speech rec...");
            purpose = new SpeechRecognizerLP(this);
            log("Recognizer initialized.");

            log("Starting recognizer...");
            purpose.query(SpeechRecognizerLP.Alphabet.START);

            // log("Waiting 10 seconds...");
            // try {sleep(10000);} catch (Exception e) {log("Can't sleep?");}
            // log("Experiment complete.");

        } else {
            
            log("Speech recognition not available on this system?");
            
        }

    }

    // public void onStart() {

    @Override
    public void onStop() {
        super.onStop();
    }

    // public class Listener implements RecognitionListener {

    //     public void logcb(String callbackName) {
    //         log("CALLBACK: " + callbackName);
    //     }

    //     public void onReadyForSpeech(Bundle params) {
    //         logcb("onReadyForSpeech");
    //     }

    //     public void onBeginningOfSpeech() {
    //         logcb("onBeginningOfSpeech");
    //         // log("Stopping recognizer...");
    //         // sr.stopListening();
    //     }

    //     public void onEndOfSpeech() {
    //         logcb("onEndOfSpeech");
    //     }

    //     public void onError(int error) {
    //         logcb("onError");
    //     }

    //     public void onResults(Bundle results) {
    //         logcb("results!");
    //         log("Cleaning up...");
    //         sr.destroy();
    
    //         log("Experiment complete.");
    //     }

    //     public void onPartialResults(Bundle partialResults) {
    //         logcb("some results...");
    //     }

    //     public void onEvent(int eventType, Bundle params) {
    //         logcb("event?");
    //     }

    //     public void onRmsChanged(float rmsdB) {
    //         // Too noisy!
    //         // logcb("seems the rms has changed.");
    //     }

    //     public void onBufferReceived(byte[] buffer) {
    //         logcb("buff aquired");
    //     }

    // }

}
