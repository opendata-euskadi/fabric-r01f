package r01f.scheduler;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import lombok.extern.slf4j.Slf4j;
import r01f.service.ServiceHandler;

@Singleton
@Slf4j
public class QuartzSchedulerServiceHandler
  implements ServiceHandler {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	private final Scheduler _quartzScheduler;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public QuartzSchedulerServiceHandler(final Scheduler scheduler) {
		_quartzScheduler = scheduler;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICE HANDLER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start() {
		log.warn("######################################################################################");
		log.warn("Starting scheduler (will wait for jobs to complete!");
		log.warn("######################################################################################");
		try {
			_quartzScheduler.start();
			log.warn("Scheduler status: {}",
					 _quartzScheduler.getMetaData()
					 				 .getSummary());
		} catch (SchedulerException schEx) {
			log.error("Error starting quartz scheduler: {}",
					  schEx.getMessage(),schEx);
		}
	}
	@Override
	public void stop() {
		log.warn("######################################################################################");
		log.warn("Stopping scheduler (will wait for jobs to complete!");
		log.warn("######################################################################################");
		try {
			_quartzScheduler.shutdown(true);	// wait for jobs to complete
		} catch (SchedulerException schEx) {
			log.error("Could NOT stop the scheduler: {}",
					  schEx.getMessage(),schEx);
		}
	}
}
