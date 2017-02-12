package backEnd.algorithms;

import backEnd.data.Matrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by Matt on 2016-11-14.
 */
public class LeastSquaresRegression {
    public static RealMatrix leastSquaresRegression(RealMatrix Y, RealMatrix expected) {
        RealMatrix a = Matrix.inverse(Y).multiply(expected);
        RealMatrix fh = Y.multiply(a);

        return fh;
    }

    public static RealMatrix leastSquaresRegression(RealMatrix Ytr, RealMatrix Yts, RealMatrix expected) {
        RealMatrix a = Matrix.inverse(Ytr).multiply(expected);
        RealMatrix fh = Yts.multiply(a);

        return fh;
    }

}
