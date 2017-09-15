package samples;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.RoutingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * API to solve RoutingProblem. Any specific RoutingModel uses it.
 */
public class RoutingProblem extends RoutingModel {

    int locationCount, vehicleCount;

    RoutingProblem(int locationCount, int vehicleCount) {
        super(locationCount, vehicleCount, 0);
        this.locationCount = locationCount;
        this.vehicleCount = vehicleCount;
    }

    // solution in business terms
    long totalCost;
    List<List<Integer>> routes;

    // Solve Method
    @Override
    public Assignment solve() {
        Assignment solution = super.solve();
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
                    int locationIndex = IndexToNode(node); // note multi-vehicles report needs to get original node index
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
}
