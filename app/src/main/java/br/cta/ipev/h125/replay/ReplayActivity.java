package br.cta.ipev.h125.replay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.R;
import br.cta.isad.Display;

public class ReplayActivity extends AppCompatActivity {

    // =====================================================
    // UI
    // =====================================================

    private Spinner spinnerFiles;

    private Button btnLoad;

    private Button btnPlay;

    private Button btnPause;

    private SeekBar seekBar;

    private TextView txtInfo;

    // =====================================================
    // REPLAY
    // =====================================================

    private ReplayEngine replayEngine;

    private FlightLogReader logReader;

    private ReplayFileManager fileManager;

    private final List<File> logFiles =
            new ArrayList<>();

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    // =====================================================
    // LIFECYCLE
    // =====================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        initViews();
        setupReplay();
        loadFiles();
    }

    // =====================================================
    // INIT
    // =====================================================

    private void initViews() {

        spinnerFiles =
                findViewById(R.id.spinnerFiles);

        btnLoad =
                findViewById(R.id.btnLoad);

        btnPlay =
                findViewById(R.id.btnPlay);

        btnPause =
                findViewById(R.id.btnPause);

        seekBar =
                findViewById(R.id.seekBarReplay);

        txtInfo =
                findViewById(R.id.txtInfo);
    }

    // =====================================================
    // SETUP
    // =====================================================

    private void setupReplay() {

        fileManager =
                new ReplayFileManager(this);

        logReader =
                new FlightLogReader();

        btnLoad.setOnClickListener(v -> loadReplay());

        btnPlay.setOnClickListener(v -> {

            if (replayEngine != null) {
                replayEngine.play();
            }
        });

        btnPause.setOnClickListener(v -> {

            if (replayEngine != null) {
                replayEngine.pause();
            }
        });

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser
                                && replayEngine != null) {

                            replayEngine.seek(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {

                    }
                });
    }

    // =====================================================
    // LOAD FILES
    // =====================================================

    private void loadFiles() {

        logFiles.clear();

        logFiles.addAll(
                fileManager.getFlightLogs()
        );

        List<String> names =
                new ArrayList<>();

        for (File f : logFiles) {
            names.add(f.getName());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        names
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerFiles.setAdapter(adapter);
    }

    // =====================================================
    // LOAD REPLAY
    // =====================================================

    private void loadReplay() {

        int index = spinnerFiles.getSelectedItemPosition();

        if (index < 0
                || index >= logFiles.size()) {
            return;
        }

        File selected = logFiles.get(index);

        List<ReplayFrame> frames = logReader.read(selected);

        replayEngine = new ReplayEngine(frames);

        // =============================================
        // DISPLAY REPLAY
        // =============================================

        replayEngine.addDisplay(cvt -> {

            handler.post(() -> {

                updateUI(cvt);

                seekBar.setProgress(
                        replayEngine.getCurrentIndex()
                );
            });
        });

        seekBar.setMax(frames.size());

        txtInfo.setText(
                "Frames: " + frames.size()
        );
    }

    // =====================================================
    // UPDATE UI
    // =====================================================

    private void updateUI(double[] cvt) {

        double time =
                cvt[Index.TEMPO.ordinal()];

        double alt =
                cvt[Index.TOP.ordinal()];

        double gs =
                cvt[Index.LAT.ordinal()];

        double hdg =
                cvt[Index.RALT.ordinal()];

        String text =
                "TIME: " + time
                        + "\nALT: " + alt
                        + "\nGS: " + gs
                        + "\nHDG: " + hdg;

        txtInfo.setText(text);

        // =================================================
        // AQUI VOCÊ ATUALIZA:
        //
        // charts
        // gauges
        // instrumentos
        // mapas
        // etc
        // =================================================
    }

    // =====================================================
    // DESTROY
    // =====================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (replayEngine != null) {
            replayEngine.pause();
        }
    }

}