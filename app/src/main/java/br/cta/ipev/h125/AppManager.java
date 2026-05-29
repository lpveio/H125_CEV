package br.cta.ipev.h125;

import android.app.Application;
import android.util.Log;

import java.util.List;

import br.cta.ipev.h125.classes.CoefsSAD;
import br.cta.ipev.h125.classes.CoefsSAD1;
import br.cta.ipev.h125.classes.CoefsSAD1_counts;
import br.cta.ipev.h125.classes.FlightLoggerFormatter;
import br.cta.ipev.h125.replay.ReplayController;
import br.cta.ipev.h125.replay.ReplayEngine;
import br.cta.ipev.h125.replay.ReplayFrame;
import br.cta.isad.Display;
import br.cta.isad.IenaPacketReceiverLogger;
import br.cta.isad.UDPConnector;
import br.cta.isad.iCounts2UE;

public class AppManager extends Application {

    private static final String TAG = "AppManager";

    // =====================================================
    // LIVE MODE
    // =====================================================
    private UDPConnector udpConnector;
    private IenaPacketReceiverLogger receiver;
    private iCounts2UE converter;
    private Thread udpThread; // Guardando a referência para evitar múltiplas threads
    public static final int FREQ_SAVE = 32;
    public static final int FREQ_ACRA = 32;
    public static final int TIME_END_FLIGHT = 60000;

    // =====================================================
    // REPLAY MODE
    // =====================================================
    private ReplayEngine replayEngine;
    private volatile boolean replayMode = false; // volatile garante consistência entre threads
    private List<ReplayFrame> framesReplay;


    @Override
    public void onCreate() {
        super.onCreate();
        ReplayController.init(this);
        Log.d(TAG, "AppManager initialized");
    }


    // =====================================================
    // UDP SETUP
    // =====================================================
    public void setUdpConnector(UDPConnector udpConnector, IenaPacketReceiverLogger receiver) {
        this.udpConnector = udpConnector;
        this.receiver = receiver;
        this.converter = this.receiver.getConverter();
        this.udpConnector.addReceived(this.receiver);

        // CONFIGURAÇÃO DO LOGGER
        this.receiver.setSaveFlight(true);
        this.receiver.setLogFormatter(new FlightLoggerFormatter());
        this.receiver.setFreqInput(FREQ_ACRA);
        this.receiver.setFreqSave(FREQ_SAVE);
        this.receiver.setTimeEndFlight(TIME_END_FLIGHT);
    }

    public void setFrameReplay(List<ReplayFrame> frames) {
        this.framesReplay = frames;
    }

    public List<ReplayFrame> getFramesReplay() {
        return framesReplay;
    }

    // =====================================================
    // BLUETOOTH CONECTION
    // =====================================================


    // =====================================================
    // CONVERSION
    // =====================================================
    public void setupConversionSimulate(boolean isSimulate) {
        if (receiver == null) return;

        if (isSimulate) {
            CoefsSAD1_counts conv = new CoefsSAD1_counts();
            this.receiver.setConverter(conv);
            this.converter = conv;
        } else {
            CoefsSAD1 conv = new CoefsSAD1();
            this.receiver.setConverter(conv);
            this.converter = conv;
        }
    }

    // =====================================================
    // DISPLAYS (Adicionado método de remoção para evitar vazamento)
    // =====================================================
    public void addDisplay(Display display) {
        if (receiver != null) {
            receiver.addDisplay(display);
        }
        if (replayEngine != null) {
            replayEngine.addDisplay(display);
        }
    }

    // Método crucial para chamar no onDestroy() das suas Activities/Fragments!
    public void removeDisplay(Display display) {
        if (receiver != null && receiver.getDisplays() != null) {
            receiver.getDisplays().remove(display);
        }
        // Se a sua ReplayEngine tiver um método para remover/limpar displays, chame aqui também
    }

    // =====================================================
    // COEFS
    // =====================================================
    public CoefsSAD getActiveCoefs() {
        if (receiver == null) return null;

        iCounts2UE currentConverter = receiver.getConverter();
        if (currentConverter instanceof CoefsSAD) {
            return (CoefsSAD) currentConverter;
        }
        return null;
    }

    // =====================================================
    // LIVE MODE CONTROL
    // =====================================================
    public synchronized void start() {
        replayMode = false;
        stopReplay(); // Garante o replay desligado

        if (udpConnector == null) {
            Log.w(TAG, "UDP connector NULL - Não é possível iniciar");
            return;
        }

        // Proteção contra múltiplas threads ativas simultaneamente
        if (udpThread != null && udpThread.isAlive()) {
            Log.d(TAG, "LIVE MODE já está rodando. Ignorando nova chamada.");
            return;
        }

        udpThread = new Thread(udpConnector);
        udpThread.start();
        Log.d(TAG, "LIVE MODE STARTED");
    }

    public synchronized void stop() {
        stopReplay();

        if (receiver != null) {
            receiver.stopLog();
        }

        if (udpConnector != null) {
            udpConnector.stop();
            Log.d(TAG, "UDP connector stopped");
        }

        if (udpThread != null) {
            udpThread.interrupt();
            udpThread = null;
        }
    }

    // =====================================================
    // REPLAY MODE CONTROL
    // =====================================================
    public synchronized void startReplay() {
        try {
            if (framesReplay == null || framesReplay.isEmpty()) {
                Log.w(TAG, "Falha ao iniciar: Lista de replay vazia");
                return;
            }

            Log.d(TAG, "Starting replay procedure...");

            // Interrompe conexões de rede em tempo real
            if (udpConnector != null) {
                udpConnector.stop();
                if (udpThread != null) {
                    udpThread.interrupt();
                    udpThread = null;
                }
                Log.d(TAG, "UDP e redes paradas para o Replay");
            }

            // Para qualquer gravação ativa
            if (receiver != null) {
                receiver.stopLog();
                Log.d(TAG, "Logger parado");
            }

            replayMode = true;

            // Se mudou o arquivo ou se não existe engine ativa, cria uma nova do zero
            if (replayEngine == null) {
                replayEngine = new ReplayEngine(framesReplay);

                // Reaproveita os displays que estavam registrados no modo live
                if (receiver != null && receiver.getDisplays() != null) {
                    List<Display> displays = receiver.getDisplays();
                    for (Display d : displays) {
                        replayEngine.addDisplay(d);
                    }
                }
                replayEngine.play();
                Log.d(TAG, "Nova ReplayEngine inicializada e rodando");
            } else {
                replayEngine.resume();
                Log.d(TAG, "ReplayEngine retomada");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro fatal em startReplay", e);
        }
    }

    public synchronized void stopReplay() {
        replayMode = false;

        if (replayEngine != null) {
            replayEngine.stop();
            // Boa prática: se sua ReplayEngine tiver um método de limpeza interna, chame-o aqui.
            replayEngine = null;
            Log.d(TAG, "ReplayEngine finalizada e liberada");
        }
    }
    // =====================================================
    // GETTERS
    // =====================================================
    public boolean isReplayMode() {
        return replayMode;
    }

    public ReplayEngine getReplayEngine() {
        return replayEngine;
    }

    public IenaPacketReceiverLogger getReceiver() {
        return receiver;
    }
}