Jodd
====

Jodd is set of open-source Java tools and frameworks; compact, but powerful.

Official web site: http://jodd.org/

Invitation web site: http://joddframework.org


## Modules of Jodd

Jodd is split into many modules, so choose what to use.
Some tools and utility modules are:

+ `jodd-core` contains many utilities, `JDateTime` and `Convert`.
+ `jodd-bean`, our infamous `BeanUtil`.
+ `jodd-props` is the super-replacement for Java `Properties`.
+ `jodd-email` for easier email sending.

and some frameworks modules:

+ `jodd-madvoc` - slick MVC framework.
+ `jodd-petite` - pragmatic DI container.
+ `jodd-lagarto` - HTML parser with `Jerry` and `CSSelly`.
+ `jodd-proxetta` - dynamic proxies and `Paramo`.

Read more in our official documentation.


## Jodd Bundle

If you are already using many Jodd jars, you can simply
use just one Jodd bundle jar. The one jar that bind them all:)


## Building Jodd

Jodd is built with Maven 3. After cloning Jodd git repo,
you can build the full release with:

    mvn -P release

If you need to generate various reports after the successful build:

    mvn -P post-release

To quickly build distribution jars (no tests are invoked):

    mvn -P dist

That's all what you need to know;)

### Versioning

Display plugin and dependency information:

    mvn -N versions:display-plugin-updates
    mvn -N versions:display-dependency-updates

Done.

## Contributing

Feel free to contribute! Follow these steps:

First time only:
+ fork the Jodd repo (`upstream`) to your GitHub account (`origin`).
+ clone `origin` as your `local` repo

Every other time:
+ update both `origin` and `local` repos from `upstream`
+ create new branch for a feature or bug fix
+ commit often :)
+ once when work is done, push local changes to your `origin`
+ send us a pull request (PR)

We will pickup up from here:)
