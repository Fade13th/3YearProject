package backEnd.algorithms;

import libsvm.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by Matt on 2017-01-09.
 */
public class SVM {
    private static svm_parameter param;
    private static svm_problem prob;

    private static RealMatrix inTr;
    private static RealMatrix outTr;

    private static int max_index = 0;

    private static void setupParam() {
        param = new svm_parameter();
        param.svm_type = svm_parameter.NU_SVR;
        param.C = 1;
        param.nu = 0.5;
        param.kernel_type = svm_parameter.LINEAR;
        param.gamma = 0;
        param.cache_size = 100;
        param.eps = 1e-3;
        param.shrinking = 1;
        param.probability = 0;

        System.out.println("SVM param setup complete");
    }

    private static void setupProb() {
        prob = new svm_problem();
        prob.l = inTr.getRowDimension();

        prob.x = new svm_node[prob.l][];
        for (int i = 0; i < prob.l; i++) {
            double[] tmp = inTr.getRow(i);
            svm_node[] nodes = new svm_node[tmp.length + 1];

            int j;
            for (j = 0; j < tmp.length; j++) {
                svm_node n = new svm_node();
                n.index = j;
                n.value = tmp[j];
                nodes[j] = n;
            }
            svm_node n = new svm_node();
            n.index = -1;
            n.value = 0;
            nodes[j] = n;

            if (j > max_index)
                max_index = j;

            prob.x[i] = nodes;
        }

        prob.y = outTr.getColumn(0);

        System.out.println("SVM problem setup complete");
    }

    private static void setup() {
        setupParam();
        setupProb();

        if(param.gamma == 0 && max_index > 0)
            param.gamma = 1.0/max_index;

        System.out.println("SVM setup complete");
    }

    public static svm_model selfOptimizingLinearLibSVM(RealMatrix Ytr, RealMatrix expected) {
        inTr = Ytr;
        outTr = expected;
        setup();

        svm.svm_check_parameter(prob, param);


        System.out.println("SVM training begin...");
        svm_model model = svm.svm_train(prob, param);
        System.out.println("SVM training complete");

        return model;
    }

    public static RealMatrix testModel(RealMatrix testSet, svm_model model) {
        double[] predictions = new double[testSet.getRowDimension()];

        for (int i = 0; i < testSet.getRowDimension(); i++) {
            double[] row = testSet.getRow(i);
            svm_node[] nodes = new svm_node[row.length + 1];

            int j;
            for (j = 0; j < row.length; j++) {
                svm_node n = new svm_node();
                n.index = j;
                n.value = row[j];
                nodes[j] = n;
            }
            svm_node n = new svm_node();
            n.index = -1;
            n.value = 0;
            nodes[j] = n;

            System.out.println("SVM prediction begin...");
            predictions[i] = svm.svm_predict(model, nodes);
            System.out.println("SVM prediction complete");
        }

        return new Array2DRowRealMatrix(predictions);
    }
}
