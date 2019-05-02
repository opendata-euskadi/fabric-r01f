package r01f.s3.model;

import java.io.InputStream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;
import r01f.s3.model.metadata.ObjectMetaData;
import r01f.types.url.UrlPath;
import r01f.util.types.Strings;


@NoArgsConstructor
@Accessors(prefix="_")
public class S3Object
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	 /** The Key value of the new object */
	@Getter @Setter  private S3ObjectKey  _key;

    /** The name of the bucket in which this object is contained */
    @Getter @Setter  private S3BucketName  _bucketName = null;

    /** The metadata stored by Amazon S3 for this object */
    @Getter @Setter private ObjectMetaData _metadata;

    /** The stream containing the contents of this object from S3 */
    @Getter @Setter private transient InputStream _objectContent;

    /** The redirect location for this object */
    @Getter @Setter private UrlPath _redirectLocation;

    /** The tagging count */
    @Getter @Setter private Integer _taggingCount;
    /**
     * Indicates if the requester is charged for downloading the data from
     * Requester Pays Buckets.
     */
    @Getter @Setter  private boolean _isRequesterCharged;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n S3 Object Info :{  ");
	    outDbgInfo.append(Strings.customized("\n key {}",_key ));
	    outDbgInfo.append(Strings.customized("\n bucketName {}",_bucketName ));
	    outDbgInfo.append(Strings.customized("\n taggingCount {}",_taggingCount ));
	    outDbgInfo.append(Strings.customized("\n redirectLocation {}",_redirectLocation ));
	    outDbgInfo.append(Strings.customized("\n metadata {}",_metadata.debugInfo() ));
		outDbgInfo.append(" }\n")	;
	    return outDbgInfo.toString();
	}
}
