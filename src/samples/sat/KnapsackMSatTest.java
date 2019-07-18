package sat;

public class KnapsackMSatTest {

    /* Output:

    // No break symmetry constrains.
Expected obj>=195993
Fact     obj =190112 : bins=9 time=3 solution is not an optimal.
Fact     obj =191103 : bins=9 time=10 solution is not an optimal.
Expected obj>=217770
Fact     obj =212912 : bins=10 time=3 solution is not an optimal.
Fact     obj =213016 : bins=10 time=10 solution is not an optimal.
Expected obj>=348432
Fact     obj =324540 : bins=16 time=3 solution is not an optimal.

    // With break symmetry constrains.
Expected obj>=195993
Fact     obj =191673 : bins=9 time=3 solution is not an optimal.
Fact     obj =191673 : bins=9 time=10 solution is not an optimal.
Expected obj>=217770
bins=10 The problem does not have solution. UNKNOWN

     */
    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");

        // Trivial formulation - binary decision variables for assignments.
        // No break symmetry constrains.
        test1(9, 3);
        test1(9, 10);
        test1(10, 3);
        test1(10, 10);
        test1(16, 3);
        // Trivial formulation - binary decision variables for assignments.
        // With break symmetry constrains.
        test2(9, 3);
        test2(9, 10);
        test2(10, 10);
    }

    static void test1(int numBins, double timeoutSeconds) {
        KnapsackMSat problem = new KnapsackMSat1(numBins, timeoutSeconds);
        problem.scaleData();
        problem.formulate();
        problem.solve();
    }

    static void test2(int numBins, double timeoutSeconds) {
        KnapsackMSat problem = new KnapsackMSat2(numBins, timeoutSeconds);
        problem.scaleData();
        problem.formulate();
        problem.solve();
    }

}
