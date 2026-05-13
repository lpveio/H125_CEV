package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidPenStyle;

import java.util.Locale;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.replay.ReplayDisplay;
import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.charts.StripChartInSecs;
import br.cta.ipev.h125.charts.iStripChart;
import br.cta.ipev.h125.databinding.ActivityCalibracaoAnemometricaBinding;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class CalibAnemo extends AppCompatActivity implements Display , ReplayDisplay {

    private ActivityCalibracaoAnemometricaBinding binding;
    AppManager manager;
    private iStripChart chartVB, chartZPB;
    private SolidPenStyle penStyle = new SolidPenStyle(0xFF279B27, true, 2, new float[]{20, 20});
    private boolean play=true;
    private double zpb , zpbMin , zpbMax , vb , vbMin , vbMax ;

    private HorizontalLineAnnotation limitHigh;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();
        setCharts();
    }

    @Override
    public void update(double[] CVT) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (play) {
                            try {
                                long now = System.currentTimeMillis();
                                double elapsedSeconds = (now) / 1000.0;

                                chartVB.plot(elapsedSeconds, vb);
                                chartZPB.plot(elapsedSeconds, zpb);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        binding.txtTempoValor.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
                        binding.txtTOPValue.setValue(CVT[Index.TOP.ordinal()]);
                        binding.txtFuelQtyKg.setValue(CVT[Index.FQTY.ordinal()]);
                        double valor = CVT[Index.FQTYP.ordinal()];
                        String texto = String.format(Locale.getDefault(), "%.2f%%", valor);
                        binding.txtFuelQtyPorc.setStringValue(texto);
                        binding.txtFLIValor.setValue(CVT[Index.FLI.ordinal()]);
                        binding.txtN2Valor.setValue(CVT[Index.N2.ordinal()]);
                        binding.txtNRValor.setValue(CVT[Index.NR.ordinal()]);
                        binding.txtN1Valor.setValue(CVT[Index.N1.ordinal()]);
                        binding.txtTRQValor.setValue(CVT[Index.TQ.ordinal()]);
                        binding.txtTOTValor.setValue(CVT[Index.TOT.ordinal()]);
                        binding.txtFFValor.setValue(CVT[Index.FF.ordinal()]);
                        binding.txtRAValorr.setValue(CVT[Index.RALT.ordinal()]);
                        binding.txtTASValor.setValue(CVT[Index.TAS.ordinal()]);
                        binding.txtGSValor.setValue(CVT[Index.GS_KN.ordinal()]);
                        binding.txtOATValor.setValue(CVT[Index.SAT.ordinal()]);
                        binding.txtPSIValor.setValue(CVT[Index.HDG_MAG.ordinal()]);

                        zpb = CVT[Index.ZPB.ordinal()];
                        binding.txtZPBValor.setValue(zpb);
                        zpbMin = CVT[Index.ZPBMin.ordinal()];
                        binding.txtZPBValorMin.setValue(zpbMin);
                        zpbMax = CVT[Index.ZPBMax.ordinal()];
                        binding.txtZPBValorMax.setValue(zpbMax);
                        vb = CVT[Index.VB.ordinal()];
                        binding.txtVBValor.setValue(vb);
                        vbMin = CVT[Index.VBMin.ordinal()];
                        binding.txtVBValorMin.setValue(vbMin);
                        vbMax = CVT[Index.VBMax.ordinal()];
                        binding.txtVBValorMax.setValue(vbMax);

                        setMemoryStstus((int) CVT[Index.MEM.ordinal()]);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void setLayout() {
        binding = ActivityCalibracaoAnemometricaBinding.inflate(getLayoutInflater());
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

    private void init() {
        manager = ((AppManager) getApplicationContext());
        manager.addDisplay(this);

    }

    private void setCharts() {

        chartVB = new StripChartInSecs(this,new DoubleRange(0d,1d), penStyle,180);
        FrameLayout chartVBLayout = (FrameLayout)findViewById(R.id.chartDESVB);
        chartVBLayout.addView(chartVB.getSurface());
        chartVB.build();

        chartZPB = new StripChartInSecs(this,new DoubleRange(0d,1d), penStyle,180);
        final FrameLayout chartZPBLayout = (FrameLayout)findViewById(R.id.chartDESZPB);
        chartZPBLayout.addView(chartZPB.getSurface());
        chartZPB.build();


        limitHigh = new HorizontalLineAnnotation(this);

    }

    public void btnControlClick(View view){
        ToggleButton btnClicked = (ToggleButton)view;
        play = !btnClicked.isChecked();
    }

    public void btnClearClick(View view){
        chartVB.clear();
        chartZPB.clear();
        manager.getActiveCoefs().resetVB_VZB();
    }

    public void btnPlotClick(View view){
        plotarTolerancias();
    }

    private void plotarTolerancias(){

        double tolMin, tolMax;

        try {

            tolMin = binding.txtVBTolMin.getText().length()==0?0:Double.parseDouble(binding.txtVBTolMin.getText().toString());
            tolMax = binding.txtVBTolMax.getText().length()==0?0:Double.parseDouble(binding.txtVBTolMax.getText().toString());
            chartVB.plotLimit2(vb,tolMax,tolMin);

            tolMin = binding.txtZPBTolMin.getText().length()==0?0:Double.parseDouble(binding.txtZPBTolMin.getText().toString());
            tolMax = binding.txtZPBTolMax.getText().length()==0?0:Double.parseDouble(binding.txtZPBTolMax.getText().toString());


            chartZPB.plotLimit(zpb,tolMax,tolMin);
        }
        catch (Exception err){
            Log.e("ANEMO", "Atualizando linha de limite de tolerância dos gráficos", err);
        }
    }

    @Override
    public void onReplayFrame(double[] cvt) {

    }
}