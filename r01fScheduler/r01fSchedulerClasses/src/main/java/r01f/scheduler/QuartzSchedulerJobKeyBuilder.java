package r01f.scheduler;

import org.quartz.Job;
import org.quartz.JobKey;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CanBeScheduledOID;
import r01f.patterns.FactoryFrom;
import r01f.patterns.IsBuilder;
import r01f.scheduler.SchedulerIDs.SchedulerID;
import r01f.scheduler.SchedulerIDs.SchedulerJobID;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class QuartzSchedulerJobKeyBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  KEY ENCODE
/////////////////////////////////////////////////////////////////////////////////////////
	public static QuartzSchedulerJobKeyBuilderJobIdStep forScheduler(final SchedulerID schedulerId) {
		return new QuartzSchedulerJobKeyBuilder() { /* nothing */ }
						.new QuartzSchedulerJobKeyBuilderJobIdStep(schedulerId);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class QuartzSchedulerJobKeyBuilderJobIdStep {
		private final SchedulerID _schedulerId;
		
	    public QuartzSchedulerJobKeyBuilderBuildStep using(final SchedulerJobID jobId) {
	    	return new QuartzSchedulerJobKeyBuilderBuildStep(_schedulerId,
	    													 jobId);
	    }
	    public <J extends Job> QuartzSchedulerJobKeyBuilderBuildStep using(final Class<J> jobType) {
	    	return new QuartzSchedulerJobKeyBuilderBuildStep(_schedulerId,
	    													 SchedulerJobID.forId(jobType.getName()));
	    }
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class QuartzSchedulerJobKeyBuilderBuildStep {
		private final SchedulerID _schedulerId;
		private final SchedulerJobID _jobId;
		
	    public JobKey build() {
	    	// The job key's name is composed like 
	    	String group = Strings.customized("{}.{}",
	    									  _schedulerId,_jobId);
	        return JobKey.jobKey("default",	
	        					 group);
	    }
	    public <O extends CanBeScheduledOID> JobKey buildFor(final O oid) {
	    	// The job key's name is composed like 
	    	String group = Strings.customized("{}.{}",
	    									  _schedulerId,_jobId);
	        return JobKey.jobKey(oid.asString(),	
	        					 group);
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  KEY DECODE
/////////////////////////////////////////////////////////////////////////////////////////
	public static <J extends Job> SchedulableObjectOidFromQuartzSchedulerKeyBuilderOidFactoryStep<J> forJobType(final Class<J> jobType) {
		return new QuartzSchedulerJobKeyBuilder() { /* nothing */ }
					.new SchedulableObjectOidFromQuartzSchedulerKeyBuilderOidFactoryStep<J>(jobType);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)	
	public class SchedulableObjectOidFromQuartzSchedulerKeyBuilderOidFactoryStep<J extends Job> {
		private final Class<J> _jobType;
		
		public <O extends CanBeScheduledOID> SchedulableObjectOidFromQuartzSchedulerKeyBuilderBuildStep<J,O> using(final FactoryFrom<String,O> oidFromStringFactory) {
			return new SchedulableObjectOidFromQuartzSchedulerKeyBuilderBuildStep<J,O>(_jobType,
																					   oidFromStringFactory);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class SchedulableObjectOidFromQuartzSchedulerKeyBuilderBuildStep<J extends Job,
																			O extends CanBeScheduledOID> {
		private final Class<J> _jobType;
		private final FactoryFrom<String,O> _oidFromStringFactory;
		
	    public O schedulableObjectOidFrom(final JobKey jobKey) {
	    	String jobKeyNameStr = jobKey.getName();
	    	String jobKeyGroupStr = jobKey.getGroup();
	    	
	    	O outId = _oidFromStringFactory.from(jobKeyNameStr);
	    	return outId;
	    }
	}
}
