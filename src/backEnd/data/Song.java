package backEnd.data;

import backEnd.extractor.FeatureExtractor;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;

/**
 * Created by matt on 12/02/17.
 */
public class Song {
    RealMatrix data;
    String name;

    private static final int offset = 26;

    public Song(String name) {
        this.name = name;
        this.data = getDataFromCsv(new File("features/default/" + name + ".csv"));
    }

    public String getName() { return name; }

    public RealMatrix getData() { return data; }

    public Song(File file, Boolean training) {
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));

        this.name = name;

        File f = null;
        if (training)
            f = new File("features/default/" + name + ".csv");
        else {
            try {
                FeatureExtractor.extract(file);
                f = new File("features/" + name + ".csv");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        this.data = getDataFromCsv(f);
    }

    private RealMatrix getDataFromCsv(File file) {
        RealMatrix m = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));

            String line = in.readLine(); // Don't want the first line

            String[] s = line.split(";");
            int f = s.length - 2;

            while ((line = in.readLine()) != null) {
                String[] split = line.split(";");
                double[] features = new double[f - offset];

                for (int i = 0; i < f - offset; i++)
                    features[i] = Double.parseDouble(split[i+2]);

                if (m == null)
                    m = new Array2DRowRealMatrix(new double[][]{features});
                else
                    m = Matrix.concat(m, new Array2DRowRealMatrix(new double[][]{features}));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    return m;
    }
}
