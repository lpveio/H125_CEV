package br.cta.ipev.seahawk;

import android.Manifest;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import java.util.List;

import br.cta.ipev.commom.screen.Tab;
import br.cta.isad.IenaPacketReceiver;
import br.cta.isad.UDPConnector;

public class DataViewActivity  extends ActivityGroup{

    public static final String TAG = "DataViewActivity";
    private AppManager missionManager;
    private TabHost tabHost;
    private boolean isTablet;
    private boolean isSimulate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_data_view);

        this.isTablet = true;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        missionManager = (AppManager) getApplicationContext();
        createMission();
        createTabs();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        missionManager.stop();
    }

    private void createMission(){

        IenaPacketReceiver ienaPacketReceiver = new IenaPacketReceiver(getBaseContext());
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
        screenTabs = setup.getScreenTabs(isTablet);

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
