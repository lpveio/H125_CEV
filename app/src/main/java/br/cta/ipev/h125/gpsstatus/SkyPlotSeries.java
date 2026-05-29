package br.cta.ipev.h125.gpsstatus;

import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGPS;

import java.util.HashMap;

public class SkyPlotSeries<E extends DataPointGPS, DataPointGlonass> {
    public HashMap<String, DataPointGPS> dataPoints = new HashMap<>();
    public HashMap<String, DataPointGlonass> dataPointsGlonass = new HashMap<>();
    String id;
}