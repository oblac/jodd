# Jodd Core for Java 9

This is not a module of the Jodd, but a _separate_ project. This project creates **Java 9** classes for the purpose of creating **MR-Jar** of `jodd-core` module.


Currently, Java 9 class files are copied manually to the `jodd-core`:

+ FROM: `jodd-core-9/build/classes/java/main/jodd`
+ TO: `jodd-core/src/main/resources9/META-INF/versions/9/jodd`