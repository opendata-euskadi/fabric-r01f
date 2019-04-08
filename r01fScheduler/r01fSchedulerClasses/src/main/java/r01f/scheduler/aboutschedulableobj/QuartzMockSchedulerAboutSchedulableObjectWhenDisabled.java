package r01f.scheduler.aboutschedulableobj;

import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.SchedulerException;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CanBeScheduledOID;
import r01f.scheduler.QuartzMockSchedulerWhenDisabled;
import r01f.types.TimeLapse;

/**
 * A scheduler impl used when the scheduler is disabled (see properties and notifier guice module)
 */
@Slf4j
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class QuartzMockSchedulerAboutSchedulableObjectWhenDisabled<O extends CanBeScheduledOID>
			  extends QuartzMockSchedulerWhenDisabled
   		   implements QuartzSchedulerWrapperAboutSchedulableObject<O> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <J extends Job> boolean isJobScheduledAboutObject(final O oid,
															 final Class<J> jobType) throws SchedulerException {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <J extends Job> boolean scheduleJobAboutObject(final O oid,
														  final Class<J> jobType,
														  final CronExpression cronExpression) {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
	@Override
	public <J extends Job> boolean scheduleJobAboutObject(final O oid, 
														  final Class<J> jobType,
														  final Date startDate,
														  final int repetitions,final TimeLapse intervalWithinRepetitions) {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <J extends Job> boolean removeScheduledJobsAbout(final O oid, Class<J> jobType) {
		log.warn("scheduler is DISABLED, check properties file!");
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
}
