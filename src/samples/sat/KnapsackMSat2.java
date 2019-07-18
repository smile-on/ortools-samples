package sat;

/**
 * Trivial formulation - binary decision variables for assignments.
 * Adds break symmetry constrains.
 */
public class KnapsackMSat2 extends KnapsackMSat1 {

    public KnapsackMSat2(int numBins, double timeoutSeconds) {
        super(numBins, timeoutSeconds);
    }

    void formulate() {
        super.formulate();
        // break symmetry by eliminating search space on symmetrical solutions
        // fact - just makes solver slower
        for (int b = 1; b < numBins; b++) {
            model.addLessOrEqual(binValue[b], binValue[b - 1]);
        }
    }

}
