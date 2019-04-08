package r01f.scheduler;

import org.quartz.TriggerKey;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CanBeScheduledOID;
import r01f.patterns.IsBuilder;
import r01f.scheduler.SchedulerIDs.SchedulerID;
import r01f.scheduler.SchedulerIDs.SchedulerTriggerID;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class QuartzSchedulerTriggerKeyBuilder 
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static QuartzSchedulerTriggerKeyBuilderTriggerIdStep forScheduler(final SchedulerID schedulerId) {
		return new QuartzSchedulerTriggerKeyBuilder() { /* nothing */ }
						.new QuartzSchedulerTriggerKeyBuilderTriggerIdStep(schedulerId);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class QuartzSchedulerTriggerKeyBuilderTriggerIdStep {
		private final SchedulerID _schedulerId;
		
	    public QuartzSchedulerTriggerKeyBuilderBuildStep at(final SchedulerTriggerID triggerId) {
	    	return new QuartzSchedulerTriggerKeyBuilderBuildStep(_schedulerId,
	    																		triggerId);
	    }
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class QuartzSchedulerTriggerKeyBuilderBuildStep {
		private final SchedulerID _schedulerId;
		private final SchedulerTriggerID _triggerId;
		
	    public TriggerKey build() {
	    	String group = Strings.customized("{}.{}",
	    									  _schedulerId,_triggerId);
	    	return TriggerKey.triggerKey("default",	// name
	                               		 group);	// group
	    }
	    public <O extends CanBeScheduledOID> TriggerKey buildFor(final O schedulableOid) {
	    	String group = Strings.customized("{}.{}",
	    									  _schedulerId,_triggerId);
	    	return TriggerKey.triggerKey(schedulableOid.asString(),	// name
	                               		 group);					// group
	    } 
	}
}
