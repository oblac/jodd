<h1 align="center">Jodd</h1>
<h4 align="center">Open-source Java micro-frameworks and tools; compact, yet powerful.</h4>
<br>

[![GitHub release](https://img.shields.io/github/release/oblac/jodd.svg)](https://jodd.org)
[![Build Status](https://img.shields.io/travis/oblac/jodd.svg)](https://travis-ci.org/oblac/jodd)
[![codecov](https://codecov.io/gh/oblac/jodd/branch/master/graph/badge.svg)](https://codecov.io/gh/oblac/jodd)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0ce3a0ae3667441fbbd261f6c9e043b0)](https://www.codacy.com/app/igo_rs/jodd)
[![Snapshot](https://api.bintray.com/packages/oblac/jodd/org.jodd%3Ajodd-core/images/download.svg)](https://bintray.com/oblac/jodd)
[![JitPack](https://jitpack.io/v/oblac/jodd.svg)](https://jitpack.io/#oblac/jodd)
[![Stack Overflow](http://img.shields.io/badge/stack%20overflow-jodd-4183C4.svg)](http://stackoverflow.com/questions/tagged/jodd)
[![BSD License](https://img.shields.io/github/license/oblac/jodd.svg)](http://jodd.org/license.html)
[![Twitter](https://img.shields.io/twitter/url/https/github.com/oblac/jodd.svg?style=social)](https://twitter.com/intent/tweet?text=Wow:&url=%5Bobject%20Object%5D)

+ Official web site (site & documentation): http://jodd.org/
+ GitHub page (5 min overview): http://oblac.github.io/jodd
+ Jodd micro-frameworks (30 min overview): http://joddframework.org
+ One-page love: http://jodd.org/about
+ Talk with us at [gitter](https://gitter.im/oblac/jodd)!

<h4 align="center">Jodd = tools + ioc + mvc + db + aop + tx + json + html < 1.7 Mb</h4>

## :zap: Jodd Modules

**Jodd** is split into many modules, so choose what to use.
Some tools and utility modules are:

+ `jodd-core` contains many utilities, including `JDateTime`.
+ `jodd-bean`, our infamous `BeanUtil`, type inspectors and converters.
+ `jodd-props` is the super-replacement for Java `Properties`.
+ `jodd-mail` for easier email sending.
+ `jodd-upload`, handles HTTP uploads.
+ `jodd-servlet` with many servlet utilities, including nice tag library.
+ `jodd-http`, tiny HTTP client.

and some **micro-frameworks**:

+ `jodd-madvoc` - slick MVC framework.
+ `jodd-petite` - pragmatic DI container.
+ `jodd-lagarto` - HTML parser with `Jerry` and `CSSelly`.
+ `jodd-decora` - pages decorator.
+ `jodd-htmlstapler` - static page resources handler.
+ `jodd-proxetta` - dynamic proxies and `Paramo`.
+ `jodd-db` - thin database layer and object mapper.
+ `jodd-json` - JSON parser and serializer.
+ `jodd-vtor` - validation framework.

Read more in our [official documentation](http://jodd.org/doc).

## :sunny: Jodd Bundle

If you are already using many **Jodd** jars, you can simply
just use the _bundle_ jar: `jodd-all`. It's a single jar with
all modules included; where all dependencies are optional. Why not :)


## :octocat: Building Jodd from source

**Jodd** is built with [Gradle](http://gradle.org/) on JDK8,
targeting Java 1.8. You don't have to install anything,
the only prerequisites are [Git](http://help.github.com/set-up-git-redirect)
and Java JDK.

### Check out sources

Simply clone **Jodd** Git repo:

    git clone https://github.com/oblac/jodd.git jodd

### Compile and test, build jars

You can build the Jodd project with:

    gradlew build

This will build all jars and run all unit tests.
To skip the tests (for faster build), execute:

    gradlew build -x test

### Build full release with reports

To generate _full release_, including running integration tests and generating various reports,
you need [Docker](https://www.docker.com/) v1.12+.

	docker-compose -f etc/docker-compose.yml up
    gradlew clean release

Integration tests requires some infrastructure (like databases), hence Docker is
used.

### Install Jodd into your local Maven

    gradlew install

## :gift_heart: Contribute

Feel free to [contribute](CONTRIBUTING.md)! Follow these steps:

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
