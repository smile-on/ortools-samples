package sat;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;

/**
 * Trivial formulation - binary decision variables for assignments.
 * No break symmetry constrains.
 */
public class KnapsackMSat1 extends KnapsackMSat {

    public KnapsackMSat1(int numBins, double timeoutSeconds) {
        super(numBins, timeoutSeconds);
    }

    IntVar[] binValue = new IntVar[numBins];

    void formulate() {
        model = new CpModel();
        // Decision Vars
        // x[bin][item] = is the item taken in the bin ?
        IntVar[][] x = new IntVar[numBins][numItems];
        for (int b = 0; b < numBins; b++) {
            for (int i = 0; i < numItems; i++)
                x[b][i] = model.newBoolVar("x_" + b + "_" + i);
        }
        // Obj
        // Maximize total value
        int valueMax = weightMax; // estimate upper limit
        for (int b = 0; b < numBins; b++) {
            binValue[b] = model.newIntVar(0, valueMax, "valueBin_" + b);
            model.addScalProdEqual(x[b], values, binValue[b]);
        }
        model.maximizeSum(binValue);
        //s.t.
        for (int b = 0; b < numBins; b++) {
            model.addScalProd(x[b], weights, weightMin, weightMax); // bin weight is within limits
            model.addScalProd(x[b], volumes, volumeMin, volumeMax); // bin volume is within limits
        }
        // an item may go in one bin only at most
        for (int i = 0; i < numItems; i++) {
            IntVar[] chosenBin = new IntVar[numBins];
            for (int b = 0; b < numBins; b++) {
                chosenBin[b] = x[b][i];
            }
            model.addLinearSum(chosenBin, 0, 1);
        }
    }

}
