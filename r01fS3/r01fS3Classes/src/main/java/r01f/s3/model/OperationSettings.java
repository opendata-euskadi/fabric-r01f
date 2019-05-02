
package r01f.s3.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.s3.S3ProgressListener;
import r01f.util.types.Strings;


@NoArgsConstructor
@Accessors(prefix="_")
public class OperationSettings
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private TransferSettings _tranferConfig;
    @Getter @Setter private Boolean _wait;
    @Getter @Setter private S3ProgressListener _progressListener;
 /////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n Upload Config :{ \n ").append(Strings.customized(" Transfer Config {}",_tranferConfig.debugInfo() ));
	    outDbgInfo.append("\n").append(Strings.customized("_wait {}", _wait));
		outDbgInfo.append(" } \n")	;
	    return outDbgInfo.toString();
	}
}

