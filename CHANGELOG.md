# Jodd Changelog

All notable changes to Jodd project are documented here.

## [v5.0](https://github.com/oblac/jodd/compare/v4.3.2...v5.0.0)

Super high-five!

### Features

+ **mail** - added `updateEmailFlags`.
+ **madvoc** - added flag to `RestActionNamingStrategy`.
+ **petite** - added `PetiteContainer#invokeMethod`.
+ **petite** - added cache for external types that are not registered into the container.
+ **madvoc** - `MadvocScope` added, injectors removed. Scopes are now defined by annotations only.
+ **madvoc** - Aux scope annotations added.
+ **proxetta** - ASM library updated to v6.1.1.

### Breaking changes

+ Some classes have been moved in different packages.
+ **core** - `TypeCache` made better. `put()` removed in favor of `get()`.
+ **core** - `AnnotationParser` added instead of clumsy `AnnotatedData` and `AnnotatedReader`.
+ **madvoc** - `Scope` annotation has been changed.
+ **madvoc** - `ApplicationScope` removed.
+ **servlet** - removed map wrappers over servlet components.
+ **bean** - added `MethodParamDescriptor` instead of array-returning param-methods.

## Previous releases

[v4.x](CHANGELOG_v4.md)
[v3.x](CHANGELOG_v3.md)
