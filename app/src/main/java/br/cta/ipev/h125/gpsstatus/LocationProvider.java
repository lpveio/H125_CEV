package br.cta.ipev.h125.gpsstatus;

import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;

import br.cta.ipev.h125.AppManager;
import br.cta.ipev.h125.classes.LogFileStatus;

public class LocationProvider {

    private static final String TAG = "LocationProvider";
    private final Bundle bundle;
    private final AppManager appManager;
    private int countSats;
    private int countSatsGA;
    private int countSatsGB;
    private int countSatsGL;
    private int countSatsGP;
    private ArrayList<String> satIds; // Mantido caso utilize em outro lugar
    private String pendingLogFileStatus = null; // Buffer para mensagens de log do NovAtel partidas

    public LocationProvider(AppManager appManager) {
        this.appManager = appManager;
        this.countSatsGP = 0;
        this.countSatsGL = 0;
        this.countSatsGA = 0;
        this.countSatsGB = 0;
        this.countSats = 0;
        this.bundle = new Bundle();

    }

    /**
     * Ponto de entrada único e unificado para as mensagens vindas do BluetoothManager.
     * Processa sentenças NMEA nativas e mensagens de gerenciamento do NovAtel.
     */
    public void processNmeaMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        //viewModel.postNmea(message);
        message = message.trim();
        appManager.updateLastNmea(message);

        // =========================================================================
        // INTERCEPTAÇÃO: Mensagens de Status de Gravação de Log do Receptor
        // =========================================================================
        if (message.startsWith("<LOGFILESTATUS") || (pendingLogFileStatus != null && message.startsWith("<"))) {
            processLogFileStatus(message);
            return;
        }

        // =========================================================================
        // PARSER NMEA PADRÃO
        // =========================================================================

        GnssData data = appManager.getGnssData();

