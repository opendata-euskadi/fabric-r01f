package r01f.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import r01f.securitycontext.SecurityContextProviderForMasterUserBase;



/**
 * see {@link SecurityContextProviderForMasterUserBase}
 */
@BindingAnnotation			
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.PARAMETER})
public @interface SecurityContextProviderForMasterUser {
	/* nothing to do */
}
 