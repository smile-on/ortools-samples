package samples;

import com.google.ortools.constraintsolver.NodeEvaluator2;

/**
 * Multi-vehicle Routing Model with basic constraints.
 */
public class RoutingBasic {

    static RoutingProblem create(int[][] costMatrix, long[] vehicleCaps, long[] shipmentVolume) {
        // load OR library at run-time.
        System.loadLibrary("jniortools");
        // problem definition holder
        int locationCount = costMatrix.length;
        int vehicleCount = vehicleCaps.length;
        RoutingProblem routing = new RoutingProblem(locationCount, vehicleCount);

        // optimization minimizes total distances traveled by all vehicles.
        NodeEvaluator2 distancesCallback = new NodeDistance(costMatrix);
        routing.setCost(distancesCallback);
        // each node has demand, vehicle trip should have total demand less than capacity of the vehicle.
        NodeEvaluator2 volumeCallback = new NodeDemand(shipmentVolume);
        boolean fix_start_cumul_to_zero = true;
        routing.addDimensionWithVehicleCapacity(volumeCallback, 0, vehicleCaps, fix_start_cumul_to_zero, "volume");
        return routing;
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
