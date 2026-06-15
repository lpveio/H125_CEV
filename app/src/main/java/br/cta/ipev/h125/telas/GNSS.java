package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import java.util.Locale;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.classes.Conversion;
import br.cta.ipev.h125.databinding.ActivityDgpsBinding;
import br.cta.ipev.h125.gpsstatus.BluetoothNovatelManager;
import br.cta.ipev.h125.gpsstatus.GNSSViewModel;
import br.cta.ipev.h125.gpsstatus.LocationProvider;


public class GNSS extends AppCompatActivity {

    private static final String TAG = "GNSS_Activity";
    private static final String NOVATEL_MAC_ADDRESS = "98:07:2D:05:13:7A";
    private static final long CLEAR_DELAY_MS = 1000L;

    private ActivityDgpsBinding binding;
    private BluetoothNovatelManager bluetoothManager;
    private LocationProvider locationProvider;
    private GNSSViewModel viewModel;
    private AppManager manager;
    private boolean isBluetoothConnected = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        init();
        setupObservers();
        startDGPS();
    }

    private void setLayout() {
        binding = ActivityDgpsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void init() {
        manager = (AppManager) getApplication();
        if (locationProvider == null) {
            locationProvider = new LocationProvider(manager);
        }
    }

    private void setupObservers() {
        // Observador 1: Atualizações Geográficas

        manager.getGnssLiveData().observe(this, data -> {
            if (data == null) return;
            binding.txtLatValor.setText(String.format(Locale.US, "%.8f° %s", data.getLatitude(), data.getLatDirection() != null ? data.getLatDirection() : ""));
            binding.txtLongValor.setText(String.format(Locale.US, "%.8f° %s", data.getLongitude(), data.getLongDirection() != null ? data.getLongDirection() : ""));
            binding.txtAltValor.setText(String.format(Locale.US, "%.2fm", data.getAltitude()));
            binding.txtSAtsValor.setText(String.valueOf(data.getSatellitesInUse()));
            binding.txtStatusGPS.setText(traduzirFixQuality(data.getFixQuality()));
            binding.txtPDopValor.setText(String.format(Locale.US, "%.2f", data.getPdop()));
            binding.txtHdopValor.setText(String.format(Locale.US, "%.2f", data.getHdop()));
            binding.txtVdopValor.setText(String.format(Locale.US, "%.2f", data.getVdop()));
        });

        // Observador 2: Atualizações Visuais de Gravação do Cartão SD (NovAtel)

        manager.getLogLiveData().observe(this, status -> {
            if (status == null) return;

            binding.txtSTATUSValor.setText(status.getState());

            if (status.isRecording()) {
                binding.btnGPS.setChecked(true);
                binding.txtGRAVANDOValor.setText("SIM");
                binding.txtGRAVANDOValor.setTextColor(ContextCompat.getColor(this, R.color.black));
                binding.txtGRAVANDOValor.setBackgroundColor(ContextCompat.getColor(this, R.color.bgValorParametro));
                binding.txtNameFIleValor.setText(status.getFileName());
                binding.txtFILESizeValor.setText(Conversion.formatBytes(status.getFileSizeBytes()));
                binding.txtFileLocalValor.setText(status.getStorage());
            } else {
                binding.btnGPS.setChecked(false);
                binding.txtGRAVANDOValor.setText("NÃO");
                binding.txtGRAVANDOValor.setTextColor(ContextCompat.getColor(this, R.color.white));
                binding.txtGRAVANDOValor.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                binding.txtNameFIleValor.setText("--");
                binding.txtFILESizeValor.setText("--");
                binding.txtFileLocalValor.setText("--");
            }

            binding.txtFreeSizeValor.setText(Conversion.formatKb(status.getFreeKb()));
            binding.txtTotalSizeValor.setText(Conversion.formatKb(status.getTotalKb()));
            if (status.getUsedKb() >= 0) {
                binding.txtUsedSizeValor.setText(Conversion.formatKb(status.getUsedKb()));
            }
        });
    }

    public void startDGPS() {
        bluetoothManager = new BluetoothNovatelManager(new BluetoothNovatelManager.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(final String message) {
                // Toda a árvore de parsing roda aqui de forma assíncrona
                locationProvider.processNmeaMessage(message);

                // Apenas renderizações de texto bruto de logs sobem para a UI Thread
                runOnUiThread(() -> {
                    binding.textNmea.setText(message);
                });
            }

            @Override
            public void onConnectionStatusChanged(final String status) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    isBluetoothConnected = status.contains("Conectado com sucesso!");
                    atualizarBotaoConexao(isBluetoothConnected);
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Erro: " + error, Toast.LENGTH_LONG).show();
                    isBluetoothConnected = false;
                    atualizarBotaoConexao(false);
                });
            }
        });

        bluetoothManager.connect(NOVATEL_MAC_ADDRESS);
    }


    private void atualizarBotaoConexao(boolean conectado) {
        if (conectado) {
            binding.buttonConnectar.setText("DESCONECTAR");
            // Altera para uma cor de destaque (ex: Vermelho para indicar ação de parar)
            binding.buttonConnectar.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        } else {
            binding.buttonConnectar.setText("CONECTAR");
            // Retorna para a cor padrão do seu app (ex: Verde ou o padrão do tema)
            binding.buttonConnectar.setBackgroundColor(ContextCompat.getColor(this, R.color.bgNomeParametro));
        }
    }


    // ... Mantenha os métodos auxiliares estruturais: atualizarBotaoConexao(), handleNmeaSentence(), processCombinedGsv(), updateSatListGPS() ...

    private String traduzirFixQuality(int quality) {
        switch (quality) {
            case 1: return "GPS Fixo (Autônomo)";
            case 2: return "DGPS (Diferencial)";
            case 4: return "RTK Fixo (Alta Precisão)";
            case 5: return "RTK Flutuante";
            case 9: return "WAAS / SBAS";
            default: return "Sem Fix / Procurando...";
        }
    }

    public void btnSendClick(View view) {
        String cmd = binding.editCommand.getText().toString();
        if (!cmd.isEmpty()) bluetoothManager.sendCommand(cmd);
    }

    public void btnConnectClick(View view) {
        if (!isBluetoothConnected) {
            bluetoothManager.connect(NOVATEL_MAC_ADDRESS);
        } else {
            bluetoothManager.stop();
        }
    }

    public void btnControlClick(View view) {
        if (!(view instanceof ToggleButton)) return;
        boolean isChecked = ((ToggleButton) view).isChecked();
        if (view.getId() == R.id.btnGPS) {
            bluetoothManager.sendCommand(isChecked ? "LOGFILE OPEN" : "LOGFILE CLOSE");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (bluetoothManager != null) bluetoothManager.stop();
    }
}