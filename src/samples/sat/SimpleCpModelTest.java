package sat;

import com.google.ortools.sat.CpModel;

/**
 * Example of using Constraint Programming (CP) solver for
 * simple optimization: Maximize 2x + 2y + 3z subject to the linear constraints.
 *
 * Finding solution using the OR-TOOLS CP-SAT solver.
 * <p/>
 * https://developers.google.com/optimization/mip/integer_opt_cp
 */
public class SimpleCpModelTest {

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        CpModel model = new CpModel();
        // Maximize 2x + 2y + 3z
        // subject to the linear constraints
    }

    /*todo upgrade 6.10 release to get fix of missed SAT API
https://github.com/google/or-tools/blob/master/ortools/sat/samples/BinPackingProblem.java

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

    // Creates the solver and solve.
    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.Solve(model);
    Console.WriteLine(solver.ResponseStats());

     */
}
