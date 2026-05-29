package br.cta.ipev.h125.telas;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GSVSentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.util.SatelliteInfo;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.DataViewActivity;
import br.cta.ipev.h125.R;
import br.cta.ipev.h125.classes.Conversion;
import br.cta.ipev.h125.classes.LogFileStatus;
import br.cta.ipev.h125.databinding.ActivityDgpsBinding;
import br.cta.ipev.h125.gpsstatus.BluetoothNovatelManager;
import br.cta.ipev.h125.gpsstatus.GNSSViewModel;
import br.cta.ipev.h125.gpsstatus.GnssData;
import br.cta.ipev.h125.gpsstatus.LocationProvider;
import br.cta.ipev.h125.gpsstatus.SkyPlotView;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGPS;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGalileo;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGlonass;
import br.cta.isad.Display;

public class GNSS extends AppCompatActivity {

    private ActivityDgpsBinding binding;
    private AppManager manager;
    private String sigmaRMS;
    private BluetoothNovatelManager bluetoothManager;
    private final String NOVATEL_MAC_ADDRESS = "98:07:2D:05:13:7A";
    private LocationProvider locationProvider;
    private GNSSViewModel viewModel;
    private List<SatelliteInfo> satellitesGALILEO;
    private List<SatelliteInfo> satellitesGLONASS;
    private List<SatelliteInfo> satellitesGPS;
    private SkyPlotView skyPlotView;
    boolean updateSatsGPS = true;
    boolean updateSatsGlonass = true;
    boolean updateSatsGalileo = true;
    boolean activeGPS = true;
    String nmeaMessage;
    private SentenceFactory sentenceFactory;
    private String pendingLogFileStatus = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setLayout();
        init();
        startDGPS();
        startDataView();

    }


    public void startDataView(){

        // 2. Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(GNSSViewModel.class);

        // 3. Cria o Observer para escutar as mudanças nos dados do GNSS
        viewModel.getGnssData().observe(this, new Observer<GnssData>() {
            @Override
            public void onChanged(GnssData data) {

                if (data != null) {
                    // Atualiza cada TextView com os dados novos que o parser extraiu
                    sigmaRMS = "1";
                    // Formatando as coordenadas para 6 casas decimais
                    binding.txtLatValor.setText(String.format(Locale.US, "%.6f° %s",
                            data.getLatitude(), data.getLatDirection() != null ? data.getLatDirection() : ""));

                    binding.txtLongValor.setText(String.format(Locale.US, "%.6f° %s",
                            data.getLongitude(), data.getLongDirection() != null ? data.getLongDirection() : ""));

                    binding.txtAltValor.setText(String.format(Locale.US, "%.2fm", data.getAltitude()));

                    binding.txtSAtsValor.setText("" + data.getSatellitesInUse());

                    // Status de Fix (Se é 3D, DGPS, RTK, etc, baseado no fixQuality da GGA)
                    String statusFormatado = traduzirFixQuality(data.getFixQuality());
                    binding.txtStatusGPS.setText(statusFormatado);
                    binding.txtPDopValor.setText(String.format("%.2f", data.getPdop()));
                    binding.txtHdopValor.setText(String.format("%.2f", data.getHdop()));
                    //binding.tvFixQuality.setText(String.format("%d", data.getFixQuality()));
                }
            }
        });

        // 4. Inicializa o Provedor de Localização (Parser)
        locationProvider = new LocationProvider(viewModel);

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


    private void handleNmeaSentence(String str) {

        if (str == null || str.isEmpty()) {
            return;
        }

        String nmea = str.trim();

        // valida checksum/formato
        if (!SentenceValidator.isValid(nmea)) {
            return;
        }

        try {

            // =========================
            // GPS , GALILEO , GLONASS
            // =========================
            if (nmea.startsWith("$GPGSV")) {

                if (updateSatsGPS && activeGPS) {

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
            }

        } catch (Exception e) {
            Log.e("NMEA", "Erro processando sentença: " + nmea, e);
        }
    }

    public void startDGPS() {

        locationProvider = new LocationProvider(viewModel);

        bluetoothManager = new BluetoothNovatelManager(new BluetoothNovatelManager.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(final String message) {
                // Aqui chegam os dados do receptor (Ex: GPGGA, BESTPOS, TRACKSTAT...)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nmeaMessage = message;
                        locationProvider.processNmeaMessage(message);
                        handleNmeaSentence(message);
                        binding.textNmea.setText(message);
                        Log.d("", "nema:" + message);
                        processLogFileStatus(message);

                    }
                });
            }


            private void processLogFileStatus(String line) {
                line = line.trim();
                // Primeira parte

                if (line.startsWith("<LOGFILESTATUS")) {
                    pendingLogFileStatus = line;
                    return;
                }

                // Segunda parte

                if (pendingLogFileStatus != null && line.startsWith("<")) {
                    String fullMessage = pendingLogFileStatus + " " + line;
                    pendingLogFileStatus = null;
                    parseLogFileStatus(fullMessage);

                }

            }

            private LogFileStatus parseLogFileStatus(String msg) {

                LogFileStatus status = LogFileStatus.getInstance();

                try {

                    String[] parts = msg.trim().split("\\s+");

                    for (int i = 0; i < parts.length; i++) {

                        String p = parts[i];

                        // =====================================================
                        // STATUS
                        // =====================================================

                        if (p.equals("OPEN")
                                || p.equals("CLOSE")
                                || p.equals("BUSY")
                                || p.equals("ERROR")
                                || p.equals("MEDIA_COPY")
                                || p.equals("MEDIA_BUSY")
                                || p.equals("MEDIA_ERROR")) {

                            status.setState(p);

                            binding.txtSTATUSValor.setText(p);

                            // =================================================
                            // GRAVANDO
                            // =================================================

                            if (p.equals("OPEN")) {

                                status.setRecording(true);


                                binding.txtGRAVANDOValor.setText("SIM");

                                binding.txtGRAVANDOValor.setTextColor(
                                        getResources().getColor(R.color.black));

                                binding.txtGRAVANDOValor.setBackgroundColor(
                                        getResources().getColor(R.color.bgValorParametro));

                            } else {

                                status.setRecording(false);

                                binding.txtGRAVANDOValor.setText("NÃO");

                                binding.txtGRAVANDOValor.setTextColor(
                                        getResources().getColor(R.color.white));

                                binding.txtGRAVANDOValor.setBackgroundColor(
                                        getResources().getColor(R.color.red));

                                // =============================================
                                // LIMPA SOMENTE DADOS DO ARQUIVO
                                // =============================================

                                status.setFileName("");
                                status.setStorage("");
                                status.setFileSizeBytes(0);


                                binding.txtNameFIleValor.setText("--");

                                binding.txtFILESizeValor.setText("--");

                                binding.txtFileLocalValor.setText("--");
                            }
                        }

                        // =====================================================
                        // FILE NAME
                        // =====================================================

                        else if (p.contains(".LOG")
                                || p.contains(".BIN")
                                || p.contains(".DAT")) {

                            status.setFileName(p.replace("\"", ""));

                            binding.txtNameFIleValor.setText(status.getFileName());

                            // =================================================
                            // FILE SIZE
                            // =================================================

                            if (i + 1 < parts.length) {

                                try {

                                    status.setFileSizeBytes(Long.parseLong(parts[i + 1]));

                                    binding.txtFILESizeValor.setText(Conversion.formatBytes(status.getFileSizeBytes()));

                                } catch (NumberFormatException ignored) {
                                }
                            }

                            // =================================================
                            // STORAGE
                            // =================================================

                            if (i + 2 < parts.length) {

                                status.setStorage(parts[i + 2]);
                                binding.txtFileLocalValor.setText(status.getStorage());
                            }

                            // =================================================
                            // FREE SPACE
                            // =================================================

                            if (i + 3 < parts.length) {

                                try {

                                    status.setFreeKb(Long.parseLong(parts[i + 3]));

                                    binding.txtFreeSizeValor.setText(Conversion.formatKb(status.getFreeKb()));

                                } catch (NumberFormatException ignored) {
                                }
                            }

                            // =================================================
                            // TOTAL SPACE
                            // =================================================

                            if (i + 4 < parts.length) {

                                try {

                                    status.setTotalKb(Long.parseLong(parts[i + 4]));

                                    binding.txtTotalSizeValor.setText(Conversion.formatKb(status.getTotalKb()));

                                } catch (NumberFormatException ignored) {
                                }
                            }

                            // =================================================
                            // USED SPACE
                            // =================================================

                            status.setUsedKb(status.getTotalKb() - status.getFreeKb());

                            if (status.getUsedKb() >= 0) {
                                binding.txtUsedSizeValor.setText(Conversion.formatKb(status.getUsedKb()));
                            }
                        }

                        // =====================================================
                        // MEMÓRIA MESMO SEM ARQUIVO
                        // =====================================================

                        try {

                            long value = Long.parseLong(p);

                            if (value > 1000) {

                                // Detecta memória após INTERNAL_FLASH / USBSTICK
                                if (i >= 1 &&
                                        (
                                                parts[i - 1].equals("INTERNAL_FLASH")
                                                        || parts[i - 1].equals("USBSTICK")
                                        )) {

                                    // FREE
                                    status.setFreeKb(value);
                                    binding.txtFreeSizeValor.setText(Conversion.formatKb(status.getFreeKb()));

                                    // TOTAL
                                    if (i + 1 < parts.length) {

                                        try {

                                            status.setTotalKb(Long.parseLong(parts[i + 1]));

                                            binding.txtTotalSizeValor.setText(Conversion.formatKb(status.getTotalKb()));

                                            // USED
                                            status.setUsedKb(status.getTotalKb() - status.getFreeKb());
                                            binding.txtUsedSizeValor.setText(Conversion.formatKb(status.getUsedKb()));

                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                            }

                        } catch (Exception ignored) {
                        }
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }

                return status;
            }


            @Override
            public void onConnectionStatusChanged(final String status) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Erro: " + error, Toast.LENGTH_LONG).show());
            }
        });

        // Para conectar (Ative o botão ou gatilho apropriado)
        bluetoothManager.connect(NOVATEL_MAC_ADDRESS);
    }


    private void setLayout() {

        binding = ActivityDgpsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);


    }

    private void init() {
        skyPlotView = findViewById(R.id.skyPlot);
        sentenceFactory = SentenceFactory.getInstance();
        satellitesGPS = new ArrayList();
        satellitesGALILEO = new ArrayList();
        satellitesGLONASS = new ArrayList();
    }

    private void scheduleSatelliteListClear(final List<SatelliteInfo> satelliteList, final Runnable onClearedFlagSet) {
        new Handler().postDelayed(() -> {
            satelliteList.clear();
            if (onClearedFlagSet != null) onClearedFlagSet.run();
        }, 10000L);
    }

    private void processCombinedGsv(GSVSentence gsv) {

        for (SatelliteInfo sat : gsv.getSatelliteInfo()) {
            int prn = Integer.parseInt(sat.getId());
            // =====================
            // GPS
            // =====================
            if (prn >= 1 && prn <= 32) {

                satellitesGPS.add(sat);

                skyPlotView.addDataPointGPS(
                        1,
                        new DataPointGPS(
                                sat.getId(),
                                sat.getAzimuth(),
                                sat.getElevation()
                        )
                );
            }

            // =====================
            // GLONASS
            // =====================
            else if (prn >= 65 && prn <= 96) {

                satellitesGLONASS.add(sat);

                skyPlotView.addDataPointGlonass(
                        1,
                        new DataPointGlonass(
                                sat.getId(),
                                sat.getAzimuth(),
                                sat.getElevation()
                        )
                );
            }

            // =====================
            // GALILEO
            // =====================
            else if (prn >= 301 && prn <= 336) {

                satellitesGALILEO.add(sat);

                skyPlotView.addDataPointGalileo(
                        1,
                        new DataPointGalileo(sat.getId(), sat.getAzimuth(),
                                sat.getElevation()
                        )
                );
            }
        }
    }

    private void updateSatListGPS() {
        scheduleSatelliteListClear(satellitesGPS, () -> updateSatsGPS = true);
    }

    public void btnSendClick(View view) {
        bluetoothManager.sendCommand("UNLOG BT1 LOGFILESTATUS");
        checkStatusCommand();
    }

    public void btnConnectClick(View view) {
       bluetoothManager.connect(NOVATEL_MAC_ADDRESS);
    }

    public void btnControlClick(View view) {

        ToggleButton btnClicked = (ToggleButton) view;
        boolean isChecked = btnClicked.isChecked();

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

    public void checkStatusCommand() {
        Log.d("", "mensagem:" + nmeaMessage);
        if (nmeaMessage.contains("<OK")) {
            Toast.makeText(this, "Mensagem Enviada com sucesso", Toast.LENGTH_LONG).show();
        }
    }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            if (bluetoothManager != null) {
                bluetoothManager.stop();
            }
        }

}