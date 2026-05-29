package br.cta.ipev.h125.replay;

import android.util.Log;

import br.cta.ipev.h125.AppManager;

public class ReplayController {

    private static AppManager appManager;

    public static void init(AppManager manager) {
        appManager = manager;
    }

    public static void play() {

        if (appManager != null && appManager.isReplayMode()) {
            appManager.getReplayEngine().resume();

        }
    }

    public static void pause() {

        if (appManager != null && appManager.isReplayMode()) {
            appManager.getReplayEngine().pause();
        }
    }

    public static void reload() {

        if (appManager != null) {
            appManager.getReplayEngine().restart();
        }
    }

    public static void seek(int index) {
        if (appManager != null && appManager.isReplayMode()) {
            appManager.getReplayEngine().seek(index);
        }
    }

    public static void speed(double vel) {

        Log.d("", "speed antes: " + appManager.getReplayEngine().getSpeed());
        if (appManager != null && appManager.isReplayMode()) {
            appManager.getReplayEngine().setSpeed(vel + 1);

            Log.d("", "speed depois: " + appManager.getReplayEngine().getSpeed());
        }

    }

    public static void top(int index) {
        if (appManager != null && appManager.isReplayMode()) {
            appManager.getReplayEngine().seek(index);
        }
    }
}