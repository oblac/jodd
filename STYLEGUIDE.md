# Styleguide and Naming conventions

Please follow this style guide and naming conventions when sending your submissions. 

## Code

+ Use TABS and not spaces for indentation.

## Test

+ Test classes ends with `*Test`, e.g. `StringUtilTest`.
+ Test methods starts with `test*`, e.g. `testReplace()`.
+ Test methods and classes are `public`.
+ It is it OK to use experimental features, like `ParameterizedTest` - as long as you keep maintaining it.
+ Nested test classes do not have the suffix in their names.
+ Nested test classes should be in package scope.
+ Use static import for `Assert` methods.
+ try to have one test method per test feature.

### Test Example

An example how a test class may look like.

```java
// imports 
public class StreamUtilTest {

  @Test
  public void testSomething() throws Exception {
      // test code
  }

  @Nested
  @DisplayName("tests for StreamUtil#close - method")
  class Close {

    @Test
    void testClose_with_null() throws Exception {
      // test code  
    }

    @Test
    void testClose_with_closeable_instance() throws Exception {
      // test code
    }
}
```
