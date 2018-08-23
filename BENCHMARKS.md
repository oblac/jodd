# Jodd Benchmarks

While Jodd is developer-friendly, we also strive for the best performances possible under the hood. For some tools or components, we are the best in the class; for others we are among the most performing. While the performance is our priority, we will _never_ sacrifice the developer-friendly environment (usage and interfaces) that Jodd has. 

## Running Benchmarks :rocket:

Benchmarks are started from command line in root directory with:
 
```
gradlew :<module>:<benchmark-class-name>
````

(e.g.: `gradlew :jodd-core:Base32Benchmark`) 


## 3rd Party Benchmarks :heart:

Other people test Jodd, too :)

+ Css selectors microbenchmark for the JVM; by [Gatling](https://gatling.io)

https://github.com/gatling/lib-benchmarks/tree/master/cssselectors-benchmark-master

+ JsonPath implementations microbenchmark for the JVM; by [Gatling](https://gatling.io) 
  
https://github.com/gatling/lib-benchmarks/tree/master/jsonpath-benchmark-master

 
