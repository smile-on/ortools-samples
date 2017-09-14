package samples;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Split work between two vehicles in optimal way, shortest sum paths, respecting vehicle's capacity limit.
 */
public class SimpleRoutingMultiVehicles {
    // load OR library at run-time.
    static {
        System.loadLibrary("jniortools");
    }

    public static void main(String[] args) throws Exception {
        // ** optimization objective is shortest sum of paths.
        // matrix over 4 locations, a depot and 3 customers
        int[][] costMatrix = {
                {0, 5, 3, 6},
                {5, 0, 8, 1},
                {3, 8, 0, 4},
                {6, 1, 4, 0}
        };
        // ** add constraint on volume.
        // 2 vehicles with individual volume capacities.
        long[] vehicleCaps = {2, 2};
        // 3 clients have same shipment volume (demand) of 1 unit and depot with no demand.
        long[] shipmentVolume = {0, 1, 1, 1};

        SimpleRoutingMultiVehicles model = new SimpleRoutingMultiVehicles(costMatrix, vehicleCaps, shipmentVolume);
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

    public SimpleRoutingMultiVehicles(int[][] costMatrix, long[] vehicleCaps, long[] shipmentVolume) {
        locationCount = costMatrix.length;
        distancesCallback = new NodeDistance(costMatrix);
        vehicleCount = vehicleCaps.length;
        volumeCaps = vehicleCaps;
        volumeCallback = new NodeDemand(shipmentVolume);
    }

    //Solve Method
    public void solve() {
        RoutingModel routing = new RoutingModel(locationCount, vehicleCount, 0);
        // optimization minimizes total distances traveled by all vehicles.
        routing.setCost(distancesCallback);
        // each node has demand, vehicle trip should have total demand less than capacity of the vehicle.
        boolean fix_start_cumul_to_zero = true;
        routing.addDimensionWithVehicleCapacity(volumeCallback, 0, volumeCaps, fix_start_cumul_to_zero, "volume"); // test => cost 18 v[0]=2,v[1]=2

        Assignment solution = routing.solve();
        if (solution == null) {
            // no solution
            globalResCost = 0;
            globalRes = null;
        } else {
            // fetch information about solution found
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
        long[] shipmentVolume;

        public NodeDemand(long[] shipmentVolume) {
            this.shipmentVolume = shipmentVolume;
        }

        @Override
        public long run(int firstIndex, int secondIndex) {
            return shipmentVolume[secondIndex];
        }
    }
}
