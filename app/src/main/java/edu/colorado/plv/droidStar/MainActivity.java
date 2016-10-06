package edu.colorado.plv.droidStar;

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

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "DROIDSTAR";

    public static final void log(String message) {
        System.out.println("[" + TAG + "] >> " + message);
    }


    @Override
    public void onStart() {
        super.onStart();

        log("Testing speech rec");

    }

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
        }

        public void onEndOfSpeech() {
            logcb("onEndOfSpeech");
        }

        public void onError(int error) {
            System.out.println("");
        }

        public void onResults(Bundle results) {
            System.out.println("");
        }

        public void onPartialResults(Bundle partialResults) {
            System.out.println("");
        }

        public void onEvent(int eventType, Bundle params) {
            System.out.println("");
        }

        public void onRmsChanged(float rmsdB) {
            System.out.println("");
        }

        public void onBufferReceived(byte[] buffer) {
            System.out.println("");
        }

    }

}
