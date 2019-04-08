/**
 *
 */
package r01f.scheduler.aboutschedulableobj;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import lombok.experimental.Accessors;
import r01f.guids.CanBeScheduledOID;
import r01f.patterns.FactoryFrom;
import r01f.scheduler.QuartzSchedulerJobKeyBuilder;
import r01f.scheduler.SchedulerConfig;
import r01f.util.types.Strings;

/**
 * Quartz Job executed by the quartz scheduler when a trigger is raised
 * The executer is created each time the quartz scheduler executes the job
 * 
 * BEWARE!!!                                                                                  
 * 		In order for this instance to be injected by guice it MUST be created by guice             
 * 		... so use a job factory!                                            
 * 		(see http://javaeenotes.blogspot.com.es/2011/09/inject-instances-in-quartz-jobs-with.html) 
 */
@Accessors(prefix="_")
public abstract class QuartzSchedulerJobAboutSchedulableObjectBase<O extends CanBeScheduledOID,
																   SELF_TYPE extends QuartzSchedulerJobAboutSchedulableObjectBase<O,SELF_TYPE>> 
  		   implements Job {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FactoryFrom<String,O> _oidFromStringFactory;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected QuartzSchedulerJobAboutSchedulableObjectBase(final SchedulerConfig cfg,
													  	   final FactoryFrom<String,O> oidFromStringFactory) {
		_oidFromStringFactory = oidFromStringFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public O getSchedulableObjectOidFrom(final JobExecutionContext context) {
		// The schedulable object oid can be get from:
		//		a) the job key		
		//		b) the job data map 
		JobKey jobKey = context != null 
					  && context.getJobDetail() != null 
					  && context.getJobDetail().getKey() != null  ? context.getJobDetail().getKey()
																  : null;
		String oidAtJobDataMap =  context != null 
				  					  && context.getJobDetail() != null 
				  					  && context.getJobDetail().getJobDataMap() != null ? context.getJobDetail()
				  							  													 .getJobDataMap()
				  							  													 .getString("schedulableObjectOid")
				  							  											: null;
		O schedulableOidFromJobKey = jobKey != null 
											? QuartzSchedulerJobKeyBuilder.forJobType(this.getClass())
																		  .using(_oidFromStringFactory)
																		  .schedulableObjectOidFrom(jobKey)
											: null;
		O schedulableOidFromJob = Strings.isNOTNullOrEmpty(oidAtJobDataMap)
											? _oidFromStringFactory.from(oidAtJobDataMap) 
											: null;
		O outOid = schedulableOidFromJobKey != null ? schedulableOidFromJobKey
												    : schedulableOidFromJob != null ? schedulableOidFromJob
														 				   			: null;
		if (outOid == null) throw new IllegalStateException("Could NOT get the schedulable object oid neither from job key neither from the job data map");
		
		return outOid;
	}
}