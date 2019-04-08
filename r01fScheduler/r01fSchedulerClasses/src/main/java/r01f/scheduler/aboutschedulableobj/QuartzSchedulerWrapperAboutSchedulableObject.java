/**
 *
 */
package r01f.scheduler.aboutschedulableobj;

import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.SchedulerException;

import r01f.guids.CanBeScheduledOID;
import r01f.scheduler.QuartzSchedulerWrapper;
import r01f.types.TimeLapse;

/**
 * Scheduler interface for some schedulable entity
 */
public interface QuartzSchedulerWrapperAboutSchedulableObject<O extends CanBeScheduledOID>
		 extends QuartzSchedulerWrapper {
	/**
	 * Check if it exists an Scheduled job about an schedulable object
	 * @param oid the object's oid
	 * @param jobType
	 * @param cronExpression
	 * @return true if the package was scheduled
	 */
	public <J extends Job> boolean isJobScheduledAboutObject(final O oid,
														  	 final Class<J> jobType) throws SchedulerException;
	/**
	 * Schedules an schedulable object
	 * @param oid the object's oid
	 * @param jobType
	 * @param cronExpression
	 * @return true if the package was scheduled
	 */
	public <J extends Job> boolean scheduleJobAboutObject(final O oid,
														  final Class<J> jobType,
										  				  final CronExpression cronExpression) throws SchedulerException;
	/**
	 * Schedules a job about an object to be executed N times starting at the given time
	 * In order to schedule just a SINGLE execution of the job use:
	 * <pre class='brush:java'>
	 * 		scheduleJobAboutObject(triggerId,
	 * 					   		   oid,
	 * 					   		   startDate,
	 * 					   			0,null);		// 0 repetitions!
	 * </pre>
	 * @param triggerId
	 * @param oid
	 * @param jobType
	 * @param triggerId
	 * @param startDate
	 * @param repetitions
	 * @param intervalWithinRepetitions
	 * @return
	 */
	public <J extends Job> boolean scheduleJobAboutObject(final O oid,
														  final Class<J> jobType,
										  				  final Date startDate,
										  				  final int repetitions,TimeLapse intervalWithinRepetitions) throws SchedulerException;
	/**
	 * Removes an scheduled schedulable object
	 * @param oid the object's oid
	 * @param jobType
	 * @param true if removed
	 */
	public <J extends Job> boolean removeScheduledJobsAbout(final O oid,
															final Class<J> jobType) throws SchedulerException;
}
