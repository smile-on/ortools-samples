package sat;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

import static com.google.ortools.sat.CpSolverStatus.FEASIBLE;
import static com.google.ortools.sat.CpSolverStatus.OPTIMAL;

/**
 * Trivial Knapsak problem solution using the OR-TOOLS CP-SAT solver.
 */
public class KnapsackSAT {

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        // data
        int items = 10;
        int[] value = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
        int[] weight = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
        int[] volume = {281, 307, 206, 111, 275, 79, 23, 65, 261, 40};
        final int weightMin = 16000, weightMax = 22000;
        final int volumeMin = 1156, volumeMax = 1600;

        CpModel model = new CpModel();
        // Decision Vars
        // x[item] = is the item taken ?
        IntVar[] x = new IntVar[items];
        for (int i = 0; i < items; i++)
            x[i] = model.newBoolVar("x");
        // Obj
        // Maximize total value
        model.maximizeScalProd(x, value);
        //s.t.
        model.addScalProd(x, weight, weightMin, weightMax);
        model.addScalProd(x, volume, volumeMin, volumeMax);

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

            System.out.println("Expected obj=21777 : x[0]=1 x[1]=1 x[2]=0 x[3]=0 x[4]=1 x[5]=0 x[6]=0 x[7]=0 x[8]=1 x[9]=1 .");
            System.out.printf("Fact     obj=%d : ", (int) solver.objectiveValue());
            for (int i = 0; i < items; i++)
                System.out.printf("x[%d]=%d ", i, (int) solver.value(x[i]));
            System.out.println(".");
        } else {
            System.err.println("The problem does not have solution. " + status);
        }
        System.out.println("\n\nexecution stats \n" + solver.responseStats());
    }
}
