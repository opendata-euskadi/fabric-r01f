package r01f.scheduler;

import org.quartz.Job;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SchedulerIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="schedulerId")
	@NoArgsConstructor
	public static final class SchedulerID
		 		      extends OIDBaseMutable<String> 
	  			   implements OID {
		private static final long serialVersionUID = -9181977752174509450L;
		
		public SchedulerID(final String oid) {
			super(oid);
		}
		public static SchedulerID valueOf(final String id) {
			return new SchedulerID(id);
		}
		public static SchedulerID forId(final String id) {
			return new SchedulerID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="schedulerJobId")
	@NoArgsConstructor
	public static final class SchedulerJobID
		 			  extends OIDBaseMutable<String> 
	  			   implements OID {
		private static final long serialVersionUID = -9181977752174509450L;
		
		public SchedulerJobID(final String oid) {
			super(oid);
		}
		public static SchedulerJobID valueOf(final String id) {
			return new SchedulerJobID(id);
		}
		public static SchedulerJobID forId(final String id) {
			return new SchedulerJobID(id);
		}
		public static SchedulerJobID forJobType(final Class<? extends Job> jobType) {
			return SchedulerJobID.forId(jobType.getName());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="schedulerTriggerId")
	@NoArgsConstructor
	public static final class SchedulerTriggerID
		 			  extends OIDBaseMutable<String> 
	  			   implements OID {
		private static final long serialVersionUID = -9181977752174509450L;
		
		public SchedulerTriggerID(final String oid) {
			super(oid);
		}
		public static SchedulerTriggerID valueOf(final String id) {
			return new SchedulerTriggerID(id);
		}
		public static SchedulerTriggerID forId(final String id) {
			return new SchedulerTriggerID(id);
		}
		public static SchedulerTriggerID forJobType(final Class<? extends Job> jobType) {
			return SchedulerTriggerID.forId(jobType.getName());
		}
	}
}
