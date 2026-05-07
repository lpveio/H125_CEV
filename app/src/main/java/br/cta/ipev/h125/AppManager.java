package br.cta.ipev.h125;

import android.app.Application;
import android.util.Log;

import br.cta.ipev.commom.acquisition.Buffer;
import br.cta.ipev.h125.classes.CoefsSAD;
import br.cta.ipev.h125.classes.CoefsSAD1;
import br.cta.ipev.h125.classes.CoefsSAD1_counts;
import br.cta.ipev.h125.classes.FlightLoggerFormatter;
import br.cta.isad.Display;
import br.cta.isad.IenaPacketReceiver;
import br.cta.isad.IenaPacketReceiverRecord;
import br.cta.isad.UDPConnector;
import br.cta.isad.iCounts2UE;

public class AppManager extends Application {
    private UDPConnector udpConnector;
    private IenaPacketReceiverRecord receiver;
    private iCounts2UE converter;
    private static final String TAG = "AppManager";

    public void setUdpConnector(UDPConnector udpConnector, IenaPacketReceiverRecord receiver) {
        this.udpConnector = udpConnector;
        this.receiver = receiver;
        this.converter = this.receiver.getConverter();
        this.udpConnector.addReceived(this.receiver);

        //Setar true caso queria gravar o voo
        this.receiver.setSaveFlight(true);
        //Setar quais parametros vai querer gravar e cabecalho
        this.receiver.setLogFormatter(new FlightLoggerFormatter());
        //Setar a frequencia de aquisicao do acra
        this.receiver.setFreqInput(32);
        //Setar a frequencia de gravacao do tablet
        this.receiver.setFreqSave(8);
        //Setar o tempo de inatividade para interromper a gravacao do arquivo
        this.receiver.setTimeEndFlight(60000);

    }

    public void setupConversionSimulate(boolean isSimulate) {
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

    public void addDisplay(Display display){
        this.receiver.addDisplay(display);
    }

    public CoefsSAD getActiveCoefs() {
        if (this.receiver != null) {
            // Obtemos a instância atual do conversor
            iCounts2UE currentConverter = this.receiver.getConverter();

            // Verificamos se é do tipo CoefsSAD (a classe base que implementa resetValues)
            if (currentConverter instanceof CoefsSAD) {
                return (CoefsSAD) currentConverter;
            }
        }
        // Retorna null se não houver receptor ou o conversor não for do tipo esperado
        return null;
    }

    public void start(){
        Thread thread = new Thread(udpConnector);
        thread.start();
    }

    public void stop(){
        this.receiver.stopLog();
        Log.d("AppManager", "Stopping UDP connector");
        if (udpConnector != null)
            udpConnector.stop();


    }

}
