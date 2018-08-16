# Jodd Changelog

All notable changes to Jodd project are documented here.

## [n/a](https://github.com/oblac/jodd/compare/v5.0.3...master)

(work in progress :)

### Bug fixes

+ **core** - fixed special cases in `NaturalOrderComparator`, making rules a bit more strict.  
+ **mail** - fixed unused `debug` and `timeout`.
+ **mail** - `RFC2822AddressParser` methods `parseToXxx()` now returns `null` for invalid emails.
+ **http** - secure connection was sending `CONNECT` string twice.

### Breaking changes

+ **mail** - `debug` and `timeout` are now applied before creating the mail server.
+ **http** - default security has been set to `TLSv1.1`.

### New Features

+ **joy** - added excluded jars for faster scanning.
+ **mail** - added custom properties.
+ **json** - added `onValue` callback for JSON serializer.
+ **json** - added `excludeEmpty` flag for JSON serializer.
+ **petite** - allow injection in the private fields of super types.



## [v5.0.3](https://github.com/oblac/jodd/compare/v5.0.2...v5.0.3)

### New Features

+ **dboom** - added detection of the quote names in annotations.
+ **dboom** - Added flags to column and table naming strategies for quote chars.



## [v5.0.2](https://github.com/oblac/jodd/compare/v5.0.1...v5.0.2)

One minor fix and one important change for the bootstrap usages.

### New Features

+ **joy** - added static method for registering joy servlet context listener.

### Bug Fixes

+ **joy** - better output of the Joy configuration that does not cut of the values.  



## [v5.0.1](https://github.com/oblac/jodd/compare/v5.0.0...v5.0.1)

Minor fixes and improvements.

### New Features

+ **core*** - added the `Maybe.or(T)` method.
+ **mail** - added `receive()` and fluent builder.
+ **cli*** - parse values bundled with options (e.g. `--name=value`)

### Bug Fixes

+ **cli** - fixed the behaviour of parameters



## [v5](https://github.com/oblac/jodd/compare/v4.3.2...v5.0.0)

Welcome to Jodd 5.

Version 5 contains a great number of new features, changes, bug fixes and performance improvements. It's all new Jodd: slick as before, just better. 

High-five!

## Previous releases

[v4.x](CHANGELOG_v4.md)
[v3.x](CHANGELOG_v3.md)
