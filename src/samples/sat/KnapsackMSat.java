package sat;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.SatParameters;

import static com.google.ortools.sat.CpSolverStatus.FEASIBLE;
import static com.google.ortools.sat.CpSolverStatus.OPTIMAL;

/**
 * Scale single Knapsack problem into multi bins Knapsack problem by cloning number of items available for expected bin count.
 */
public abstract class KnapsackMSat {

    public KnapsackMSat(int numBins, double timeoutSeconds) {
        this.numBins = numBins;
        this.timeoutSeconds = timeoutSeconds;
    }

    int numBins = 1; // scale to M bins
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
    double timeoutSeconds = 1; // cp is faster to first solution but MIP got to better quality faster for now.

    // formulate must create SAT model
    abstract void formulate();


    void solve() {
        CpSolver solver = new CpSolver();
        SatParameters.Builder parameters = solver.getParameters();
        parameters.setMaxTimeInSeconds(timeoutSeconds);
        parameters.setLogSearchProgress(true); // Whether the solver should log the search progress to stderr.

        CpSolverStatus status = solver.solve(model);

        // show solution
        boolean hasSolution = (status == OPTIMAL || status == FEASIBLE);
        if (hasSolution) {
            if (status != OPTIMAL)
                System.err.println("solution is not an optimal.");

            int oneBinOptimalVal = KnapsackSAT.optimalVal;
            System.out.printf("Expected obj>=%d\n", oneBinOptimalVal * numBins);
            System.out.printf("Fact     obj =%d : bins=%d time=%d ", (int) solver.objectiveValue(), numBins, (int) timeoutSeconds);
        } else {
            System.err.println("The problem does not have solution. " + status);
        }
//        System.out.println("\n\nexecution stats \n" + solver.responseStats());
        // cp-sat logging is in stderr output of the protobuf of the model.
        // with setLogSearchProgress(true) The logger will be stderr as we reuse the glog library.
        // This will eventually become release 7.1
        // https://groups.google.com/forum/#!topic/or-tools-discuss/CEdRanlYa0o
    }

}
