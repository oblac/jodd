# Jodd TODOs

This is a list of long-term TODOs and ideas. Some are still half-baked; we have
them on this list just not to forget the idea. The list is split into modules,
without any particular order.

> "Every once in a while, I take my head out of the clouds and do a reality check."

Game over?

## Core

+ Add smart download in `NetUtil`: monitor progress, etc.
  This tool would have some downloading options to tweak download better.
+ Check `FileUtil.move`/`copy dir`/`file` variants to be more performant.
  See which variant is the fastest one. Noticed a slow execution in sitegenerator.
+ Add `FileNameUtil.calculateRelativePath`.


## Lagarto/Jerry

+ Remove circular dep `Node` <-> `Document`.
+ Make `Node` API for adding nodes more user friendly.

## Props

+ Move extract/sub/inner methods in separate class. With one method in `Props`
  class we are going to create a new instance of this tool class, and there
  you will be able to convert to and from `Props`.

## JSON

+ Add factories in `JsonParser` for various types.
+ Add JSON parser generator based on JSON scheme - this should give the fastest
  parser as it is directly related to a scheme. 

## Props-plugin

+ Add `+=` operator
+ Add `<=` operator
+ Check when categories are listed in values, do not recognize them as categories but test
  Categories must start on empty or whitespaced line

## Madvoc

+ Add Converters, that convert input text automatically.
  For example, convert `"1,473.00"` into `"1473"`.
+ `MadvocServletFilter` may consumes action path, like load static content.
+ Check `AnnotatedFieldInterceptor`, if it works correctly (just a check).
+ More flexible `ActionMethodParser`, i.e. point where action path is build.
  For example, user may choose if extension is "json" or not, based on class name.
+ Add `JSONResult(returnCode, jsonString)`.

## EMail

+ Add callback method for `receive()`, so users may control if there is an
  exception and continue fetching if there is one.
+ Add `UploadFile`, so user can download email attachments directly to e.g. disk instead to memory.

## Cache

+ LFU, add optional count reduction
+ LFU & LRU, add optional percentage reduction: eg, delete 25% od cache.
+ Add caches on method level, annotation based.
  Each cache has an id, each one can be evicted, use annotations or manual config.
+ Add Madvoc filter for storing caching response for some time.
  Configurable, eg. 1 cache setting per actionString (`class#method`) or from additional annotation etc.

## JTX

+ Add 'Rollback on' feature - that does rollback only on certain exception types.
+ Add `Thread` timeout in separate class.
+ How to explicitly specify TX scope in annotation? 
+ Check nested TX, once again (just a re-check).

## Proxetta

+ Add statistics: number of proxified methods etc.
+ Add unique interface over all 3 proxy types, so they can be combined, if possible.

## DbOom

+ `LoadById`, load values into existing instance.
+ Add batch update somehow.
+ Add matcher for `match()` method.
+ Add more matching wildcards for set, update etc. For now you can update only
  full object, and enable to update non-null parts, too.
+ Make constants like`COLS_INCLUDE_ALL` to be `enum`.

## Joy

+ Localization arguments, {0}, {1}...
+ Don't encode text tags in resource bundles
+ `Pager` - refactor to be more user friendly when user has to write custom code