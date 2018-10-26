package routing;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * API to solve Routing problem (VRP). Any specific RoutingModel extends it.
 */
public class Routing extends RoutingModel {

    int locationCount, vehicleCount;

    Routing(int[][] distanceMatrix, int vehicleCount) {
        super(distanceMatrix.length, vehicleCount, 0); // locationCount, vehicleCount
        this.locationCount = distanceMatrix.length;
        this.vehicleCount = vehicleCount;
        // optimization minimizes total distances traveled by all vehicles.
        NodeEvaluator2 distancesCallback = new NodeDistance(distanceMatrix);
        setCost(distancesCallback);
    }

    // Google VRP format
    Assignment solution;
    // solution in business terms
    long totalCost;
    List<List<Integer>> routes;

    // Solve Method
    @Override
    public Assignment solve() {
        solution = super.solve();
        if (solution == null) {
            // no solution
            totalCost = 0;
            routes = null;
        } else {
            // fetch information about solution found
            totalCost = solution.objectiveValue();
            routes = new ArrayList();
            for (int routeNumber = 0; routeNumber < vehicleCount; routeNumber++) { // from 0 to vehicles- 1
                List<Integer> route = new ArrayList();
                for (long node = start(routeNumber); !isEnd(node); node = solution.value(nextVar(node))) {
                    int locationIndex = indexToNode(node); // note multi-vehicles report needs to get original node index
                    route.add(locationIndex);
                }
                routes.add(route);
            }
        }
        return solution;
    }

    public void printSolution() {
        if (routes == null)
            System.out.println(String.format("no solution found"));
        else
            System.out.println(String.format("cost %d solution %s", totalCost, routes));
    }

    //  Distance Evaluation between from and to nodes.
    static class NodeDistance extends NodeEvaluator2 {
        private int[][] costMatrix;

        public NodeDistance(int[][] costMatrix) {
            this.costMatrix = costMatrix;
        }

        @Override
        public long run(int fromNode, int toNode) {
            return costMatrix[fromNode][toNode];
        }
    }
}
