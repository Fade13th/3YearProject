package backEnd;

import Util.Config;
import backEnd.algorithms.LeastSquaresRegression;
import backEnd.algorithms.RBF;
import backEnd.algorithms.SVM;
import backEnd.data.*;
import backEnd.extractor.SongOrganiser;
import backEnd.io.XMLParser;
import libsvm.svm_model;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Matt on 2016-11-12.
 */
public class Algorithm {
    RealMatrix Y, arousal, valence;

    RealMatrix trainData, testData;
    RealMatrix trainVal, testVal;
    RealMatrix trainAro, testAro;

    float trainPercent = 0.5f;

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

    private void halfTest() throws Exception {
        System.out.println("Setting up matrices...");
        setupMatrices();
        System.out.println("Matrices setup");

        //RealMatrix vNorm = Matrix.normalise(valence);
        //RealMatrix aNorm = Matrix.normalise(arousal);

        TrainingSong testSong = SongOrganiser.getSongData("181");

        System.out.println("Creating SVM model for valence...");
        svm_model valenceModel = SVM.selfOptimizingLinearLibSVM(trainData, trainVal);
        System.out.println("Testing valence model...");
        RealMatrix vTest = SVM.testModel(testSong.getData(), valenceModel);

        System.out.println("Creating SVM model for arousal...");
        svm_model arousalModel = SVM.selfOptimizingLinearLibSVM(trainData, trainAro);
        System.out.println("Testing arousal model...");
        RealMatrix aTest = SVM.testModel(testSong.getData(), arousalModel);

        System.out.println("Valence error: " + Matrix.error(testSong.getValenceScores(), vTest));
        System.out.println("Arousal error: " + Matrix.error(testSong.getArousalScores(), aTest));

        //RealMatrix vTest = LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, vtr);
        //RealMatrix aTest = LeastSquaresRegression.leastSquaresRegression(Ytr, Yts, atr);

        //RealMatrix vTest = RBF.radialBasisFunctions(Ytr, Yts, vtr, 10);
        //RealMatrix aTest = RBF.radialBasisFunctions(Ytr, Yts, atr, 10);

        Graph graph = new Graph(testSong.getValenceScores(), testSong.getArousalScores(), vTest, aTest);
        graph.pack();
        graph.setVisible(true);
    }

    private void setupMatrices() throws Exception {
        SongOrganiser so = new SongOrganiser(false);

        Set<String> songNames = so.getKeys();

        int trainSize = (int) Math.ceil(songNames.size() * trainPercent);

        int i = 0;

        for (String name : songNames) {
            TrainingSong song = so.getSongData(name);

            if (i < trainSize) {
                if (trainData == null) {
                    trainData = song.getData();
                    trainVal = song.getValenceScores();
                    trainAro = song.getArousalScores();
                }
                else {
                    trainData = Matrix.concat(trainData, song.getData());
                    trainVal = Matrix.concat(trainVal, song.getValenceScores());
                    trainAro = Matrix.concat(trainAro, song.getArousalScores());
                }
            }
            else {
                if (testData == null) {
                    testData = song.getData();
                    testVal = song.getValenceScores();
                    testAro = song.getArousalScores();
                }
                else {
                    testData = Matrix.concat(testData, song.getData());
                    testVal = Matrix.concat(testVal, song.getValenceScores());
                    testAro = Matrix.concat(testAro, song.getArousalScores());
                }
            }
            i++;
        }
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
        Config.init();
        new Algorithm();
        /*
        try {
            runWithCsv("housing.data");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
