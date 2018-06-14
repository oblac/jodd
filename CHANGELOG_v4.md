# Jodd v4.x Changelog

## [4.3.2](https://github.com/oblac/jodd/compare/v4.3.1...4.3.2)

Minor fixes.

### Features

+ **bean** - added missing date-time type converters.
+ **dboom** - added missing date-time sql type converters.
+ **json** - added missing date-time json type converters.

### Bug Fixes

+ **core** - fixed special cases of `StringTemplateParser` usage.
+ **mail** - fixed NPE when creating SSL variant of email server.


## [4.3.1](https://github.com/oblac/jodd/compare/v4.3.0...4.3.1)

Just few minor changes.

### Features

+ **madvoc** - added two more configurations: `defaultViewPageName` and `defaultViewExtensions`.
+ **joy** - add option to use custom web application with `useWebApp()` method.

### Breaking changes

+ **code** - removed `JoddArrayList`. 


## [4.3.0](https://github.com/oblac/jodd/compare/v4.2.0...4.3.0)

An emotional roller coaster of the release :)

### Breaking changes

+ **all** - no more internal modules, removed `Defaults` 
+ **upload** - module moved to core
+ **core** - `JDateTime` removed.
+ **core** - `Printf` removed.
+ **core** - `LocaleUtil` and `DateFormatSymbolsEx` removed.

### Features

+ **json** - the order of the keys in JSON is now preserved.
+ **core** - added `TimeUtil` utils. 
+ **core** - added `JulianDate` class, just for Julian dates.
+ **joy** - allow to set manually props files to load.

### Bug Fixes

+ **http** - special case of media and charset heading value.
+ **madvoc** - fixes body reading on Jetty.

## [4.2.0](https://github.com/oblac/jodd/compare/v4.1.5...4.2.0)

### Features

+ **json** - lazy parser mode performance improved... a lot.
+ **core** - added `TypeCache` with 4 different implementations.

## [4.1.5](https://github.com/oblac/jodd/compare/v4.1.4...4.1.5)

Like in every movie flick, having a nice release is always a good thing. Jodd is ready for an Oscar :) In this episode find some sweet addons.

### Features

+ **bean** - `UUID` type converter added.
+ **json** - added `UUID` JSON type serializer.
+ **petite** - added `@PetiteValue` annotation for injecting parameters.
+ **petite** - added `implicitParamInjection` flag to enable/disable implicit injection.
+ **props** - added optional default values when getting props.
+ **json** - added `PrettyJsonSerializer`!
+ **core** - enabled unlimited crypto security (requires > Java8 u151)
+ **core** - added engines for digest, hashes and encryption 
+ **core** - `StringUtil` methods for conversion to and from `byte[]`.

### Bug Fixes

+ **gradle** - fixes the `pom.xml` for `jodd-all`.


## [4.1.4](https://github.com/oblac/jodd/compare/v4.1.1...v4.1.4)

+ **core** added `jre9` classifier release
+ **gradle** migrated to new `maven-publish` plugin
+ **all** jars are now signed with the correct PGP key

## [4.1.1](https://github.com/oblac/jodd/compare/v4.1.0...v4.1.1)

### Bug Fixes

+ **core** - fixed a regression with the stream copying.
+ **email** - fixed access issue for a `Builder`.
+ **core** - `JavaBridge` now works with Java9.

### Features

+ **http** - added a `header(Map)` method to `HttpRequest`.

### Breaking changes

+ **core** - `ClassScanner` now begins scanning with `scan()`.
+ **http** - methods with `boolean` argument renamed to `*Overwrite`.
+ **http** - methods named `remove` renamed. 

## [4.1.0](https://github.com/oblac/jodd/compare/v3.9.1...v4.1.0)

This is the first 4.x release. It contains a _massive_ set of changes from the version 3.

Hey, what happened to 4.0? It was not production ready. We introduced some performance regressions. THey are fixed now. 

## Previous releases

[v3](CHANGELOG_v3.md)
