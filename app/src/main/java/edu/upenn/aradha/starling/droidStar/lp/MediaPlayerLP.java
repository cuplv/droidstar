package edu.upenn.aradha.starling.droidStar.lp;

import java.util.List;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler.Callback;
import android.content.Intent;
import android.content.Context;

import android.media.MediaPlayer;

import edu.colorado.plv.droidStar.LearningPurpose;

import static edu.colorado.plv.droidStar.Static.*;

public class MediaPlayerLP extends LearningPurpose {
    protected MediaPlayer mp;

    protected static String demoURL =
        "https://csel.cs.colorado.edu/~krsr8608/panjatheme.mp3";

    // INPUTS
    public static String PREPARE = "Prepare";
    public static String PREPARE_ASYNC = "PrepareAsync";
    public static String STOP = "Stop";
    public static String PAUSE = "Pause";
    public static String RESET = "Reset";
    public static String RELEASE = "Release";
    public static String SET_DATA_SOURCE = "SetDataSource";
    public static String START = "Start";

    protected List<String> uniqueInputSet() {
        List<String> inputs = new ArrayList();
        inputs.add(PREPARE);
        inputs.add(PREPARE_ASYNC);
        inputs.add(STOP);
        inputs.add(PAUSE);
        inputs.add(RESET);
        inputs.add(RELEASE);
        inputs.add(SET_DATA_SOURCE);
        inputs.add(START);

        return inputs;
    }

    // OUTPUTS
    public static String BUFFERING_UPDATE = "BufferingUpdate";
    public static String COMPLETION = "Completion";
    public static String INFO = "Info";
    public static String PREPARED = "Prepared";
    public static String SEEK_COMPLETE = "SeekComplete";
    public static String VIDEO_SIZE_CHANGED = "VideoSizeChanged";
    public static String MP_ERROR = "Error";

    public boolean isError(String output) {
        return output.equals(MP_ERROR);
    }

    public String shortName() {
        return "MediaPlayer";
    }

    public int betaTimeout() {
        return 500;
    }

    public MediaPlayerLP(Context c) {
        super(c);
        this.mp = null;
    }

    protected void resetActions(Context context, Callback callback) {
        hardReset();
    }

    protected void hardReset() {
        if (this.mp != null) { this.mp.release(); }
        
        this.mp = new MediaPlayer();
        Listener listener = new Listener();

        // register all callbacks
        this.mp.setOnBufferingUpdateListener(listener);
        this.mp.setOnCompletionListener(listener);
        this.mp.setOnErrorListener(listener);
        this.mp.setOnInfoListener(listener);
        this.mp.setOnPreparedListener(listener);
        this.mp.setOnSeekCompleteListener(listener);
        this.mp.setOnVideoSizeChangedListener(listener);
    }

    protected void setDataSourceC() throws Exception {
        this.mp.setDataSource(demoURL);
    }

    public void giveInput(String input) throws Exception {
        if (input.equals(SET_DATA_SOURCE)) {
            setDataSourceC();
        } else if (input.equals(PREPARE)) {
            this.mp.prepare();
        } else if (input.equals(PREPARE_ASYNC)) {
            this.mp.prepareAsync();
        } else if (input.equals(START)) {
            this.mp.start();
        } else if (input.equals(STOP)) {
            this.mp.stop();
        } else if (input.equals(PAUSE)) {
            this.mp.pause();
        } else if (input.equals(RESET)) {
            this.mp.reset();
        } else if (input.equals(RELEASE)) {
            this.mp.release();
        } else {
            logl("Unknown command to MediaPlayer");
            throw new IllegalArgumentException("Unknown command to MediaPlayer");
        }
    }

    

    public class Listener
            implements
            MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener,
            MediaPlayer.OnInfoListener,
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnVideoSizeChangedListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            respond(MP_ERROR);
            return true;
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            // respond(BUFFERING_UPDATE);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            respond(COMPLETION);
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            respond(INFO);
            return true;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            respond(PREPARED);
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            respond(SEEK_COMPLETE);
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            respond(VIDEO_SIZE_CHANGED);
        }
    }
}
