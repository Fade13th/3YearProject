package backEnd.data;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by matt on 12/02/17.
 */
public class TrainingSong extends Song {
    RealMatrix valenceScores;

    public RealMatrix getValenceScores() {
        return valenceScores;
    }

    public void setValenceScores(RealMatrix valenceScores) {
        this.valenceScores = valenceScores.getSubMatrix(0, data.getRowDimension()-1, 0, 0);
    }

    public RealMatrix getArousalScores() {
        return arousalScores;
    }

    public void setArousalScores(RealMatrix arousalScores) {
        this.arousalScores = arousalScores.getSubMatrix(0, data.getRowDimension()-1, 0, 0);
    }

    RealMatrix arousalScores;

    //The V/A data on these songs only exists between 15s and 45s
    public TrainingSong(String name) {
        super(name);
        this.data = this.data.getSubMatrix(30,this.data.getRowDimension()-1,0, this.data.getColumnDimension()-1);
    }


}
