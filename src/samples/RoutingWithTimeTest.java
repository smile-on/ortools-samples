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
                {5, 0, 8, 1},
                {3, 8, 0, 4},
                {6, 1, 4, 0}
        };
        // ** add constraint on volume.
        // 2 vehicles with individual volume capacities.
        long[] vehicleCaps = {2, 2};
        // 3 clients have same shipment volume (demand) of 1 unit and depot with no demand.
        long[] shipmentVolume = {0, 1, 1, 1};
        // 3 clients have time windows when service is accepted. Time is in logical units (your's to scale).
        long[][] serviceTimes = {{0, 24}, {3, 7}, {3, 14}, {3, 7}}; // [client] [start, end]

        Routing model = new RoutingWithTime(distanceMatrix, vehicleCaps, shipmentVolume, serviceTimes);
        model.solve();
        model.printSolution();
    }


}
