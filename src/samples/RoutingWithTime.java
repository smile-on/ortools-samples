package samples;

import com.google.ortools.constraintsolver.NodeEvaluator2;

/**
 * Multi-vehicle Routing Model with time-window constraints on service.
 * Google VRP library defines time dimension as the cumulative variable for each vehicle's route.
 * Here we define time as the total time between locations,
 * which is the service time at the starting location plus the travel time to the next location.
 * @see https://developers.google.com/optimization/routing/tsp/vehicle_routing_time_windows
 */
public class RoutingWithTime {

    static RoutingProblem create(int[][] costMatrix, long[] vehicleCaps, long[] shipmentVolume) {
        RoutingProblem routing = RoutingBasic.create(costMatrix, vehicleCaps, shipmentVolume);

        //todo add time dimension via cumulative var
        /*
        model.cumulVar(model.end(vehicle), "time").setMax(vehicleEndTime.get(vehicle));
        for (int order = 0; order < numberOfOrders; ++order) {
          model.cumulVar(order, "time").setRange(
              orderTimeWindows.get(order).first,
              orderTimeWindows.get(order).second);
          int[] orders = {order};

         routing.addDimension(total_time_callback,  // total time function callback
                         horizon, // An upper bound for the accumulated time over each vehicle's route.
                         horizon,
                         fix_start_cumul_to_zero,
                         "Time")
        }
         */
        return routing;
    }


}
