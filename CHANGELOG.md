# Jodd Change Log

All notable changes to Jodd project are documented here.

## [Unreleased](https://github.com/oblac/jodd/compare/v3.8.1...master)

### Bug Fixes

+ **db** - `SqlBuilder#generateQuery` may be called multiple times.
+ **http** - `response#cookies()` does not throw exception on invalid cookies.
+ **http** - fixed special case with `Cookie` parsing. 
+ **core** - natural comparison has been fixed to follow comparator contracts.

### Features

+ **db** - added `resetAll` method for hard-resetting the queries.
+ **http** - address parsing and exception message is much better.
+ **http** - added optional encoding for `HttpRequest#readFrom`.
+ **email** - email parser is improved.
+ **core** - natural comparison improved and accents added.
+ **core** - added `ThreadFactoryBuilder.
+ **core** - added `Futures` utilities.
+ **core** - added stream-related `Collection` utilities.

### Breaking changes

+ **email** - renamed `EmailAddress`, it is used now as a Email parser.
+ **dboom** - method `_` has been removed. Use `append` instead.

### System

+ **gradle** - updated to Gradle 3.3


## [3.8.1](https://github.com/oblac/jodd/compare/v3.8.0...v3.8.1)

### Bug Fixes

+ **core** - fixed issue with `StringBand` calculation.

## Performance

+ **core** - added performance check for `StringBand`.

### Features

+ **lagarto** - added `contents`, `.after`, `replaceWith`, `unwrap`, `prepend`, `prevAll()`, `nextAll` to Jerry.
+ **http** - added `trustAllCerts` to http client.
+ **core** - added `Chalk` class.

## Breaking changes

+ **core** - `CommandLine` removes custom shell execution code.
