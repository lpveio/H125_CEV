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

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GSVSentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.util.SatelliteInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.cta.ipev.h125.R;
import br.cta.ipev.h125.classes.Conversion;
import br.cta.ipev.h125.classes.LogFileStatus;
import br.cta.ipev.h125.databinding.ActivityDgpsBinding;
import br.cta.ipev.h125.gpsstatus.BluetoothNovatelManager;
import br.cta.ipev.h125.gpsstatus.GNSSViewModel;
import br.cta.ipev.h125.gpsstatus.GnssData;
import br.cta.ipev.h125.gpsstatus.LocationProvider;
import br.cta.ipev.h125.gpsstatus.SkyPlotView;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGalileo;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGPS;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGlonass;

public class GNSS extends AppCompatActivity {

    private static final String TAG = "GNSS_Activity";
    private static final String NOVATEL_MAC_ADDRESS = "98:07:2D:05:13:7A";
    private static final long CLEAR_DELAY_MS = 1000L;

    private ActivityDgpsBinding binding;
    private BluetoothNovatelManager bluetoothManager;
    private LocationProvider locationProvider;
    private GNSSViewModel viewModel;

    private List<SatelliteInfo> satellitesGALILEO;
    private List<SatelliteInfo> satellitesGLONASS;
    private List<SatelliteInfo> satellitesGPS;
    private SkyPlotView skyPlotView;
    private boolean isBluetoothConnected = false;
    private boolean updateSatsGPS = true;
    private boolean updateSatsGlonass = true;
    private boolean updateSatsGalileo = true;

    private SentenceFactory sentenceFactory;
    private String pendingLogFileStatus = null;
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void init() {
        skyPlotView = binding.skyPlot; // Utilizando o ViewBinding em vez do findViewById
        sentenceFactory = SentenceFactory.getInstance();
        satellitesGPS = new ArrayList<>();
        satellitesGALILEO = new ArrayList<>();
        satellitesGLONASS = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(GNSSViewModel.class);
        locationProvider = new LocationProvider(viewModel);

    }

    private void setupObservers() {
        viewModel.getGnssData().observe(this, data -> {
            if (data == null) return;

            binding.txtLatValor.setText(String.format(Locale.US, "%.8f° %s",
                    data.getLatitude(), data.getLatDirection() != null ? data.getLatDirection() : ""));

            binding.txtLongValor.setText(String.format(Locale.US, "%.8f° %s",
                    data.getLongitude(), data.getLongDirection() != null ? data.getLongDirection() : ""));

            binding.txtAltValor.setText(String.format(Locale.US, "%.2fm", data.getAltitude()));
            binding.txtSAtsValor.setText(String.valueOf(data.getSatellitesInUse()));
            binding.txtStatusGPS.setText(traduzirFixQuality(data.getFixQuality()));
            binding.txtPDopValor.setText(String.format(Locale.US, "%.2f", data.getPdop()));
            binding.txtHdopValor.setText(String.format(Locale.US, "%.2f", data.getHdop()));
            binding.txtVdopValor.setText(String.format(Locale.US, "%.2f", data.getVdop()));
        });
    }

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

    public void startDGPS() {
        bluetoothManager = new BluetoothNovatelManager(new BluetoothNovatelManager.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(final String message) {
                // EXCELENTE PRÁTICA: O parser roda na thread de background do Bluetooth.
                locationProvider.processNmeaMessage(message);
                // Regras visuais e lógicas de UI sobem de forma controlada para a Main Thread
                runOnUiThread(() -> {
                    binding.textNmea.setText(message);
                    handleNmeaSentence(message);
                    processLogFileStatus(message);

                });
            }

            @Override
            public void onConnectionStatusChanged(final String status) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show());
                if (status.contains("Conectado com sucesso!")) {
                    isBluetoothConnected = true;
                    atualizarBotaoConexao(true);
                } else if (status.contains("Desconectado")) {
                    isBluetoothConnected = false;
                    atualizarBotaoConexao(false);
                }


            }

