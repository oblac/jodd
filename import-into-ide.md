
# Import Jodd into your favorite IDE

## Intellij IDEA 12

The following has been tested against Intellij IDEA 12.

### Steps

_Within your locally cloned Jodd working directory:_

1. Generate IDEA metadata with `./gradlew cleanIdea idea`
2. Open project in IDEA as usual
3. Set the Project JDK as appropriate
4. Add git support
5. Code away

### Tips

In any case, please do not check in your own generated .iml, .ipr, or .iws files.
You'll notice these files are already intentionally in `.gitignore`. The same policy goes for eclipse metadata.

### FAQ

Q. What about IDEA's own [Gradle support](http://confluence.jetbrains.net/display/IDEADEV/Gradle+integration)?

A. Keep an eye on http://youtrack.jetbrains.com/issue/IDEA-53476

## Eclipse

Working on it...