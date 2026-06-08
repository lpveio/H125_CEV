package br.cta.ipev.h125;

import android.Manifest;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.cta.ipev.commom.screen.Tab;
import br.cta.ipev.h125.classes.CoefsSAD1;
import br.cta.ipev.h125.replay.FlightLogReader;
import br.cta.ipev.h125.replay.ReplayController;
import br.cta.ipev.h125.replay.ReplayFileManager;
import br.cta.ipev.h125.replay.ReplayFrame;
import br.cta.ipev.h125.replay.ReplayTopMarker;
import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.setup.LoadingAlert;
import br.cta.ipev.h125.setup.Setup;
import br.cta.isad.IenaPacketReceiverLogger;
import br.cta.isad.UDPConnector;

public class DataViewActivity extends ActivityGroup {

    public static final String TAG = "DataViewActivity";
    private static final String PREF_FILE_NAME = "APPTipo";
    private static final int REQUEST_PERMISSIONS = 1001;
    private AppManager missionManager;
    private TabHost tabHost;
    private int AppIndex = 1;
    private ReplayFileManager fileManager;
    private LinearLayout linearReplay;
    private final List<File> logFiles = new ArrayList<>();
    private List<ReplayFrame> frames;
    private FlightLogReader logReader;
    private Button btnPlay;
    private Button btnPause;
    private Button btnReload;
    private SeekBar seekBar;
    private Spinner spinnerReplay;
    private Spinner spinnerTOP;
    private final List<ReplayTopMarker> topMarkers = new ArrayList<>();
    private LoadingAlert loadingAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // =====================================================
        // REQUEST PERMISSIONS
        // =====================================================

        checkAndRequestPermissions();

        setContentView(R.layout.activity_data_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        missionManager = (AppManager) getApplicationContext();
        loadingAlert = new LoadingAlert(this);
        fileManager = new ReplayFileManager(this);
        linearReplay = findViewById(R.id.telareplay);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnReload = findViewById(R.id.btnRestart);
        seekBar = findViewById(R.id.seekBarReplay);
        spinnerReplay = findViewById(R.id.spinnerSpeed);
        spinnerTOP = findViewById(R.id.spinnerTop);
        logReader = new FlightLogReader();

        //Setar TRUE para app para pilot
        boolean appPilot = false;


        if (appPilot) {
            createMission(1);
            createTabs(1);

        } else {
            controlReplay();
            createDialog();
        }

    }

    // =========================================================
    // PERMISSIONS
    // =========================================================

