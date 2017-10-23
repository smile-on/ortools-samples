package samples;

import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.NodeEvaluator2;
import com.google.ortools.constraintsolver.RoutingDimension;

/**
 * Multi-vehicle Routing Model with time-window constraints on service at each client location.
 * Google VRP library defines time dimension as the cumulative variable for each vehicle's route.
 * Here we define time constraint as the total time to serve the location to be within window.
 * Total time to serve accumulates at each step, the travel time to the location and service time.
 *
 * Uses two time dimensions to account for arrival and departure time.
 * @see <a href="https://developers.google.com/optimization/routing/tsp/vehicle_routing_time_windows">vehicle routing with time windows</a>
 */
public class RoutingWithTime extends RoutingBasic {
    static final String DEPART_TIME_DIMENSION = "DepartureTime";
    static final String ARRIVAL_TIME_DIMENSION = "ArrivalTime";

    RoutingWithTime(int[][] distanceMatrix, long[] vehicleCaps, long[] shipmentVolume,
                    int[] servicingTime, long[][] serviceTimes) {
        super(distanceMatrix, vehicleCaps, shipmentVolume);
        // define time dimension
        long horizonTime = 24; // An upper bound for the accumulated time over each vehicle's route.
        long maxWaiting = 4;   // un upper bound on time waiting for location window to open.
        long speed = 1;        // scale as you want
        ArrivalTimeCallback arrivalTimeCallback = new ArrivalTimeCallback(distanceMatrix, speed, servicingTime);
        addDimension(arrivalTimeCallback, // time function callback
                maxWaiting,           // slack max
                horizonTime, // vehicle capacity
                true,        // fix_start_cumul_to_zero
                ARRIVAL_TIME_DIMENSION);
        DepartureTimeCallback departureTimeCallback = new DepartureTimeCallback(arrivalTimeCallback, servicingTime);
        addDimension(departureTimeCallback,  // time function callback
                maxWaiting,           // slack max
                horizonTime, // vehicle capacity
                true,        // fix_start_cumul_to_zero
                DEPART_TIME_DIMENSION);
        // add constraints in time dimension via cumulative var
        RoutingDimension enterTime = getDimensionOrDie(ARRIVAL_TIME_DIMENSION);
        RoutingDimension exitTime = getDimensionOrDie(DEPART_TIME_DIMENSION);
        int locations = distanceMatrix.length;
        // Note TW constraints for depot(#0) is ignored by google library, use horizonTime instead
        for (int location = 0; location < locations; location++) {
            int start = 0, end = 1;
            enterTime.cumulVar(location).setRange(serviceTimes[location][start], serviceTimes[location][end]);
            exitTime.cumulVar(location).setRange(serviceTimes[location][start], serviceTimes[location][end]);
        }
    }

    // Evaluation of time to reach toNode = travel time
    static class ArrivalTimeCallback extends NodeEvaluator2 {
        long[][] times;

        public ArrivalTimeCallback(int[][] distanceMatrix, long speed, int[] serviceTimes) {
            int locations = distanceMatrix.length;
            times = new long[locations][locations];

            for (int i = 0; i < locations; i++)
                for (int j = 0; j < locations; j++) {
                    int serviceBeforeDeparture = serviceTimes[i];
                    long travelTime = distanceMatrix[i][j] / speed;
                    times[i][j] = serviceBeforeDeparture + travelTime;
                }
        }

        @Override
        public long run(int fromNode, int toNode) {
            return times[fromNode][toNode];
        }
    }

    // Evaluation of time to exit toNode = service + travel time
    static class DepartureTimeCallback extends NodeEvaluator2 {
        long[][] times;

        public DepartureTimeCallback(ArrivalTimeCallback arrivalTimeCallback, int[] serviceTimes) {
            int locations = serviceTimes.length;
            times = new long[locations][locations];

            for (int i = 0; i < locations; i++)
                for (int j = 0; j < locations; j++) {
                    long exitTime = arrivalTimeCallback.run(i, j) + serviceTimes[j];
                    times[i][j] = exitTime;
                }
        }

        @Override
        public long run(int fromNode, int toNode) {
            return times[fromNode][toNode];
        }
    }

    // Demo of using new time dimension in solution reporting
    @Override
    public void printSolution() {
        super.printSolution();
        RoutingDimension enterTime = getDimensionOrDie(ARRIVAL_TIME_DIMENSION);
        RoutingDimension exitTime = getDimensionOrDie(DEPART_TIME_DIMENSION);
        for (int routeNumber = 0; routeNumber < vehicleCount; routeNumber++) { // from 0 to vehicles-1
            StringBuffer itinerary = new StringBuffer();
            for (long node = start(routeNumber); !isEnd(node); node = solution.value(nextVar(node))) {
                int locationIndex = IndexToNode(node); // note multi-vehicles report needs to get original node index
                IntVar enterTimeVar = enterTime.cumulVar(node);
                IntVar exitTimeVar = exitTime.cumulVar(node);
                itinerary.append(String.format("@%d[%d-%d]{%d-%d} ", locationIndex,
                        solution.value(enterTimeVar), solution.value(exitTimeVar),
                        enterTimeVar.min(), exitTimeVar.max()));
            }
            System.out.println(String.format("route#%d %s", routeNumber, itinerary));
        }
    }

}
