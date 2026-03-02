package br.cta.ipev.seahawk.telas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import br.cta.ipev.seahawk.AppManager;
import br.cta.ipev.seahawk.databinding.ActivityTelaTresBinding;
import br.cta.isad.Display;

public class TelaTres extends AppCompatActivity implements Display {
    private ActivityTelaTresBinding binding;
    private boolean isTablet;
    private LocationManager locationManager;


    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        init();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, location -> {
            float speedMs = location.getSpeed(); // m/s
            float speedKmh = speedMs * 3.6f;
            binding.speedView.speedTo(speedKmh);
            binding.velocidade.setText(""+ speedKmh);
        });

    }

    private void setLayout(){
        binding = ActivityTelaTresBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void init(){
        ((AppManager)getApplicationContext()).addDisplay(this);
        this.isTablet = getResources().getBoolean(br.cta.ipev.commom.R.bool.isTablet);
    }
}
