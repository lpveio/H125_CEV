package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.databinding.ActivityCalibracaoAnemometricaBinding;
import br.cta.ipev.h125.databinding.ActivityDutchRollBinding;
import br.cta.isad.Display;

public class DutchRoll extends AppCompatActivity implements Display {

    private ActivityDutchRollBinding binding;
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


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLayout() {
        binding = ActivityDutchRollBinding.inflate(getLayoutInflater());
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