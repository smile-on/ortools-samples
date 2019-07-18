package sat;

import com.google.ortools.sat.*;

import static com.google.ortools.sat.CpSolverStatus.FEASIBLE;
import static com.google.ortools.sat.CpSolverStatus.OPTIMAL;

/**
 * Formulation with "Row Generation" like decomposition.
 * The objective is max number of bins above threshold to minimize symmetry in formulation.
 * The business obj of max value is achieved through iterations of tiding up limits on bin.
 * <p>
 * New constraint are added at each iteration base on solving suboptimal problem (BSP).
 */
public class KnapsackMSat3 extends KnapsackMSat {

    public KnapsackMSat3(int numBins, double timeoutSeconds) {
        super(numBins, timeoutSeconds);
    }


    int lbWeight = weightMin;
    int lbVoluem = volumeMin;
    int epsilon = 1; // min delta on load limits

    void formulate() {
        model = new CpModel();
        // Decision Vars
        // x[bin][item] = is the item taken in the bin ?
        IntVar[][] x = new IntVar[numBins][numItems];
        for (int b = 0; b < numBins; b++) {
            for (int i = 0; i < numItems; i++)
                x[b][i] = model.newBoolVar("x_" + b + "_" + i);
        }
        // assisting vars = loaded W ,V
        IntVar[] loadW = new IntVar[numBins];
        IntVar[] loadV = new IntVar[numBins];
        for (int b = 0; b < numBins; b++) {
            // sum_i(x[b][i] * w[i]) = load[b]
            loadW[b] = model.newIntVar(0, weightMax, "loadW_" + b);
            model.addScalProdEqual(x[b], weights, loadW[b]);
            loadV[b] = model.newIntVar(0, volumeMax, "loadV_" + b);
            model.addScalProdEqual(x[b], volumes, loadV[b]);
        }
        // Obj
        // Maximize number of loaded bins
        IntVar[] binGood = new IntVar[numBins];
        for (int b = 0; b < numBins; b++) {
            // binGood[b] => load is in the range
            // not binGood[b] => load is NOT in the range
            binGood[b] = model.newBoolVar("goodBin_" + b);
            // bin weight is within limits
            model.addGreaterOrEqual(loadW[b], lbWeight).onlyEnforceIf(binGood[b]);
            model.addLessOrEqual(loadW[b], weightMax).onlyEnforceIf(binGood[b]);
            model.addLessOrEqual(loadW[b], lbWeight - epsilon).onlyEnforceIf(binGood[b].not());
            model.addGreaterOrEqual(loadW[b], weightMax + epsilon).onlyEnforceIf(binGood[b].not());
            // bin volume is within limits
            model.addGreaterOrEqual(loadV[b], lbVoluem).onlyEnforceIf(binGood[b]);
            model.addLessOrEqual(loadV[b], volumeMax).onlyEnforceIf(binGood[b]);
            model.addLessOrEqual(loadV[b], lbVoluem - epsilon).onlyEnforceIf(binGood[b].not());
            model.addGreaterOrEqual(loadV[b], volumeMax + epsilon).onlyEnforceIf(binGood[b].not());
        }
        model.maximizeSum(binGood);
        //s.t.
        // an item may go in one bin only at most
        for (int i = 0; i < numItems; i++) {
            IntVar[] chosenBin = new IntVar[numBins];
            for (int b = 0; b < numBins; b++) {
                chosenBin[b] = x[b][i];
            }
            model.addLinearSum(chosenBin, 0, 1);
        }
//        // break symmetry by eliminating search space on symmetrical solutions
//        // fact - just makes solver slower
//        for (int b = 1; b < numBins; b++) {
//            model.addLessOrEqual(binValue[b], binValue[b - 1]);
//        }
    }

    // push low bound up on all bins
    void addLbConstraints() {

    }

    void solve() {
        CpSolver solver = new CpSolver();
        SatParameters.Builder parameters = solver.getParameters();
        parameters.setMaxTimeInSeconds(timeoutSeconds);

        CpSolverStatus status = solver.solve(model);
        //todo loop iterations

        // show solution
        boolean hasSolution = (status == OPTIMAL || status == FEASIBLE);
        if (hasSolution) {
            if (status != OPTIMAL)
                System.err.println("solution is not an optimal.");

            int oneBinOptimalVal = KnapsackSAT.optimalVal;
            System.out.printf("Expected obj>=%d\n", oneBinOptimalVal * numBins);
            System.out.printf("Fact     obj =%d : bins=%d ", (int) solver.objectiveValue(), numBins);
        } else {
            System.err.println("The problem does not have solution. " + status);
        }
        System.out.println("\n\nexecution stats \n" + solver.responseStats());
    }

}
