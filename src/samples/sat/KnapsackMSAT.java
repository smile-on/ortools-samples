package sat;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

import static com.google.ortools.sat.CpSolverStatus.FEASIBLE;
import static com.google.ortools.sat.CpSolverStatus.OPTIMAL;

/**
 * Scale single Knapsack problem into multi bins Knapsack problem by cloning number of items available for expected bin count.
 * Trivial formulation.
 */
public class KnapsackMSAT {

    int numBins = 16; // scale to M bins
    final int weightMin = 16000, weightMax = 22000;
    final int volumeMin = 1156, volumeMax = 1600;

    // data
    int numItems;
    int[] values;
    int[] weights;
    int[] volumes;


    void scaleData() {
        int oneBinNumItems = KnapsackSAT.numItems;
        numItems = oneBinNumItems * numBins;
        values = new int[numItems];
        weights = new int[numItems];
        volumes = new int[numItems];

        for (int b = 0; b < numBins; b++) {
            for (int i = 0; i < oneBinNumItems; i++) {
                int gi = b * oneBinNumItems + i;
                values[gi] = KnapsackSAT.values[i];
                weights[gi] = KnapsackSAT.weights[i];
                volumes[gi] = KnapsackSAT.volumes[i];
            }
        }
    }


    CpModel model; // in CP-SAT format
    double timeoutSeconds = 10; // cp is faster to first solution but MIP got to better quality faster for now.

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
        IntVar[] binValue = new IntVar[numBins];
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


    void solve() {
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timeoutSeconds);
        CpSolverStatus status = solver.solve(model);

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


    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        KnapsackMSAT problem = new KnapsackMSAT();
        problem.scaleData();
        problem.formulate();
        problem.solve();
    }

}
