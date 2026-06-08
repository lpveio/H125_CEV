package br.cta.ipev.h125.telas;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation;
import com.scichart.data.model.DoubleRange;

import java.util.Arrays;
import java.util.List;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.charts.ChartParameter;
import br.cta.ipev.h125.charts.StripChartInSecs;
import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.databinding.ActivityPilotBinding;
import br.cta.isad.Display;

public class Pilot extends AppCompatActivity implements Display {

    private ActivityPilotBinding binding;
    AppManager manager;
    private double pilotValue;
    private ChartParameter selectedChartA = ChartParameter.TRQ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();
        setOption();
    }


    private double getPilotValue(ChartParameter parameter, double[] CVT) {
        return CVT[parameter.valueIndex.ordinal()];
    }

    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    pilotValue = getPilotValue(selectedChartA, CVT);
                    binding.txtRAValue.setValue(pilotValue);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLayout() {
        binding = ActivityPilotBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

    }


    private void setOption() {

        List<ChartParameter> itens = Arrays.asList(ChartParameter.values());
        ArrayAdapter<ChartParameter> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_chart, itens);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_chart);
        binding.spinnerPilot.setAdapter(adapter);
        binding.spinnerPilot.setSelection(0);
        binding.spinnerPilot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id
                                                                ) {
                                                                    selectedChartA = itens.get(position);
                                                                }

                                                                @Override
                                                                public void onNothingSelected(
                                                                        AdapterView<?> parent
                                                                ) {

                                                                }
                                                            }
        );


    }

    private void init() {
        manager = ((AppManager) getApplicationContext());
        manager.addDisplay(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }


}