# Jodd Changelog

All notable changes to Jodd project are documented here.

## [n/a](https://github.com/oblac/jodd/compare/v5.0.15...master)



## [v5.0.15](https://github.com/oblac/jodd/compare/v5.0.13...v5.0.15)

### New Features

+ **madvoc** - added `@In.defaultValue()`
+ **json** - added type parser map.
+ **methref** - added counter for methref detection.

### Breaking changes

+ **methref** - methods `on()` and `to()` renamed to `of()` and `get()`. 
+ **pathref** - methods `on()` and `to()` renamed to `of()` and `get()`.
+ **jerry** - removed `$()` method in favor of `s()` - graal compatible.

### Bug fixes

+ **http** - cookies not passed down in `HttpBrowser` after a redirect occurs.
+ **lagarto** - fixed issue with the conditional comments 

## [v5.0.13](https://github.com/oblac/jodd/compare/v5.0.12...v5.0.13)

### New Features

+ **proxetta** - updated to ASM 7.1.

### Breaking changes

+ **jerry** - method `$()` deprecated (and will be removed soon). 

### Bug fixes

+ **core** - `MimeTypes` return extensions in correct order.
+ **http** - fixes special case when server returns `null` for chunked encoding.
+ **http** - fixed `SSLSocketHttpConnectionProvider` that was being ignored.
+ **db** - fix for primitives in `SqlChunk`.
+ **proxetta** - fixed an issue when generics were defined in declaration of supertype.


## [v5.0.12](https://github.com/oblac/jodd/compare/v5.0.11...v5.0.12)

### Bug fixes

+ **db** - fixed empty checking for numberic and char fields
+ **db** - debug log works better for dates
+ **json** - fixed lazy map
+ **stapler** - fixed issue with data urls in CSS

## [v5.0.11](https://github.com/oblac/jodd/compare/v5.0.10...v5.0.11)

### Breaking changes

+ **bean** - internals of Java system classes are no longer accessible.

### Bug fixes

+ **db** - fixed special case of double columns, used in PostgreSQL.
+ **stapler** - fixed issue with absolute urls in CSS.


## [v5.0.10](https://github.com/oblac/jodd/compare/v5.0.9...v5.0.10)

### New features

+ **decora** - added `decora.cache` flag that caches decorator content
+ **decora** - added methods on `DecoraManager` to register the decorator content or files.
+ **json** - internal improvements (thanx to [Gatling](https://gatling.io) !)

### Bug fixes

+ **json** - fixed issue with parsing slashes in lazy mode.


## [v5.0.9](https://github.com/oblac/jodd/compare/v5.0.8...v5.0.9)

### Bug fixes

+ **core** - removed usage of `Random`.
+ **dboom** - removed using aspects for `Loggable` connections.
+ **http** - use `connectionTimeout` to timeout proxy sockets, too. 


## [v5.0.8](https://github.com/oblac/jodd/compare/v5.0.7...5.0.8)

### New features

+ **json** - added `strictTypes` flag for `JsonParser`.

### Breaking changes

+ **bean** - method/field descriptors are no longer getter/setters.

### Bug fixes

+ **http** - fixed particular case of changing the host value.
+ **madvoc** - the matched path chunk with non-macros will win over the path chunk with macros. 


## [v5.0.7](https://github.com/oblac/jodd/compare/v5.0.6...v5.0.7)

### New features

+ **http** - added `EMLComposer.compose` for `ReceivedEmail`.
+ **http** - added max number of redirects, defaulting to 50.

### Breaking changes

+ **db** - `GenericDao` uses generic id, not `long` anymore.
+ **madvoc** - empty parameters are `null` now.

### Bug fixes

+ **props** - fixed conversion to `Map`.
+ **db** - fixed `LocalDate` and `LocalDateTime` mappings.
+ **http** - allowing `contentTypeJson` to be used in any order.
+ **http** - fixed an issue with re-reading the request.
+ **http** - fixed an issue with query param parsing.
+ **core** - fixed up Java version parsing.


## [v5.0.6](https://github.com/oblac/jodd/compare/v5.0.5...v5.0.6)

### New features

+ **bean** - added special support for inner `Supplier` for `BeanUtil`.
+ **proxetta** - updated to ASM7.

### Bug fixes

+ **core** - fixed `java.version` parsing under Java 11.


## [v5.0.5](https://github.com/oblac/jodd/compare/v5.0.4...v5.0.5)

Just refreshing with some bug fixes and few new features.

### Bug fixes

+ **json** - fixed particular case with lazy parsing and escaped chars.
+ **mail** - fixed special case with 1 body and 1 attachment.
+ **core** - fixed Zip slip vulnerability.

### New features

+ **core** - `FileUtil.mkdirs` return created folder.
+ **email** - added new method for unsetting the email flags in builder.


## [v5.0.4](https://github.com/oblac/jodd/compare/v5.0.3...v5.0.4)

Many little improvements plus one important fix for `jodd-mail`. Sorry for any inconvenience.

### Bug fixes

+ **core** - fixed special cases in `NaturalOrderComparator`, making rules a bit more strict.
+ **mail** - fixed unused `debug` and `timeout`.
+ **mail** - `RFC2822AddressParser` methods `parseToXxx()` now returns `null` for invalid emails.
+ **http** - secure connection was sending `CONNECT` string twice.

### Breaking changes

+ **mail** - `debug` and `timeout` are now applied before creating the mail server.
+ **http** - default security has been set to `TLSv1.1`.

### New features

+ **joy** - added excluded jars for faster scanning.
+ **mail** - added custom properties.
+ **json** - added `onValue` callback for JSON serializer.
+ **json** - added `excludeEmpty` flag for JSON serializer.
+ **json** - added `allowClass` for whitelisting class names.
+ **petite** - allow injection in the private fields of super types.



## [v5.0.3](https://github.com/oblac/jodd/compare/v5.0.2...v5.0.3)

### New features

+ **dboom** - added detection of the quote names in annotations.
+ **dboom** - Added flags to column and table naming strategies for quote chars.



## [v5.0.2](https://github.com/oblac/jodd/compare/v5.0.1...v5.0.2)

One minor fix and one important change for the bootstrap usages.

### New features

+ **joy** - added static method for registering joy servlet context listener.

### Bug fixes

+ **joy** - better output of the Joy configuration that does not cut of the values.



## [v5.0.1](https://github.com/oblac/jodd/compare/v5.0.0...v5.0.1)

Minor fixes and improvements.

### New features

+ **core*** - added the `Maybe.or(T)` method.
+ **mail** - added `receive()` and fluent builder.
+ **cli*** - parse values bundled with options (e.g. `--name=value`)

### Bug fixes

+ **cli** - fixed the behaviour of parameters



## [v5](https://github.com/oblac/jodd/compare/v4.3.2...v5.0.0)

Welcome to Jodd 5.

Version 5 contains a great number of new features, changes, bug fixes and performance improvements. It's all new Jodd: slick as before, just better.

High-five!

## Previous releases

[v4.x](CHANGELOG_v4.md)
[v3.x](CHANGELOG_v3.md)
