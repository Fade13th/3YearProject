package backEnd;

import backEnd.algorithms.LeastSquaresRegression;
import backEnd.algorithms.RBF;
import backEnd.algorithms.SVM;
import backEnd.data.Features;
import backEnd.data.Matrix;
import backEnd.data.VAMap;
import backEnd.extractor.SongOrganiser;
import backEnd.io.XMLParser;
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
    RealMatrix Y, arousal, valence;

    public Algorithm() {
        try {
            //runRegression();
            halfTest();
            //nineTenthTrainingTest();
            //tenFoldValidate();
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

    private RealMatrix getmappedMatrix(Features features, VAMap valence, VAMap arousal, ArrayList<String> songNames) throws Exception {
        RealMatrix M = null;
        for (String songName : songNames) {
            try {
                RealMatrix Y = features.toMatrixAll(songName);
                Y = Y.getSubMatrix(30,Y.getRowDimension()-1,0,Y.getColumnDimension()-1); //Line only here because result data is limited as such

                RealMatrix Z = valence.getAverageMatrix(songName);
                RealMatrix Z2 = arousal.getAverageMatrix(songName);

                if (Z.getRowDimension() != Y.getRowDimension()) continue;

                Y = Matrix.addColumn(Y, Z.getColumn(0));
                Y =Matrix.addColumn(Y, Z2.getColumn(0));

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

    private void halfTest() throws Exception {
        setupMatrices();

        //outToMatlab(Y, valence, "valence");
        //outToMatlab(Y, arousal, "arousal");

        Y = Matrix.addColumn(Y, 1.0);
       // Y = Matrix.normalise(Y);

        //RealMatrix vNorm = Matrix.normalise(valence);
        //RealMatrix aNorm = Matrix.normalise(arousal);
        RealMatrix vNorm = valence;
        RealMatrix aNorm = arousal;

        RealMatrix Ytr = new Array2DRowRealMatrix(Y.getRowDimension()/2, Y.getColumnDimension());
        RealMatrix Yts = new Array2DRowRealMatrix(Y.getRowDimension()/2, Y.getColumnDimension());
        RealMatrix atr = new Array2DRowRealMatrix(Y.getRowDimension()/2, 1);
        RealMatrix ats = new Array2DRowRealMatrix(Y.getRowDimension()/2, 1);
        RealMatrix vtr = new Array2DRowRealMatrix(Y.getRowDimension()/2, 1);
        RealMatrix vts = new Array2DRowRealMatrix(Y.getRowDimension()/2, 1);

        for (int i = 0; i < 2*(Y.getRowDimension()/2); i++) {
            if (i < Y.getRowDimension()/2) {
                Ytr.setRow(i, Y.getRow(i));
                atr.setRow(i, aNorm.getRow(i));
                vtr.setRow(i, vNorm.getRow(i));
            }
            else {
                int j = i - Y.getRowDimension()/2;
                Yts.setRow(j, Y.getRow(i));
                ats.setRow(j, aNorm.getRow(i));
                vts.setRow(j, vNorm.getRow(i));
            }
        }

        //RealMatrix vTest = SVM.selfOptimizingLinearLibSVM(Ytr, Yts, vtr);
        //RealMatrix aTest = SVM.selfOptimizingLinearLibSVM(Ytr, Yts, atr);

        RealMatrix vTest = LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, vtr);
        //RealMatrix aTest = LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, atr);

        //RealMatrix vTest = RBF.radialBasisFunctions(Ytr, Yts, vtr, 10);
        //RealMatrix aTest = RBF.radialBasisFunctions(Ytr, Yts, atr, 10);

        System.out.println("Valence error: " + Matrix.error(vts, vTest));
        //System.out.println("Arousal error: " + Matrix.error(ats, aTest));

        Graph graph = new Graph(vts, vTest, vts, vTest);
        graph.pack();
        graph.setVisible(true);
    }

    private void nineTenthTrainingTest() throws Exception {
        setupMatrices();

        //outToMatlab(Y, valence, "valence");
        //outToMatlab(Y, arousal, "arousal");

        Y = Matrix.addColumn(Y, 1.0);
        Y = Matrix.normalise(Y);

        RealMatrix vNorm = Matrix.normalise(valence);
        RealMatrix aNorm = Matrix.normalise(arousal);

        RealMatrix Ytr = new Array2DRowRealMatrix(9*Y.getRowDimension()/10, Y.getColumnDimension());
        RealMatrix Yts = new Array2DRowRealMatrix(Y.getRowDimension()/10, Y.getColumnDimension());
        RealMatrix atr = new Array2DRowRealMatrix(9*Y.getRowDimension()/10, 1);
        RealMatrix ats = new Array2DRowRealMatrix(Y.getRowDimension()/10, 1);
        RealMatrix vtr = new Array2DRowRealMatrix(9*Y.getRowDimension()/10, 1);
        RealMatrix vts = new Array2DRowRealMatrix(Y.getRowDimension()/10, 1);

        for (int i = 0; i < 10*(Y.getRowDimension()/10); i++) {
            if (i < 9*Y.getRowDimension()/10) {
                Ytr.setRow(i, Y.getRow(i));
                atr.setRow(i, aNorm.getRow(i));
                vtr.setRow(i, vNorm.getRow(i));
            }
            else {
                int j = i - 9*Y.getRowDimension()/10;
                Yts.setRow(j, Y.getRow(i));
                ats.setRow(j, aNorm.getRow(i));
                vts.setRow(j, vNorm.getRow(i));
            }
        }

        //RealMatrix vTest = LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, vtr);
        //RealMatrix aTest = LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, atr);

        //RealMatrix vTest = RBF.radialBasisFunctions(Ytr, Yts, vtr, 50);
        //RealMatrix aTest = RBF.radialBasisFunctions(Ytr, Yts, atr, 50);

        RealMatrix vTest = SVM.selfOptimizingLinearLibSVM(Ytr, Yts, vtr);
        //RealMatrix aTest = SVM.selfOptimizingLinearLibSVM(Ytr, Yts, atr);

        System.out.println("Valence error: " + Matrix.error(vts, vTest));
        //System.out.println("Arousal error: " + Matrix.error(ats, aTest));

        Graph graph = new Graph(vts, vTest, vts, vTest);
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
        SongOrganiser so = new SongOrganiser();
        RealMatrix[] allData = Matrix.randPerm(so.getMatrices());

        this.Y = allData[0];
        this.valence = allData[1];
        this.arousal = allData[2];
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

    private static void runWithCsv(String filename) throws Exception {
        RealMatrix m = Matrix.fromCsv(filename);

        m = Matrix.randPerm(m);

        RealMatrix Y = m.getSubMatrix(0,m.getRowDimension()-1,0,m.getColumnDimension()-2);
        RealMatrix target = m.getColumnMatrix(m.getColumnDimension()-1);

        Y = Matrix.addColumn(Y, 1.0);
        Y = Matrix.normalise(Y);

        RealMatrix tNorm = Matrix.normalise(target);

        int p = Y.getColumnDimension();
        int N = Y.getRowDimension() - (Y.getRowDimension()%10);

        RealMatrix Ytr = new Array2DRowRealMatrix((9*(int)Math.floor(N/10)), p);
        RealMatrix Yts = new Array2DRowRealMatrix((int)Math.floor(N/10), p);
        RealMatrix ttr = new Array2DRowRealMatrix((9*(int)Math.floor(N/10)), 1);
        RealMatrix tts = new Array2DRowRealMatrix((int)Math.floor(N/10), 1);

        double totalError = 0.0;

       // for (int i = 0; i < 10; i++) {
        int i = 0;

            int start = i * (int)Math.floor(N/10);
            int end = (i+1) * (int)Math.floor(N/10);
            int k = 0;

            for (int j = 0; j < N; j++) {
                if (j >= start && j < end) {
                    Yts.setRow(k, Y.getRow(j));
                    tts.setEntry(k,0,tNorm.getEntry(j,0));
                    k++;
                }
                else if (j < start) {
                    Ytr.setRow(j, Y.getRow(j));
                    ttr.setEntry(j,0,tNorm.getEntry(j,0));
                }
                else {
                    int t = j - k;
                    Ytr.setRow(t, Y.getRow(t));
                    ttr.setEntry(t,0,tNorm.getEntry(t,0));
                }
            }
            RealMatrix predicted = RBF.radialBasisFunctions(Ytr, Yts, ttr, 10);
            totalError += Matrix.error(tts, predicted);

            //totalaError += Matrix.error(ats, LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, atr));
            //totalvError += Matrix.error(vts, LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, vtr));

            Graph graph = new Graph(tts, predicted, tts, predicted);
            graph.pack();
            graph.setVisible(true);
       // }

        System.out.println("Average error: " + totalError);
        //System.out.println("Average error: " + totalError/10);
    }

    public static void main(String[] args) {
        new Algorithm();
        /*
        try {
            runWithCsv("housing.data");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
