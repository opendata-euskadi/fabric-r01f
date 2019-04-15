package r01f.persistence.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inspired by ninja framework UnitOfWork: https://github.com/ninjaframework/ninja/blob/develop/ninja-core/src/main/java/ninja/jpa/UnitOfWork.java
 * Usage:
 *		[1] - For read-only queries annotate the service method with @UnitOfWork (and it may be faster because there are no transactions started). 
 * 		[2] - For saving / updating and deleting data always annotate the service method with @Transactional
 *	 	[3]	- For several transactions within one service method invocation:
 *				a) use @UnitOfWork around the service method that wraps/orchestates the fine-grained methods 
 *				   ... and use @Transactional at fine-grained methods to demarcate transactions within the same @UnitOfWork
 * 				b. use @Transactional to demarcate transactions within a service method without @UnitOfWork
 * See:	http://www.ninjaframework.org/documentation/working_with_relational_dbs/jpa.html 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitOfWork {
	// nothing now
}