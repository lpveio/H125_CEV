package br.cta.ipev.seahawk;

import android.app.Application;

import br.cta.isad.Display;
import br.cta.isad.IenaPacketReceiver;
import br.cta.isad.UDPConnector;
import br.cta.isad.iCounts2UE;

public class AppManager extends Application {
    private UDPConnector udpConnector;
    private IenaPacketReceiver receiver;
    private iCounts2UE converter;

    public void setUdpConnector(UDPConnector udpConnector, IenaPacketReceiver receiver) {
        this.udpConnector = udpConnector;
        this.receiver = receiver;
        this.converter = this.receiver.getConverter();
        this.udpConnector.addReceived(this.receiver);
    }

    public void setupConversionSimulate(boolean isSimulate) {
        if (isSimulate) {
            this.receiver.setConverter(new CoefsSAD1_counts());
        } else {
            this.receiver.setConverter(new CoefsSAD1());
        }
    }

    public void addDisplay(Display display){
        this.receiver.addDisplay(display);
    }

    public void start(){
        Thread thread = new Thread(udpConnector);
        thread.start();
    }

    public void stop(){
        if (udpConnector != null)
            udpConnector.stop();
    }

}
