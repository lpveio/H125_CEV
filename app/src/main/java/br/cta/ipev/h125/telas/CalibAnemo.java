package br.cta.ipev.h125.telas;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidPenStyle;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.charts.StripChartInSecs;
import br.cta.ipev.h125.charts.iStripChart;
import br.cta.ipev.h125.classes.LogFileStatus;
import br.cta.ipev.h125.databinding.ActivityCalibracaoAnemometricaBinding;
import br.cta.ipev.h125.charts.ChartParameter;
import br.cta.ipev.h125.gpsstatus.GNSSViewModel;
import br.cta.ipev.h125.replay.ReplayController;
import br.cta.ipev.h125.setup.Index;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class CalibAnemo extends AppCompatActivity implements Display {

    private ActivityCalibracaoAnemometricaBinding binding;
    private AppManager manager;
    private GNSSViewModel viewModel;
    private iStripChart chartA;
    private iStripChart chartB;
    private final SolidPenStyle penStyle = new SolidPenStyle(0xFF279B27, true, 2, new float[]{20, 20});
    private boolean play = true;
    private double chartAValue;
    private double chartBValue;
    private double chartAMax = Double.NEGATIVE_INFINITY;
    private double chartAMin = Double.POSITIVE_INFINITY;
    private double chartBMax = Double.NEGATIVE_INFINITY;
    private double chartBMin = Double.POSITIVE_INFINITY;
    private ChartParameter selectedChartA = ChartParameter.VI;
    private ChartParameter selectedChartB = ChartParameter.ZPI;
    private HorizontalLineAnnotation limitHigh;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setLayout();
        init();
        setCharts();
        setupObservers();
    }


    private void setupObservers() {
        manager.getGnssLiveData().observe(this, data -> {
            if (data == null) return;
            binding.txtSAtsValor.setText(String.valueOf(data.getSatellitesInUse()));
            updateSatelliteIndicator(data.getSatellitesInUse());

        });

        manager.getLogLiveData().observe(this, data -> {
            if (data == null) return;

            if (data.isRecording()) {
                binding.txtDGPSValue.setText("GRAVANDO");
                binding.txtDGPSValue.setTextColor(getResources().getColor(R.color.black));
                binding.txtDGPSValue.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                binding.txtDGPSValue.setText("OFF");
                binding.txtDGPSValue.setTextColor(getResources().getColor(R.color.white));
                binding.txtDGPSValue.setBackgroundColor(getResources().getColor(R.color.red));
            }

        });



    }

    @Override
    public void update(double[] CVT) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    atualizarValoresTela(CVT);
                    atualizarChartsRealtime(CVT);
                    setMemoryStstus((int) CVT[Index.MEM.ordinal()]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void atualizarValoresTela(double[] CVT) {

        binding.txtTempoValor.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
        binding.txtTOPValue.setValue(CVT[Index.TOP.ordinal()]);
        binding.txtFuelQtyKg.setValue(CVT[Index.FQTY.ordinal()]);
        double fuelPercent = CVT[Index.FQTYP.ordinal()];



        String fuelText = String.format(Locale.getDefault(), "%.2f%%", fuelPercent);
        binding.txtFuelQtyPorc.setStringValue(fuelText);
        binding.txtFLIValor.setValue(CVT[Index.FLI.ordinal()]);
        binding.txtN2Valor.setValue(CVT[Index.N2.ordinal()]);
        binding.txtNRValor.setValue(CVT[Index.NR.ordinal()]);
        binding.txtN1Valor.setValue(CVT[Index.N1.ordinal()]);
        binding.txtTRQValor.setValue(CVT[Index.TRQ.ordinal()]);
        binding.txtTOTValor.setValue(CVT[Index.TOT.ordinal()]);
        binding.txtFFValor.setValue(CVT[Index.FF.ordinal()]);
        binding.txtRAValorr.setValue(CVT[Index.RALT.ordinal()]);
        binding.txtTASValor.setValue(CVT[Index.TAS.ordinal()]);
        binding.txtZPValor.setValue(CVT[Index.ZPI.ordinal()]);
        binding.txtGSValor.setValue(CVT[Index.GS_KN.ordinal()]);
        binding.txtOATValor.setValue(CVT[Index.SAT.ordinal()]);
        binding.txtVZIValor.setValue(CVT[Index.VZI.ordinal()]);
        binding.txtPSIValor.setValue(CVT[Index.HDG_MAG.ordinal()]);


    }

    private void atualizarChartsRealtime(double[] CVT) {

        chartAValue = getChartValue(selectedChartA, CVT);
        chartBValue = getChartValue(selectedChartB, CVT);

        chartAMax = Math.max(chartAMax, chartAValue);
        chartAMin = Math.min(chartAMin, chartAValue);
        chartBMax = Math.max(chartBMax, chartBValue);
        chartBMin = Math.min(chartBMin, chartBValue);

        binding.txtChartAValor.setValue(chartAValue);
        binding.txtChartAValorMin.setValue(chartAMin);
        binding.txtChartAValorMax.setValue(chartAMax);
        binding.txtChartBValor.setValue(chartBValue);
        binding.txtChartBValorMin.setValue(chartBMin);
        binding.txtChartBValorMax.setValue(chartBMax);

        if (!play) {
            return;
        }

        try {
            long now = System.currentTimeMillis();
            double elapsedSeconds = now / 1000.0;
            chartA.plot(elapsedSeconds, chartAValue);
            chartB.plot(elapsedSeconds, chartBValue);

        } catch (Exception e) {
            Log.e("ANEMO", "Erro plotando gráficos", e);
        }
    }

    private void updateSatelliteIndicator(int satellites) {

        int color;
        if (satellites < 5) {
            color = ContextCompat.getColor(this, R.color.red);
        } else if (satellites < 10) {
            color = ContextCompat.getColor(this, R.color.yellow);
        } else {
            color = ContextCompat.getColor(this, R.color.green);
        }

        GradientDrawable drawable = (GradientDrawable) binding.txtSAtsValor.getBackground();
        drawable.setColor(color);

    }

    private void setLayout() {

        binding = ActivityCalibracaoAnemometricaBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        binding.txtMEMValor.setOnClickListener(v -> {

            if (binding.memStatus.getVisibility() == View.VISIBLE) {
                binding.memStatus.setVisibility(View.GONE);

            } else {
                binding.memStatus.setVisibility(View.VISIBLE);
            }
        });
    }

    private void init() {
        manager = (AppManager) getApplicationContext();
        manager.addDisplay(this);
        viewModel = new ViewModelProvider(this).get(GNSSViewModel.class);
    }

    private void setCharts() {

        List<ChartParameter> itens = Arrays.asList(ChartParameter.values());
        ArrayAdapter<ChartParameter> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_chart, itens);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_chart);
        binding.spinnerTipoChartA.setAdapter(adapter);
        binding.spinnerTipoChartB.setAdapter(adapter);
        binding.spinnerTipoChartA.setSelection(0);
        binding.spinnerTipoChartB.setSelection(1);
        binding.spinnerTipoChartA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id
                            ) {
                                selectedChartA = itens.get(position);
                                atualizarChartLabels();
                            }

                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                            }
                        }
                );

        binding.spinnerTipoChartB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id
                            ) {

                                selectedChartB = itens.get(position);
                                atualizarChartLabels();
                            }

                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                            }
                        }
                );

        chartA = new StripChartInSecs(this, new DoubleRange(0d, 1d), penStyle, 180);
        binding.stripchartA.addView(chartA.getSurface());
        chartA.build();

        chartB = new StripChartInSecs(this, new DoubleRange(0d, 1d), penStyle, 180
        );

        binding.stripchartB.addView(chartB.getSurface());
        chartB.build();
        limitHigh = new HorizontalLineAnnotation(this);
        atualizarChartLabels();
    }

    private void atualizarChartLabels() {

        binding.txtChartaNeg.setText(selectedChartA.label + " Tol(-)");

        binding.txtChartaPos.setText(selectedChartA.label + " Tol(+)"
        );
        binding.txtChartbNeg.setText(selectedChartB.label + " Tol(-)"
        );
        binding.txtChartbPos.setText(selectedChartB.label + " Tol(+)"
        );
    }

    private double getChartValue(ChartParameter parameter, double[] CVT) {
        return CVT[parameter.valueIndex.ordinal()];
    }


    public void btnControlClick(View view) {

        ToggleButton btnClicked = (ToggleButton) view;
        play = !btnClicked.isChecked();
    }

    public void btnClearClick(View view) {

        chartA.clear();
        chartB.clear();
        chartAMax = Double.NEGATIVE_INFINITY;
        chartAMin = Double.POSITIVE_INFINITY;
        chartBMax = Double.NEGATIVE_INFINITY;
        chartBMin = Double.POSITIVE_INFINITY;
    }

    public void btnPlotClick(View view) {
        plotarTolerancias();
    }

    private void plotarTolerancias() {

        try {

            double tolMinA = binding.txtVBTolMin.getText().length() == 0 ? 0 : Double.parseDouble(binding.txtVBTolMin.getText().toString());
            double tolMaxA = binding.txtVBTolMax.getText().length() == 0 ? 0 : Double.parseDouble(binding.txtVBTolMax.getText().toString());


            if (chartAValue > 100 && chartAValue < 400 ) {
                chartA.plotLimit2(chartAValue, tolMaxA, tolMinA);
            } else if ( chartAValue > 400) {
                chartA.plotLimit(chartAValue, tolMaxA, tolMinA);
            } else {
                chartA.plotLimit3(chartAValue, tolMaxA, tolMinA);
            }

            double tolMinB = binding.txtZPBTolMin.getText().length() == 0 ? 0 : Double.parseDouble(binding.txtZPBTolMin.getText().toString());
            double tolMaxB = binding.txtZPBTolMax.getText().length() == 0 ? 0 : Double.parseDouble(binding.txtZPBTolMax.getText().toString());

            if (chartBValue > 100 && chartBValue < 400 ) {
                chartB.plotLimit2(chartBValue, tolMaxB, tolMinB);
            } else if ( chartBValue > 400) {
                chartB.plotLimit(chartBValue, tolMaxB, tolMinB);
            } else {
                chartB.plotLimit3(chartBValue, tolMaxB, tolMinB);
            }

        } catch (Exception err) {

            Log.e("ANEMO", "Erro plotando tolerâncias", err);
        }
    }

    private void setMemoryStstus(int mem_counts) {

        int memory = (mem_counts >> 9) & 0x7F;

        int status_full = (mem_counts >> 8) & 0x01;

        int status_saving = (mem_counts >> 7) & 0x01;

        int error_code = mem_counts & 0x3F;

        binding.memStatusFull.setText(status_full == 1 ? "Mémoria Cheia: SIM" : "Mémoria Cheia: NÃO");

        binding.memStatusSaving.setText(status_saving == 1 ? "Gravando: SIM" : "Gravando: NÃO");

        double porcentagem = (memory / 127.0) * 100.0;

        String porcentagemString = String.format(Locale.getDefault(), "%.1f", porcentagem);

        binding.memStatusOcupado.setText("Espaço Ocupado: " + porcentagemString + "%");

        if (error_code == 0) {
            binding.txtMEMValor.setText(porcentagemString + "%");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.removeDisplay(this);
    }

}