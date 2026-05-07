package br.cta.ipev.h125.replay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FlightLogReader {

    public List<ReplayFrame> read(File file) {

        List<ReplayFrame> frames = new ArrayList<>();

        try {

            BufferedReader reader =
                    new BufferedReader(new FileReader(file));

            String line;

            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                // ignora header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] split = line.split(",");

                double[] values =
                        new double[split.length];

                for (int i = 0;
                     i < split.length;
                     i++) {

                    values[i] =
                            Double.parseDouble(split[i]);
                }

                double time = values[0];

                frames.add(new ReplayFrame(time, values));
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return frames;
    }
}
