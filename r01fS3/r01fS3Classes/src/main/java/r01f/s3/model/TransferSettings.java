
package r01f.s3.model;

import java.text.DecimalFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;
import static r01f.util.types.BinaryUnitsUtils.*;

@NoArgsConstructor
@Accessors(prefix="_")
public class TransferSettings
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	 /** Default minimum part size for upload parts. */
    static final long DEFAULT_MINIMUM_UPLOAD_PART_SIZE = 5 * MB;

    /** Default size threshold for when to use multipart uploads.  */
    static final long DEFAULT_MULTIPART_UPLOAD_THRESHOLD = 16 * MB;

    /** Default size threshold for Amazon S3 object after which multi-part copy is initiated. */

    static final long DEFAULT_MULTIPART_COPY_THRESHOLD = 5 * GB;

    /** Default minimum size of each part for multi-part copy. */
    static final long DEFAULT_MINIMUM_COPY_PART_SIZE = 100 * MB;

    /** Shut down thead pools */
    static final boolean SHUT_DOWN_THEAD_POOLS = true;

    static final boolean DISABLE_PARALLEL_DOWNLOADS = true;

    static int DEFAULT_THREAD_POOL_SIZE = 10;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private Long _minimumUploadPartSize;
    @Getter @Setter private Long _multipartUploadThreshold;

    @Getter @Setter private Long _multipartCopyThreshold;
    @Getter @Setter private Long _multipartCopyPartSize;

    @Getter @Setter private Boolean _shutDownThreadPools;
    @Getter @Setter private Boolean _disableParallelDownloads;

    @Getter @Setter private int _threadPoolSize;

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
//////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String asHumanReadableSize(final long size) {
	    if (size <  KB)                 return _floatForm(        size     ) + " byte";
        if (size >= KB && size < MB)    return _floatForm((double)size / KB) + " KB";
        if (size >= MB && size < GB)    return _floatForm((double)size / MB) + " MB";
        if (size >= GB  )    return _floatForm((double)size / GB) + " GB";
        return "???";
	}
 	private static String _floatForm (double d)   {
       return new DecimalFormat("#.##").format(d);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n Transfer Settings :{ \n ");
	    outDbgInfo.append(Strings.customized("minimumUploadPartSize {}",
	    		                                                    asHumanReadableSize(_minimumUploadPartSize) ));

	    outDbgInfo.append("\n").append(Strings.customized(" shutDownThreadPools : {}",
	    		                                                                  _shutDownThreadPools));
	    outDbgInfo.append("\n").append(Strings.customized(" multipartUploadThreshold : {}",
	    	                                                    					asHumanReadableSize ( _multipartUploadThreshold)));
	    outDbgInfo.append("\n").append(Strings.customized(" multipartCopyThreshold : {}",
	    																			asHumanReadableSize(_multipartCopyPartSize)));
	    outDbgInfo.append("\n").append(Strings.customized(" disableParallelDownloads : {}", _disableParallelDownloads));
		outDbgInfo.append(" } \n")	;
	    return outDbgInfo.toString();
	}
}

