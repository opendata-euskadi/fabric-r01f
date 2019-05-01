package r01f.client.api.s3;

import lombok.experimental.Accessors;
import r01f.client.api.delegates.s3.S3ClientAPIDelegateForBuckets;
import r01f.client.api.delegates.s3.S3ClientAPIDelegateForFolderFiler;
import r01f.client.api.delegates.s3.S3ClientAPIDelegateForObjects;
import r01f.client.api.delegates.s3.S3ClientAPIDelegateForOperationsHighLevel;
import r01f.client.api.delegates.s3.S3ClientAPIDelegateForOperationsLowLevel;


/**
 * Base type for every API implementation of S3API
 */
@Accessors(prefix="_")
public class S3ClientAPI
     extends S3ClientAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUCKETS SUB-APIs (created at the constructor)
/////////////////////////////////////////////////////////////////////////////////////////
	private final S3ClientAPIDelegateForBuckets _forBuckets;

/////////////////////////////////////////////////////////////////////////////////////////
//  S3 OBJECTS SUB-APIs (created at the constructor)
/////////////////////////////////////////////////////////////////////////////////////////
	private final S3ClientAPIDelegateForObjects _forObjects;

/////////////////////////////////////////////////////////////////////////////////////////
//	S3 FOLDER FILER SUB-APIs (created at the constructor)
/////////////////////////////////////////////////////////////////////////////////////////
	private final S3ClientAPIDelegateForFolderFiler  _forFolderFiler;

/////////////////////////////////////////////////////////////////////////////////////////
//  S3 HIGH LEVEL MULTIPART SUB-API :
/*	 * The AWS SDK for Java exposes a high-level API, called TransferManager, that simplifies multipart uploads
	 *  (see Uploading Objects Using Multipart Upload API).  You can upload data from a file or a stream.
	 *  You can also set advanced options, such as the part size you want to use for the multipart upload,
	 *  or the number of concurrent threads you want to use when uploading the parts.*/
/////////////////////////////////////////////////////////////////////////////////////////
	private final S3ClientAPIDelegateForOperationsHighLevel  _forMultipart;

/////////////////////////////////////////////////////////////////////////////////////////
//S3 LOW LEVEL MULTIPART SUB-API :
/*	The AWS SDK for Java exposes a low-level API that closely resembles the Amazon S3 REST API
 *  for multipart uploads (see Uploading Objects Using Multipart Upload API. Use the low-level API when you need to pause and resume multipart uploads, vary part sizes during the upload, or do not know the size of the upload data in advance.
	When you don't have these requirements, use the high-level */
	private final S3ClientAPIDelegateForOperationsLowLevel  _forMultipartLowLevel;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public S3ClientAPI(final S3ClientConfig clientConfiguration) {	// comes from injection

		super(clientConfiguration);
		_forBuckets = new S3ClientAPIDelegateForBuckets(_s3Client);
		_forObjects  = new S3ClientAPIDelegateForObjects (_s3Client);
		_forMultipart = new S3ClientAPIDelegateForOperationsHighLevel(_s3Client);
		_forMultipartLowLevel = new S3ClientAPIDelegateForOperationsLowLevel(_s3Client);
		_forFolderFiler = new S3ClientAPIDelegateForFolderFiler(_s3Client);

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-APIs
/////////////////////////////////////////////////////////////////////////////////////////
	public S3ClientAPIDelegateForBuckets forBuckets() {
		return _forBuckets;
	}
	public S3ClientAPIDelegateForObjects forObjects() {
		return _forObjects;
	}
	public S3ClientAPIDelegateForOperationsHighLevel forMultipart() {
		return _forMultipart;
	}
	public S3ClientAPIDelegateForOperationsLowLevel forMultipartLowLevel() {
		return _forMultipartLowLevel;
	}
	public S3ClientAPIDelegateForFolderFiler forFolderFiler() {
		return _forFolderFiler;
	}


}