    private void checkAndRequestPermissions() {

        List<String> permissions =
                new ArrayList<>();

        // =====================================================
        // LOCATION
        // =====================================================

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION
            );
        }

        // =====================================================
        // BLUETOOTH ANDROID 12+
        // =====================================================

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            }
        }

        // =====================================================
        // STORAGE ANDROID <= 10
        // =====================================================

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {

                permissions.add(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                );
            }
        }

        // =====================================================
        // REQUEST
        // =====================================================

        if (!permissions.isEmpty()) {

            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(new String[0]),
                    REQUEST_PERMISSIONS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_PERMISSIONS) {

            for (int result : grantResults) {

                if (result !=
                        PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(
                            this,
                            "Permissões necessárias não concedidas",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }
            }

            Toast.makeText(
                    this,
                    "Permissões concedidas",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // REPLAY CONTROL
    // =========================================================

    private void controlReplay() {

        btnPlay.setOnClickListener(v -> {
            ReplayController.play();
        });

        btnPause.setOnClickListener(v -> {
            ReplayController.pause();
        });

        btnReload.setOnClickListener(v -> {
            ReplayController.reload();
            seekBar.setProgress(0);
        });

        String[] names = {"1x", "2x", "3x", "4x", "5x", "6x", "7x", "8x", "9x", "10x"};

        ArrayAdapter<String> adapterSpinner =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerReplay.setAdapter(adapterSpinner);
        spinnerReplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        Log.d(TAG, "Speed selecionado: " + i);
                        ReplayController.speed(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

        spinnerTOP.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position < 0 || position >= topMarkers.size()) {
                            return;
                        }

                        ReplayTopMarker marker = topMarkers.get(position);
                        ReplayController.top(marker.frameIndex
                        );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        ReplayController.seek(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        ReplayController.pause();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        ReplayController.play();
                    }
                });
    }

    // =========================================================
    // LOAD FRAMES
    // =========================================================

    private void loadFrames(Spinner spinnerFiles, Runnable onFinished) {
        int index = spinnerFiles.getSelectedItemPosition();

        if (index < 0 || index >= logFiles.size()) {
            return;
        }

        File selected = logFiles.get(index);
        loadingAlert.startAlertDialogO();

        new Thread(() -> {

            try {

                List<ReplayFrame> loadedFrames = logReader.read(selected);

                if (loadedFrames == null) {
                    runOnUiThread(() -> {
                        loadingAlert.closeAlertDialog();
                        Toast.makeText(DataViewActivity.this, "Erro ao carregar replay", Toast.LENGTH_LONG).show();});

                    return;
                }

                frames = loadedFrames;

                frames.sort(java.util.Comparator.comparingDouble(f -> f.values[Index.TEMPO.ordinal()]));

                extractTopMarkers();

                runOnUiThread(() -> {

                    if (onFinished != null) {
                        onFinished.run();
                    }

                    tabHost.post(() -> loadingAlert.closeAlertDialog()
                    );
                });

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(() -> {

                    loadingAlert.closeAlertDialog();
                    Toast.makeText(DataViewActivity.this, "Erro ao carregar replay", Toast.LENGTH_LONG).show();});
            }

        }).start();
    }

    // =========================================================
    // TOP MARKERS
    // =========================================================

    private void extractTopMarkers() {

        topMarkers.clear();

        int lastTop = Integer.MIN_VALUE;

        for (int i = 0; i < frames.size(); i++) {

            ReplayFrame frame = frames.get(i);

            int top =
                    (int) frame.values[Index.TOP.ordinal()];

            if (top <= 0) {
                continue;
            }

            if (top != lastTop) {

                ReplayTopMarker marker =
                        new ReplayTopMarker(
                                i,
                                top,
                                frame.values[Index.TEMPO.ordinal()]
                        );

                topMarkers.add(marker);

                lastTop = top;
            }
        }

        updateTopSpinner();
    }

    private void updateTopSpinner() {

        List<String> items = new ArrayList<>();
        for (ReplayTopMarker marker : topMarkers) {
            items.add(marker.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTOP.setAdapter(adapter);
    }

    // =========================================================
    // FILES
    // =========================================================

    private void loadFiles(Spinner spinner) {

        logFiles.clear();

        logFiles.addAll(fileManager.getFlightLogs());

        List<String> names = new ArrayList<>();

        for (File f : logFiles) {

            names.add(f.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    // =========================================================
    // DIALOG
    // =========================================================

    private void createDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_h125_option, null);
        RadioButton rbSAD01 = view.findViewById(R.id.rbSAD01);
        RadioButton rbSAD02 = view.findViewById(R.id.rbSAD02);
        LinearLayout layoutReplay = view.findViewById(R.id.layout_replay);
        Spinner spinnerReplay = view.findViewById(R.id.spinnerFiles);

        loadFiles(spinnerReplay);

        if (loadApp() == 2) {

            rbSAD02.setChecked(true);

            layoutReplay.setVisibility(View.VISIBLE);

        } else {

            rbSAD01.setChecked(true);

            layoutReplay.setVisibility(View.GONE);
        }

        rbSAD01.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        layoutReplay.setVisibility(View.GONE);
                    }
                });

        rbSAD02.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        layoutReplay.setVisibility(View.VISIBLE);
                    }
                });

        new AlertDialog.Builder(this)
                .setTitle("APLICATIVO H-125 CEV-AR")
                .setMessage(
                        "Selecione a aplicação desejada."
                )
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(
                        android.R.string.yes,
                        (dialog, which) -> {

                            AppIndex = ((RadioButton) ((AlertDialog) dialog).findViewById(R.id.rbSAD01)).isChecked() ? 1 : 2;

                            if (AppIndex == 2) {

                                loadFrames(
                                        spinnerReplay,
                                        () -> {
                                            createMission(AppIndex);
                                            createTabs(AppIndex);
                                        }
                                );

                            } else {

                                createMission(AppIndex);
                                createTabs(AppIndex);
                            }
                        })
                .show();
    }

    // =========================================================
    // PREFERENCES
    // =========================================================

    private int loadApp() {

        SharedPreferences prefs =
                getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        return prefs.getInt("app", 1);
    }

    private void saveTipoApp(int app) {

        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("app", app);
        editor.apply();
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();
        missionManager.stop();
    }

    // =========================================================
    // MISSION
    // =========================================================

    private void createMission(int appIndex) {

        IenaPacketReceiverLogger ienaPacketReceiver = new IenaPacketReceiverLogger(getBaseContext());

        if (appIndex == 1) {

            ienaPacketReceiver.setConverter(new CoefsSAD1());
            missionManager.setUdpConnector(new UDPConnector(1024), ienaPacketReceiver);
            missionManager.start();

        } else {
            seekBar.setMax(frames.size() - 1);
            missionManager.setFrameReplay(frames);
            missionManager.startReplay();
        }
    }

    // =========================================================
    // TABS
    // =========================================================

    private void createTabs(int appIndex) {

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup(getLocalActivityManager());
        Setup setup = new Setup();
        List<Tab> screenTabs;

        if (appIndex == 1) {
            screenTabs = setup.getScreenTabs(getResources().getBoolean(R.bool.isTablet));
        } else {
            screenTabs = setup.getScreenTabs2(getResources().getBoolean(R.bool.isTablet));
            linearReplay.setVisibility(View.VISIBLE);
        }

        for (Tab screenTab : screenTabs) {
            TabHost.TabSpec spec = tabHost.newTabSpec(screenTab.Tag);
            spec.setContent(new Intent().setClass(this, screenTab.Class));
            spec.setIndicator(screenTab.Indicator);
            tabHost.addTab(spec);
        }

        for (int i =
             tabHost.getTabWidget().getTabCount() - 1;
             i >= 0;
             i--) {

            tabHost.setCurrentTab(i);
        }

        if (tabHost.getTabWidget().getTabCount() == 1) {
            tabHost.getTabWidget().setVisibility(View.GONE);
        }
    }
}