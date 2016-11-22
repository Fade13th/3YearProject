package algorithm;

import algorithm.algorithms.LeastSquaresRegression;
import algorithm.algorithms.RBF;
import algorithm.data.Features;
import algorithm.data.Matrix;
import algorithm.data.VAMap;
import algorithm.io.XMLParser;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Matt on 2016-11-12.
 */
public class Algorithm {
    Features data;
    VAMap valenceResults;
    VAMap arousalResults;

    RealMatrix Y, arousal, valence;

    public Algorithm() {
        data = XMLParser.extractFeatures("values.xml");
        try {
            valenceResults = new VAMap("valence_cont_average.csv");
            arousalResults = new VAMap("arousal_cont_average.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            tenFoldValidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RealMatrix getSongMatrices(Features features, ArrayList<String> songNames) throws Exception {
        RealMatrix Y = null;

        for (String songName : songNames) {
            if (Y == null) {
                Y = features.toMatrixAll(songName);
                Y = Y.getSubMatrix(30,Y.getRowDimension()-1,0,Y.getColumnDimension()-1); //Line only here because result data is limited as such
            }
            else {
                RealMatrix m = features.toMatrixAll(songName);
                m = m.getSubMatrix(30,m.getRowDimension()-1,0,m.getColumnDimension()-1); //Line only here because result data is limited as such
                try {
                    Y = Matrix.concat(Y, m);
                }
                catch (Exception e) {
                    System.err.println("Error evaluating features for song: " + songName);
                }
            }
        }
        return Y;
    }

    private RealMatrix getSongMatrices(VAMap map, ArrayList<String> songNames) throws Exception {
        RealMatrix Y = null;

        for (String songName : songNames) {
            if (Y == null) {
                Y = map.getAverageMatrix(songName);
            }
            else {
                RealMatrix m = map.getAverageMatrix(songName);
                try {
                    Y = Matrix.concat(Y, m);
                }
                catch (Exception e) {
                    System.err.println("Error evaluating features for song: " + songName);
                }
            }
        }
        return Y;
    }

    private RealMatrix getmappedMatrix(Features features, VAMap map, ArrayList<String> songNames) throws Exception {
        RealMatrix M = null;
        for (String songName : songNames) {
            try {
                RealMatrix Y = features.toMatrixAll(songName);
                Y = Y.getSubMatrix(30,Y.getRowDimension()-1,0,Y.getColumnDimension()-1); //Line only here because result data is limited as such

                RealMatrix Z = map.getAverageMatrix(songName);

                if (Z.getRowDimension() != Y.getRowDimension()) continue;

                Y = Matrix.addColumn(Y, Z.getColumn(0));

                if (M == null) M = Y;
                else
                    M = Matrix.concat(M, Y);
            }
            catch (Exception e) {
                System.err.println("Error evaluating features for song: " + songName);
            }
        }
        return M;
    }

    private void runRegression() throws Exception {
        setupMatrices();

        //outToMatlab(Y, valence, "valence");
        //outToMatlab(Y, arousal, "arousal");

        Y = Matrix.addColumn(Y, 1.0);
        Y = Matrix.normalise(Y);

        RealMatrix vNorm = Matrix.normalise(valence);
        RealMatrix aNorm = Matrix.normalise(arousal);

        RealMatrix vTest = LeastSquaresRegression.leastSquaresRegression(Y, vNorm);
        RealMatrix aTest = LeastSquaresRegression.leastSquaresRegression(Y, aNorm);

        System.out.println(Matrix.error(vNorm, vTest));
        System.out.println(Matrix.error(aNorm, aTest));

        Graph graph = new Graph(vNorm, vTest, aNorm, aTest);
        graph.pack();
        graph.setVisible(true);
    }

    private void tenFoldValidate() throws Exception {
        setupMatrices();

        Y = Matrix.addColumn(Y, 1.0);
        Y = Matrix.normalise(Y);

        RealMatrix vNorm = Matrix.normalise(valence);
        RealMatrix aNorm = Matrix.normalise(arousal);

        int p = Y.getColumnDimension();
        int N = Y.getRowDimension() - (Y.getRowDimension()%10);

        RealMatrix Ytr = new Array2DRowRealMatrix((9*(int)Math.floor(N/10)), p);
        RealMatrix Yts = new Array2DRowRealMatrix((int)Math.floor(N/10), p);
        RealMatrix vtr = new Array2DRowRealMatrix((9*(int)Math.floor(N/10)), 1);
        RealMatrix vts = new Array2DRowRealMatrix((int)Math.floor(N/10), 1);
        RealMatrix atr = new Array2DRowRealMatrix((9*(int)Math.floor(N/10)), 1);
        RealMatrix ats = new Array2DRowRealMatrix((int)Math.floor(N/10), 1);

        double totalaError = 0.0;
        double totalvError = 0.0;

        for (int i = 0; i < 10; i++) {
            int start = i * (int)Math.floor(N/10);
            int end = (i+1) * (int)Math.floor(N/10);
            int k = 0;

            for (int j = 0; j < N; j++) {
                if (j >= start && j < end) {
                    Yts.setRow(k, Y.getRow(j));
                    vts.setEntry(k,0,vNorm.getEntry(j,0));
                    ats.setEntry(k,0,aNorm.getEntry(j,0));
                    k++;
                }
                else if (j < start) {
                    Ytr.setRow(j, Y.getRow(j));
                    vtr.setEntry(j,0,vNorm.getEntry(j,0));
                    atr.setEntry(j,0,aNorm.getEntry(j,0));
                }
                else {
                    int t = j - k;
                    Ytr.setRow(t, Y.getRow(t));
                    vtr.setEntry(t,0,vNorm.getEntry(t,0));
                    atr.setEntry(t,0,aNorm.getEntry(t,0));
                }
            }

            totalaError += Matrix.error(ats, RBF.radialBasisFunctions(Ytr, Yts, atr, 10));
            totalvError += Matrix.error(vts, RBF.radialBasisFunctions(Ytr, Yts, vtr, 10));


            //totalaError += Matrix.error(ats, LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, atr));
            //totalvError += Matrix.error(vts, LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, vtr));
        }

        System.out.println("Arousal average error: " + totalaError/10);
        System.out.println("Valence average error: " + totalvError/10);
    }

    private void setupMatrices() throws Exception {
        ArrayList<String> resultSongs = valenceResults.getSongNames();
        ArrayList<String> songNames = data.getSongNames();
        songNames.retainAll(resultSongs);

        RealMatrix Yvalence = getmappedMatrix(data, valenceResults, songNames);
        RealMatrix Yarousal = getmappedMatrix(data, arousalResults, songNames);

        Y = Yarousal.getSubMatrix(0, Yarousal.getRowDimension()-1, 0, Yarousal.getColumnDimension()-2);
        arousal = Yarousal.getSubMatrix(0, Yarousal.getRowDimension()-1, Yarousal.getColumnDimension()-1, Yarousal.getColumnDimension()-1);
        valence = Yvalence.getSubMatrix(0, Yvalence.getRowDimension()-1, Yvalence.getColumnDimension()-1, Yvalence.getColumnDimension()-1);
    }

    private void outToMatlab(RealMatrix data, RealMatrix expected, String filename) {
        try {
            RealMatrix combined = Matrix.addColumn(data, expected.getColumn(0));
            File f = new File(filename);

            PrintWriter out = new PrintWriter(f);
            out.write(Matrix.toString(combined));
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Algorithm();
    }
}
