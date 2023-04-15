package com.accident.warning.system.app.utils;

import android.os.Handler;
import android.os.HandlerThread;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SimpleCountDownTimer {
    private OnCountDownListener onCountDownListener;
    private long fromMinutes;
    private long fromSeconds;
    private long delayInSeconds = 1;
    private Calendar calendar = Calendar.getInstance();
    private long seconds, minutes;
    private boolean finished;
    private Handler handler = new Handler();
    private HandlerThread handlerThread;
    private boolean isBackgroundThreadRunning;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private Runnable runnable = this::decrementMinutes;

    public SimpleCountDownTimer(long fromMinutes, long fromSeconds, long delayInSeconds, OnCountDownListener onCountDownListener) {
        if (fromMinutes <= 0 && fromSeconds <= 0) {
            throw new IllegalStateException(getClass().getSimpleName() + " can't work in state 0:00");
        }
        if (delayInSeconds > 1) {
            this.delayInSeconds = delayInSeconds;
        }
        this.onCountDownListener = onCountDownListener;

        setCountDownValues(fromMinutes, fromSeconds);
    }

    private void setCountDownValues(long fromMinutes, long fromSeconds) {
        this.fromMinutes = fromMinutes;
        this.fromSeconds = fromSeconds;
        minutes = this.fromMinutes;

        if (fromMinutes > 0 && fromSeconds <= 0) {
            seconds = 0;
            return;
        }

        if (fromSeconds <= 0 || fromSeconds > 59) {
            seconds = 59;
            return;
        }

        seconds = this.fromSeconds;
    }

    public long getSecondsTillCountDown() {
        return seconds;
    }

    public long getMinutesTillCountDown() {
        return minutes;
    }

    public void setTimerPattern(String pattern) {
        if (pattern.equalsIgnoreCase("mm:ss") || pattern.equalsIgnoreCase("m:s") || pattern.equalsIgnoreCase("mm") ||
                pattern.equalsIgnoreCase("ss") || pattern.equalsIgnoreCase("m") || pattern.equalsIgnoreCase("s"))
            simpleDateFormat.applyPattern(pattern);
    }

    public final void runOnBackgroundThread() {
        if (isBackgroundThreadRunning) {
            return;
        }

        handlerThread = new HandlerThread(getClass().getSimpleName());
        startBackgroundThreadIfNotRunningAndEnabled();
        handler = new Handler(handlerThread.getLooper());
    }

    private void startBackgroundThreadIfNotRunningAndEnabled() {
        if (handlerThread != null && !handlerThread.isAlive()) {
            handlerThread.start();
            isBackgroundThreadRunning = true;
        }
    }

    @NotNull
    private String getCountDownTime() {
        calendar.set(Calendar.MINUTE, (int) minutes);
        calendar.set(Calendar.SECOND, (int) seconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    private void decrementMinutes() {
        seconds--;

        if (minutes == 0 && seconds == 0) {
            finish();
        }

        if (seconds < 0) {
            if (minutes > 0) {
                seconds = 59;
                minutes--;
            }
        }
        runCountdown();
    }

    private void finish() {
        onCountDownListener.onCountDownFinished();
        finished = true;
        pause();
    }

    private void decrementSeconds() {
        handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(delayInSeconds));

    }

    public final void start(boolean resume) {

        if (!resume) {
            setCountDownValues(fromMinutes, fromSeconds);
            finished = false;
        }

        runCountdown();
    }

    private void runCountdown() {
        if (!finished) {
            updateUI();
            decrementSeconds();
        }
    }

    private void updateUI() {
        onCountDownListener.onCountDownActive(getCountDownTime());
    }

    public final void pause() {
        handler.removeCallbacks(runnable);
    }

    public interface OnCountDownListener {
        void onCountDownActive(String time);

        void onCountDownFinished();
    }
}