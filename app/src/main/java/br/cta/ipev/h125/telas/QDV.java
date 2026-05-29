package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.classes.LogFileStatus;
import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.databinding.ActivityQdvBinding;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class QDV extends AppCompatActivity implements Display {

    private ActivityQdvBinding binding;
    AppManager manager;
    LogFileStatus status = LogFileStatus.getInstance();


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

                    //Log.d("", "status:" + status.isRecording());

                    if (status.isRecording()) {
                        binding.txtDGPSValue.setText("GRAVANDO");
                        binding.txtDGPSValue.setTextColor(getResources().getColor(R.color.black));
                        binding.txtDGPSValue.setBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        binding.txtDGPSValue.setText("OFF");
                        binding.txtDGPSValue.setTextColor(getResources().getColor(R.color.white));
                        binding.txtDGPSValue.setBackgroundColor(getResources().getColor(R.color.red));
                    }
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
                    binding.txtVBValor.setValue(CVT[Index.VI.ordinal()]);
                    binding.txtHDGMAGValor.setValue(CVT[Index.HDG_MAG.ordinal()]);
                    binding.txtPHIValor.setValue(CVT[Index.PHI.ordinal()]);
                    binding.txtTETAValor.setValue(CVT[Index.THETA.ordinal()]);
                    binding.txtAUWValor.setValue(CVT[Index.AUW.ordinal()]);
                    binding.txtPOleoValor.setValue(CVT[Index.OIL_PRESS.ordinal()]);

                    setMemoryStstus((int) CVT[Index.MEM.ordinal()]);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLayout() {
        binding = ActivityQdvBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        binding.txtMEMValor.setOnClickListener(v -> {
            if (binding.memStatus.getVisibility() == View.VISIBLE) {
                binding.memStatus.setVisibility(View.GONE);   // esconde
            } else {
                binding.memStatus.setVisibility(View.VISIBLE); // mostra
            }
        });
    }

    private void setMemoryStstus (int mem_counts) {

        int memory = (mem_counts >> 9) & 0x7F;
        int status_full = (mem_counts >> 8) & 0x01;
        int status_saving = (mem_counts >> 7) & 0x01;
        int error_code = mem_counts & 0x3F;

        if (status_full == 1) {
            binding.memStatusFull.setText("Mémoria Cheia: SIM");
        } else {
            binding.memStatusFull.setText("Mémoria Cheia: NÃO");
        }

        if (status_saving == 1) {
            binding.memStatusSaving.setText("Gravando: SIM");
        } else {
            binding.memStatusSaving.setText("Gravando: NÃO");
        }

        double porcentagem = (memory / 127.0) * 100.0;


        String porcentagem_string = String.format(Locale.getDefault(), "%.1f", porcentagem );

        binding.memStatusOcupado.setText("Espaço Ocupado: " + porcentagem_string + "%");


        if (error_code == 0) {
            binding.txtMEMValor.setText(porcentagem_string + "%");
            binding.memStatusError.setText("Erros: " + error_code + "(: Log Mode + Ok)");
            binding.memStatusError.setTextColor(getResources().getColor(R.color.black));
            if (porcentagem > 80){
                binding.txtMEMValor.setBackgroundColor(getResources().getColor(R.color.red));
                binding.txtMEMValor.setTextColor(getResources().getColor(R.color.white));
            } else {
                binding.txtMEMValor.setBackgroundColor(getResources().getColor(R.color.black));
                binding.txtMEMValor.setTextColor(getResources().getColor(R.color.white));
            }

        } else if (error_code == 0x3) {
            binding.txtMEMValor.setText("ERRO");
            binding.memStatusError.setText("Erros: " + error_code + "(Logging to fast)");
            binding.memStatusError.setTextColor(getResources().getColor(R.color.red));
            binding.txtMEMValor.setTextColor(getResources().getColor(R.color.white));
            binding.txtMEMValor.setBackgroundColor(getResources().getColor(R.color.red));
        } else if (error_code == 0x4) {
            binding.txtMEMValor.setText("ERRO");
            binding.txtMEMValor.setTextColor(getResources().getColor(R.color.white));
            binding.txtMEMValor.setBackgroundColor(getResources().getColor(R.color.red));
            binding.memStatusError.setText("Erros: " + error_code + "(: CF Memory Fault or NO Memory present)");
            binding.memStatusError.setTextColor(getResources().getColor(R.color.red));
        } else if (error_code == 0x3F) {
            String hex = String.format("%02X", error_code);
            binding.txtMEMValor.setText("ERRO");
            binding.txtMEMValor.setTextColor(getResources().getColor(R.color.white));
            binding.txtMEMValor.setBackgroundColor(getResources().getColor(R.color.red));
            binding.memStatusError.setText("Erros: " + hex + "(: CF Card detected and initialization complete)");
            binding.memStatusError.setTextColor(getResources().getColor(R.color.red));
        } else {
            String hex = String.format("%02X", error_code);
            binding.txtMEMValor.setText("ERRO");
            binding.txtMEMValor.setTextColor(getResources().getColor(R.color.white));
            binding.txtMEMValor.setBackgroundColor(getResources().getColor(R.color.red));
            binding.memStatusError.setText("Erros: " + hex  + "(: Reserved for future use)");
            binding.memStatusError.setTextColor(getResources().getColor(R.color.red));
        }
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