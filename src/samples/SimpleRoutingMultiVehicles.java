package samples;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingModel;

import java.util.ArrayList;
import java.util.List;

/*
*/
public class SimpleRoutingMultiVehicles {
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
        // 2 vehicles with individual volume capacities
        long[] vehicleCaps = new long[2];
        vehicleCaps[0] = 2;
        vehicleCaps[1] = 3;

        SimpleRoutingMultiVehicles model = new SimpleRoutingMultiVehicles(costMatrix, vehicleCaps);
        model.solve();
        model.reportSolution();
    }

    // model's environment
    int locationCount;
    NodeEvaluator2 distancesCallback;
    int vehicleCount;
    long[] volumeCaps;
    NodeEvaluator2 volumeCallback;
    // solution
    List<List<Integer>> globalRes;
    long globalResCost;

    public SimpleRoutingMultiVehicles(int[][] costMatrix, long[] vehicleCaps) {
        locationCount = costMatrix.length;
        distancesCallback = new NodeDistance(costMatrix);
        vehicleCount = vehicleCaps.length;
        volumeCaps = vehicleCaps;
        volumeCallback = new NodeDemand();
    }

    //Solve Method
    public void solve() {
        RoutingModel routing = new RoutingModel(locationCount, vehicleCount, 0);
        // optimization minimizes total distances traveled by all vehicles.
        routing.setCost(distancesCallback);
        // each node has demand, vehicle trip should have total demand less than capacity of the vehicle.
        long slack_max = 0;
        boolean fix_start_cumul_to_zero = true;
//        routing.addDimension(volumeCallback, 0, volumeCaps[0], fix_start_cumul_to_zero, "volume");  // cost 18 v[0]=2,v[1]=2
        //todo fix EXCEPTION_ACCESS_VIOLATION
        routing.addDimensionWithVehicleCapacity(volumeCallback, 0, volumeCaps, fix_start_cumul_to_zero, "volume"); // cost 13 v[0]=2,v[1]=3

        Assignment solution = routing.solve();
        if (solution == null) { // no solution
            globalResCost = 0;
            globalRes = null;
            return;
        }
        globalResCost = solution.objectiveValue();
        globalRes = new ArrayList();
        for (int routeNumber = 0; routeNumber < vehicleCount; routeNumber++) { // from 0 to vehicles- 1
            List<Integer> route = new ArrayList();
            for (long node = routing.start(routeNumber); !routing.isEnd(node); node = solution.value(routing.nextVar(node))) {
                int locationIndex = routing.IndexToNode(node); // note multi-vehicles report needs to get original node index
                route.add(locationIndex);
            }
            globalRes.add(route);
        }
    }

    public void reportSolution() {
        if (globalRes == null)
            System.out.println(String.format("no solution found"));
        else
            System.out.println(String.format("cost %d solution %s", globalResCost, globalRes));
    }

    // Node Distance Evaluation
    static class NodeDistance extends NodeEvaluator2 {
        private int[][] costMatrix;

        public NodeDistance(int[][] costMatrix) {
            this.costMatrix = costMatrix;
        }

        @Override
        public long run(int firstIndex, int secondIndex) {
            return costMatrix[firstIndex][secondIndex];
        }
    }

    // Node Demand Evaluation
    static class NodeDemand extends NodeEvaluator2 {
        @Override
        public long run(int firstIndex, int secondIndex) {
            if (secondIndex == 0)
                return 0; // deport has no demand
            else
                return 1; // any client's location has demand of 1 unit
        }
    }
}
