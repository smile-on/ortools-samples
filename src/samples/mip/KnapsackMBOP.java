package mip;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import static com.google.ortools.linearsolver.MPSolver.OptimizationProblemType.BOP_INTEGER_PROGRAMMING;
import static com.google.ortools.linearsolver.MPSolver.ResultStatus.FEASIBLE;
import static com.google.ortools.linearsolver.MPSolver.ResultStatus.OPTIMAL;

/**
 * Multiply Knapsacks problem solution using the OR-TOOLS BOP-SAT solver.
 */
public class KnapsackMBOP {

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        int bins = 16;
        int itemsTotal = OneBin.items * bins;

        MPSolver solver = new MPSolver("demo", BOP_INTEGER_PROGRAMMING);
        // Decision Vars [bin][item]
        MPVariable[][] x = new MPVariable[bins][];
        for (int b = 0; b < bins; b++)
            x[b] = solver.makeBoolVarArray(itemsTotal);
        // Obj
        MPObjective objective = solver.objective();
        objective.setMaximization();
        for (int b = 0; b < bins; b++)
            for (int i = 0; i < OneBin.items; i++)
                for (int j = 0; j < bins; j++) {
                    int n = OneBin.items * j + i; // n's item in total list
                    objective.setCoefficient(x[b][n], OneBin.value[i]);
                }
        //s.t.
        // a bin weight limits
        for (int b = 0; b < bins; b++) {
            MPConstraint cWeight = solver.makeConstraint(16000, 22000);
            for (int j = 0; j < bins; j++)
                for (int i = 0; i < OneBin.items; i++) {
                    int n = OneBin.items * j + i;
                    cWeight.setCoefficient(x[b][n], OneBin.weight[i]);
                }
        }
        // a bin volume limits
        for (int b = 0; b < bins; b++) {
            MPConstraint cVolume = solver.makeConstraint(1156, 1600);
            for (int j = 0; j < bins; j++)
                for (int i = 0; i < OneBin.items; i++) {
                    int n = OneBin.items * j + i;
                    cVolume.setCoefficient(x[b][n], OneBin.volume[i]);
                }
        }
        // an item may go in one bin only
        for (int j = 0; j < bins; j++)
            for (int i = 0; i < OneBin.items; i++) {
                int n = OneBin.items * j + i;
                MPConstraint cOneBin = solver.makeConstraint(0, 1);
                for (int b = 0; b < bins; b++) {
                    cOneBin.setCoefficient(x[b][n], 1);
                }
            }
        // compromised optimality to increase solvable limit from 14 to 16 bins
        // pre seed each bin with top valuable item
        int[] topBestValuePosition = findTopValuables(bins);
        for (int b = 0; b < bins; b++) {
            // fix one item per bin
            int pos = topBestValuePosition[b];
            MPConstraint preSeed = solver.makeConstraint(1, 1);
            preSeed.setCoefficient(x[b][pos], 1);
        }

        //solve
        long ms = 10 * 1000;
        solver.setTimeLimit(ms);
        MPSolver.ResultStatus status = solver.solve();

        // show solution
        if (status.equals(OPTIMAL) || status.equals(FEASIBLE)) {
            System.err.printf("The problem status %s \n", status);
            System.out.printf("Expected obj>=%d\n", OneBin.optimalVal * bins);
            System.out.printf("Fact     obj =%d : bins=%d ", (int) objective.value(), bins);
            System.out.println("\n[\n");
            for (int b = 0; b < bins; b++) {
                for (int i = 0; i < itemsTotal; i++) {
                    System.out.printf("%d ", (int) x[b][i].solutionValue());
                }
                System.out.println();
            }
            System.out.println("\n]");
        } else {
            System.err.printf("The problem does not have solution. %s \n", status);
        }
    }


    // returns positions of n top valuables in the mix
    private static int[] findTopValuables(int n) {
        int[] topBestValuePosition = new int[n];
        // NB ideally you need to sort all items by value density; this is shortcut as we got artificial example.
        int posBestValueInSample = 8;
        for (int i = 0; i < n; i++) {
            topBestValuePosition[i] = i * OneBin.items + posBestValueInSample;
        }
        return topBestValuePosition;
    }

    // data
    static class OneBin {
        static int items = 10;
        static int[] value = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
        static int[] weight = {1008, 2087, 5522, 5250, 5720, 4998, 275, 3145, 12580, 382};
        static int[] volume = {281, 307, 206, 111, 275, 79, 23, 65, 261, 40};
        static int optimalVal = 21777;
    }
}
