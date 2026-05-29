package br.cta.ipev.h125.replay;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.setup.Index;
import br.cta.isad.Display;

public class ReplayEngine {

    private static final String TAG =
            "ReplayEngine";

    // =====================================================
    // DATA
    // =====================================================

    private final List<ReplayFrame> frames;

    // reutiliza interface do LIVE MODE
    private final List<Display> displays =
            new ArrayList<>();

    // =====================================================
    // STATE
    // =====================================================

    private boolean playing = false;

    private boolean paused = false;

    private volatile int currentIndex = 0;

    private double speed = 1.0;

    private Thread replayThread;

    // fallback para logs inválidos

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ReplayEngine(List<ReplayFrame> frames) {
        this.frames = frames;
    }

    // =====================================================
    // DISPLAY
    // =====================================================

    public void addDisplay(Display display) {

        if (!displays.contains(display)) {
            displays.add(display);
        }
    }

    // =====================================================
    // PLAY
    // =====================================================

    public void play() {

        // evita criar múltiplas threads
        if (playing) {
            return;
        }

        playing = true;
        paused = false;

        replayThread = new Thread(() -> {

            Log.d(TAG, "Replay started");

            while (playing
                    && currentIndex < frames.size()) {

                try {

                    // =====================================
                    // PAUSE
                    // =====================================

                    if (paused) {

                        Thread.sleep(100);
                        continue;
                    }

                    // =====================================
                    // FRAME ATUAL
                    // =====================================

                    ReplayFrame currentFrame = frames.get(currentIndex);

                    notifyDisplays(currentFrame.values);



                    // =====================================
                    // CALCULA DELAY REAL
                    // =====================================

                    long delay = AppManager.FREQ_SAVE;

                    if (currentIndex < frames.size() - 1) {

                        ReplayFrame nextFrame = frames.get(currentIndex + 1);

                        double currentTime = currentFrame.values[Index.TEMPO.ordinal()];
                        double nextTime = nextFrame.values[Index.TEMPO.ordinal()];
                        double dtSeconds = nextTime - currentTime;

                        // proteção contra logs inválidos
                        if (dtSeconds > 0 && dtSeconds < 1.0) {
                            delay = (long)((dtSeconds * 1000.0) / speed);

                        } else {

                            Log.w(
                                    TAG, "Delta tempo inválido: " + dtSeconds
                            );
                        }
                    }

                    // =====================================
                    // ESPERA
                    // =====================================

                    if (delay > 0) {
                        Thread.sleep(delay);
                    }

                    // =====================================
                    // PRÓXIMO FRAME
                    // =====================================
                    currentIndex++;


                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    Log.d(TAG, "Replay interrompido");

                    break;

                } catch (Exception e) {

                    Log.e(TAG, "Erro replay", e);
                }
            }

            playing = false;

            Log.d(TAG, "Replay finished");

        });

        replayThread.start();
    }

    // =====================================================
    // PAUSE
    // =====================================================

    public void pause() {

        paused = true;

        Log.d(TAG, "Replay paused");
    }

    // =====================================================
    // RESTART
    // =====================================================

    public void restart() {

        currentIndex = 0;

        play();
    }

    // =====================================================
    // RESUME
    // =====================================================

    public void resume() {

        if (!playing) {

            // replay ainda não iniciado
            play();

            return;
        }

        paused = false;

        Log.d(TAG, "Replay resumed");
    }

    // =====================================================
    // STOP
    // =====================================================

    public void stop() {

        playing = false;

        paused = false;

        currentIndex = 0;

        if (replayThread != null) {

            replayThread.interrupt();

            replayThread = null;
        }

        Log.d(TAG, "Replay stopped");
    }

    // =====================================================
    // SEEK
    // =====================================================

    public synchronized void seek(int index) {

        if (index >= 0
                && index < frames.size()) {

            currentIndex = index;

            // atualiza imediatamente a tela

            ReplayFrame frame =
                    frames.get(currentIndex);

            notifyDisplays(frame.values);
        }
    }

    // =====================================================
    // SPEED
    // =====================================================

    public void setSpeed(double speed) {

        if (speed <= 0) {
            return;
        }

        this.speed = speed;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getFrameCount() {
        return frames.size();
    }

    public double getSpeed() {
        return speed;
    }

    // =====================================================
    // NOTIFY
    // =====================================================

    private void notifyDisplays(double[] cvt) {

        for (Display d : displays) {

            try {

                d.update(cvt);

            } catch (Exception e) {

                Log.e(TAG, "Erro display", e);
            }
        }
    }
}