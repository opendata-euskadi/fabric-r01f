package r01f.locale.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotaci�n que hay que a�adir a las clases en las que se quiere inyectar un {@link I18NService}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18NLocalized {
	/* empty */
}
