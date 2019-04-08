package r01f.scheduler;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Injector;

import lombok.extern.slf4j.Slf4j;

/**
 * Quartz scheduler job factory.
 * (see http://javaeenotes.blogspot.com.es/2011/09/inject-instances-in-quartz-jobs-with.html)
 */
@Singleton
@Slf4j
public class QuartzSchedulerJobFactory 
  implements JobFactory {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Injector _guiceInjector;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public QuartzSchedulerJobFactory(final Injector guiceInjector) {
		_guiceInjector = guiceInjector;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("cast")
    public Job newJob(final TriggerFiredBundle bundle,
    				  final Scheduler scheduler) throws SchedulerException {
		Job outJob = null;
		try {
	        outJob = (Job)_guiceInjector.getInstance(bundle.getJobDetail()
	        										  	   .getJobClass());
		} catch(Throwable th) {
			log.error("Could not create a quartz job of type {}: {}",
					  bundle.getJobDetail()
	        				.getJobClass(),
	        		  th.getMessage(),th);
		}
		return outJob;
    }  
}
