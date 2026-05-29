package br.cta.ipev.h125.gpsstatus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import br.cta.ipev.h125.databinding.FragmentGnssstatusBinding;

public class GNSSStatusFragment extends Fragment {

    private FragmentGnssstatusBinding binding;
    private String sigmaRMS;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGnssstatusBinding.inflate(inflater, container, false);
        observeViewModel();
        return binding.getRoot();
    }

    private void observeViewModel() {
        GNSSViewModel viewModel = new ViewModelProvider(requireActivity()).get(GNSSViewModel.class);
        viewModel.getGnssData().observe(getViewLifecycleOwner(), new Observer<GnssData>() {
            @Override
            public void onChanged(GnssData data) {
                if (data != null) {

                    try {

                        binding.tvStatusLocation.setText(data.getFixStatus());
                        if (data.getFixStatus().equals("Fix")) {
                            binding.imageNoFix.setVisibility(View.GONE);
                            binding.imageFix.setVisibility(View.VISIBLE);
                        }

                        binding.tvLatitude.setText(String.format("%.6f", data.getLatitude()));
                        binding.tvLongitude.setText(String.format("%.6f", data.getLongitude()));
                        binding.tvAltitude.setText(String.format("%.2f", data.getAltitude()));
                        sigmaRMS = "1";



                        if (sigmaRMS.equals("1")) {
                            binding.tvHRMS.setText(String.format("%.2f", data.getHrms()));
                            binding.tvVRMS.setText(String.format("%.2f", data.getVrms()));
                        } else {
                            binding.tvHRMS.setText(String.format("%.2f", data.getTwohrms()));
                            binding.tvVRMS.setText(String.format("%.2f", data.getTwovrms()));
                        }
                        binding.tvMSL.setText(String.format("%.2f", data.getAltitude()));
                        binding.tvUndulation.setText(String.format("%.2f", data.getAltitude()));
                        binding.tvPDOP.setText(String.format("%.2f", data.getPdop()));
                        binding.tvHDOP.setText(String.format("%.2f", data.getHdop()));
                        binding.tvVDOP.setText(String.format("%.2f", data.getVdop()));
                        binding.tvFixQuality.setText(String.format("%d", data.getFixQuality()));
                        binding.tvSatellitesInView.setText(String.format("%d / %d", data.getSatellitesInUse(), data.getSatellitesInView()));

                    } catch (Exception e) {
                        Log.e("", "Erro : " + e.getMessage());
                    }

                }
            }
        });
    }


}