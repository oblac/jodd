Jodd
====

**Jodd** is set of open-source Java tools and micro frameworks; compact, yet powerful.

Official web site: http://jodd.org/

Invitation web site: http://joddframework.org


## Jodd Modules

**Jodd** is split into many modules, so choose what to use.
Some tools and utility modules are:

+ `jodd-core` contains many utilities, including `JDateTime` and `Convert`.
+ `jodd-bean`, our infamous `BeanUtil`.
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
+ `jodd-vtor` - validation framework.

Read more in our [official documentation](http://jodd.org/doc).


## Jodd Bundle

If you are already using many **Jodd** jars, you can simply
just use the *bundle* jar. It's a single jar that bind all modules:)


## Building Jodd from source

**Jodd** uses [Gradle](http://gradle.org/) build system. Good thing is that you don't have to install anything; the only prerequisites are [Git](http://help.github.com/set-up-git-redirect) and Java JDK.

### Check out sources

    git clone https://github.com/oblac/jodd.git jodd

### Compile and test, build jars

    gradlew build

### Build full release with reports

    gradlew release

### Install Jodd into your local Maven

    gradlew install

## Contributing

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

We will pickup up from here:)
