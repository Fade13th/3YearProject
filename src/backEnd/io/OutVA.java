package backEnd.io;

import Util.Config;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by matt on 04/04/17.
 */
public class OutVA {
    public static void saveVA(RealMatrix valence, RealMatrix arousal, String name) {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(new File(Config.V_A_SCORES + File.separator + name + ".csv")));

            out.write("V;A\n");

            for (int i = 0; i < valence.getRowDimension(); i++) {
                out.write(valence.getRow(i)[0] + ";" + arousal.getRow(i)[0] + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (out != null)
                try {
                    out.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
