//package com.google.ortools.samples;
package samples;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingModel;

import java.util.ArrayList;

public class SimpleRoutingTest {
    //Static Add Library
    static {
        System.loadLibrary("jniortools");
    }

    public static void main(String[] args) throws Exception {
        // matrix over 4 locations, a deport and 3 customers
        int[][] costMatrix = {
                {0,5,3,6},
                {5,0,8,1},
                {3,8,0,4},
                {6,1,4,0}
        };
        SimpleRoutingTest model = new SimpleRoutingTest(costMatrix);
        model.solve();
        model.reportSolution();
    }

    // model's environment
    int locationCount;
    NodeEvaluator2 distancesCallback;
    // solution
    ArrayList<Integer> globalRes;
    long globalResCost;

    public SimpleRoutingTest(int[][] costMatrix) {
        distancesCallback = new NodeDistance(costMatrix);
        locationCount = costMatrix.length;
    }

    //Solve Method
    public void solve() {
        RoutingModel routing = new RoutingModel(locationCount, 1, 0);
//    routing.setFirstSolutionStrategy(RoutingModel.ROUTING_PATH_CHEAPEST_ARC);
        routing.setCost(distancesCallback);

        Assignment solution = routing.solve();
        if (solution != null) {
            globalRes = new ArrayList();
            int route_number = 0;
            for (long node = routing.start(route_number); !routing.isEnd(node); node = solution.value(routing.nextVar(node))) {
                globalRes.add((int) node);
            }
        }
        globalResCost = solution.objectiveValue();
    }

    public void reportSolution() {
        System.out.println(String.format("cost %d solution %s", globalResCost, globalRes));
    }

    //Node Distance Evaluation
    public static class NodeDistance extends NodeEvaluator2 {
        private int[][] costMatrix;

        public NodeDistance(int[][] costMatrix) {
            this.costMatrix = costMatrix;
        }

        @Override
        public long run(int firstIndex, int secondIndex) {
            return costMatrix[firstIndex][secondIndex];
        }
    }

}
