package jodd.proxetta.petite.fixtures;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation für Methoden, die geloggt werden sollen. Dies kann sein fürs Tracing oder für Performance-Logging oder ....
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {TYPE, ANNOTATION_TYPE, CONSTRUCTOR, FIELD, METHOD, PARAMETER, LOCAL_VARIABLE, PACKAGE})
public @interface Logged {

}
