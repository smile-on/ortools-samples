package sat;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

import static com.google.ortools.sat.CpSolverStatus.FEASIBLE;
import static com.google.ortools.sat.CpSolverStatus.OPTIMAL;

/**
 * Trivial Knapsack problem solution using the OR-TOOLS CP-SAT solver.
 */
public class KnapsackSAT {

    // data for a single bin problem
    static int[] values = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
    static int[] weights = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
    static int[] volumes = {281, 307, 206, 111, 275, 79, 23, 65, 261, 40};
    static int numItems = values.length;

    static final int weightMin = 16000, weightMax = 22000;
    static final int volumeMin = 1156, volumeMax = 1600;
    static final int optimalVal = 21777;

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        CpModel model = new CpModel();
        // Decision Vars
        // x[item] = is the item taken ?
        IntVar[] x = new IntVar[numItems];
        for (int i = 0; i < numItems; i++)
            x[i] = model.newBoolVar("x");
        // Obj
        // Maximize total value
        model.maximizeScalProd(x, values);
        //s.t.
        model.addScalProd(x, weights, weightMin, weightMax);
        model.addScalProd(x, volumes, volumeMin, volumeMax);

        //solve
        CpSolver solver = new CpSolver();
        double ms = 10 / 1000.0;
        solver.getParameters().setMaxTimeInSeconds(ms);
        CpSolverStatus status = solver.solve(model);

        // show solution
        boolean hasSolution = (status == OPTIMAL || status == FEASIBLE);
        if (hasSolution) {
            if (status != OPTIMAL)
                System.err.println("solution is not an optimal.");

            System.out.printf("Expected obj=%d : x[0]=1 x[1]=1 x[2]=0 x[3]=0 x[4]=1 x[5]=0 x[6]=0 x[7]=0 x[8]=1 x[9]=1 .\n", optimalVal);
            System.out.printf("Fact     obj=%d : ", (int) solver.objectiveValue());
            for (int i = 0; i < numItems; i++)
                System.out.printf("x[%d]=%d ", i, (int) solver.value(x[i]));
            System.out.println(".");
        } else {
            System.err.println("The problem does not have solution. " + status);
        }
        System.out.println("\n\nexecution stats \n" + solver.responseStats());
    }
}
