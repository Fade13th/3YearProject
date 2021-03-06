package backEnd.algorithms;

import backEnd.data.Matrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by Matt on 2016-11-21.
 */
public class RBF {
    int K;
    int Ntr;

    RealMatrix Ytr;
    RealMatrix C;
    double sigma;

    public RealMatrix radialBasisFunctions(RealMatrix ytr, RealMatrix expected, int k) {
        Ntr = ytr.getRowDimension();
        K = k;
        Ytr = ytr;

        RealMatrix rowA = Ytr.getRowMatrix((int)Math.floor(Math.random()*Ntr));
        RealMatrix rowB = Ytr.getRowMatrix((int)Math.floor(Math.random()*Ntr));

        sigma = Matrix.twoNorm(rowA.subtract(rowB));

        C = KMeans.KMeans(Ytr, K, 100);

        RealMatrix A = new Array2DRowRealMatrix(Ntr, K);

        for (int i = 0; i < Ntr; i++) {
            for (int j = 0; j < K; j++) {
                RealMatrix ytri = Ytr.getRowMatrix(i);
                RealMatrix cj = C.getRowMatrix(j);

                A.setEntry(i, j, Math.exp(-Matrix.twoNorm(ytri.subtract(cj))/(sigma*sigma)));
            }
        }

        RealMatrix lambda = Matrix.inverse(A).multiply(expected);

        return lambda;
    }

    public RealMatrix predict(RealMatrix Yts, RealMatrix lambda) {
        int Nts = Yts.getRowDimension();

        RealMatrix yh = new Array2DRowRealMatrix(Nts, 1);
        RealMatrix u  = new Array2DRowRealMatrix(K, 1);

        for (int i = 0; i < Nts; i++) {
            for (int j = 0; j < K; j++) {
                RealMatrix ytsi = Yts.getRowMatrix(i);
                RealMatrix cj = C.getRowMatrix(j);

                u.setEntry(j, 0, Math.exp(-Matrix.twoNorm(ytsi.subtract(cj))/(sigma*sigma)));
            }
            yh.setEntry(i, 0, lambda.transpose().multiply(u).getEntry(0,0));
        }

        return yh;
    }
}
