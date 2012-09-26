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

Jodd is built with Maven 3 on JDK6. After cloning Jodd git repo,
you can build the full release with:

    mvn -P release

If you need to generate various reports after the successful build:

    mvn -P post-release

To quickly build distribution jars (no tests are invoked):

    mvn -P dist

That's all what you need to know;)

### Versioning

Display plugin information (in `jodd-parent`):

    mvn versions:display-plugin-updates


## Contributing

Feel free to contribute! Fork the Jodd repo, create a branch for a feature or bug fix, and send us a pull request.
