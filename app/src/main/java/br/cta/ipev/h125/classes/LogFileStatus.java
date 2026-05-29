package br.cta.ipev.h125.classes;

public class LogFileStatus {

    // =====================================================
    // SINGLETON
    // =====================================================

    private static final LogFileStatus instance =
            new LogFileStatus();

    public static LogFileStatus getInstance() {

        return instance;
    }

    // =====================================================
    // VARIABLES
    // =====================================================

    private boolean recording;

    private String state;

    private String fileName;

    private String storage;

    private long fileSizeBytes;

    private long freeKb;

    private long totalKb;

    private long usedKb;

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {

        this.recording = recording;
    }

    public String getState() {

        return state;
    }

    public void setState(String state) {

        this.state = state;
    }

    public String getFileName() {

        return fileName;
    }

    public void setFileName(String fileName) {

        this.fileName = fileName;
    }

    public String getStorage() {

        return storage;
    }

    public void setStorage(String storage) {

        this.storage = storage;
    }

    public long getFileSizeBytes() {

        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {

        this.fileSizeBytes = fileSizeBytes;
    }

    public long getFreeKb() {

        return freeKb;
    }

    public void setFreeKb(long freeKb) {

        this.freeKb = freeKb;
    }

    public long getTotalKb() {

        return totalKb;
    }

    public void setTotalKb(long totalKb) {

        this.totalKb = totalKb;
    }

    public long getUsedKb() {

        return usedKb;
    }

    public void setUsedKb(long usedKb) {

        this.usedKb = usedKb;
    }

    // =====================================================
    // CLEAR
    // =====================================================

    public void clearFileInfo() {

        fileName = "";

        storage = "";

        fileSizeBytes = 0;
    }
}