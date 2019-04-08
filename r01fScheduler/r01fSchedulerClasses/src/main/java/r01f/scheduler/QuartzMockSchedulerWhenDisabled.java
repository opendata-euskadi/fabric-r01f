package r01f.scheduler;

import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.SchedulerException;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.types.TimeLapse;

/**
 * A scheduler impl used when the scheduler is disabled (see properties and notifier guice module)
 */
@Slf4j
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class QuartzMockSchedulerWhenDisabled
   		   implements QuartzSchedulerWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <J extends Job> boolean isJobScheduled(final Class<J> jobType) throws SchedulerException {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <J extends Job> boolean scheduleJob(final Class<J> jobType, 
											   final CronExpression cronExpression)	throws SchedulerException {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
	@Override
	public <J extends Job> boolean scheduleJob(final Class<J> jobType, 
											   final Date startDate,
											   final int repetitions,final TimeLapse intervalWithinRepetitions) throws SchedulerException {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <J extends Job> boolean removeScheduledJobs(final Class<J> jobType) throws SchedulerException {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
	@Override
	public boolean removeAllScheduled() {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String schedulerDebugInfo() {
		return "scheduler is DISABLED, check properties file!";
	}
}
