package r01f.client.api.delegates.s3;

import java.io.File;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;

import r01f.api.interfaces.s3.S3ServiceForObjects;
import r01f.api.interfaces.s3.impl.S3ServiceForObjectsImpl;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;
import r01f.s3.model.DeleteResult;
import r01f.s3.model.PutResult;
import r01f.s3.model.S3Object;
import r01f.s3.model.metadata.ObjectMetaData;

public class S3ClientAPIDelegateForObjects
  implements S3ServiceForObjects {
///////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	protected final S3ServiceForObjectsImpl _serviceForObjectsImpl;

///////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUTCTOR
///////////////////////////////////////////////////////////////////////////////////////////
	public S3ClientAPIDelegateForObjects(final AmazonS3 s3Client){
		_serviceForObjectsImpl = new  S3ServiceForObjectsImpl(s3Client);
	}
///////////////////////////////////////////////////////////////////////////////////////////
//METHODS
///////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PutResult putObject(final S3BucketName bucketName,final S3ObjectKey key,
							   final InputStream stream) {
		return _serviceForObjectsImpl.putObject(bucketName,key,
												stream);
	}
	@Override
	public PutResult putObject(final S3BucketName bucketName,final S3ObjectKey key,
							   final InputStream stream,final ObjectMetaData objectMetada) {
		return _serviceForObjectsImpl.putObject(bucketName,key,
												stream,objectMetada);
	}
	@Override
	public PutResult putObject(final S3BucketName bucketName,final  S3ObjectKey key,
							   final  File file) {
		return _serviceForObjectsImpl.putObject(bucketName,key,
												file);
	}
	@Override
	public S3Object getObject(final S3BucketName bucketName,final S3ObjectKey key) {
		return _serviceForObjectsImpl.getObject(bucketName,key);
	}
	@Override
	public DeleteResult deleteObject(final S3BucketName bucketName,final S3ObjectKey key) {
		return _serviceForObjectsImpl.deleteObject(bucketName,key);
	}
}
