package br.cta.ipev.h125.setup;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class Alerta extends AppCompatTextView {

    public Alerta(Context context) {
        super(context);
    }

    public Alerta(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Alerta(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAlertText(String text) {
        setText(text);
    }
    public void setAlertFLI(double valor) {
        int color;

        if (valor < 10) {
            color = Color.GREEN; // Faixa verde
        } else if (valor >= 10 && valor < 30) {
            color = Color.YELLOW; // Faixa amarela
        } else {
            color = Color.RED; // Faixa vermelha
        }
        setBackgroundColor(color);
    }



    public void setAlertTQ(double value) {
        int color;

        if (value <= 69.0) {
            color = Color.GREEN;
        } else if (value > 69.0 && value <= 78.0) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }
        setBackgroundColor(color);
    }

    public void setAlertTOT(double value) {
        int color;
        if (value <= 879.0) {
            color = Color.GREEN;
        } else if (value > 879.0 && value <= 897.0) {
            color = Color.YELLOW;
        } else   {
            color = Color.RED;

        }
        setBackgroundColor(color);
    }

    public void setAlertN1(double value) {
        int color;
        if (value <= 99.0) {
            color = Color.GREEN;
        } else if (value > 99.0 && value <= 100.0) {
            color = Color.YELLOW;
        } else  {
            color = Color.RED;

        }
        setBackgroundColor(color);
    }

    public  void setAlerDDC(double value) {
        int color;
        if (value >= 10.0 && value <= 90.0){
            color = Color.GREEN;
        } else  {
            color = Color.RED;
        }
        setBackgroundColor(color);
    }

    public  void setAlerDDN(double value) {
        int color;
        if (value >= 10.0 && value <= 90.0){
            color = Color.GREEN;
        } else   {
            color = Color.RED;
        }
        setBackgroundColor(color);
    }
}
