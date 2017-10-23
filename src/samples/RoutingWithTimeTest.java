package samples;

/**
 * Split work between two vehicles in optimal way, shortest sum paths, respecting vehicle's capacity limit.
 * Each client has time window when service allowed.
 */
public class RoutingWithTimeTest {

    public static void main(String[] args) throws Exception {
        // load OR library at run-time.
        System.loadLibrary("jniortools");
        // ** optimization objective is shortest sum of paths.
        // matrix over 4 locations, a depot and 3 customers
        int[][] distanceMatrix = {
                {0, 5, 3, 6},
                {5, 0, 5, 1},
                {3, 5, 0, 4},
                {6, 1, 4, 0}
        };
        // each location has servicing time to unload the order
        int[] servicingTime = {0, 1, 1, 1};
        // ** add constraint on volume.
        // 2 vehicles with individual volume capacities.
        long[] vehicleCaps = {2, 2};
        // 3 clients have same shipment volume (demand) of 1 unit and depot with no demand.
        long[] shipmentVolume = {0, 1, 1, 1};
        // 3 clients have time windows when service is accepted. Time is in logical units (your's to scale).
        long[][] serviceTimeWindows = {{0, 10}, {3, 7}, {13, 18}, {3, 7}}; // [client] [start, end]

        Routing model = new RoutingWithTime(distanceMatrix, vehicleCaps, shipmentVolume, servicingTime, serviceTimeWindows);
        model.solve();
        model.printSolution();
        // Note a model uses two time dimensions to account for arrival and departure time has hard time to account for
        // wait time represented as slack variable in one or both dimensions.
        // route#0 @0[0-0]{0-0} @3[6-7]{3-7} @2[13-13]{13-18}
    }


}
