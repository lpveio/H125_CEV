package br.cta.ipev.h125.setup;

import android.widget.TextView;

import br.cta.ipev.h125.R;

public class Alertas {

    public static void setAlertFLI(TextView textView, int value) {

        if (value <= 9.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametro));
        } else if (value > 9.0 && value <= 10.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroAmarelo));
        } else if (value > 10.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroVermelho));

        }

    }

    public static void setAlertTQ(TextView textView, int value) {


        if (value <= 69.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametro));
        } else if (value > 69.0 && value <= 78.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroAmarelo));
        } else if (value > 78.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroVermelho));

        }
    }

    public static void setAlertTOT(TextView textView, int value) {
        if (value <= 879.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametro));
        } else if (value > 879.0 && value <= 897.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroAmarelo));
        } else if (value > 897.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroVermelho));

        }
    }

    public static void setAlertN1(TextView textView, int value) {
        if (value <= 99.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametro));
        } else if (value > 99.0 && value <= 100.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroAmarelo));
        } else if (value > 100.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroVermelho));

        }
    }

    public static void setAlerDDC(TextView textView, int value) {
        if (value >= 10.0 && value <= 90.0){
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametro));
        } else if (value > 90.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroVermelho));
        }
    }

    public static void setAlerDDN(TextView textView, int value) {
        if (value >= 10.0 && value <= 90.0){
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametro));
        } else if (value > 90.0) {
            textView.setBackgroundColor(textView.getResources().getColor(R.color.bgValorParametroVermelho));
        }
    }


}