            @Override
            public void onError(final String error) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Erro: " + error, Toast.LENGTH_LONG).show();
                    // Se deu erro, garante que o estado volte para desconectado
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

    private void handleNmeaSentence(String str) {
        if (str == null || str.isEmpty()) return;

        String nmea = str.trim();
        if (!SentenceValidator.isValid(nmea)) return;

        try {
            if (nmea.startsWith("$GPGSV") && updateSatsGPS) {
                GSVSentence gsv = (GSVSentence) sentenceFactory.createParser(nmea);

                if (gsv.isFirst()) {
                    satellitesGPS.clear();
                    satellitesGALILEO.clear();
                    satellitesGLONASS.clear();
                    skyPlotView.removeAll();
                }

                processCombinedGsv(gsv);

                if (gsv.isLast()) {
                    updateSatsGPS = false;
                    updateSatsGlonass = false;
                    updateSatsGalileo = false;
                    updateSatListGPS();


                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro processando sentença: " + nmea, e);
        }
    }

    private void processLogFileStatus(String line) {
        line = line.trim();
        if (line.startsWith("<LOGFILESTATUS")) {
            pendingLogFileStatus = line;
            return;
        }

        if (pendingLogFileStatus != null && line.startsWith("<")) {
            String fullMessage = pendingLogFileStatus + " " + line;
            pendingLogFileStatus = null;
            parseLogFileStatus(fullMessage);
        }
    }

    private void parseLogFileStatus(String msg) {
        LogFileStatus status = LogFileStatus.getInstance();
        try {
            String[] parts = msg.trim().split("\\s+");

            for (int i = 0; i < parts.length; i++) {
                String p = parts[i];

                // Verificação unificada de Status
                if (isStatusToken(p)) {
                    status.setState(p);
                    binding.txtSTATUSValor.setText(p);

                    if (p.equals("OPEN")) {
                        status.setRecording(true);
                        binding.txtGRAVANDOValor.setText("SIM");
                        binding.txtGRAVANDOValor.setTextColor(ContextCompat.getColor(this, R.color.black));
                        binding.txtGRAVANDOValor.setBackgroundColor(ContextCompat.getColor(this, R.color.bgValorParametro));
                    } else {
                        status.setRecording(false);
                        binding.txtGRAVANDOValor.setText("NÃO");
                        binding.txtGRAVANDOValor.setTextColor(ContextCompat.getColor(this, R.color.white));
                        binding.txtGRAVANDOValor.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

                        status.setFileName("");
                        status.setStorage("");
                        status.setFileSizeBytes(0);

                        binding.txtNameFIleValor.setText("--");
                        binding.txtFILESizeValor.setText("--");
                        binding.txtFileLocalValor.setText("--");
                    }
                }
                // Verificação de arquivos de log
                else if (p.contains(".LOG") || p.contains(".BIN") || p.contains(".DAT")) {
                    status.setFileName(p.replace("\"", ""));
                    binding.txtNameFIleValor.setText(status.getFileName());

                    if (i + 1 < parts.length) {
                        tryToSetFileSizeBytes(parts[i + 1], status);
                    }
                    if (i + 2 < parts.length) {
                        status.setStorage(parts[i + 2]);
                        binding.txtFileLocalValor.setText(status.getStorage());
                    }
                    if (i + 3 < parts.length) {
                        tryToSetFreeKb(parts[i + 3], status);
                    }
                    if (i + 4 < parts.length) {
                        tryToSetTotalKb(parts[i + 4], status);
                    }
                    status.setUsedKb(status.getTotalKb() - status.getFreeKb());
                    if (status.getUsedKb() >= 0) {
                        binding.txtUsedSizeValor.setText(Conversion.formatKb(status.getUsedKb()));
                    }
                }
                // Memória interna / externa sem arquivo vinculado
                else if ((p.equals("INTERNAL_FLASH") || p.equals("USBSTICK")) && (i + 1 < parts.length)) {
                    try {
                        long value = Long.parseLong(parts[i + 1]);
                        status.setFreeKb(value);
                        binding.txtFreeSizeValor.setText(Conversion.formatKb(status.getFreeKb()));

                        if (i + 2 < parts.length) {
                            status.setTotalKb(Long.parseLong(parts[i + 2]));
                            binding.txtTotalSizeValor.setText(Conversion.formatKb(status.getTotalKb()));
                            status.setUsedKb(status.getTotalKb() - status.getFreeKb());
                            binding.txtUsedSizeValor.setText(Conversion.formatKb(status.getUsedKb()));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao decodificar LogFileStatus", e);
        }
    }

    private boolean isStatusToken(String p) {
        return p.equals("OPEN") || p.equals("CLOSE") || p.equals("BUSY") ||
                p.equals("ERROR") || p.equals("MEDIA_COPY") ||
                p.equals("MEDIA_BUSY") || p.equals("MEDIA_ERROR");
    }

    private void tryToSetFileSizeBytes(String part, LogFileStatus status) {
        try {
            status.setFileSizeBytes(Long.parseLong(part));
            binding.txtFILESizeValor.setText(Conversion.formatBytes(status.getFileSizeBytes()));
        } catch (NumberFormatException ignored) {}
    }

    private void tryToSetFreeKb(String part, LogFileStatus status) {
        try {
            status.setFreeKb(Long.parseLong(part));
            binding.txtFreeSizeValor.setText(Conversion.formatKb(status.getFreeKb()));
        } catch (NumberFormatException ignored) {}
    }

    private void tryToSetTotalKb(String part, LogFileStatus status) {
        try {
            status.setTotalKb(Long.parseLong(part));
            binding.txtTotalSizeValor.setText(Conversion.formatKb(status.getTotalKb()));
        } catch (NumberFormatException ignored) {}
    }

    private void processCombinedGsv(GSVSentence gsv) {
        for (SatelliteInfo sat : gsv.getSatelliteInfo()) {
            try {
                int prn = Integer.parseInt(sat.getId());
                if (prn >= 1 && prn <= 32) {
                    satellitesGPS.add(sat);
                    skyPlotView.addDataPointGPS(1, new DataPointGPS(sat.getId(), sat.getAzimuth(), sat.getElevation()));
                } else if (prn >= 65 && prn <= 96) {
                    satellitesGLONASS.add(sat);
                    skyPlotView.addDataPointGlonass(1, new DataPointGlonass(sat.getId(), sat.getAzimuth(), sat.getElevation()));
                } else if (prn >= 301 && prn <= 336) {
                    satellitesGALILEO.add(sat);
                    skyPlotView.addDataPointGalileo(1, new DataPointGalileo(sat.getId(), sat.getAzimuth(), sat.getElevation()));
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Erro conversão PRN satélite", e);
            }
        }
    }

    private void updateSatListGPS() {
        handler.postDelayed(() -> {
            satellitesGPS.clear();
            updateSatsGPS = true;

        }, CLEAR_DELAY_MS);
    }

    public void btnSendClick(View view) {
        String cmd = binding.editCommand.getText().toString();
        if (!cmd.isEmpty()) {
            bluetoothManager.sendCommand(cmd);
        }
    }

    public void btnConnectClick(View view) {
        if (!isBluetoothConnected) {
            bluetoothManager.connect(NOVATEL_MAC_ADDRESS);
        }
    }

    public void btnControlClick(View view) {
        if (!(view instanceof ToggleButton)) return;

        boolean isChecked = ((ToggleButton) view).isChecked();

        if (view.getId() == R.id.btnGPS) {
            if (isChecked) {
                Toast.makeText(this, "Ligando DGPS", Toast.LENGTH_LONG).show();
                bluetoothManager.sendCommand("LOGFILE OPEN");
            } else {
                Toast.makeText(this, "Desligando DGPS", Toast.LENGTH_LONG).show();
                bluetoothManager.sendCommand("LOGFILE CLOSE");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (bluetoothManager != null) {
            bluetoothManager.stop();
        }
    }
}