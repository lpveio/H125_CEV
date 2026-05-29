package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.databinding.ActivityParamReplayBinding;
import br.cta.ipev.h125.databinding.ActivityQdvBinding;
import br.cta.ipev.h125.setup.Index;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class Param_Replay extends AppCompatActivity implements Display {

    private ActivityParamReplayBinding binding;
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

                    binding.txtTempoValor.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
                    binding.txtFuelQtyKg.setValue(CVT[Index.FQTY.ordinal()]);
                    double valor = CVT[Index.FQTYP.ordinal()];
                    String texto = String.format(Locale.getDefault(), "%.2f%%", valor);
                    binding.txtFuelQtyPorc.setStringValue(texto);
                    binding.txtFLIValor.setValue(CVT[Index.FLI.ordinal()]);
                    binding.txtTOPValue.setValue(CVT[Index.TOP.ordinal()]);
                    binding.txtXPAValor.setValue(CVT[Index.XPA.ordinal()]);
                    binding.txtXPCValor.setValue(CVT[Index.XPC.ordinal()]);
                    binding.txtN2Valor.setValue(CVT[Index.N2.ordinal()]);
                    binding.txtNRValor.setValue(CVT[Index.NR.ordinal()]);
                    binding.txtN1Valor.setValue(CVT[Index.N1.ordinal()]);
                    binding.txtTRQValor.setValue(CVT[Index.TRQ.ordinal()]);
                    binding.txtTOTValor.setValue(CVT[Index.TOT.ordinal()]);
                    binding.txtFFValor.setValue(CVT[Index.FF.ordinal()]);
                    binding.txtRAValor.setValue(CVT[Index.RALT.ordinal()]);
                    binding.txtTASValor.setValue(CVT[Index.TAS.ordinal()]);
                    binding.txtGSValor.setValue(CVT[Index.GS_KN.ordinal()]);
                    binding.txtOATValor.setValue(CVT[Index.SAT.ordinal()]);
                    binding.txtZPBValor.setValue(CVT[Index.ZPI.ordinal()]);
                    binding.txtVZIValor.setValue(CVT[Index.VZI.ordinal()]);
                    binding.txtVBValor.setValue(CVT[Index.VI.ordinal()]);
                    binding.txtHDGMAGValor.setValue(CVT[Index.HDG_MAG.ordinal()]);
                    binding.txtPHIValor.setValue(CVT[Index.PHI.ordinal()]);
                    binding.txtTETAValor.setValue(CVT[Index.THETA.ordinal()]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLayout() {
        binding = ActivityParamReplayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    private void init() {
        manager = ((AppManager) getApplicationContext());
        manager.addDisplay(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.removeDisplay(this);
    }

}