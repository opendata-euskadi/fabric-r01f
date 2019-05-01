package r01f.s3.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.s3.S3ObjectKey;
import r01f.s3.model.metadata.ObjectMetaData;
import r01f.util.types.Strings;


@NoArgsConstructor
@Accessors(prefix="_")
public class PutResult
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	  /** The Key value of the new object */
	@Getter @Setter  private S3ObjectKey  _key;
	 /**
     * The version ID of the new, uploaded object. This field will only be
     * present if object versioning has been enabled for the bucket to which the
     * object was uploaded.
     */
	@Getter @Setter  private String _versionId;

    /** The ETag value of the new object */
	@Getter @Setter  private String _eTag;

    /** The time this object expires, or null if it has no expiration */
	@Getter @Setter  private Date _expirationTime;

    /** The expiration rule for this object */
	@Getter @Setter  private String _expirationTimeRuleId;

    /** The content MD5 */
	@Getter @Setter   private String _contentMd5;

    /** The metadata returned as a result of PutObject operation.*/
	@Getter @Setter  private ObjectMetaData _metadata;

    /**
     * Indicate if the requester is charged for conducting this operation from
     * Requester Pays Buckets.
     */
	//@Getter @Setter  private boolean isRequesterCharged;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n Put Result :{  ");
	    outDbgInfo.append(Strings.customized("\n key {}",_key ));
	    outDbgInfo.append(Strings.customized("\n versionId {}",_versionId ));
	    outDbgInfo.append(Strings.customized("\n eTag {}",_eTag ));
	    outDbgInfo.append(Strings.customized("\n contentMd5 {}",_contentMd5 ));
	    outDbgInfo.append(Strings.customized("\n expirationTime {}",_expirationTime ));
	    outDbgInfo.append(Strings.customized("\n expirationTimeRuleId {}",_expirationTimeRuleId ));
	    outDbgInfo.append(Strings.customized("\n metadata {}",_metadata.debugInfo() ));
		outDbgInfo.append(" }\n")	;
	    return outDbgInfo.toString();
	}
}
