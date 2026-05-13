package br.cta.ipev.h125;

import android.Manifest;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.cta.ipev.commom.compat.ScreenManager;
import br.cta.ipev.commom.screen.Tab;
import br.cta.ipev.h125.classes.CoefsSAD1;
import br.cta.ipev.h125.classes.CoefsSAD1_counts;
import br.cta.ipev.h125.replay.ReplayFileManager;
import br.cta.ipev.h125.setup.Setup;
import br.cta.isad.IenaPacketReceiver;
import br.cta.isad.IenaPacketReceiverRecord;
import br.cta.isad.UDPConnector;

public class DataViewActivity  extends ActivityGroup{

    public static final String TAG = "DataViewActivity";
    private AppManager missionManager;
    private TabHost tabHost;
    private ScreenManager screenManager;
    private boolean isTablet;
    private boolean isSimulate = false;
    private static final String PREF_FILE_NAME = "APPTipo";
    private int AppIndex = 1;
    private ReplayFileManager fileManager;
    private final List<File> logFiles =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_data_view);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        missionManager = (AppManager) getApplicationContext();
        fileManager = new ReplayFileManager(this);

        //Aplicacao sem option , desmarcar os seguintes itens:
        createMission(AppIndex);
        createTabs();

        //Aplicacao com option
        //createDialog();

    }

    private void loadFiles(Spinner spinner) {

        logFiles.clear();
        logFiles.addAll(fileManager.getFlightLogs());

        List<String> names = new ArrayList<>();

        for (File f : logFiles) {
            names.add(f.getName());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void createDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_h125_option, null);

        RadioButton rbSAD01 = view.findViewById(R.id.rbSAD01);
        RadioButton rbSAD02 = view.findViewById(R.id.rbSAD02);
        LinearLayout layoutReplay = view.findViewById(R.id.layout_replay);
        Spinner spinnerReplay = view.findViewById(R.id.spinnerFiles);

        // Chama função para preencher spinner

        loadFiles(spinnerReplay);
        // Define seleção inicial
        if (loadApp() == 2) {
            rbSAD02.setChecked(true);
            layoutReplay.setVisibility(View.VISIBLE);
        } else {
            rbSAD01.setChecked(true);
            layoutReplay.setVisibility(View.GONE);
        }

        // Quando clicar no SAD01 -> esconder layout
        rbSAD01.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutReplay.setVisibility(View.GONE);
            }
        });

        // Quando clicar no SAD02 -> mostrar layout
        rbSAD02.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutReplay.setVisibility(View.VISIBLE);
            }
        });

        new AlertDialog.Builder(DataViewActivity.this)
                .setTitle("TIPO DE APLICAÇÃO H-125")
                .setMessage("Selecione a aplicação desejada.")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AppIndex = ((RadioButton) ((AlertDialog) dialog)
                                .findViewById(R.id.rbSAD01)).isChecked() ? 1 : 2;

                        saveTipoApp(AppIndex);
                        createMission(AppIndex);
                        createTabs();
                    }
                })
                .show();
    }

    private int loadApp(){
        SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        return (prefs.getInt("app", 1));
    }

    private void saveTipoApp(int app){
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("app", app); // value to store
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        missionManager.stop();
    }

    private void createMission(int appIndex){
        isSimulate = false;
        IenaPacketReceiverRecord ienaPacketReceiver = new IenaPacketReceiverRecord(getBaseContext());
        if (!isSimulate) {
            ienaPacketReceiver.setConverter(new CoefsSAD1());
        } else {
            ienaPacketReceiver.setConverter(new CoefsSAD1_counts());
        }

        missionManager.setUdpConnector(new UDPConnector(1024),ienaPacketReceiver);
        missionManager.start();
    }


    private void createTabs(){
        this.tabHost = (findViewById(android.R.id.tabhost));
        this.tabHost.setup(this.getLocalActivityManager());

        TabHost.TabSpec spec = tabHost.newTabSpec("Config");
        Setup setup = new Setup();
        List<Tab> screenTabs;
        screenTabs = setup.getScreenTabs(getResources().getBoolean(R.bool.isTablet));

        for(Tab screenTab:screenTabs){
            spec = tabHost.newTabSpec(screenTab.Tag);
            spec.setContent(new Intent().setClass(this,screenTab.Class));
            spec.setIndicator(screenTab.Indicator);
            tabHost.addTab(spec);
        }

        for(int i=tabHost.getTabWidget().getTabCount()-1;i>=0;i--){
            tabHost.setCurrentTab(i);
        }

        if (tabHost.getTabWidget().getTabCount() == 1) {
            tabHost.getTabWidget().setVisibility(View.GONE); // Oculta apenas as abas

        }


    }
}
