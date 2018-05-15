# Styleguide and Naming conventions

Please follow this style guide and naming conventions when sending your submissions. Thank you!

## Code :coffee:

+ Use **TABS** and not spaces for indentation. We just had to choose one.
+ Interfaces may contain static factories for known implementations. Examples:
```java
	Value.of(123);      // creates a value
	List.arrayList();   // creates new array list
	Component.get();    // returns default singleton implementation
```

+ _The common sense_ is assumed! We do not have extra code to prevent mis-usages of the API.
+ Check for `null` using `Objects.requireNonNull()`, but only where it is really needed. Again, if code itself throws NPE, no need for additional checks.
+ `*Util` classes do not have `private` constructor (_common sense principle_).
+ Returning `null` should be generally avoided for the public methods.
+ Only beans have accessors (`getFoo()` and `setFoo()` methods). Method should not be named with e.g. `get` if it is not a bean.
+ Util static methods that return new instance should be named e.g. `create()` or `createFooBar()` if there is no argument or `fooBarOf(argument)` if there is an argument provided; but _never_ `getFooBar()`. Getters should never create a new instance of anything.
+ _Builder_ pattern function should be named `create()`. Final builder method (that actually returns instance that is building) should be named `get()` or `buildFoo()`.
+ Use `_this()` for base abstract classes of some fluent interfaces API:
```java
	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}
```
+ Use `final` method arguments.

### Singletons, implementations and defaults

_Singletons_ are generally avoided. Singletons have static method `get()` used to fetch the singleton instange:

```java
public class MyFoo {
    private static final MyFoo MY_FOO = new MyFoo();

    public static MyFoo get() {
        return MY_FOO;
    }
}
```

_Default implementations_ of some interface are stored in static class named `Implementation`.
To emphasize the changing of the value, the `set()` method is part of the `Implementation` inner class.

```java
public static interface MyFoo {
    class Implementation {
    	private static MyFoo myFoo = new DefaultMyFoo();
    	public static void set(MyFoo myFoo) {
    		this.myFoo = myFoo;
    	}
    }

    public static MyFoo get() {
        return Implementation.myFoo;
    }
}
```

_Defaults_ is configuration for classes that are created by user directly (such as `JsonParser` or `HttpRequest`).
Those classes may have some defaults so you don't need to change them all the time. Default configuration
does not have sense on singletons! Inner class `Defaults` should contain only public static fields of common types.

```java
public class MyFoo {
	public static class Defaults {
		public static boolean someFlag = false;
	}
}
```

### About deprecation and @Since tag

For now, we are _not_ able to maintain deprecated methods and the use of `@Since` tag versions. We simply don't have enough resources for that atm, sorry. It's better not to have it, but to have it all wrong.


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
+ Each benchmark class must contain results in the Javadoc of the class: just copy/paste whatever is the JMH output.
