Jodd
====

[![BSD License](http://img.shields.io/badge/license-BSD-orange.svg)](http://jodd.org/license.html)
[![Build Status](https://travis-ci.org/oblac/jodd.png)](https://travis-ci.org/oblac/jodd)
[![Coverage Status](https://coveralls.io/repos/oblac/jodd/badge.png?branch=master)](https://coveralls.io/r/oblac/jodd?branch=master)
[![Stack Overflow](http://img.shields.io/badge/stack%20overflow-jodd-4183C4.svg)](http://stackoverflow.com/questions/tagged/jodd)
[![Ready tasks](https://badge.waffle.io/oblac/jodd.png?label=ready&title=Ready)](https://waffle.io/oblac/jodd)

**Jodd** is set of open-source Java tools and micro frameworks; compact, yet powerful.

**Jodd = tools + ioc + mvc + db + aop + tx + json + html < 1.5 Mb**

Read about **Jodd**:

+ Official web site (site, documentation, information): http://jodd.org/

+ GitHub page (5 min overview): http://oblac.github.io/jodd

+ Jodd micro-frameworks (30 min overview): http://joddframework.org

+ One-page love: http://jodd.org/about


## Jodd Modules

**Jodd** is split into many modules, so choose what to use.
Some tools and utility modules are:

+ `jodd-core` contains many utilities, including `JDateTime`.
+ `jodd-bean`, our infamous `BeanUtil`, type inspectors and converters.
+ `jodd-props` is the super-replacement for Java `Properties`.
+ `jodd-email` for easier email sending.
+ `jodd-upload`, handles HTTP uploads.
+ `jodd-servlet` with many servlet utilities, including nice tag library.
+ `jodd-http`, tiny HTTP client.

and some micro frameworks:

+ `jodd-madvoc` - slick MVC framework.
+ `jodd-petite` - pragmatic DI container.
+ `jodd-lagarto` - HTML parser with `Jerry` and `CSSelly`.
+ `jodd-lagarto-web` - `Lagarto` addon for web: `Decora`, `HtmlStapler` and more.
+ `jodd-proxetta` - dynamic proxies and `Paramo`.
+ `jodd-db` - thin database layer and object mapper.
+ `jodd-json` - JSON parser and serializer.
+ `jodd-vtor` - validation framework.

Read more in our [official documentation](http://jodd.org/doc).

## Jodd Bundle

If you are already using many **Jodd** jars, you can simply
just use the _bundle_ jar. It's a single jar with
all modules included :)


## Building Jodd from source

**Jodd** is built with [Gradle](http://gradle.org/) on JDK6,
targeting Java 1.5. You don't have to install anything,
the only prerequisites are [Git](http://help.github.com/set-up-git-redirect)
and Java JDK.

### Check out sources

Simply clone **Jodd** Git repo:

    git clone https://github.com/oblac/jodd.git jodd

### Compile and test, build jars

You can build the project with:

    gradlew build

This will build all jars and run all unit tests.
To skip the tests (for faster build), execute:

    gradlew build -x test

### Build full release with reports

To generate full release, including running integration tests and generating various reports:

    gradlew release

For integration tests you will need also to set up databases named: 'jodd-test' on local MySql (access: root/root!) and PostgreSQL (postgres/root!).

### Install Jodd into your local Maven

    gradlew install

## Contribute

Feel free to contribute! Follow these steps:

First time only:

+ fork the **Jodd** repo (`upstream`) to your GitHub account (`origin`).
+ clone `origin` as your `local` repo

Every other time:

+ update both `origin` and `local` repos from `upstream`
+ create new branch for a feature or bug fix
+ commit often :)
+ once when work is done, push local changes to your `origin`
+ send us a pull request (PR)

We will pickup up from there :)

:rocket: