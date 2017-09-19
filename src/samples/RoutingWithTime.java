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
 * @see <a href="https://developers.google.com/optimization/routing/tsp/vehicle_routing_time_windows">vehicle routing with time windows</a>
 */
public class RoutingWithTime extends RoutingBasic {
    static final String TIME_DIMENSION = "Time";

    RoutingWithTime(int[][] distanceMatrix, long[] vehicleCaps, long[] shipmentVolume, long[][] serviceTimes) {
        super(distanceMatrix, vehicleCaps, shipmentVolume);
        // define time dimension
        long horizonTime = 24; // An upper bound for the accumulated time over each vehicle's route.
        long speed = 1;        // scale as you want
        TotalTimeCallback totalTimeCallback = new RoutingWithTime.TotalTimeCallback(distanceMatrix, speed);
        addDimension(totalTimeCallback,  // total time function callback
                horizonTime, // slack max
                horizonTime, // vehicle capacity
                true,        // fix_start_cumul_to_zero
                TIME_DIMENSION);
        // add constraints in time dimension via cumulative var
        RoutingDimension time = getDimensionOrDie(TIME_DIMENSION);
        int locations = distanceMatrix.length;
        for (int location = 1; location < locations; location++) {
            int start = 0, end = 1;
            time.cumulVar(location).setRange(serviceTimes[location][start], serviceTimes[location][end]);
        }
    }

    // Evaluation of time to reach toNode = service + travel time
    static class TotalTimeCallback extends NodeEvaluator2 {
        long[][] travelTimes;
        long serviceTime = 1; // all nodes have 1 unit as service time

        public TotalTimeCallback(int[][] distanceMatrix, long speed) {
            int locations = distanceMatrix.length;
            travelTimes = new long[locations][locations];

            for (int i = 0; i < locations; i++)
                for (int j = 0; j < locations; j++) {
                    travelTimes[i][j] = distanceMatrix[i][j] / speed;
                }
        }

        @Override
        public long run(int fromNode, int toNode) {
            long travelTime = travelTimes[fromNode][toNode];
            return travelTime + serviceTime;
        }
    }

    // Demo of using new time dimension in solution reporting
    @Override
    public void printSolution() {
        super.printSolution();
        RoutingDimension time = getDimensionOrDie(TIME_DIMENSION);
        for (int routeNumber = 0; routeNumber < vehicleCount; routeNumber++) { // from 0 to vehicles- 1
            StringBuffer report = new StringBuffer();
            for (long node = start(routeNumber); !isEnd(node); node = solution.value(nextVar(node))) {
                int locationIndex = IndexToNode(node); // note multi-vehicles report needs to get original node index
                IntVar timeVar = time.cumulVar(node);
                report.append(String.format("@%d time %d{%d-%d} ", locationIndex, solution.value(timeVar),
                        timeVar.min(), timeVar.max()));
            }
            System.out.println(String.format("route %d %s", routeNumber, report));
        }
    }

}
