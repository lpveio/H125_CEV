package br.cta.ipev.seahawk.telas;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import br.cta.ipev.seahawk.AppManager;
import br.cta.ipev.seahawk.Index;
import br.cta.ipev.seahawk.databinding.ActivityComandoVooBinding;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class Comando_Voo extends AppCompatActivity implements Display {

    private ActivityComandoVooBinding binding;
    private double lastTopValue = -1; // valor inicial impossível
    private static final int DEV_MODE_CLICKS_REQUIRED = 5;
    private int devClickCount = 0;
    private boolean isSimulate = false;
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

                    binding.txtTempo.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));

                    double currentTop = CVT[Index.TOP.ordinal()];
                    if (currentTop != lastTopValue) {
                        lastTopValue = currentTop;
                        appendTopLogToFile(currentTop);
                    }
                    binding.txtTOP.setValue(currentTop);

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

    public void handleDevModeClick() {
        if (isSimulate) {
            Toast.makeText(this, "Modo Simulação já está habilitado", Toast.LENGTH_SHORT).show();
            return;
        }

        devClickCount++;
        int remaining = DEV_MODE_CLICKS_REQUIRED - devClickCount;

        if (remaining > 0) {
            Toast.makeText(this, "Faltam " + remaining + " toques para habilitar", Toast.LENGTH_SHORT).show();
        } else {
            isSimulate = true;
            devClickCount = 0;
            Toast.makeText(this, "Modo Simulação habilitado!", Toast.LENGTH_SHORT).show();

            // Ativa simulação no AppManager
            manager.setupConversionSimulate(true);
        }
    }

    private void init() {
        manager = ((AppManager) getApplicationContext());
        manager.addDisplay(this);

        binding.txtTOP.setOnClickListener(v -> handleDevModeClick());
        binding.txtTOP.setOnLongClickListener(v -> {

            if (isSimulate) {
                manager.setupConversionSimulate(false);
                Toast.makeText(this, "Modo Simulação desativado", Toast.LENGTH_SHORT).show();
                isSimulate = false;

            } else {
                Toast.makeText(this, "Modo Simulação já está desativado", Toast.LENGTH_SHORT).show();
            }

            return true; // indica que o evento de long click foi tratado
        });

    }

    private void appendTopLogToFile(double topValue) {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss", Locale.getDefault()).format(new Date());
        String line = "TOP: " + topValue + " | Data: " + timeStamp + "\n";

        try {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadDir, "TOP.txt");

            FileWriter writer = new FileWriter(file, true); // true = append
            writer.append(line);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}