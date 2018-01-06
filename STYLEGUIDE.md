# Styleguide and Naming conventions

Please follow this style guide and naming conventions when sending your submissions. Thank you! 

## Code :coffee:

+ Use **TABS** and not spaces for indentation. We just had to choose one. 
+ Interfaces may contain static factories (see `Value.of()`) for known implementations.
+ _The common sense_ is assumed. We do not handle all possible mis-usages of the API. For example, we will not check for `null` and then throw custom exception when it is obvious that methods should accept non-null value.
+ `*Util` classes do not have `private` constructor (_common sense principle_).
+ `null` usage should be generally avoided for the public methods.
+ Only beans have accessors (`getFoo()` and `setFoo()` methods). Method should not be named with e.g. `get` if it is not a bean.
+ Util methods that return new instance should be named e.g. `createFooBar()` if there is no argument or `fooBarOf(argument)` if there is an argument provided.

## Test :hearts:

+ Test classes ends with `*Test`, e.g. `StringUtilTest`.
+ Test methods starts with `test*`, e.g. `testReplace()`.
+ Test methods and classes are package scoped.
+ It is it OK to use experimental features of Junit 5 - as long as you keep maintaining it.
+ Nested test classes do not have the suffix in their names.
+ Use static import for `Assert` methods.
+ Use static import for optional `Assume` methods.
+ Try to have one test method per feature.

### Test class example

```java
class FooTest {

	@Test
	void testSomething() {
	  // test code
	}
	
	@Nested
	@DisplayName("tests for a feature set")
	class FeatureSet {
		
		@Test
		void testFeature_with_null() {
		  // test code  
		}
	
		@Test
		void testFeature_with_something_else() {
		  // test code
		}
	}
}
```

## Benchmarks :zap:

+ Each benchmark is stored in separated `*Benchmark` class.
+ Benchmarks are executed via [JMH](http://openjdk.java.net/projects/code-tools/jmh/).
+ Benchmark methods are annotated with an annotation.
+ Don't use `BlackHole` argument if you can return the value.
+ Each benchmark class must contain results in the Javadoc of the class:
just copy/paste whatever is the JMH output.
+ Benchmarks are started with: `gradlew :<module>:<benchmark-class-name>` (e.g.: `gradlew :jodd-core:Base32Benchmark`) 


## Modules :rocket:

+ Jodd module has `get()` static method that returns the module instance.
+ Default configuration of the module is stored as a bean in modules instance. Try to minimise usage of this static call - rather get a value and pass it as an argument, then to fetch the value twice. Default configuration is a bean. Configuration can be split in multiple beans.  
+ Module instance may have set of runtime components - parts of the module that represents some logic. 
+ Do not store module's configuration as a constant. We expect config may change in the runtime.
+ Components are returned using methods without the `get` prefix.
+ Main components should have self-describing interface.