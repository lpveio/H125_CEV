package br.cta.ipev.h125.setup;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import br.cta.ipev.commom.instruments.odometers.AlertRange;
import br.cta.ipev.commom.screen.BaseSetup;
import br.cta.ipev.commom.screen.Tab;
import br.cta.ipev.h125.replay.ReplayActivity;
import br.cta.ipev.h125.telas.CalibAnemo;
import br.cta.ipev.h125.telas.QDV;
import br.cta.ipev.h125.telas.Ralt;

public class Setup extends BaseSetup{

    @Override
    public List<Tab> getScreenTabs(boolean forTablets) {
        List<Tab>screenTabs = new ArrayList<Tab>();
        screenTabs.add(0,new Tab("RA","RALT", Ralt.class,false,true));
        //screenTabs.add(1,new Tab("QDV","PRINCIPAL", QDV.class,true,false));
       // screenTabs.add(1,new Tab("S-CHART","S-CHART", SChart.class,true,false));
       // screenTabs.add(2,new Tab("Cal. Anem.","ANEMO", CalibAnemo.class,true,false));
        screenTabs.add(0,new Tab("Motores","MOTORES", ReplayActivity.class,true,false));
       // screenTabs.add(4,new Tab("C.VOO-PP","C.VOO-PP", Comando_Voo.class,true,true));
       // screenTabs.add(5,new Tab("C.VOO(2)-PP","C.VOO(2)-PP", Comando_Voo_2.class,true,true));
        //screenTabs.add(6,new Tab("DutchRoll","DUTCH ROLL", DutchRoll.class,true,false));
        return (super.getScreenForTablets(screenTabs,forTablets));

    }

    public static class AlertConfig {
        public static final AlertRange alertRed = new AlertRange(100d, 120d, Color.RED);
        public static final AlertRange RxEH = new AlertRange(45d, 100d, Color.YELLOW);
        public static final AlertRange RxEV = new AlertRange(50d, 100d, Color.YELLOW);
        public static final AlertRange RxLa = new AlertRange(-100d, -35d, Color.YELLOW);
        public static final AlertRange RxLb = new AlertRange(40d, 100d, Color.YELLOW);
        public static final AlertRange RHAxa = new AlertRange(-100d, -15d, Color.YELLOW);
        public static final AlertRange RHAxb = new AlertRange(15d, 100d, Color.YELLOW);
        public static final AlertRange RSA1 = new AlertRange(10d, 100d, Color.YELLOW);
        public static final AlertRange alertDDL = new AlertRange(-100d, -75d,Color.YELLOW);
        public static final AlertRange alertDDLa = new AlertRange(75d,100d,Color.YELLOW);
    }

    @Override
    public void setAlerts(Context context) {

    }


}
