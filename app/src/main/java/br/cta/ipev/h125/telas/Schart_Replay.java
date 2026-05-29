package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scichart.drawing.common.SolidPenStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.charts.ChartParameter;
import br.cta.ipev.h125.charts.StripChartInSecs;
import br.cta.ipev.h125.charts.iStripChart;
import br.cta.ipev.h125.databinding.ActivityParamReplayBinding;
import br.cta.ipev.h125.databinding.ActivityQdvBinding;
import br.cta.ipev.h125.databinding.ActivitySchartReplayBinding;
import br.cta.ipev.h125.setup.Index;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class Schart_Replay extends AppCompatActivity implements Display {

    private ActivitySchartReplayBinding binding;
    AppManager manager;
    private iStripChart chartA, chartB, chartC;
    private SolidPenStyle penStyle = new SolidPenStyle(0xFF279B27, true, 2, new float[]{20, 20});
    private double chartAValue;
    private double chartBValue;
    private double chartCValue;
    private boolean play = true;
    private int selectedChartA;
    private int selectedChartB;
    private int selectedChartC;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();
        setCharts();

    }


    public void setCharts(){

        List<String> itens = new ArrayList<>();
        for (Index idx : Index.values()) {
            itens.add(idx.name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_chart2, itens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerTipoChartA.setAdapter(adapter);
        binding.spinnerTipoChartB.setAdapter(adapter);
        binding.spinnerTipoChartC.setAdapter(adapter);

        binding.spinnerTipoChartA.setSelection(0);
        binding.spinnerTipoChartB.setSelection(1);
        binding.spinnerTipoChartC.setSelection(2);
        binding.spinnerTipoChartA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id
                                                                ) {
                                                                    selectedChartA = position;
                                                                    chartA.clear();
                                                                    chartB.clear();
                                                                    chartC.clear();
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

                                                                    selectedChartB = position;
                                                                    chartA.clear();
                                                                    chartB.clear();
                                                                    chartC.clear();
                                                                }

                                                                @Override
                                                                public void onNothingSelected(
                                                                        AdapterView<?> parent
                                                                ) {

                                                                }
                                                            }
        );

        binding.spinnerTipoChartC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id
                                                                ) {
                                                                    selectedChartC = position;
                                                                    chartA.clear();
                                                                    chartB.clear();
                                                                    chartC.clear();
                                                                }

                                                                @Override
                                                                public void onNothingSelected(
                                                                        AdapterView<?> parent
                                                                ) {

                                                                }
                                                            }
        );

        chartA = new StripChartInSecs(this,penStyle,30);
        final FrameLayout chartALayout = (FrameLayout) findViewById(R.id.chartA);
        chartALayout.addView(chartA.getSurface());
        chartA.build();

        chartB = new StripChartInSecs(this,penStyle,30);
        FrameLayout chartBLayout = (FrameLayout)findViewById(R.id.chartB);
        chartBLayout.addView(chartB.getSurface());
        chartB.build();

        chartC = new StripChartInSecs(this, penStyle,30);
        FrameLayout chartCLayout = (FrameLayout)findViewById(R.id.chartC);
        chartCLayout.addView(chartC.getSurface());
        chartC.build();
    }

    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    atualizarValoresTela(CVT);
                    atualizarChartsRealtime(CVT);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void atualizarValoresTela(double[] CVT) {

        binding.txtTempoValor.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
        binding.txtTOPValor.setValue(CVT[Index.TOP.ordinal()]);

    }

    private void atualizarChartsRealtime(double[] CVT) {

        chartAValue = CVT[selectedChartA];
        chartBValue = CVT[selectedChartB];
        chartCValue = CVT[selectedChartC];


        binding.txtAValor.setValue(chartAValue);
        binding.txtBValor.setValue(chartBValue);
        binding.txtCValor.setValue(chartCValue);

        if (!play) {
            return;
        }

        try {
            long now = System.currentTimeMillis();
            double elapsedSeconds = now / 1000.0;
            chartA.plot(elapsedSeconds, chartAValue);
            chartB.plot(elapsedSeconds, chartBValue);
            chartC.plot(elapsedSeconds, chartCValue);

        } catch (Exception e) {
            Log.e("ANEMO", "Erro plotando gráficos", e);
        }
    }

    private void setLayout() {
        binding = ActivitySchartReplayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    public void btnClearClick(View view) {

        chartA.clear();
        chartB.clear();
        chartC.clear();
    }

    public void btnControlClick(View view) {
        ToggleButton btnClicked = (ToggleButton) view;
        play = !btnClicked.isChecked();
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