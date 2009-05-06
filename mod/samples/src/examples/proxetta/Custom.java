// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={METHOD,CONSTRUCTOR,TYPE, PARAMETER})
public @interface Custom {
}
