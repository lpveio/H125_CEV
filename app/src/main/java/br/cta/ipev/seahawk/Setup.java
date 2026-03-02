package br.cta.ipev.seahawk;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import br.cta.ipev.commom.instruments.odometers.AlertRange;
import br.cta.ipev.commom.screen.BaseSetup;
import br.cta.ipev.commom.screen.Tab;
import br.cta.ipev.seahawk.telas.Comando_Voo;
import br.cta.ipev.seahawk.telas.TelaDois;

public class Setup extends BaseSetup{

    @Override
    public List<Tab> getScreenTabs(boolean forTablets) {
        List<Tab>screenTabs = new ArrayList<Tab>();
        screenTabs.add(0,new Tab("1","Comandos VOO", Comando_Voo.class,true,true));
        screenTabs.add(1,new Tab("2","Input Data", TelaDois.class,true,true));
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
