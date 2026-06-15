package br.cta.ipev.h125.gpsstatus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.cta.ipev.h125.classes.LogFileStatus;

public class GNSSViewModel extends ViewModel {
    private final MutableLiveData<GnssData> gnssData = new MutableLiveData<>();
    private final MutableLiveData<String> nmeaMessage = new MutableLiveData<>();
    private final MutableLiveData<LogFileStatus> logFileStatus = new MutableLiveData<>();
    public LiveData<GnssData> getGnssData() { return gnssData; }
    public LiveData<LogFileStatus> getLogFileStatus() {return logFileStatus;}
    public LiveData<String> getNmeaMessage() { return nmeaMessage; }
    public void updateData(GnssData data) { gnssData.postValue(data); }
    public void updateLogFileStatus(LogFileStatus status) {logFileStatus.postValue(status);}
    public void postNmea(String message) { nmeaMessage.postValue(message); }
}
