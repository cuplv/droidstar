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

    private static void logl(String m) {
        log("MAIN", m);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            logl("------------------------------");

            logl("Testing speech rec...");

            

            Queue<String> q1 = new ArrayDeque();
            q1.add(DELTA);
            q1.add(SpeechRecognizerLP.STOP);
            q1.add(DELTA);
            q1.add(SpeechRecognizerLP.START);
            q1.add(DELTA);
            q1.add(DELTA);

            Queue<String> q2 = new ArrayDeque();
            q2.add(DELTA);
            q2.add(SpeechRecognizerLP.STOP);
            q2.add(DELTA);
            q2.add(SpeechRecognizerLP.START);
            q2.add(DELTA);
            q2.add(DELTA);


            Queue<Queue<String>> qs = new ArrayDeque();
            qs.add(q1);
            qs.add(q2);

            // AsyncTransducer ds = new AsyncTransducer(this, new SpeechRecognizerLP(this));

            // TrivialLearner learner = new TrivialLearner(qs, ds);
            // learner.learn();

            Transducer trans = new Transducer(this, new SpeechRecognizerLP(this));
            Runnable learner = new ThreadLearner(qs, trans);
            new Thread(learner).start();

        } else {
            
            logl("Speech recognition not available on this system?");
            
        }

    }

}
