/**
 *
 */
package r01f.scheduler;

import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.scheduler.SchedulerIDs.SchedulerTriggerID;
import r01f.scheduler.aboutschedulableobj.QuartzSchedulerWrapperAboutSchedulableObject;
import r01f.types.TimeLapse;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


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
public abstract class QuartzSchedulerWrapperBase
           implements QuartzSchedulerWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The scheduler config
	 */
	protected final SchedulerConfig _schedulerConfig;
    /**
     * Quartz scheduler
     */
    protected final Scheduler _scheduler;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public QuartzSchedulerWrapperBase(final SchedulerConfig cfg,
									  final Scheduler scheduler) {
    	_schedulerConfig = cfg;
    	_scheduler = scheduler;
    }   
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public <J extends Job> boolean isJobScheduled(final Class<J> jobType) throws SchedulerException {
    	JobKey jobKey = QuartzSchedulerJobKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
    												.using(jobType)
    												.build();
   		return _scheduler.checkExists(jobKey);
    }
    @Override
    public <J extends Job> boolean scheduleJob(final Class<J> jobType,
    										   final CronExpression cronExpression) throws SchedulerException  {
        log.info("SCHEDULE a job of type={} > cron expression={}",
        		 jobType.getName(),
        		 cronExpression);
        
        // b.- Create the trigger scheduled with the cron expression
    	if (cronExpression == null) log.warn("BEWARE!!!! NO cron expression set: default dayly at 00:00");
        CronScheduleBuilder scheduleBuilder = cronExpression != null 
        											? CronScheduleBuilder.cronSchedule(cronExpression)
        											: CronScheduleBuilder.dailyAtHourAndMinute(0,0);
        Trigger trigger = _createTrigger(SchedulerTriggerID.forJobType(jobType),
        								 scheduleBuilder);
        
        // c.- Create a job that will execute the package and link it to the trigger
        JobDetail job = _createJob(jobType);
        
        // d.- Link the job and the trigger
        boolean outScheduled = _linkJobToTrigger(job,trigger);
        
        // return
        return outScheduled;
    }
	@Override
	public <J extends Job> boolean scheduleJob(final Class<J> jobType,
											   final Date startDate,
							   				   final int repetitions,final TimeLapse intervalWithinRepetitions) throws SchedulerException  {
    	if (startDate == null) throw new IllegalArgumentException("start date is mandatory!!");
    	
        log.info("SCHEDULE job of type {} with oid={} > start date={} repeat {} times every {}",
        		 jobType.getName(),
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
        Trigger trigger = _createTrigger(SchedulerTriggerID.forJobType(jobType),
        								 scheduleBuilder);
        
        // c.- Create a job that will execute the package and link it to the trigger
        JobDetail job = _createJob(jobType);
        
        // d.- Link the job and the trigger
        boolean outScheduled = _linkJobToTrigger(job,trigger);

        // return
        return outScheduled;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private Trigger _createTrigger(final SchedulerTriggerID triggerId,
								   final ScheduleBuilder<?> scheduleBuilder) {
        TriggerKey triggerKey = QuartzSchedulerTriggerKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
        														.at(triggerId)
        														.build();
        Trigger trigger = TriggerBuilder.newTrigger()
                               			.withIdentity(triggerKey)
                               			.withSchedule(scheduleBuilder)
                               			.build();
        log.debug("\t-created quartz trigger with key={}",
        		  trigger.getKey());
        return trigger;
	}
	private <J extends Job> JobDetail _createJob(final Class<J> jobType) {
        JobKey jobKey = QuartzSchedulerJobKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
        											.using(jobType)
        											.build();
        JobDetail job =  JobBuilder.newJob(jobType)
                               	   .withIdentity(jobKey)
                               	   .build();
        log.debug("\t-created quartz job with key={} for a job of type={}",
        		  job.getKey(),
        		  jobType.getName());
        return job;
	}
	protected boolean _linkJobToTrigger(final JobDetail job,final Trigger trigger) throws SchedulerException {
        boolean outScheduled = false;
        if (!_scheduler.checkExists(job.getKey())) {
        	log.info("\t-link job={} to the trigger={} (the job did NOT previously exist in the scheduler)",
        			  job.getKey(),trigger.getKey());
            _scheduler.scheduleJob(job,trigger);
        } else {
        	log.info("\t-job={} already exists at the scheduler: remove it and link it to trigger={}",
        			 job.getKey(),
        			 trigger.getKey());
            _scheduler.deleteJob(job.getKey());
            _scheduler.scheduleJob(job,trigger);
        }
        outScheduled = true;
        return outScheduled;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UN SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////	
    @Override
    public <J extends Job> boolean removeScheduledJobs(final Class<J> jobType) throws SchedulerException {
    	boolean outUnscheduled = false;
        JobKey jobKey = QuartzSchedulerJobKeyBuilder.forScheduler(_schedulerConfig.getSchedulerId())
        											.using(jobType)
        											.build();
        if (_scheduler.checkExists(jobKey)) {
            outUnscheduled = _scheduler.deleteJob(jobKey);
        } else {
            log.warn("job with type={} cannot be removed from the schedulerz job={} does NOT exists",
            		 jobType.getName(),
            		 jobKey);
        }
    	return outUnscheduled;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean removeAllScheduled() throws SchedulerException {
		_scheduler.clear();
    	return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
    public String schedulerDebugInfo() {
    	StringBuilder sw = new StringBuilder();
    	
    	SchedulerMetaData schedulerMetaData = null;
        Collection<JobExecutionContext> currentJobs = null;
        try {
        	// Get data
        	schedulerMetaData = _scheduler.getMetaData();
        	currentJobs = _scheduler.getCurrentlyExecutingJobs();
        	
        	// Print
        	sw.append("Scheduler metadata:\n")
        	  .append(schedulerMetaData.getSummary());
        	
	        if (CollectionUtils.hasData(currentJobs)) {
	        	sw.append("Currently executing jobs:\n");
				for (JobExecutionContext key : currentJobs) {
					sw.append(Strings.customized("\n\t-{}",
												 key.getJobDetail().getKey().getName()));
				}
	        }
        } catch (SchedulerException schEx) {
        	log.error("Error while getting scheduler debug info: {}",
        			  schEx.getMessage(),schEx);
        	sw.append(Throwables.getStackTraceAsString(schEx));
        }
        return sw.toString();
    }
}
