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
