package r01f.client.api.delegates.s3;

import com.amazonaws.services.s3.AmazonS3;

import r01f.api.interfaces.s3.S3ServiceForMultipartOperationsLowLevel;
import r01f.api.interfaces.s3.impl.S3ServiceForMultipartOperationsLowLevelImpl;
import r01f.s3.model.GetRequest;
import r01f.s3.model.PutRequest;

public class S3ClientAPIDelegateForOperationsLowLevel
  implements S3ServiceForMultipartOperationsLowLevel {
///////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	protected final S3ServiceForMultipartOperationsLowLevelImpl  _serviceForMultipartOperationsLowLevel;

///////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUTCTOR
///////////////////////////////////////////////////////////////////////////////////////////
	public S3ClientAPIDelegateForOperationsLowLevel(final AmazonS3 s3Client) {
		_serviceForMultipartOperationsLowLevel = new  S3ServiceForMultipartOperationsLowLevelImpl(s3Client);
	}
///////////////////////////////////////////////////////////////////////////////////////////
//METHODS
///////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void putObject(PutRequest putRequest) {
		_serviceForMultipartOperationsLowLevel.putObject(putRequest);
	}
	@Override
	public void getObject(GetRequest downloadRequest) {
		_serviceForMultipartOperationsLowLevel.getObject(downloadRequest);
	}
}
