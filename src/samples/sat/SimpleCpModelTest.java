package sat;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

import static com.google.ortools.sat.CpSolverStatus.FEASIBLE;
import static com.google.ortools.sat.CpSolverStatus.OPTIMAL;

/**
 * Example of using Constraint Programming (CP) solver for
 * simple optimization: Maximize 2x + 2y + 3z subject to the linear constraints.
 * <p>
 * Finding solution using the OR-TOOLS CP-SAT solver.
 * <p/>
 * example at https://developers.google.com/optimization/cp/cp_solver
 * has been modified to match original MIP example
 */
public class SimpleCpModelTest {

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        CpModel model = new CpModel();
        // Decision Vars
        // x, y, z	≥	0
        // x, y, z integers
        long upperBound = 10; // on int values in the problem
        IntVar x = model.newIntVar(0, upperBound, "x");
        IntVar y = model.newIntVar(0, upperBound, "y");
        IntVar z = model.newIntVar(0, upperBound, "z");
        IntVar[] intVars = {x, y, z};
        // Maximize 2x + 2y + 3z
        model.maximizeScalProd(intVars, new int[]{2, 2, 3});
        // subject to the linear constraints
        // c1: 2x + 7 y + 3z	≤	50
        model.addScalProd(intVars, new int[]{2, 7, 3}, 0, 50);
        // c2: 3x - 5y + 7z	≤	45
        model.addScalProd(intVars, new int[]{3, -5, 7}, 0, 45);
        // c3: 5x + 2y - 6z	≤	37
        model.addScalProd(intVars, new int[]{5, 2, -6}, 0, 37);

        // Creates the solver and solve.
        CpSolver solver = new CpSolver();
        // solver.getParameters()
        CpSolverStatus status = solver.solve(model);
        // display solution
        boolean hasSolution = (status == OPTIMAL || status == FEASIBLE);
        if (hasSolution) {
            if (status != OPTIMAL)
                System.err.println("solution is not an optimal.");

            System.out.println("Expected obj=35 : x=7 y=3 z=5");
            System.out.printf("  obj=%f : x=%d y=%d z=%d \n",
                    solver.objectiveValue(),
                    solver.value(x), solver.value(y), solver.value(z));
        } else {
            System.err.println("The problem does not have solution. " + status);
        }
        System.out.println("\n\nexecution stats \n" + solver.responseStats());
    }

    /*
https://github.com/google/or-tools/blob/master/ortools/sat/samples/BinPackingProblemSat.java

    IntVar boolvar = model.NewBoolVar("boolvar");
    IntVar x = model.NewIntVar(0,10, "x");
    IntVar delta = model.NewIntVar(-5, 5,"delta");
    IntVar squaredDelta = model.NewIntVar(0, 25,"squaredDelta");

    model.Add(x == 4).OnlyEnforceIf(boolvar);
    model.Add(x == 0).OnlyEnforceIf(boolvar.Not());
    model.Add(delta == x - 5 );

    long[,] tuples = { {-5, 25}, {-4, 16}, {-3, 9}, {-2, 4}, {-1, 1}, {0, 0},
                       {1, 1}, {2, 4}, {3, 9}, {4, 16}, {5, 25} };
    model.AddAllowedAssignments(new IntVar[] {delta, squaredDelta}, tuples);

    model.Minimize(squaredDelta);

     */
}
