![](jodd-github-logo.png)
<br>

[![GitHub release](https://img.shields.io/github/release/oblac/jodd.svg)](https://jodd.org)
[![Build Status](https://img.shields.io/travis/oblac/jodd.svg)](https://travis-ci.org/oblac/jodd)
[![codecov](https://codecov.io/gh/oblac/jodd/branch/master/graph/badge.svg)](https://codecov.io/gh/oblac/jodd)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0ce3a0ae3667441fbbd261f6c9e043b0)](https://www.codacy.com/app/igo_rs/jodd)
[![JitPack](https://jitpack.io/v/oblac/jodd.svg)](https://jitpack.io/#oblac/jodd)
[![Stack Overflow](https://img.shields.io/badge/stack%20overflow-jodd-4183C4.svg)](https://stackoverflow.com/questions/tagged/jodd)
[![BSD License](https://img.shields.io/badge/license-BSD--2--Clause-blue.svg)](https://jodd.org/license.html)
[![Twitter](https://img.shields.io/twitter/url/https/github.com/oblac/jodd.svg?style=social)](https://twitter.com/intent/tweet?text=Hey,@joddorg%20is%20cool!&url=%5Bobject%20Object%5D)
[![Help Contribute to Open Source](https://www.codetriage.com/oblac/jodd/badges/users.svg)](https://www.codetriage.com/oblac/jodd)


> We want to inspire the world to create. To use tech skills and build something truly... Awesome. Beautiful. Helpful. Impactful. Now you can produce lightweight code and focus on unleashing your full potential. Jodd is a set of developer-friendly and open-source Java micro-frameworks. It's designed to make things simple, but not simpler.

+ Official web site (site & documentation): https://jodd.org
+ Jodd micro-frameworks (30 min overview): https://joddframework.org
+ Talk to Jodd via [gitter](https://gitter.im/oblac/jodd) or [slack](https://jodd.slack.com).

<h4 align="center">Jodd = tools + ioc + mvc + db + aop + tx + json + html < 1.7 Mb</h4>

# :zap: Jodd

**Jodd** is set of micro-frameworks and developer-friendly tools and utilities.

_Use what you like._

**Cool libraries**:

+ `jodd-json` - JSON parser and serializer.
+ `jodd-lagarto` - HTML parser with `Jerry` and `CSSelly` and `Form` tag.
+ `jodd-http` - tiny HTTP client.
+ `jodd-mail` - for easier email sending.

**Micro-frameworks**:

+ `jodd-madvoc` - slick MVC framework.
+ `jodd-petite` - pragmatic DI container.
+ `jodd-proxetta` - dynamic proxies and `Paramo`.
+ `jodd-db` - thin database layer and object mapper.
+ `jodd-jtx` - transactions management.

**Less used tools**:

+ `jodd-decora` - pages decorator.
+ `jodd-htmlstapler` - static page resources handler.
+ `jodd-vtor` - validation framework.

**Full Stack Bundle**:

+ `jodd-joy` - super-easy app framework, built with *Jodd* micro-frameworks.

**Utilities**:

+ `jodd-core` - contains many utilities, including `JDateTime`.
+ `jodd-bean` - our infamous `BeanUtil`, type inspectors and converters.
+ `jodd-props` - is the super-replacement for Java `Properties`.
+ `jodd-servlet` - with many servlet utilities, including nice tag library.

Read more in our [official documentation](http://jodd.org/doc).

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

+ fork the **Jodd** repo (`upstream`) to your GitHub account (`origin`)
+ clone `origin` as your `local` repo
+ install the [ZenHub](https://www.zenhub.com) plugin to track tasks

Every other time:

+ update both `origin` and `local` repos from `upstream`
+ create new branch for a feature or bug fix
+ commit often :)
+ once when work is done, push local changes to your `origin`
+ send us a pull request (PR)

We will pickup up from there :)

:rocket:
