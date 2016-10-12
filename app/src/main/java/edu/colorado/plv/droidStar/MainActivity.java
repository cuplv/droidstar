package edu.colorado.plv.droidStar;

import java.util.Queue;
import java.util.ArrayDeque;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler.Callback;

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

import static edu.colorado.plv.droidStar.Static.*;

public class MainActivity extends Activity {

    private Transducer ds;
    private TrivialLearner learner;

    private static void logl(String m) {
        log("MAIN", m);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            logl("------------------------------");

            logl("Testing speech rec...");

            this.ds = new Transducer(new SpeechRecognizerLP(this));

            Queue<String> q1 = new ArrayDeque();
            // q1.add(SpeechRecognizerLP.CANCEL);
            q1.add(SpeechRecognizerLP.START);
            q1.add(DELTA);
            q1.add(DELTA);
            q1.add(SpeechRecognizerLP.STOP);
            q1.add(DELTA);

            Queue<Queue<String>> qs = new ArrayDeque();
            qs.add(q1);

            this.learner = new TrivialLearner(qs, this.ds);
            this.learner.learn();

        } else {
            
            logl("Speech recognition not available on this system?");
            
        }

    }

}
