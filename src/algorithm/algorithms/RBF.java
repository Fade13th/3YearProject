package algorithm.algorithms;

import algorithm.data.Matrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by Matt on 2016-11-21.
 */
public class RBF {
    public static RealMatrix radialBasisFunctions(RealMatrix Ytr, RealMatrix Yts, RealMatrix expected, int K) {
        int Ntr = Ytr.getRowDimension();
        int Nts = Yts.getRowDimension();

        double[] rowA = Ytr.getRow((int)Math.floor(Math.random()*Ntr));
        double[] rowB = Ytr.getRow((int)Math.floor(Math.random()*Ntr));

        double total = 0.0;

        for (int i = 0; i < rowA.length; i++)
            total += (rowA[i] - rowB[i]) * (rowA[i] - rowB[i]);

        double sigma = Math.sqrt(total);

        RealMatrix C = KMeans.KMeans(Ytr, K, 100);

        RealMatrix A = new Array2DRowRealMatrix(Ntr, K);

        for (int i = 0; i < Ntr; i++) {
            for (int j = 0; j < K; j++) {
                double tot = 0.0;
                for (int k = 0; k < C.getColumnDimension(); k++) {
                    tot += (Ytr.getEntry(i, k) - C.getEntry(j, k)) * (Ytr.getEntry(i, k) - C.getEntry(j, k));
                }

                A.setEntry(i, j, Math.exp(-Math.sqrt(tot)/(sigma*sigma)));
            }
        }

        RealMatrix lambda = Matrix.inverse(A).multiply(expected);

        RealMatrix yh = new Array2DRowRealMatrix(Nts, 1);
        RealMatrix u  = new Array2DRowRealMatrix(K, 1);

        for (int i = 0; i < Nts; i++) {
            for (int j = 0; j < K; j++) {
                double tot = 0.0;
                for (int k = 0; k < Yts.getColumnDimension(); k++) {
                    tot += (Yts.getEntry(i, k) - C.getEntry(j, k)) * (Yts.getEntry(i, k) - C.getEntry(j, k));
                }
                u.setEntry(j, 0, Math.exp(-Math.sqrt(tot)));
            }
            yh.setEntry(i, 0, lambda.transpose().multiply(u).getEntry(0,0));
        }

        return yh;
    }
}
