package br.cta.ipev.h125.gpsstatus;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class LocationProvider {

    private Bundle bundle;
    private final GNSSViewModel viewModel;

    private int countSats;
    private int countSatsGA;
    private int countSatsGB;
    private int countSatsGL;
    private int countSatsGP;

    private ArrayList<String> satIds; // Mantido caso utilize em outro lugar

    public LocationProvider(GNSSViewModel viewModel) {
        this.viewModel = viewModel;

        this.countSatsGP = 0;
        this.countSatsGL = 0;
        this.countSatsGA = 0;
        this.countSatsGB = 0;
        this.countSats = 0;
        this.bundle = new Bundle();
    }

    /**
     * Ponto de entrada único para as mensagens vindas do BluetoothManager.
     * Substitui completamente os antigos listeners do Android.
     */
    public void processNmeaMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        viewModel.postNmea(message);
        //Log.i("NMEA_EXTERNAL", message);

        GnssData data = viewModel.getGnssData().getValue();
        if (data == null) {
            data = new GnssData();
        }

        try {
            message = message.trim();
            String[] strArrSplit = message.split(",");

            if (strArrSplit.length == 0) {
                return;
            }

            String sentenceType = strArrSplit[0];

            // =========================================================
            // GGA - Extrai posição, altitude e satélites do ProPak
            // =========================================================
            if (sentenceType.contains("GGA")) {

                // Latitude (Formato NMEA: DDMM.MMMMM)
                if (strArrSplit.length > 2 && !strArrSplit[2].isEmpty()) {
                    String latDir = strArrSplit.length > 3 ? strArrSplit[3] : "N";
                    data.setLatitude(convertNmeaToDecimal(strArrSplit[2], latDir));
                    data.setLatDirection(latDir);
                }

                // Longitude (Formato NMEA: DDDMM.MMMMM)
                if (strArrSplit.length > 4 && !strArrSplit[4].isEmpty()) {
                    String longDir = strArrSplit.length > 5 ? strArrSplit[5] : "E";
                    data.setLongitude(convertNmeaToDecimal(strArrSplit[4], longDir));
                    data.setLongDirection(longDir);
                }

                // Fix Quality
                if (strArrSplit.length > 6 && !strArrSplit[6].isEmpty()) {
                    int fixQuality = Integer.parseInt(strArrSplit[6]);
                    data.setDiffStatus(fixQuality);
                    data.setFixQuality(fixQuality);
                    data.setFixStatus(fixQuality > 0 ? "Fix" : "No Fix");
                }

                // Satellites in use
                if (strArrSplit.length > 7 && !strArrSplit[7].isEmpty()) {
                    int sats = Integer.parseInt(strArrSplit[7]);
                    data.setSatellitesInUse(sats);
                }

                // HDOP
                if (strArrSplit.length > 8 && !strArrSplit[8].isEmpty()) {
                    data.setSigma(strArrSplit[8]);
                    data.calculateSigma();
                }

                // Altitude
                if (strArrSplit.length > 9 && !strArrSplit[9].isEmpty()) {
                    data.setAltitude(Double.parseDouble(strArrSplit[9]));
                }
            }

            // =========================================================
            // GSA
            // =========================================================
            if (sentenceType.contains("GSA")) {
                if (strArrSplit.length > 15 && !strArrSplit[15].isEmpty()) {
                    data.setPdop(parseFloatSafe(strArrSplit[15]));
                }
                if (strArrSplit.length > 16 && !strArrSplit[16].isEmpty()) {
                    data.setHdop(parseFloatSafe(strArrSplit[16]));
                }
                if (strArrSplit.length > 17 && !strArrSplit[17].isEmpty()) {
                    String vdop = removeChecksum(strArrSplit[17]);
                    data.setVdop(parseFloatSafe(vdop));
                }
            }

            // =========================================================
            // GNS
            // =========================================================
            if (sentenceType.endsWith("GNS")) {
                if (strArrSplit.length > 7 && !strArrSplit[7].isEmpty()) {
                    bundle.putInt("satellitesUse", Integer.parseInt(strArrSplit[7]));
                }
                if (strArrSplit.length > 9 && !strArrSplit[9].isEmpty()) {
                    bundle.putFloat("mslHeight", parseFloatSafe(strArrSplit[9]));
                }
                if (strArrSplit.length > 10 && !strArrSplit[10].isEmpty()) {
                    bundle.putFloat("undulation", parseFloatSafe(strArrSplit[10]));
                }
            }

            // =========================================================
            // GST
            // =========================================================
            if (sentenceType.contains("GST")) {
                // RMS Vertical
                if (strArrSplit.length > 8 && !strArrSplit[8].isEmpty()) {
                    String value = removeChecksum(strArrSplit[8]);
                    float vrms = parseFloatSafe(value);
                    data.setVrms(vrms);
                    data.setTwovrms(vrms * 2.0f);
                }
                // RMS Horizontal
                if (strArrSplit.length > 7 && !strArrSplit[6].isEmpty() && !strArrSplit[7].isEmpty()) {
                    double rms = (Math.pow(parseFloatSafe(strArrSplit[6]), 2.0)
                            + Math.pow(parseFloatSafe(strArrSplit[7]), 2.0)) / 2.0;
                    float hrms = (float) Math.sqrt(rms);
                    data.setHrms(hrms);
                    data.setTwohrms(hrms * 2.0f);
                }
            }

            // =========================================================
            // GSV
            // =========================================================
            if (sentenceType.contains("GSV")) {
                updateSatelliteCounters(sentenceType, strArrSplit);
                int total = countSatsGP + countSatsGL + countSatsGA + countSatsGB;
                countSats = total;
                data.setSatinView(total);
            }

            // =========================================================
            // RMC
            // =========================================================
            if (sentenceType.contains("RMC")) {
                if (strArrSplit.length > 7 && !strArrSplit[7].isEmpty()) {
                    float speedKnots = parseFloatSafe(strArrSplit[7]);
                   // Log.d("NMEA_SPEED", "Speed Knots: " + speedKnots);
                    // data.setSpeed(speedKnots);
                }
            }

        } catch (Exception e) {
            Log.e("LocationProvider", "Erro NMEA : " + e.getMessage());
        }

        viewModel.updateData(data);
    }

    private void updateSatelliteCounters(String sentenceType, String[] strArrSplit) {
        try {
            if (strArrSplit.length < 4) return;

            boolean isLastMessage = strArrSplit[1].equals(strArrSplit[2]);
            if (!isLastMessage) return;

            int sats = Integer.parseInt(strArrSplit[3]);

            if (sentenceType.contains("GPGSV")) {
                countSatsGP = sats;
            } else if (sentenceType.contains("GLGSV")) {
                countSatsGL = sats;
            } else if (sentenceType.contains("GAGSV")) {
                countSatsGA = sats;
            } else if (sentenceType.contains("GBGSV")) {
                countSatsGB = sats;
            }
        } catch (Exception e) {
            Log.e("LocationProvider", "Erro GSV : " + e.getMessage());
        }
    }

    /**
     * Converte o formato de coordenada NMEA (DDMM.MMMM ou DDDMM.MMMM) para Graus Decimais.
     */
    private double convertNmeaToDecimal(String coordinate, String direction) {
        try {
            double raw = Double.parseDouble(coordinate);
            int degrees = (int) (raw / 100);
            double minutes = raw - (degrees * 100);
            double decimal = degrees + (minutes / 60.0);

            if (direction.equalsIgnoreCase("S") || direction.equalsIgnoreCase("W")) {
                decimal = -decimal;
            }
            return decimal;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String removeChecksum(String value) {
        if (value == null) return "";
        int index = value.indexOf("*");
        if (index >= 0) {
            return value.substring(0, index);
        }
        return value;
    }

    private float parseFloatSafe(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return 0f;
        }
    }
}