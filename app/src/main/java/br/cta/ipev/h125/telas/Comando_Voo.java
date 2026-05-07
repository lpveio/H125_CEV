package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.databinding.ActivityComandoVooBinding;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class Comando_Voo extends AppCompatActivity implements Display {

    private ActivityComandoVooBinding binding;
    AppManager manager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();
    }

    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    /*
                    binding.txtTempo.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
                    binding.chartDeltas.setDdc(CVT[Index.DDC.ordinal()]);
                    binding.chartDeltas.setDdl(CVT[Index.DDL.ordinal()]);
                    binding.chartDeltas.setDdm(CVT[Index.DDM.ordinal()]);
                    binding.chartDeltas.setDdn(CVT[Index.DDN.ordinal()]);
                    if (CVT[Index.DDN.ordinal()] > 50) {
                        binding.chartDeltas.setDDNLeftColor();
                    } else {
                        binding.chartDeltas.setDDNRightColor();
                    }
                    binding.txtDDC.setValue(CVT[Index.DDC.ordinal()]);
                    binding.txtDDL.setValue(CVT[Index.DDL.ordinal()]);
                    binding.txtDDM.setValue(CVT[Index.DDM.ordinal()]);
                    binding.txtDDN.setValue(CVT[Index.DDN.ordinal()]);

                     */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLayout() {
        binding = ActivityComandoVooBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void init() {
        manager = ((AppManager) getApplicationContext());
        manager.addDisplay(this);

    }

}