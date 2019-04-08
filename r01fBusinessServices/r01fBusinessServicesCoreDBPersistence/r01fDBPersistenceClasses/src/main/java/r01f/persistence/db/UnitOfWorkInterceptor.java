package r01f.persistence.db;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * A guice method interceptor for the @UnitOfWork annotation
 */
public class UnitOfWorkInterceptor 
  implements MethodInterceptor {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
    @Inject
    private com.google.inject.persist.UnitOfWork _unitOfWork;
    
    // ThreadLocal<Boolean> tracks if the unit of work was begun implicitly by this thread.
    // According to the docs we can start and end the UnitOfWork as often as we want. 
    // But this has to be balanced in some way. Otherwise we get:
    //
    // 		java.lang.IllegalStateException: Work already begun on this thread. Looks like you have called UnitOfWork.begin() twice without a balancing call to end() in between.
	//		at com.google.common.base.Preconditions.checkState(Preconditions.java:174)
	//		at com.google.inject.persist.jpa.JpaPersistService.begin(JpaPersistService.java:73)
    //    
    // That way all begin() and end() calls are balanced because we only have one unit for this thread.
    //
    // Guice's JpaLocalTxnInterceptor intercepts @Transactional annotated methods; it calls unitOfWork.begin()
    // if an entityManager has been previously requested (the entityManager is attached to a ThreadLocal -> see JpaPersistService)
    // ... so if both @UnitOfWork and @Transactional annotations are present
    // 
    final ThreadLocal<Boolean> _didWeStartWork = new ThreadLocal<Boolean>();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (_didWeStartWork.get() == null) {
            _unitOfWork.begin();
            _didWeStartWork.set(Boolean.TRUE);
        } else {
            // If unit of work already started we don't do anything here...
            // another UnitOfWorkInterceptor point point will take care...
            // This happens if you are nesting your calls.
            return invocation.proceed();
        }
        try {

            return invocation.proceed();

        } finally {
            if (_didWeStartWork.get() != null) {
                _didWeStartWork.remove();
                _unitOfWork.end();
            }
        }
    }
}