package r01f.s3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;

@NoArgsConstructor
@Accessors(prefix="_")
public class S3ObjectSummaryItem
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	fields
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter S3BucketName _bucketName;
	@Getter @Setter S3ObjectKey _key;
	@Getter @Setter Boolean  _isFolder;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
	        return "S3ObjectSummary{" +
	                "bucketName='" + _bucketName + '\'' +
	                ", key='" + _key + '\'' +
	                ", isFolder='" + _isFolder + '\'' +
	                /*", eTag='" + eTag + '\'' +
	                ", size=" + size +
	                ", lastModified=" + lastModified +
	                ", storageClass='" + storageClass + '\'' +
	                ", owner=" + owner +*/
	                '}';
	    }
}
