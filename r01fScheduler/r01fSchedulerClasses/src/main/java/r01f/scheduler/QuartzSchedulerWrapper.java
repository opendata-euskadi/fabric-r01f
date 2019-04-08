/**
 *
 */
package r01f.scheduler;

import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.SchedulerException;

import r01f.types.TimeLapse;

/**
 * Scheduler interface scheduler wrapper
 */
public interface QuartzSchedulerWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if there's an scheduled job associated with the given trigger
	 * @param jobType
	 * @param triggerId
	 * @return
	 */
	public <J extends Job> boolean isJobScheduled(final Class<J> jobType) throws SchedulerException;
	/**
	 * Schedules a job
	 * @param jobType the job obj type
	 * @param cronExpression
	 * @return true if the package was scheduled
	 */
	public <J extends Job> boolean scheduleJob(final Class<J> jobType,
											   final CronExpression cronExpression) throws SchedulerException ;
	/**
	 * Schedules a job 
	 * In order to schedule just a SINGLE execution of the job use:
	 * <pre class='brush:java'>
	 * 		scheduleJob(job,
	 * 					startDate,
	 * 					0,null);		// 0 repetitions!
	 * </pre>
	 * @param jobType the job obj type
	 * @param startDate
	 * @param repetitions
	 * @param intervalWithinRepetitions
	 * @return
	 */
	public <J extends Job> boolean scheduleJob(final Class<J> jobType,
											   final Date startDate,
							   				   final int repetitions,TimeLapse intervalWithinRepetitions) throws SchedulerException ;
	/**
	 * Removes all scheduled jobs
	 * @param jobType
	 * @return
	 */
	public <J extends Job> boolean removeScheduledJobs(final Class<J> jobType) throws SchedulerException;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Clears all scheduled
     * @return
     */
    public boolean removeAllScheduled() throws SchedulerException;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return info about scheduler
     */
    public String schedulerDebugInfo();
}
