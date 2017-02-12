package backEnd.data;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Matt on 2016-11-14.
 */
public class Features {
    HashMap<String, HashMap<String, ArrayList<ArrayList<Double>>>> data;

    public Features() {
        data = new HashMap<>();
    }

    public Features(HashMap<String, HashMap<String, ArrayList<ArrayList<Double>>>> data) {
        this.data = data;
    }

    public void addSongInfo(String name, HashMap<String, ArrayList<ArrayList<Double>>> data) {
        this.data.put(name, data);
    }
    public HashMap<String, ArrayList<ArrayList<Double>>> getSongInfo(String name) {
        return data.get(name);
    }

    public ArrayList<ArrayList<Double>> getSongMFCCs(String name) {
        return data.get(name).get("MFCC");
    }

    public ArrayList<ArrayList<Double>> getSongFeature(String name, String feature) { return data.get(name).get(feature); }

    private RealMatrix toMatrix(String song, String feature) {
        ArrayList<ArrayList<Double>> MFCCs = getSongMFCCs(song);
        double[][] m = new double[MFCCs.size()][MFCCs.get(0).size()];

        for (int i = 0; i < MFCCs.size(); i++) {
            for (int j = 0; j < MFCCs.get(0).size(); j++) {
                m[i][j] = MFCCs.get(i).get(j);
            }
        }
        return new Array2DRowRealMatrix(m);
    }
    public RealMatrix toMatrixMFCC(String song) {
        return toMatrix(song, "MFCC");
    }

    public RealMatrix toMatrixAll(String song) throws Exception {
        HashMap<String, ArrayList<ArrayList<Double>>> songData = data.get(song);

        Set features = songData.keySet();
        Iterator<String> iter = features.iterator();

        //Create the initial matrix first using the first feature
        String feature = iter.next();
        ArrayList<ArrayList<Double>> fData = getSongFeature(song, feature);
        double[][] d = new double[fData.size()][fData.get(0).size()];

        for (int i = 0; i < fData.size(); i++) {
            for (int j = 0; j < fData.get(0).size(); j++) {
                d[i][j] = fData.get(i).get(j);
            }
        }
        RealMatrix m = new Array2DRowRealMatrix(d);

        //Add subsequent features as columns to the right of current matrix
        while (iter.hasNext()) {
            feature = iter.next();
            ArrayList<ArrayList<Double>> fData1 = getSongFeature(song, feature);
            double[][] d1 = new double[fData1.size()][fData1.get(0).size()];

            for (int i = 0; i < fData1.size(); i++) {
                for (int j = 0; j < fData1.get(0).size(); j++) {
                    d1[i][j] = fData1.get(i).get(j);
                }
            }
            RealMatrix n = new Array2DRowRealMatrix(d1);

            if (n.getRowDimension() > m.getRowDimension()) {
                for (int j = 0; j < m.getColumnDimension(); j++) {
                    m = Matrix.addColumn(n, m.getColumn(j), true);
                }
            }
            else {
                for (int j = 0; j < n.getColumnDimension(); j++) {
                    m = Matrix.addColumn(m, n.getColumn(j), true);
                }
            }
        }

        return m;
    }

    public ArrayList<String> getSongNames() {
        Set keys = data.keySet();
        Iterator<String> iter = keys.iterator();

        ArrayList<String> names = new ArrayList<>();
        while (iter.hasNext())
            names.add(iter.next());

        return names;
    }
}