        try {
            String[] strArrSplit = message.split(",");
            if (strArrSplit.length == 0) {
                return;
            }

            String sentenceType = strArrSplit[0];

            // GGA - Extrai posição, altitude e satélites do ProPak
            if (sentenceType.contains("GGA")) {
                if (strArrSplit.length > 2 && !strArrSplit[2].isEmpty()) {
                    String latDir = strArrSplit.length > 3 ? strArrSplit[3] : "N";
                    data.setLatitude(convertNmeaToDecimal(strArrSplit[2], latDir));
                    data.setLatDirection(latDir);
                }

                if (strArrSplit.length > 4 && !strArrSplit[4].isEmpty()) {
                    String longDir = strArrSplit.length > 5 ? strArrSplit[5] : "E";
                    data.setLongitude(convertNmeaToDecimal(strArrSplit[4], longDir));
                    data.setLongDirection(longDir);
                }

                if (strArrSplit.length > 6 && !strArrSplit[6].isEmpty()) {
                    int fixQuality = Integer.parseInt(strArrSplit[6]);
                    data.setDiffStatus(fixQuality);
                    data.setFixQuality(fixQuality);
                    data.setFixStatus(fixQuality > 0 ? "Fix" : "No Fix");
                }

                if (strArrSplit.length > 7 && !strArrSplit[7].isEmpty()) {
                    int sats = Integer.parseInt(strArrSplit[7]);
                    data.setSatellitesInUse(sats);
                }

                if (strArrSplit.length > 8 && !strArrSplit[8].isEmpty()) {
                    data.setSigma(strArrSplit[8]);
                    data.calculateSigma();
                }

                if (strArrSplit.length > 9 && !strArrSplit[9].isEmpty()) {
                    data.setAltitude(Double.parseDouble(strArrSplit[9]));
                }
            }

            // GSA
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

            // GNS
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

            // GST
            if (sentenceType.contains("GST")) {
                if (strArrSplit.length > 8 && !strArrSplit[8].isEmpty()) {
                    String value = removeChecksum(strArrSplit[8]);
                    float vrms = parseFloatSafe(value);
                    data.setVrms(vrms);
                    data.setTwovrms(vrms * 2.0f);
                }
                if (strArrSplit.length > 7 && !strArrSplit[6].isEmpty() && !strArrSplit[7].isEmpty()) {
                    double rms = (Math.pow(parseFloatSafe(strArrSplit[6]), 2.0)
                            + Math.pow(parseFloatSafe(strArrSplit[7]), 2.0)) / 2.0;
                    float hrms = (float) Math.sqrt(rms);
                    data.setHrms(hrms);
                    data.setTwohrms(hrms * 2.0f);
                }
            }

            // GSV
            if (sentenceType.contains("GSV")) {
                updateSatelliteCounters(sentenceType, strArrSplit);
                int total = countSatsGP + countSatsGL + countSatsGA + countSatsGB;
                countSats = total;
                data.setSatinView(total);
            }

            // RMC
            if (sentenceType.contains("RMC")) {
                if (strArrSplit.length > 7 && !strArrSplit[7].isEmpty()) {
                    float speedKnots = parseFloatSafe(strArrSplit[7]);
                    // Armazene a velocidade em 'data' se necessário futuramente
                }
            }

            // Envia o objeto geográfico atualizado para os observadores da UI Thread
            appManager.notifyGnssChanged();

        } catch (Exception e) {
            Log.e(TAG, "Erro NMEA : " + e.getMessage());
        }
    }

    /**
     * Une e gerencia strings em lote de Logs de Armazenamento NovAtel.
     */
    private void processLogFileStatus(String line) {

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

    /**
     * Decodifica de forma isolada os dados de telemetria do Cartão SD e Memória.
     */
    private void parseLogFileStatus(String msg) {

        LogFileStatus status = appManager.getLogFileStatus();

        try {
            String[] parts = msg.trim().split("\\s+");

            for (int i = 0; i < parts.length; i++) {
                String p = parts[i];

                if (isStatusToken(p)) {
                    status.setState(p);
                    if (p.equals("OPEN")) {
                        status.setRecording(true);
                    } else {
                        status.setRecording(false);
                        status.setFileName("");
                        status.setStorage("");
                        status.setFileSizeBytes(0);
                    }
                }
                else if (p.contains(".LOG") || p.contains(".BIN") || p.contains(".DAT")) {
                    status.setFileName(p.replace("\"", ""));

                    if (i + 1 < parts.length) {
                        try { status.setFileSizeBytes(Long.parseLong(parts[i + 1])); } catch (NumberFormatException ignored) {}
                    }
                    if (i + 2 < parts.length) {
                        status.setStorage(parts[i + 2]);
                    }
                    if (i + 3 < parts.length) {
                        try { status.setFreeKb(Long.parseLong(parts[i + 3])); } catch (NumberFormatException ignored) {}
                    }
                    if (i + 4 < parts.length) {
                        try { status.setTotalKb(Long.parseLong(parts[i + 4])); } catch (NumberFormatException ignored) {}
                    }
                    status.setUsedKb(status.getTotalKb() - status.getFreeKb());
                }
                else if ((p.equals("INTERNAL_FLASH") || p.equals("USBSTICK")) && (i + 1 < parts.length)) {
                    try {
                        long value = Long.parseLong(parts[i + 1]);
                        status.setFreeKb(value);
                        if (i + 2 < parts.length) {
                            status.setTotalKb(Long.parseLong(parts[i + 2]));
                            status.setUsedKb(status.getTotalKb() - status.getFreeKb());
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            // Notifica o ViewModel sobre a alteração das estatísticas de log
             appManager.notifyLogChanged();

        } catch (Exception e) {
            Log.e(TAG, "Erro ao decodificar LogFileStatus : " + e.getMessage());
        }
    }

    private boolean isStatusToken(String p) {
        return p.equals("OPEN") || p.equals("CLOSE") || p.equals("BUSY") ||
                p.equals("ERROR") || p.equals("MEDIA_COPY") ||
                p.equals("MEDIA_BUSY") || p.equals("MEDIA_ERROR");
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
            Log.e(TAG, "Erro GSV : " + e.getMessage());
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