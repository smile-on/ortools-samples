# OR-Tools Samples

Java is not favorite language among supported by [Google's or-tools project](https://developers.google.com/optimization/). This repo is a collection of `Java` samples to facilitate learning of or-tools project. The or-tools project source code has more [Java examples in github repository](https://github.com/google/or-tools/tree/master/examples/java).

## Running samples
Users are welcome to use IDE of their choice but any sample in this repository can be run by command line:
```shell
$ javac @options.arg src/samples/routing/SimpleRoutingTest.java
$ java -cp 'out:lib/*' -Djava.library.path=lib/rt  routing.SimpleRoutingTest
```

```cmd
> javac @options.arg src\samples\routing\SimpleRoutingTest.java
> java -cp "out;lib\*" -Djava.library.path=lib\rt  routing.SimpleRoutingTest
```

These java examples use Google OR-TOOLS routing APIs v6.9 [Release 6.9.5762](https://github.com/google/or-tools/releases/tag/v6.9). Notes v6.9 requires minimum Java 9.

## Samples

The map of routes used in all samples. 

![map](./route-map.gif) 

[SimpleRoutingTest](./src/samples/routing/SimpleRoutingTest.java) - simplest test. Search of a shortest path to go around all customers.

There are no constraints => fully optimal route for single vehicle: **cost 13 solution [0, 2, 3, 1]**.

[SimpleRoutingMultiVehicles](./src/samples/routing/SimpleRoutingMultiVehicles.java) - search for optimal solution with two vehicles.

Limitation on volume is added. Each vehicle can take up to 2 loads => optimal routes with work split between two vehicles:  **cost 18 solution [[0, 3, 1], [0, 2]]**.

[RoutingWithTimeTest](./src/samples/routing/RoutingWithTimeTest.java) -  search for optimal solution with time constraint. This demo is collection of Java classes that structures VRP problem into simple pieces. `Routing` implements simplest VRP. `RoutingBasic` adds volume constraint. `RoutingWithTime` adds time windows constraints. Locations 3 and 1 are set with first priority in service time => optimal routes became longer: **cost 23 solution [[0, 3, 2], [0, 1]]**.

