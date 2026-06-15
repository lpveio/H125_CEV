package br.cta.ipev.h125.gpsstatus;

public class GnssData {
    private double latitude;
    private double longitude;
    private double altitude;
    private float horizontalAccuracy;
    private float verticalAccuracy;
    private float pdop;
    private float hdop;
    private float vdop;
    private String sigma;
    private float hrms;
    private float twohrms;
    private String latDirection;
    private String longDirection;
    private int satinView;
    private float vrms;
    private float twovrms;
    private int diffStatus;
    private int satellitesInView;
    private int satellitesInUse;
    private String fixStatus;
    private float sigmaTotal;



    private int fixQuality;

    public GnssData() {
        // Construtor vazio para facilitar atualizações parciais
    }

    public int getFixQuality() {return fixQuality;}

    public void setFixQuality(int fixQuality) {this.fixQuality = fixQuality;}

    public String getLatDirection() {return latDirection;}

    public void setLatDirection(String latDirection) {this.latDirection = latDirection;}

    public String getLongDirection() {return longDirection;}

    public void setLongDirection(String longDirection) {this.longDirection = longDirection;}


    public float getSigmaTotal() { return sigmaTotal; }
    public void setSigmaTotal(float sigmaTotal) { this.sigmaTotal = sigmaTotal; }

    public int getSatinView() {return satinView;}
    public void setSatinView(int satinView) {this.satinView = satinView;}

    public float getHrms() {return hrms;}

    public void setHrms(float hrms) {this.hrms = hrms;}




    public float getTwohrms() {return twohrms;}

    public void setTwohrms(float twohrms) {this.twohrms = twohrms;}

    public float getVrms() {return vrms;}

    public void setVrms(float vrms) {this.vrms = vrms;}

    public float getTwovrms() {return twovrms;}

    public void setTwovrms(float twovrms) {this.twovrms = twovrms;}
    public String getSigma() {return sigma;}

    public void setSigma(String sigma) {this.sigma = sigma;}

    // Getters e Setters para todos os campos
    public int getDiffStatus()  { return diffStatus; }
    public void setDiffStatus(int diffStatus) { this.diffStatus = diffStatus; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAltitude() { return altitude; }
    public void setAltitude(double altitude) { this.altitude = altitude; }

    public float getHorizontalAccuracy() { return horizontalAccuracy; }
    public void setHorizontalAccuracy(float horizontalAccuracy) { this.horizontalAccuracy = horizontalAccuracy; }

    public float getVerticalAccuracy() { return verticalAccuracy; }
    public void setVerticalAccuracy(float verticalAccuracy) { this.verticalAccuracy = verticalAccuracy; }

    public float getPdop() { return pdop; }
    public void setPdop(float pdop) { this.pdop = pdop; }

    public float getHdop() { return hdop; }
    public void setHdop(float hdop) { this.hdop = hdop; }

    public float getVdop() { return vdop; }
    public void setVdop(float vdop) { this.vdop = vdop; }

    public int getSatellitesInView() { return satellitesInView; }
    public void setSatellitesInView(int satellitesInView) { this.satellitesInView = satellitesInView; }

    public int getSatellitesInUse() { return satellitesInUse; }
    public void setSatellitesInUse(int satellitesInUse) { this.satellitesInUse = satellitesInUse; }

    public String getFixStatus() { return fixStatus; }
    public void setFixStatus(String fixStatus) { this.fixStatus = fixStatus; }

    public void calculateSigma() {
        this.sigmaTotal = (float) Math.sqrt(Math.pow(hrms, 2) + Math.pow(vrms, 2));
    }

    /* loaded from: classes.dex */
    public static class SatelliteModel {
        private float azimuth;
        private float cNo;
        private float elevation;
        private int id;
        private int type;

        public SatelliteModel(int type, int id, float elevation, float azimuth, float cNo) {
            this.type = type;
            this.id = id;
            this.cNo = cNo;
            this.elevation = elevation;
            this.azimuth = azimuth;
        }

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public float getcNo() {
            return this.cNo;
        }

        public void setcNo(float cNo) {
            this.cNo = cNo;
        }

        public float getElevation() {
            return this.elevation;
        }

        public void setElevation(float elevation) {
            this.elevation = elevation;
        }

        public float getAzimuth() {
            return this.azimuth;
        }

        public void setAzimuth(float azimuth) {
            this.azimuth = azimuth;
        }
    }

}