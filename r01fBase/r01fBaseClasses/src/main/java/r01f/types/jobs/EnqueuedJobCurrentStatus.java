package r01f.types.jobs;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Models a job enqueued to be background-processed 
 */
@MarshallType(as="enqueuedJobStatus")
@Accessors(prefix="_")
public class EnqueuedJobCurrentStatus 
     extends EnqueuedJobBase {

	private static final long serialVersionUID = -3386912588270099640L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * any data about the progress
	 * (could be a percentage or a concrete number)
	 */
	@MarshallField(as="progress")
	@Getter @Setter private long _progress;
	/**
	 * any data about the remaining data to be processed
	 * (could be a percentage or a concrete number... obviously if it's a percentage, 
	 *  if progress=80, then remaining=20) 
	 */
	@MarshallField(as="remaining")
	@Getter @Setter private long _remaining;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public EnqueuedJobCurrentStatus() {
		// nothing
	}
	public EnqueuedJobCurrentStatus(final EnqueuedJobOID jobOid,final Date enqueuedDate,
									final EnqueuedJobStatus status,
									final long progress,final long remaining,
									final String detail) {
		super(jobOid,enqueuedDate,
			  status,
			  detail);
		_progress = progress;
		_remaining = remaining;
	}
	public EnqueuedJobCurrentStatus(final EnqueuedJob job,
									final EnqueuedJobStatus status,
									final long progress,final long remaining,
									final String detail) {
		super(job.getOid(),job.getEnqueuedTimeStamp(),
			  status);
		_progress = progress;
		_remaining = remaining;
		_detail = detail;
		
	}
			
}
