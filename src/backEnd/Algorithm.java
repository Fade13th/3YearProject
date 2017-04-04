package backEnd;

import Util.Config;
import backEnd.algorithms.LeastSquaresRegression;
import backEnd.algorithms.RBF;
import backEnd.algorithms.SVM;
import backEnd.data.*;
import backEnd.extractor.SongOrganiser;
import backEnd.io.OutVA;
import backEnd.io.XMLParser;
import libsvm.svm_model;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Matt on 2016-11-12.
 */
public class Algorithm {
    RealMatrix Y, arousal, valence;

    RealMatrix trainData;
    List<TrainingSong> testSongs;

    RealMatrix trainVal, testVal;
    RealMatrix trainAro, testAro;

    float trainPercent = 0.9f;

    public Algorithm() {
        try {
            SVMRun();
            //RBFTest();
        }
        catch (Exception e) {
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


    svm_model valenceModel;
    svm_model arousalModel;

    private void SVMRun() throws Exception {
        System.out.println("Setting up matrices...");
        setupMatrices();
        System.out.println("Matrices setup");

        System.out.println("Creating SVM model for valence...");
        valenceModel = SVM.selfOptimizingLinearLibSVM(trainData, trainVal);
        System.out.println("Testing valence model...");

        System.out.println("Creating SVM model for arousal...");
        arousalModel = SVM.selfOptimizingLinearLibSVM(trainData, trainAro);
        System.out.println("Testing arousal model...");

        float totValErr = 0;
        float totAroErr = 0;
        float i = 0;

        for (TrainingSong testSong : testSongs) {
            RealMatrix vTest = SVM.testModel(testSong.getData(), valenceModel);
            RealMatrix aTest = SVM.testModel(testSong.getData(), arousalModel);

            totValErr += Matrix.error(testSong.getValenceScores(), vTest);
            totAroErr += Matrix.error(testSong.getArousalScores(), aTest);
            i++;

            OutVA.saveVA(vTest, aTest, testSong.getName());
            /*
            Graph graph = new Graph(testSong.getValenceScores(), testSong.getArousalScores(), vTest, aTest);
            graph.pack();
            graph.setVisible(true);*/
        }

        System.out.println("Valence error: " + totValErr/i);
        System.out.println("Arousal error: " + totAroErr/i);
    }

    public void SVMTest(Song song) {
        RealMatrix vTest = SVM.testModel(song.getData(), valenceModel);
        RealMatrix aTest = SVM.testModel(song.getData(), arousalModel);

        OutVA.saveVA(vTest, aTest, song.getName());
    }

    private void RBFTest() throws Exception {
        System.out.println("Setting up matrices...");
        setupMatrices();
        System.out.println("Matrices setup");

        Matrix dataM = new Matrix();
        Matrix valM = new Matrix();
        Matrix aroM = new Matrix();

        trainData = dataM.normalise(trainData);
        trainVal = valM.normalise(trainVal);
        trainAro = aroM.normalise(trainAro);

        RealMatrix Yts = testSongs.get(0).getData();

        for (int i = 1; i < testSongs.size(); i++) {
            Yts = Matrix.concat(Yts, testSongs.get(i).getData());
        }

        RBF rbfV = new RBF();
        RBF rbfA = new RBF();

        RealMatrix lambdaV = rbfV.radialBasisFunctions(trainData, trainVal, 100);
        RealMatrix lambdaA = rbfA.radialBasisFunctions(trainData, trainAro, 100);

        for (int i = 0; i < testSongs.size(); i++) {
            RealMatrix vTest = rbfV.predict(valM.normalise(testSongs.get(i).getData()), lambdaV);
            RealMatrix aTest = rbfA.predict(aroM.normalise(testSongs.get(i).getData()), lambdaA);

            RealMatrix vReal = testSongs.get(i).getValenceScores();
            RealMatrix aReal = testSongs.get(i).getArousalScores();

            Graph graph = new Graph(vReal, aReal, vTest, aTest);
            graph.pack();
            graph.setVisible(true);
        }


    }

    private void setupMatrices() throws Exception {
        SongOrganiser so = new SongOrganiser(true);

        Set<String> songSet = so.getKeys();
        List<String> songNames = new ArrayList<>();
        songNames.addAll(songSet);
        Collections.shuffle(songNames);

        int trainSize = (int) Math.ceil(songNames.size() * trainPercent);

        int i = 0;

        testSongs = new ArrayList<>();

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
                testSongs.add(song);

                if (testVal == null) {
                    testVal = song.getValenceScores();
                    testAro = song.getArousalScores();
                }
                else {
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
