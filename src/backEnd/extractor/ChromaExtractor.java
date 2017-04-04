package backEnd.extractor;

import Util.Config;

import java.io.File;
import java.io.IOException;

/**
 * Created by matt on 04/04/17.
 */
public class ChromaExtractor {
    public static void extractChroma(File file) {
        Runtime rt = Runtime.getRuntime();

        String name = file.getName();
        String songName = file.getAbsolutePath() + ".wav";
        String out;

        if (!name.contains("."))
            out = Config.CHRMOA + File.separator + name + ".csv";
        else
            out = Config.CHRMOA + File.separator + name;

        String cmd = "./openSMILE-2.1.0/SMILExtract -C " + Config.CHROMA_CONFIG + " -I " + songName + " -O " + out;

        try {
            Process pr = rt.exec(cmd);
            try {
                //TODO: Multithread this to speed up
                pr.waitFor();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
