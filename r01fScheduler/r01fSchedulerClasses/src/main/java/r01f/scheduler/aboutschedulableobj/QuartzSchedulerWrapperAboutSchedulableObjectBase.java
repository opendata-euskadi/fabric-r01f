/**
 *
 */
package r01f.scheduler.aboutschedulableobj;

import java.util.Date;
import java.util.Properties;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CanBeScheduledOID;
import r01f.scheduler.QuartzSchedulerJobKeyBuilder;
import r01f.scheduler.QuartzSchedulerTriggerKeyBuilder;
import r01f.scheduler.QuartzSchedulerWrapperBase;
import r01f.scheduler.SchedulerConfig;
import r01f.scheduler.SchedulerIDs.SchedulerTriggerID;
import r01f.types.TimeLapse;


/**
 * Implements the {@link QuartzSchedulerWrapperAboutSchedulableObject} interface on a quartz scheduler
 *
 * Quartz can be configured
 * <ul>
 * 		<li>Using the quartz.properties file (anywhere in the classpath)<br /> 
 * 			If quartz does not find quartz.properties file, it uses the default config at quartz JAR 
 * 			(org/quartz/quartz.properties)</li>
 * 		<li>Hand a{@link Properties} object with the config</li>
 *
 * In this type a {@link Properties} object is used; this {@link Properties} object is created from 
 * a properties file 
 */
