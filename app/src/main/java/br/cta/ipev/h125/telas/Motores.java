package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import br.cta.ipev.commom.instruments.NumericDisplay;
import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.databinding.ActivityMotorBinding;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class Motores extends AppCompatActivity implements Display {

    private ActivityMotorBinding binding;
    AppManager manager;
    private NumericDisplay[][]txtReport;
    private ToggleButton tbPlay;
    private Button btnReset;
    private List<ToggleButton> btnPlay;
    private int[] BUTTONS_IDS = {
            R.id.btnDDC_A,
            R.id.btnVB
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();
        //initializeReport();
    }

    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    binding.txtTempoValor.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
                    binding.txtTOPValue.setValue(CVT[Index.TOP.ordinal()]);
                    binding.txtFuelQtyKg.setValue(CVT[Index.FQTY.ordinal()]);
                    binding.txtFLIValor.setValue(CVT[Index.FLI.ordinal()]);
                    binding.txtN2Valor.setValue(CVT[Index.N2.ordinal()]);
                    binding.txtNRValor.setValue(CVT[Index.NR.ordinal()]);
                    binding.txtN1Valor.setValue(CVT[Index.N1.ordinal()]);
                    binding.txtTRQValor.setValue(CVT[Index.TRQ.ordinal()]);
                    binding.txtTOTValor.setValue(CVT[Index.TOT.ordinal()]);
                    binding.txtFFValor.setValue(CVT[Index.FF.ordinal()]);
                    binding.txtRAValor.setValue(CVT[Index.RALT.ordinal()]);
                    binding.txtOATValor.setValue(CVT[Index.SAT.ordinal()]);
                    binding.txtZVBValor.setValue(CVT[Index.ZPI.ordinal()]);
                    binding.txtVBValor.setValue(CVT[Index.VI.ordinal()]);

                    setMemoryStstus((int) CVT[Index.MEM.ordinal()]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLayout() {
        binding = ActivityMotorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void btnControlClick(View view){
        ToggleButton btnClicked = (ToggleButton)view;
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

    private void initializeReport(){
        txtReport = new NumericDisplay[6][6];
        txtReport[0][0] = (NumericDisplay) findViewById(R.id.txtN1_11);
        txtReport[0][1] = (NumericDisplay) findViewById(R.id.txtN1_21);
        txtReport[0][2] = (NumericDisplay) findViewById(R.id.txtTOT11);
        txtReport[0][3] = (NumericDisplay) findViewById(R.id.txtTOT21);
        txtReport[0][4] = (NumericDisplay) findViewById(R.id.txtTRQ11);
        txtReport[0][5] = (NumericDisplay) findViewById(R.id.txtTRQ21);

        txtReport[1][0] = (NumericDisplay) findViewById(R.id.txtN1_12);
        txtReport[1][1] = (NumericDisplay) findViewById(R.id.txtN1_22);
        txtReport[1][2] = (NumericDisplay) findViewById(R.id.txtTOT12);
        txtReport[1][3] = (NumericDisplay) findViewById(R.id.txtTOT22);
        txtReport[1][4] = (NumericDisplay) findViewById(R.id.txtTRQ12);
        txtReport[1][5] = (NumericDisplay) findViewById(R.id.txtTRQ22);

        txtReport[2][0] = (NumericDisplay) findViewById(R.id.txtN1_13);
        txtReport[2][1] = (NumericDisplay) findViewById(R.id.txtN1_23);
        txtReport[2][2] = (NumericDisplay) findViewById(R.id.txtTOT13);
        txtReport[2][3] = (NumericDisplay) findViewById(R.id.txtTOT23);
        txtReport[2][4] = (NumericDisplay) findViewById(R.id.txtTRQ13);
        txtReport[2][5] = (NumericDisplay) findViewById(R.id.txtTRQ23);

        txtReport[3][0] = (NumericDisplay) findViewById(R.id.txtN1_14);
        txtReport[3][1] = (NumericDisplay) findViewById(R.id.txtN1_24);
        txtReport[3][2] = (NumericDisplay) findViewById(R.id.txtTOT14);
        txtReport[3][3] = (NumericDisplay) findViewById(R.id.txtTOT24);
        txtReport[3][4] = (NumericDisplay) findViewById(R.id.txtTRQ14);
        txtReport[3][5] = (NumericDisplay) findViewById(R.id.txtTRQ24);

        txtReport[4][0] = (NumericDisplay) findViewById(R.id.txtN1_15);
        txtReport[4][1] = (NumericDisplay) findViewById(R.id.txtN1_25);
        txtReport[4][2] = (NumericDisplay) findViewById(R.id.txtTOT15);
        txtReport[4][3] = (NumericDisplay) findViewById(R.id.txtTOT25);
        txtReport[4][4] = (NumericDisplay) findViewById(R.id.txtTRQ15);
        txtReport[4][5] = (NumericDisplay) findViewById(R.id.txtTRQ25);

        txtReport[5][0] = (NumericDisplay) findViewById(R.id.txtN1_16);
        txtReport[5][1] = (NumericDisplay) findViewById(R.id.txtN1_26);
        txtReport[5][2] = (NumericDisplay) findViewById(R.id.txtTOT16);
        txtReport[5][3] = (NumericDisplay) findViewById(R.id.txtTOT26);
        txtReport[5][4] = (NumericDisplay) findViewById(R.id.txtTRQ16);
        txtReport[5][5] = (NumericDisplay) findViewById(R.id.txtTRQ26);
    }

    private void init() {
        manager = ((AppManager) getApplicationContext());
        manager.addDisplay(this);

    }

    /*
    private void resetFirstReport(){
        this.CVT.resetMinMax();
    }


     */

    private void updateFirstReport(){
        /*
        binding.txtRPM1Valor.setParameter(this.CVT.RPM1);
        binding.txtRPM1ValorMin.setParameter(this.CVT.RPM1);
        binding.txtRPM1ValorMax.setParameter(this.CVT.RPM1);

        binding.txtRPM2Valor.setParameter(this.CVT.RPM2);
        binding.txtRPM2ValorMin.setParameter(this.CVT.RPM2);
        binding.txtRPM2ValorMax.setParameter(this.CVT.RPM2);

        binding.txtRPMRValor.setParameter(this.CVT.RPMR);
        binding.txtRPMRValorMin.setParameter(this.CVT.RPMR);
        binding.txtRPMRValorMax.setParameter(this.CVT.RPMR);

        binding.txtTGT1Valor.setParameter(this.CVT.TGT1);
        binding.txtTGT1ValorMin.setParameter(this.CVT.TGT1);
        binding.txtTGT1ValorMax.setParameter(this.CVT.TGT1);

        binding.txtTGT2Valor.setParameter(this.CVT.TGT2);
        binding.txtTGT2ValorMin.setParameter(this.CVT.TGT2);
        binding.txtTGT2ValorMax.setParameter(this.CVT.TGT2);

        binding.txtNG1Valor.setParameter(this.CVT.NG1);
        binding.txtNG1ValorMin.setParameter(this.CVT.NG1);
        binding.txtNG1ValorMax.setParameter(this.CVT.NG1);

        binding.txtNG2Valor.setParameter(this.CVT.NG2);
        binding.txtNG2ValorMin.setParameter(this.CVT.NG2);
        binding.txtNG2ValorMax.setParameter(this.CVT.NG2);

        binding.txtTQ1Valor.setParameter(this.CVT.TQ1);
        binding.txtTQ1ValorMin.setParameter(this.CVT.TQ1);
        binding.txtTQ1ValorMax.setParameter(this.CVT.TQ1);

        binding.txtTQ2Valor.setParameter(this.CVT.TQ2);
        binding.txtTQ2ValorMin.setParameter(this.CVT.TQ2);
        binding.txtTQ2ValorMax.setParameter(this.CVT.TQ2);

         */
    }

    private void updateSecondReport(){

        /*
        for (int i=0;i<updateReport.length;i++){
            if(updateReport[i]){
                txtReport[i][0].setParameter(CVT.TGT1);
                txtReport[i][1].setParameter(CVT.TGT2);
                txtReport[i][2].setParameter(CVT.NG1);
                txtReport[i][3].setParameter(CVT.NG2);
                txtReport[i][4].setParameter(CVT.TQ1);
                txtReport[i][5].setParameter(CVT.TQ2);
            }

         */
        }

}