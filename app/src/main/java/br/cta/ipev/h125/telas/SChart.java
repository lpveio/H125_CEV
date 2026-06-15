package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidPenStyle;

import br.cta.ipev.commom.acquisition.Parameter;
import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.charts.ChartRef;
import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.charts.StripChartInSecs;
import br.cta.ipev.h125.charts.iStripChart;
import br.cta.ipev.h125.databinding.ActivitySchartBinding;
import br.cta.ipev.h125.charts.iChartReference;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class SChart extends AppCompatActivity implements Display {

    private ActivitySchartBinding binding;
    AppManager manager;
    private iChartReference chartARef,chartBRef,chartCRef;
    private iStripChart chartA, chartB, chartC;
    private SolidPenStyle penStyle = new SolidPenStyle(0xFF279B27, true, 2, new float[]{20, 20});
    private boolean play=true;


    private int[] BTN_CHART_A_IDS = {
            R.id.btnDDC_A,
            R.id.btnDDL_A,
            R.id.btnDDM_A,
            R.id.btnDDN_A,
            R.id.btnDin_A
    };

    private int[] BTN_CHART_B_IDS = {
            R.id.btnDDC_B,
            R.id.btnDDL_B,
            R.id.btnDDM_B,
            R.id.btnDDN_B,
            R.id.btnDin_B
    };

    private int[] BTN_CHART_C_IDS = {
            R.id.btnDDC_C,
            R.id.btnDDL_C,
            R.id.btnDDM_C,
            R.id.btnDDN_C,
            R.id.btnDin_C
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();
        setCharts();
    }

    public void btnChartA(View view){
        ToggleButton btnClicked = (ToggleButton)view;
        if(btnClicked.isChecked()){
            binding.txtA.setText(btnClicked.getText());
            //chartARef = getReference(btnClicked.getId());
            chartA.clear();
        }
        updateButtonsState(btnClicked.getId(),BTN_CHART_A_IDS);
    }

    public void btnChartB(View view){
        ToggleButton btnClicked = (ToggleButton)view;
        if(btnClicked.isChecked()){
            binding.txtB.setText(btnClicked.getText());
            //chartBRef = getReference(btnClicked.getId());
            chartB.clear();
            //chartB.setyAxisRange(chartBRef.getRange());
        }
        updateButtonsState(btnClicked.getId(),BTN_CHART_B_IDS);
    }

    public void btnChartC(View view){
        ToggleButton btnClicked = (ToggleButton)view;
        if(btnClicked.isChecked()){
            binding.txtC.setText(btnClicked.getText());
            //chartCRef = getReference(btnClicked.getId());
            chartC.clear();
            //chartC.setyAxisRange(chartCRef.getRange());
        }
        updateButtonsState(btnClicked.getId(),BTN_CHART_C_IDS);
    }

    public void btnDinamica(View view){
        ToggleButton btnDinA =  ((ToggleButton) (findViewById(R.id.btnDin_A)));
        ToggleButton btnDinB =  ((ToggleButton) (findViewById(R.id.btnDin_B)));
        ToggleButton btnDinC =  ((ToggleButton) (findViewById(R.id.btnDin_C)));

        binding.txtA.setText(getResources().getString(R.string.TETA));
        binding.txtB.setText(getResources().getString(R.string.PHI));
        binding.txtC.setText(getResources().getString(R.string.PSI));

        //chartARef = getReference(R.id.btnDin_A);
       // chartBRef = getReference(R.id.btnDin_B);
       // chartCRef = getReference(R.id.btnDin_C);

        chartA.clear();
        chartB.clear();
        chartC.clear();

        disableButtons(btnDinA,BTN_CHART_A_IDS);
        disableButtons(btnDinB,BTN_CHART_B_IDS);
        disableButtons(btnDinC,BTN_CHART_C_IDS);


        btnDinA.setChecked(true);
        btnDinB.setChecked(true);
        btnDinC.setChecked(true);

    }

    private void disableButtons(ToggleButton btn, int[]groupButton){
        for (int id : groupButton){
            if(btn.getId()!= id){
                ((ToggleButton)findViewById(id)).setChecked(false);
            }
        }
    }

    /*
    private iChartReference getReference(int id){
        switch (id) {
            case R.id.btnDDC_A:
            case R.id.btnDDC_B:
            case R.id.btnDDC_C:
                return new ChartDDC(this.CVT.DDC);
            case R.id.btnDDL_A:
            case R.id.btnDDL_B:
            case R.id.btnDDL_C:
                return new ChartDDL(this.CVT.DDL);
            case R.id.btnDDM_A:
            case R.id.btnDDM_B:
            case R.id.btnDDM_C:
                return new ChartDDM(this.CVT.DDM);
            case R.id.btnDDN_A:
            case R.id.btnDDN_B:
            case R.id.btnDDN_C:
                return new ChartDDN(this.CVT.DDN);
            case R.id.btnDin_A:
                binding.txtA.setText(getResources().getString(R.string.DELTA_THETA));
                chartA.clear();
                return new ChartTheta(this.CVT.THETA);
            case R.id.btnDin_B:
                binding.txtB.setText(getResources().getString(R.string.DELTA_PHI));
                chartB.clear();
                return new ChartPHI(this.CVT.PHI);
            case R.id.btnDin_C:
                binding.txtC.setText(getResources().getString(R.string.DELTA_PSI));
                chartC.clear();
                return new ChartPSI(this.CVT.PSI);
            default:
                return new ChartNZ(this.CVT.NZ);
        }
    }


     */

    class ChartDDN extends ChartRef {
        private double deltaRange = 100;
        public ChartDDN(Parameter ref) {
            super(ref);
            double min = ref.getValue() < deltaRange ? 0 : ref.getValue() - deltaRange;
            double max = ref.getValue() > 100 - deltaRange ? 100 : ref.getValue() + deltaRange;
            this.range = new DoubleRange(min,max);
        }
    }

    class ChartDDM extends ChartDDN{
        public ChartDDM(Parameter ref) {
            super(ref);
        }
    }

    class ChartDDL extends ChartDDN{
        public ChartDDL(Parameter ref) {
            super(ref);
        }
    }

    class ChartDDC extends ChartDDN{
        public ChartDDC(Parameter ref) {
            super(ref);
        }
    }

    class ChartNX extends ChartRef{

        public ChartNX(Parameter ref) {
            super(ref);
        }
    }

    class ChartNY extends ChartRef{

        public ChartNY(Parameter ref) {
            super(ref);
        }
    }

    class ChartNZ extends ChartRef{

        public ChartNZ(Parameter ref) {
            super(ref);
        }
    }

    class ChartTheta extends ChartRef {
        protected double deltaRange = 30;

        public ChartTheta(Parameter ref) {
            super(ref);
        }

    }

    class ChartPHI extends ChartRef {
        protected double deltaRange = 60;

        public ChartPHI(Parameter ref) {
            super(ref);
        }
    }

    class ChartPSI extends ChartRef {
        protected double deltaRange = 30;
        public ChartPSI(Parameter ref) {
            super(ref);
        }
    }

    private void updateButtonsState(int idBtnClicked, int[] btn_group){
        for (int id: btn_group) {
            if (idBtnClicked != id){
                ((ToggleButton)findViewById(id)).setChecked(false);
            }
        }
    }


    @Override
    public void update(double[] CVT) {

        // 🔥 Extrai valores (fora da UI thread)
        final double tempo = CVT[Index.TEMPO.ordinal()];
        //final double ddc   = CVT[Index.DDC.ordinal()];
        //final double ddl   = CVT[Index.DDL.ordinal()];
        //final double ddm   = CVT[Index.DDM.ordinal()];
        //final double ddn   = CVT[Index.DDN.ordinal()];

        // 🚀 GRÁFICOS (background thread - mais performático)
        if (play) {
            try {
                long now = System.currentTimeMillis();
                double elapsedSeconds = (now) / 1000.0;

                //chartA.plot(elapsedSeconds, ddc);
               // chartB.plot(elapsedSeconds, ddl);
                //chartC.plot(elapsedSeconds, ddm);
            } catch (Exception e) {
                // fallback caso a lib não seja thread-safe
                runOnUiThread(() -> {
                   // chartA.plot(tempo, ddc);
                   // chartB.plot(tempo, ddl);
                   // chartC.plot(tempo, ddm);
                });
            }
        }

        // 🎯 UI (main thread)
        runOnUiThread(() -> {
            try {
                // Tempo formatado
                binding.txtTempo.setText(Convertions.sec2dhms(tempo));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void setCharts() {

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

    private void setLayout() {
        binding = ActivitySchartBinding.inflate(getLayoutInflater());
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