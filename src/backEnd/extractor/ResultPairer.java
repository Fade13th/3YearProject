package backEnd.extractor;

import backEnd.data.Song;
import backEnd.data.TrainingSong;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.io.*;
import java.util.Map;

/**
 * Created by matt on 12/02/17.
 */
public class ResultPairer {
    private final Map<String, TrainingSong> songMap;

    public ResultPairer(Map<String, TrainingSong> songMap) {
        this.songMap = songMap;
    }

    public void pairValence() { pairer(true); }
    public void pairArousal() { pairer(false); }

    private void pairer(boolean valence) {
        File f;
        if (valence)
            f = new File("valence_cont_average.csv");
        else
            f = new File("arousal_cont_average.csv");
        
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(f));

            String line = in.readLine();

            while ((line = in.readLine()) != null) {
                String[] split = line.split(",");

                if (songMap.containsKey(split[0])) {
                    double[] values = new double[split.length-1];

                    for (int i = 0; i < values.length; i++) {
                        values[i] = Double.parseDouble(split[i+1]);
                    }

                    if (valence)
                        songMap.get(split[0]).setValenceScores(new Array2DRowRealMatrix(values));
                    else
                        songMap.get(split[0]).setArousalScores(new Array2DRowRealMatrix(values));
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
