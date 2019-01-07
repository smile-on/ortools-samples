package sat;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import static com.google.ortools.linearsolver.MPSolver.OptimizationProblemType.BOP_INTEGER_PROGRAMMING;
import static com.google.ortools.linearsolver.MPSolver.ResultStatus.OPTIMAL;

/**
 * Trivial Knapsak problem solution using the OR-TOOLS BOP-SAT solver.
 */
public class KnapsakBOP {

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        // data
        int items = 10;
        int[] value = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
        int[] weight = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
        int[] volume = {281, 307, 206, 111, 275, 79, 23, 65, 261, 40};

        MPSolver solver = new MPSolver("demo", BOP_INTEGER_PROGRAMMING);
        // Decision Vars
        MPVariable[] x = solver.makeBoolVarArray(items);
        // Obj
        MPObjective objective = solver.objective();
        objective.setMaximization();
        for (int i = 0; i < items; i++) {
            objective.setCoefficient(x[i], value[i]);
        }
        //s.t.
        MPConstraint cWeight = solver.makeConstraint(16000, 22000);
        for (int i = 0; i < items; i++) {
            cWeight.setCoefficient(x[i], weight[i]);
        }
        MPConstraint cVolume = solver.makeConstraint(1156, 1600);
        for (int i = 0; i < items; i++) {
            cVolume.setCoefficient(x[i], volume[i]);
        }
        //solve
        long ms = 10;
        solver.setTimeLimit(ms);
        MPSolver.ResultStatus status = solver.solve();

        // show solution
        if (status != OPTIMAL) {
            System.err.println("The problem does not have an optimal solution.");
        } else {
            System.out.println("Expected obj=21777 : x[0]=1 x[1]=1 x[2]=0 x[3]=0 x[4]=1 x[5]=0 x[6]=0 x[7]=0 x[8]=1 x[9]=1");
            System.out.printf("         obj=%d : ", (int) objective.value());
            for (int i = 0; i < items; i++)
                System.out.printf("x[%d]=%d ", i, (int) x[i].solutionValue());
            System.out.println(".");
        }
    }
}
