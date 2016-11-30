# OR-Tools Samples

Java is not favorite language among supported by [Google's or-tools project](https://developers.google.com/optimization/). This repo is a collection of `Java` samples to facilitate learning of or-tools project.
The or-tools project source code has two places of Java examples:
 * Java **samples** in main
[or-tools github repository](https://github.com/google/or-tools/tree/master/examples/com/google/ortools/samples)
 * Java **tutorial examples** are [limited to a few](https://github.com/google/or-tools/tree/master/documentation/tutorials/java/com/google/ortools/tutorial).

## Running samples
Users are welcome to use IDE of their choice but no preferences are carried over here. Any sample in this repository can be run by command line:
```
$ javac @options.arg src\samples\SimpleRoutingTest.java
$ java -cp "out;lib\*" -Djava.library.path=lib\rt  samples.SimpleRoutingTest
```

