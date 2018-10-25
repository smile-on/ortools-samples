package routing;

import com.google.ortools.constraintsolver.NodeEvaluator2;

/**
 * Multi-vehicle Routing Model adds basic demand_at_node constraint.
 */
public class RoutingBasic extends Routing {
    static final String VOLUME_DIMENSION = "Volume";

    RoutingBasic(int[][] distanceMatrix, long[] vehicleCaps, long[] shipmentVolume) {
        // problem definition holder
        super(distanceMatrix, vehicleCaps.length);
        // each node has demand, vehicle trip should have total demand less than capacity of the vehicle.
        NodeEvaluator2 volumeCallback = new NodeDemand(shipmentVolume);
        boolean fix_start_cumul_to_zero = true;
        addDimensionWithVehicleCapacity(volumeCallback, 0, vehicleCaps, fix_start_cumul_to_zero, VOLUME_DIMENSION);
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
