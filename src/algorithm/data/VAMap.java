package algorithm.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Matt on 2016-11-14.
 */
public class VAMap {
    HashMap<String, HashMap<String, ArrayList<Double>>> data;

    public VAMap(String filename) throws IOException {
        if (!filename.endsWith(".csv"))
            throw new IOException("Files must be of csv type");

        HashMap<String, HashMap<String, ArrayList<Double>>> result = new HashMap<>();

        Reader in = null;
        try {
            in = new FileReader(filename);
            Iterator<CSVRecord> records = CSVFormat.RFC4180.parse(in).iterator();
            records.next();

            HashMap<String, ArrayList<Double>> songs = new HashMap<>();

            while (records.hasNext()) {
                CSVRecord record = records.next();

                ArrayList<Double> values = new ArrayList<>();

                for (int i = 1; i < record.size(); i++) {
                    values.add(Double.valueOf(record.get(i)));
                }
                songs.put(record.get(0), values);
            }
            result.put(filename, songs);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        data = result;
    }

    public VAMap(HashMap<String, HashMap<String, ArrayList<Double>>> data) {
        this.data = data;
    }

    private RealMatrix getMatrix(String song, String name) {
        Set keys = data.keySet();
        Iterator i = keys.iterator();

        while (i.hasNext()) {
            String s = (String) i.next();
            if (s.contains(name)) {
                HashMap<String, ArrayList<Double>> songs = data.get(s);

                ArrayList<Double> songData = songs.get(song);

                double[] d = new double[songData.size()];

                for (int j = 0; j < d.length; j++)
                    d[j] = songData.get(j);

                return new Array2DRowRealMatrix(d);
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    public RealMatrix getAverageMatrix(String song){
        return getMatrix(song, "average");
    }

    public ArrayList<String> getSongNames() {
        Set keys = data.get("valence_cont_average.csv").keySet();
        Iterator<String> iter = keys.iterator();

        ArrayList<String> names = new ArrayList<>();
        while (iter.hasNext())
            names.add(iter.next());

        return names;
    }
}
