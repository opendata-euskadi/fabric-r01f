package r01f.s3.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.s3.S3ObjectKey;
import r01f.util.types.Strings;

/**
 * A successfully deleted object.
 */
@NoArgsConstructor
@Accessors(prefix="_")
public class DeleteResult
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Setter @Getter private S3ObjectKey _key;
    @Setter @Getter private String _versionId;
    @Setter @Getter private boolean _deleteMarker;
    @Setter @Getter private String _deleteMarkerVersionId;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n Delete Result :{  ");
	    outDbgInfo.append(Strings.customized("\n   key {}",_key ));
		outDbgInfo.append(" }\n")	;
	    return outDbgInfo.toString();
	}
}
