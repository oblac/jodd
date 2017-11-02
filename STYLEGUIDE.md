# Styleguide and Naming conventions

Please follow this style guide and naming conventions when sending your submissions. 

## Code

+ Use TABS and not spaces for indentation (it)

## Test

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

## Benchmarks

+ Each benchmark is stored in separated `*Benchmark` class.
+ Benchmarks are executed via [jmh](http://openjdk.java.net/projects/code-tools/jmh/).
+ Benchmark methods are annotated with an annotation.
+ Don't use `BlackHole` argument if you can return the value.
+ Each benchmark class must contain results in the Javadoc of the class:
just copy/paste whatever is the JMH output.
+ Benchmarks are started with: `gradlew :<module>:<benchmark-class-name>` (e.g.: `gradlew :jodd-core:Base32Benchmark`) 


## Modules Architecture

+ Interfaces may contain static factories (see `Value.of()`).
+ Common sense is assumed. Jodd does not handle all possible misusages of the API. For example, we will not check for `null` and then throw custom exception when it is obvious that methods should accept non-null value.
+ `null` usage should be generally avoided.
+ Jodd module has `get()` static method that returns the module instance.
+ Default configuration of the module is stored as a bean in modules instance. Try to minimise usage of this static call - rather get a value and pass it as an argument, then to fetch the value twice. Default configuration is a bean. Configuration can be split in multiple beans.  
+ Module instance may have set of runtime components - parts of the module that represents some logic.
