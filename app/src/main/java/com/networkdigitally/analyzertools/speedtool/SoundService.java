package com.networkdigitally.analyzertools.speedtool;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class SoundService extends Service {
    private final IBinder binder = new SoundService.LocalBinder();

    private boolean paused = true;

    private int duration = 1; // seconds
    private int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double sample2[] = new double[numSamples];
    private final byte generatedSnd[] = new byte[2 * numSamples];
    private AudioTrack audioTrack;

    private double freqOfTone = 500; // hz
    private int delay = 0; // sample

    public static final int MIN_FREQUENCY_VALUE = 100;
    public static final int MAX_FREQUENCY_VALUE = 4000;
    public static final int MIN_DELAY_VALUE = 0;
    public static final int MAX_DELAY_VALUE = 441;

    private SoundService.AudioControlTask audioControlTask = null;
    private SoundService.AudioUpdateTask audioUpdateTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STREAM);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (audioControlTask != null) {
            audioControlTask.cancel(true);
            audioControlTask = null;
        }

        if (audioUpdateTask != null) {
            audioUpdateTask.cancel(true);
            audioUpdateTask = null;
        }

        if (audioTrack != null)
            audioTrack.release();

        super.onDestroy();
    }

    public int getCurrentFrequency() {
        return (int)freqOfTone;
    }

    public int getCurrentDelay() {
        return delay;
    }

    /**
     * @return If the music is currently paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Pauses music if it is currently playing
     */
    public void pauseMusic() {
        if (!paused) {
            paused = true;

            audioTrack.pause();

            if (audioControlTask != null) {
                audioControlTask.cancel(true);
                audioControlTask = null;
            }

            if (audioUpdateTask != null) {
                audioUpdateTask.cancel(true);
                audioUpdateTask = null;
            }
        }
    }

    /**
     * Resumes the music if it is currently paused
     */
    public void resumeMusic() {
        if (paused) {
            paused = false;

            if (audioUpdateTask == null) {
                audioUpdateTask = new SoundService.AudioUpdateTask();
                audioUpdateTask.execute();
            }
        }
    }

    /**
     * Update frequency
     */
    public void updateFrequency(double delta) {
        try {
            freqOfTone += delta;
            if (freqOfTone <= MIN_FREQUENCY_VALUE)
                freqOfTone = MIN_FREQUENCY_VALUE;
            else if (freqOfTone >= MAX_FREQUENCY_VALUE)
                freqOfTone = MAX_FREQUENCY_VALUE;
        } catch (Exception e) {

        }
    }

    /**
     * Update delay
     */
    public void updateDelay(double delta) {
        try {
            delay += delta/10;
            if (delay <= MIN_DELAY_VALUE)
                delay = MIN_DELAY_VALUE;
            else if (delay >= MAX_DELAY_VALUE)
                delay = MAX_DELAY_VALUE;
        } catch (Exception e) {

        }
    }

    private void genTone() {
        for (int i = 0; i < numSamples; ++i) {
            //  float angular_frequency =
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));

            if (i + delay + 1 > numSamples)
                sample2[i] = Math.sin(2 * Math.PI * i / (sampleRate / (freqOfTone * 2)));
            else
                sample2[i] = Math.sin(2 * Math.PI * i / (sampleRate / (freqOfTone * 2)))+sample[i+delay];
        }
        int idx = 0;

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        for (double dVal : sample2) {
            short val = (short) (dVal * 32767);
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        audioTrack.write(generatedSnd, 0, numSamples);
    }

    /**
     * Converts milliseconds to a Human readable format
     *
     * @param milliseconds time in milliseconds
     * @return Human readable representation of the time
     */
    private String getHumanReadableTime(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    public class LocalBinder extends Binder {
        public SoundService getService() {
            return SoundService.this;
        }
    }

    private class AudioUpdateTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            genTone();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            audioControlTask = new SoundService.AudioControlTask();
            audioControlTask.execute();
        }

        @Override
        protected void onCancelled() {
            if (audioControlTask != null) {
                audioControlTask.cancel(true);
                audioControlTask = null;
            }
        }
    }

    private class AudioControlTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int playState = audioTrack.getPlayState();
            if (playState == AudioTrack.PLAYSTATE_PAUSED ||
                    playState == AudioTrack.PLAYSTATE_STOPPED)
                audioTrack.play();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            audioUpdateTask = new SoundService.AudioUpdateTask();
            audioUpdateTask.execute();
        }

        @Override
        protected void onCancelled() {
            audioTrack.pause();
        }
    }
}

