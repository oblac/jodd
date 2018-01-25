# Jodd Change Log

All notable changes to Jodd project are documented here.

## [Unreleased](https://github.com/oblac/jodd/compare/v4.1.0...master)

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
