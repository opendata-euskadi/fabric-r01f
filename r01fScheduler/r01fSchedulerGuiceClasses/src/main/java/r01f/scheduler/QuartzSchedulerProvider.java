package r01f.scheduler;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import r01f.exceptions.Throwables;
import r01f.scheduler.SchedulerConfig;

/**
 * Provider that creates a quartz {@link Scheduler}
 */
@Singleton
public class QuartzSchedulerProvider 
  implements Provider<Scheduler> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The scheduler config
	 */
	private final SchedulerConfig _schedulerConfig;
	/**
	 * Job factory (see http://javaeenotes.blogspot.com.es/2011/09/inject-instances-in-quartz-jobs-with.html)
	 */
	private final QuartzSchedulerJobFactory _jobFactory;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public QuartzSchedulerProvider(final SchedulerConfig cfg,
								   final QuartzSchedulerJobFactory jobFactory) {
		_schedulerConfig = cfg;
		_jobFactory = jobFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Scheduler get() {
		Scheduler outScheduler = null;
        try {
        	StdSchedulerFactory sf = new StdSchedulerFactory();
        	sf.initialize(_schedulerConfig.getSchedulerProperties());
			outScheduler = sf.getScheduler();
			outScheduler.setJobFactory(_jobFactory);
			
		} catch (SchedulerException schEx) {
			Throwables.throwUnchecked(schEx);	// throw an unchecked exception
		}
        return outScheduler;	// never gets here
	}
}
