package br.cta.ipev.h125.telas;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.Locale;
import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.classes.AtmosferaUtils;
import br.cta.ipev.h125.databinding.ActivityPesoBinding;
import br.cta.isad.Display;

public class Peso_Corrigido extends AppCompatActivity {
    private ActivityPesoBinding binding;
    private boolean isTablet;
    AppManager manager;
    private final AtmosferaUtils atm = new AtmosferaUtils();
    private double somaPeso;
    private LineDataSet dsAlvo, dsCorrigido, dsSup1, dsInf1, dsSup2, dsInf2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setupChart();
        setListeners();
    }


    private void setListeners() {
        TextWatcher calculoPeso = new SimpleTextWatcher(this::calcularPeso);
        TextWatcher calculoTudo = new SimpleTextWatcher(this::calcularTudo);

        binding.textEditPesoInicial.addTextChangedListener(calculoPeso);
        binding.textEditLastro.addTextChangedListener(calculoPeso);
        binding.textEditFuel.addTextChangedListener(calculoPeso);
        binding.textEditNr.addTextChangedListener(calculoTudo);
        binding.textEditNr0.addTextChangedListener(calculoTudo);
        binding.textEditAtt.addTextChangedListener(calculoTudo);
        binding.textEditTemp.addTextChangedListener(calculoTudo);
        binding.textEditAlvo.addTextChangedListener(calculoTudo);
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable callback;
        SimpleTextWatcher(Runnable callback) { this.callback = callback; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) { callback.run(); }
    }

    private void calcularPeso() {
        try {
            double pesoInicial = parseDouble(binding.textEditPesoInicial);
            double lastro = parseDouble(binding.textEditLastro);
            double fuel = parseDouble(binding.textEditFuel);
            somaPeso = pesoInicial + fuel + lastro;

            binding.textEditPesoTotal.setText(String.format(Locale.US, "%.0f", somaPeso));
            calcularTudo();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao calcular peso.", Toast.LENGTH_SHORT).show();
        }
    }

    private void calcularTudo() {
        try {
            double altitudeFt = parseDouble(binding.textEditAtt);
            double tempC = parseDouble(binding.textEditTemp);
            double pesoAlvo = parseDouble(binding.textEditAlvo);
            double nr = parseDouble(binding.textEditNr);
            double nr0 = parseDouble(binding.textEditNr0);

            if (altitudeFt == 0 || tempC == 0 || pesoAlvo == 0 || nr == 0 || nr0 == 0) return;

            double pa = atm.calcularPa(altitudeFt);
            double k = atm.calcularK(tempC);
            double rho = atm.calcularRho(pa, k);
            double rho2 = atm.calcularRho2(somaPeso, pesoAlvo);
            double sigma = atm.calcularSigma(rho);

            double pesoCorrigido = (somaPeso / sigma) * Math.pow((nr0 / nr), 2);
            double altMeta = atm.calcularPaux(k, pa, rho2, altitudeFt);

            binding.textEditSigma.setText(String.format(Locale.US, "%.6f", sigma));
            binding.textEditPesoCorrigido.setText(String.format(Locale.US, "%.0f", pesoCorrigido));
            binding.textEditAltMeta.setText(String.format(Locale.US, "%.0f", altMeta));
            binding.textEditTempMeta.setText(String.format(Locale.US, "%.1f", atm.TempMeta - 273.15));

            atualizarGrafico(pesoAlvo, pesoCorrigido);

        } catch (Exception e) {
            Toast.makeText(this, "Erro nos cálculos.", Toast.LENGTH_SHORT).show();
        }
    }

    private double parseDouble(android.widget.EditText e) {
        String s = e.getText().toString().trim();
        return s.isEmpty() ? 0 : Double.parseDouble(s);
    }

    private void setupChart() {
        dsAlvo = makeDataSet(new ArrayList<>(), "Peso Alvo", Color.GREEN, 2f, false);
        dsCorrigido = makeDataSet(new ArrayList<>(), "Peso Corrigido", Color.BLUE, 1f, true);
        dsSup1 = makeDashedSet(new ArrayList<>(), "+1% Limite", Color.MAGENTA);
        dsInf1 = makeDashedSet(new ArrayList<>(), "-1% Limite", Color.MAGENTA);
        dsSup2 = makeDashedSet(new ArrayList<>(), "+2% Limite", Color.RED);
        dsInf2 = makeDashedSet(new ArrayList<>(), "-2% Limite", Color.RED);

        LineData lineData = new LineData(dsAlvo, dsCorrigido, dsSup1, dsInf1, dsSup2, dsInf2);
        binding.lineChart.setData(lineData);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.getAxisRight().setEnabled(false);
        binding.lineChart.setTouchEnabled(false);

        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        xAxis.setAxisMinimum(-1f);
        xAxis.setAxisMaximum(1f);

        YAxis yAxis = binding.lineChart.getAxisLeft();
        yAxis.setDrawGridLines(true);
    }

    private void atualizarGrafico(double pesoAlvo_, double pesoCorrigido_) {
        float pesoAlvo = (float) pesoAlvo_;
        float pesoCorrigido = (float) pesoCorrigido_;

        dsAlvo.setValues(java.util.List.of(new Entry(-1f, pesoAlvo), new Entry(1f, pesoAlvo)));
        dsCorrigido.setValues(java.util.List.of(new Entry(0f, pesoCorrigido)));

        float limSup1 = pesoAlvo * 1.01f;
        float limInf1 = pesoAlvo * 0.99f;
        float limSup2 = pesoAlvo * 1.02f;
        float limInf2 = pesoAlvo * 0.98f;

        dsSup1.setValues(java.util.List.of(new Entry(-1f, limSup1), new Entry(1f, limSup1)));
        dsInf1.setValues(java.util.List.of(new Entry(-1f, limInf1), new Entry(1f, limInf1)));
        dsSup2.setValues(java.util.List.of(new Entry(-1f, limSup2), new Entry(1f, limSup2)));
        dsInf2.setValues(java.util.List.of(new Entry(-1f, limInf2), new Entry(1f, limInf2)));

        binding.lineChart.getAxisLeft().setAxisMinimum((float) (pesoAlvo * 0.975));
        binding.lineChart.getAxisLeft().setAxisMaximum((float) (pesoAlvo * 1.025));
        binding.lineChart.notifyDataSetChanged();
        binding.lineChart.invalidate();
    }

    private LineDataSet makeDataSet(ArrayList<Entry> entries, String label, int color, float width, boolean drawCircle) {
        LineDataSet ds = new LineDataSet(entries, label);
        ds.setColor(color);
        ds.setLineWidth(width);
        ds.setDrawCircles(drawCircle);
        ds.setDrawValues(drawCircle);
        if (drawCircle) ds.setCircleColor(color);
        return ds;
    }

    private LineDataSet makeDashedSet(ArrayList<Entry> entries, String label, int color) {
        LineDataSet ds = makeDataSet(entries, label, color, 1.5f, false);
        ds.enableDashedLine(10f, 10f, 0f);
        ds.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 10f}, 0));
        return ds;
    }


    private void setLayout(){
        binding = ActivityPesoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