@Slf4j
@Accessors(prefix="_")
public abstract class QuartzSchedulerWrapperAboutSchedulableObjectBase<O extends CanBeScheduledOID>
			  extends QuartzSchedulerWrapperBase
           implements QuartzSchedulerWrapperAboutSchedulableObject<O> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public QuartzSchedulerWrapperAboutSchedulableObjectBase(final SchedulerConfig cfg,
    												   		final Scheduler scheduler) {
    	super(cfg,
    		  scheduler);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public <J extends Job> boolean isJobScheduledAboutObject(final O oid,
														  	 final Class<J> jobType) throws SchedulerException {
    	if (oid == null) throw new IllegalArgumentException("oid is mandatory!");
    	if (jobType == null) throw new IllegalArgumentException("job type is mandatory!");
    	
        JobKey jobKey = QuartzSchedulerJobKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
        											.using(jobType)
        											.buildFor(oid);
   		return _scheduler.checkExists(jobKey);
	}
    @Override
    public <J extends Job> boolean scheduleJobAboutObject(final O oid,
														  final Class<J> jobType,
										  				  final CronExpression cronExpression) throws SchedulerException {
    	if (oid == null) throw new IllegalArgumentException("oid is mandatory!");
    	if (jobType == null) throw new IllegalArgumentException("job type is mandatory!");
    	
        log.info("SCHEDULE an object of type {} with oid={} > cron expression={}",
        		 oid.getScheduleableObjectType(),oid,
        		 cronExpression);
        
        // b.- Create the trigger scheduled with the cron expression
    	if (cronExpression == null) log.warn("BEWARE!!!! NO cron expression set: default dayly at 00:00");
        CronScheduleBuilder scheduleBuilder = cronExpression != null 
        											? CronScheduleBuilder.cronSchedule(cronExpression)
        											: CronScheduleBuilder.dailyAtHourAndMinute(0,0);
        Trigger trigger = _createTrigger(oid,
        								 SchedulerTriggerID.forJobType(jobType),
        								 scheduleBuilder);
        
        // c.- Create a job that will execute the package and link it to the trigger
        JobDetail job = _createJob(jobType,
        						   oid);
        
        // d.- Link the job and the trigger
        boolean outScheduled = _linkJobToTrigger(job,trigger);
        
        // return
        return outScheduled;
    }
	@Override
	public <J extends Job> boolean scheduleJobAboutObject(final O oid,
														  final Class<J> jobType,
										  				  final Date startDate,
										  				  final int repetitions,TimeLapse intervalWithinRepetitions) throws SchedulerException {
    	if (oid == null) throw new IllegalArgumentException("oid is mandatory!");
    	if (jobType == null) throw new IllegalArgumentException("job type is mandatory!");
    	if (startDate == null) throw new IllegalArgumentException("start date is mandatory!!");
    	
        log.info("SCHEDULE an object of type {} with oid={} > start date={} repeat {} times every {}",
        		 oid.getScheduleableObjectType(),oid,
        		 startDate,repetitions,intervalWithinRepetitions);
        
        // b.- Create the trigger scheduled to be started at the given date 
        if (repetitions > 0 && intervalWithinRepetitions == null) log.warn("BEWARE!!! {} repetions was set BUT no interval within them: defaulting to 1 minute within repetitions",
        																   repetitions);
        TimeLapse theIntervalWithinRepetitions = intervalWithinRepetitions != null
        											? intervalWithinRepetitions
        											: repetitions > 0 ? TimeLapse.createFor("1m")	// 1 minute by default
        															  : null;
        SimpleScheduleBuilder scheduleBuilder = repetitions > 0
        											? SimpleScheduleBuilder.simpleSchedule()
                               											   .withRepeatCount(repetitions)
                               											   .withIntervalInMilliseconds(theIntervalWithinRepetitions.asMilis())
                               						: SimpleScheduleBuilder.simpleSchedule()
                               											   .withRepeatCount(0);		// 0 repetitions
        Trigger trigger = _createTrigger(oid,
        								 SchedulerTriggerID.forJobType(jobType),
        								 scheduleBuilder);
        
        // c.- Create a job that will execute the package and link it to the trigger
        JobDetail job = _createJob(jobType,
        						   oid);
        
        // d.- Link the job and the trigger
        boolean outScheduled = _linkJobToTrigger(job,trigger);

        // return
        return outScheduled;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private Trigger _createTrigger(final O schedulableOid,
								   final SchedulerTriggerID triggerId,
								   final ScheduleBuilder<?> scheduleBuilder) {
        TriggerKey triggerKey = QuartzSchedulerTriggerKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
        														.at(triggerId)
        														.buildFor(schedulableOid);
        Trigger trigger = TriggerBuilder.newTrigger()
                               			.withIdentity(triggerKey)
                               			.withSchedule(scheduleBuilder)
                               			.build();
        log.debug("\t-quartz trigger with key={} created for {} with oid={}",
        		  trigger.getKey(),
        		  schedulableOid.getScheduleableObjectType(),schedulableOid);
        return trigger;
	}
	private <J extends Job> JobDetail _createJob(final Class<J> jobType,
												 final O schedulableOid) {
        JobKey jobKey = QuartzSchedulerJobKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
        											.using(jobType)
        											.buildFor(schedulableOid);
        JobDetail job =  JobBuilder.newJob(jobType)
                               	   .withIdentity(jobKey)
                               	   .usingJobData("schedulableObjectOid",	// job data map key
                               			   		 schedulableOid.asString())	// context data
                               	   .build();
        log.debug("\t-quartz job with key={} created for a {} with oid={}",
        		  job.getKey(),
        		  schedulableOid.getScheduleableObjectType(),schedulableOid);
        return job;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UN SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////	
    @Override
    public <J extends Job> boolean removeScheduledJobsAbout(final O oid,
															final Class<J> jobType) throws SchedulerException {
    	if (oid == null) throw new IllegalArgumentException("oid is mandatory!");
    	if (jobType == null) throw new IllegalArgumentException("job type is mandatory!");
    	
    	boolean outUnscheduled = false;
    	try {
	        JobKey jobKey = QuartzSchedulerJobKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
	        											.using(jobType)
	        											.buildFor(oid);
	        if (_scheduler.checkExists(jobKey)) {
	            outUnscheduled = _scheduler.deleteJob(jobKey);
	        } else {
	            log.warn("{} with oid={} cannot be removed from the schedulerz job={} does NOT exists",
	            		 oid.getScheduleableObjectType(),oid,
	            		 jobKey);
	        }
    	} catch (SchedulerException schEx) {
        	log.error("Could NOT remov scheduled jobs about {} with oid={}: {}",
        			  oid.getScheduleableObjectType(),oid,
        			  schEx.getMessage(),
        			  schEx);
        }
    	return outUnscheduled;
    }
}
