package algorithm.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

/**
 * Created by Matt on 2016-11-21.
 */
public class KMeans {
    public static RealMatrix KMeans(RealMatrix Y, int K, int iterations) {
        double[][] means = new double[K][Y.getColumnDimension()];
        ArrayList<double[]>[] clusters = new ArrayList[K];

        for (int k = 0; k < means.length; k++) {
            means[k] = Y.getRow(k);
            clusters[k] = new ArrayList<>();
        }

        for (int i = 0; i < iterations; i++) {
            //Add points to the cluster mean they're nearest to
            for (int j = 0; j < Y.getRowDimension(); j++) {
                double[] point = Y.getRow(j);
                try {
                    int minimumPos = minDistance(point, means);
                    clusters[minimumPos].add(point);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Find the new means for the next iteration
            for (int l = 0; l < clusters.length; l++) {
                ArrayList<double[]> cluster = clusters[l];
                int N = cluster.size();

                double[] totaled = new double[cluster.get(0).length];

                for (int m = 0; m < N; m++) {
                    double[] point = cluster.get(m);
                    for (int n = 0; n < point.length; n++) {
                        totaled[n] += point[n];
                    }
                }

                //Update the new means
                for (int n = 0; n < totaled.length; n++) {
                    means[l][n] = totaled[n]/N;
                }
            }

            //Clear out clusters for new iteration
            for (int k = 0; k < means.length; k++) {
                clusters[k] = new ArrayList<>();
            }
        }

        return new Array2DRowRealMatrix(means);
    }

    private static int minDistance(double[] point, double[][] means) throws Exception {
        int minPos = 0;
        double minVal = euclideanDistance(point, means[0]);

        for (int i = 1; i < means.length; i++) {
            double min = euclideanDistance(point, means[i]);
            if (min < minVal) {
                minVal = min;
                minPos = i;
            }
        }

        return minPos;
    }

    private static double euclideanDistance(double[] a, double[] b) throws Exception {
        double total = 0.0;

        if (a.length != b.length) throw new Exception("Dimensionality of points does not match");

        for (int i = 0; i < a.length; i++)
            total += (a[i] - b[i]) * (a[i] - b[i]);

        return Math.sqrt(total);
    }
}
