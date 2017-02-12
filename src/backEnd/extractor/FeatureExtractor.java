package backEnd.extractor;

import java.io.File;
import java.io.IOException;

/**
 * Created by Matt on 2017-02-12.
 */
public class FeatureExtractor {
    public static void extractAll() throws IOException {
        Runtime rt = Runtime.getRuntime();

        File dir = new File("clips_45seconds");
        if (!dir.isDirectory())
            throw new IOException("Could not find directory containing training music files");

        File out = new File("features" + File.separator + "default");

        for (final File file: dir.listFiles()) {
            Process pr = rt.exec("");
        }
    }

    public static void extract(File file) throws IOException {
        File out = new File("features");

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("");
    }
}
