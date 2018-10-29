package mip;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import static com.google.ortools.linearsolver.MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING;
import static com.google.ortools.linearsolver.MPSolver.ResultStatus.OPTIMAL;

/**
 * Example of using Mixed Integer Programming (MIP) solver for
 * simple optimization: Maximize 2x + 2y + 3z subject to the linear constraints.
 * <p>
 * Finding solution using the Coin-or branch and cut (CBC) solver.
 * <p/>
 * https://developers.google.com/optimization/mip
 */
public class TrivialMipTest {

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");
        final double infinity = MPSolver.infinity();

        MPSolver solver = new MPSolver("SimpleMip", CBC_MIXED_INTEGER_PROGRAMMING);
        // Decision Vars
        // x, y, z	≥	0
        // x, y, z integers
        MPVariable x = solver.makeIntVar(0, infinity, "x");
        MPVariable y = solver.makeIntVar(0, infinity, "y");
        MPVariable z = solver.makeIntVar(0, infinity, "z");
        // Obj: Maximize 2x + 2y + 3z
        MPObjective objective = solver.objective();
        objective.setMaximization();
        objective.setCoefficient(x, 2);
        objective.setCoefficient(y, 2);
        objective.setCoefficient(z, 3);
        // s.t. : subject to the linear constraints
        // c1: 2x + 7 y + 3z	≤	50
        MPConstraint c1 = solver.makeConstraint();
        c1.setCoefficient(x, 2);
        c1.setCoefficient(y, 7);
        c1.setCoefficient(z, 3);
        c1.setBounds(0, 50);
        // c2: 3x - 5y + 7z	≤	45
        MPConstraint c2 = solver.makeConstraint();
        c2.setBounds(0, 45);
        c2.setCoefficient(x, 3);
        c2.setCoefficient(y, -5);
        c2.setCoefficient(z, 7);
        // c3: 5x + 2y - 6z	≤	37
        MPConstraint c3 = solver.makeConstraint();
        c3.setBounds(0, 37);
        c3.setCoefficient(x, 5);
        c3.setCoefficient(y, 2);
        c3.setCoefficient(z, -6);

        // solve MIP
        long ms = 10;
        solver.setTimeLimit(ms);
        MPSolver.ResultStatus status = solver.solve();

        // display solution
        if (status != OPTIMAL) {
            System.err.println("The problem does not have an optimal solution!");
        } else {
            System.out.println("Expected obj=35 : x=7 y=3 z=5");
            System.out.printf("  obj=%f : x=%d y=%d z=%d \n",
                    solver.objective().value(),
                    (int)x.solutionValue(), (int)y.solutionValue(), (int)z.solutionValue());
        }
    }

}
