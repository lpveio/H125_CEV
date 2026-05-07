package br.cta.ipev.h125.replay;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ReplayFileManager {

    private final Context context;

    public ReplayFileManager(Context context) {
        this.context = context;
    }

    /**
     * Retorna todos os logs disponíveis
     */
    public List<File> getFlightLogs() {

        List<File> result = new ArrayList<>();

        File dir = new File(
                context.getExternalFilesDir(null),
                "logs"
        );

        if (!dir.exists()) {
            return result;
        }

        File[] files = dir.listFiles();

        if (files == null) {
            return result;
        }

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.compare(
                        o2.lastModified(),
                        o1.lastModified()
                );
            }
        });

        for (File f : files) {

            if (f.isFile()) {

                String name =
                        f.getName().toLowerCase();

                if (name.endsWith(".txt")
                        || name.endsWith(".csv")) {

                    result.add(f);
                }
            }
        }

        return result;
    }
}
