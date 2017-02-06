package algorithm.data;

import org.apache.commons.math3.linear.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Matt on 2016-11-14.
 */
public class Matrix {
    public static RealMatrix inverse(RealMatrix m) {
        RealMatrix mT = m.transpose();
        RealMatrix mTm = mT.multiply(m);

        int i = mTm.getColumnDimension();
        int j = mTm.getRowDimension();

        double[][] iden = new double[j][i];

        for (int ii = 0; ii < i; ii++) {
            for (int jj = 0; jj < j; jj++) {
                if (ii == jj)
                    iden[jj][ii] = 1.0;
                else
                    iden[jj][ii] = 0.0;
            }
        }
        RealMatrix I = new Array2DRowRealMatrix(iden);

        DecompositionSolver solver = new LUDecomposition(mTm).getSolver();
        RealMatrix x = solver.solve(I);

        RealMatrix inv = x.multiply(mT);

        return inv;
    }

    public static String toString(RealMatrix m) {
        String builder = "";

        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                builder += m.getEntry(i, j) + "\t";
            }
            builder += "\n";
        }

        return builder;
    }

    public static RealMatrix addColumn(RealMatrix m, double value) throws Exception {
        int height = m.getRowDimension();

        double[] ones = new double[height];
        Arrays.fill(ones, value);

        return addColumn(m, ones);
    }

    public static RealMatrix addColumn(RealMatrix m, double[] column, boolean padding) throws Exception {
        double[] padded = new double[m.getRowDimension()];

        if (m.getRowDimension() < column.length) throw new Exception("Column to add cannot be longer than the matrix");
        else if (m.getRowDimension() != column.length) {
            int diff = m.getRowDimension() - column.length;

            //If padding set true, add 0 padding to front
            if (padding) {
                for (int i = 0; i < diff; i++)
                    padded[i] = 0;

                for (int i = diff; i < padded.length; i++)
                    padded[i] = column[i - diff];
            }
            //If padding set false, add 0 padding to end
            else {
                for (int i = 0; i < column.length; i++)
                    padded[i] = column[i];

                for (int i = column.length; i < padded.length; i++)
                    padded[i] = 0;
            }
        }
        else padded = column;

        return addColumn(m, padded);
    }

    public static RealMatrix addColumn(RealMatrix m, double[] column) throws Exception {
        if (column.length != m.getRowDimension()) throw new Exception("Column to add must be the same length as the matrix");

        int width = m.getColumnDimension();
        int height = m.getRowDimension();

        RealMatrix out = new Array2DRowRealMatrix(height, width+1);
        out.setSubMatrix(m.getData(), 0, 0);

        out.setColumn(width, column);

        return out;
    }

    public static double[] normalise(double[] data) {
        double[] result = new double[data.length];

        double total = 0.0;
        for (Double d : data) total += d;

        double mean = total/data.length;
        total = 0.0;
        for (double d : data) total += (d - mean)*(d - mean);

        double stdDev = total/data.length;

        for(int i = 0; i < data.length; i++) {
            result[i] = (data[i] - mean)/stdDev;
        }

        return result;
    }

    public static double[][] normalise(double[][] data) {
        double[][]result = data.clone();

        int iMax = data.length;
        int jMax = data[0].length;

        for (int j = 0; j < jMax; j++) {
            double[] column = new double[iMax];
            double total = 0.0;

            for (int i = 0; i < iMax; i++) {
                column[i] = data[i][j];
                total += data[i][j];
            }
            double mean = total/column.length;

            total = 0.0;
            for(Double d : column) {
                total += (d - mean)*(d - mean);
            }
            double stdDev = Math.sqrt(total/column.length);

            for (int i = 0; i < iMax; i++) {
                if (stdDev != 0.0)
                    result[i][j] = (data[i][j] - mean)/stdDev;
                else result[i][j] = mean;
            }
        }

        return result;
    }

    public static RealMatrix normalise(RealMatrix data) {
        return new Array2DRowRealMatrix(normalise(data.getData()));
    }

    public static RealMatrix concat(RealMatrix a, RealMatrix b) throws Exception {
        if (a.getColumnDimension() != b.getColumnDimension())
            throw new Exception("Matrices must have the same number of columns");

        double[][] adata = a.getData();
        double[][] bdata = b.getData();

        RealMatrix c = new Array2DRowRealMatrix(adata.length + bdata.length, adata[0].length);

        c.setSubMatrix(a.getData(), 0, 0);
        c.setSubMatrix(b.getData(), adata.length, 0);

        return c;
    }

    public static double error(RealMatrix expected, RealMatrix received) throws Exception {
        if (expected.getColumnDimension() != received.getColumnDimension()) throw new Exception("Vectors must be the same length");

        double total = 0.0;

        for (int i = 0; i < expected.getRowDimension(); i++) {
            total += Math.abs(expected.getEntry(i, 0) - received.getEntry(i, 0));
        }

        return 100*total/expected.getRowDimension();
    }

    public static RealMatrix randPerm(RealMatrix m) {
        double[][] data = m.getData();
        double[][] randData = new double[data.length][data[0].length];

        ArrayList<double[]> dataArr = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            dataArr.add(data[i]);
        }

        int j = 0;

        while (dataArr.size() > 0) {
            int rand = (int) Math.floor(Math.random() * dataArr.size());
            randData[j] = dataArr.get(rand);
            dataArr.remove(rand);
            j++;
        }

        return new Array2DRowRealMatrix(randData);
    }

    public static RealMatrix fromCsv(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        double[][] data = null;

        try {
            String line = br.readLine().replaceAll("[\\t| ]+", "\t");
            int x = line.replaceAll("[\\t| ]+", "\t").replaceAll("^\t", "").split("\t").length;

            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            String[] lines = sb.toString().split(System.lineSeparator());

            int y = lines.length;

            data = new double[y][x];

            for (int i = 0; i < lines.length; i++) {
                String[] l = lines[i].replaceAll("[\\t| ]+", "\t").replaceAll("^\t", "").split("\t");

                for (int j = 0; j < l.length; j++) {
                    data[i][j] = Double.parseDouble(l[j]);
                }
            }
        }
        finally {
            try {
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Array2DRowRealMatrix(data);
    }

    public static double twoNorm(RealMatrix A) {
        double max = 0.0;

        RealMatrix AtA = A.transpose().multiply(A);

        EigenDecomposition eigenDecomposition = new EigenDecomposition(AtA);
        double[] eigenvalues = eigenDecomposition.getRealEigenvalues();

        for (int i = 0; i < eigenvalues.length; i++) {
            double val = Math.sqrt(eigenvalues[i]);
            if (val > max) max = val;
        }
        return max;
    }
}
