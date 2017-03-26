package backEnd.extractor;

import Util.Config;
import backEnd.data.Song;
import backEnd.data.TrainingSong;
import frontEnd.Colour;

import java.io.File;
import java.io.IOException;

/**
 * Created by Matt on 2017-02-12.
 */
public class FeatureExtractor {
    private static boolean overwrite = false;

    public static void extractAll() throws IOException {
        Runtime rt = Runtime.getRuntime();

        File dir = new File("clips_45seconds");
        if (!dir.isDirectory())
            throw new IOException("Could not find directory containing training music files");

        File out = new File(Config.FEATURES + File.separator + "default");

        for (final File file: dir.listFiles()) {
            if (file.getName().endsWith(".wav")) {
                if (!overwrite && new File(out + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".csv").exists())
                    continue;

                Process pr = rt.exec("./openSMILE-2.1.0/SMILExtract -C " + Config.MFCC_CONFIG + " -I " + file.getAbsolutePath() + " -O " + out + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".csv");
                try {
                    //TODO: Multithread this to speed up
                    pr.waitFor();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void extract(File file) throws IOException {
        File out = new File(Config.FEATURES);

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("./openSMILE-2.1.0/SMILExtract -C " + Config.MFCC_CONFIG + " -I " + file.getAbsolutePath() + " -O " + out + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".csv");
    }

    public static void main(String[] args) {
        SongOrganiser so = new SongOrganiser();
    }
}
