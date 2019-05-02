package r01f.client.api.delegates.s3;

import com.amazonaws.services.s3.AmazonS3;

import r01f.api.interfaces.s3.S3ServiceForBuckets;
import r01f.api.interfaces.s3.impl.S3ServiceForBucketsImpl;
import r01f.s3.S3BucketName;

public class S3ClientAPIDelegateForBuckets
  implements S3ServiceForBuckets {
///////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	protected final S3ServiceForBucketsImpl _serviceForBucketsImpl;

///////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUTCTOR
///////////////////////////////////////////////////////////////////////////////////////////
	public S3ClientAPIDelegateForBuckets(final AmazonS3 s3Client){
		_serviceForBucketsImpl = new  S3ServiceForBucketsImpl(s3Client);
	}
///////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void createBucket(final S3BucketName bucketName) {
		_serviceForBucketsImpl.createBucket(bucketName);
	}
	@Override
	public void deleteBucket(final S3BucketName bucketName) {
		_serviceForBucketsImpl.deleteBucket(bucketName);
	}
	@Override
	public boolean existBucket(final S3BucketName bucketName) {
		return _serviceForBucketsImpl.existBucket(bucketName);
	}
	@Override
	public boolean notExistBucket(final S3BucketName bucketName) {
		return !_serviceForBucketsImpl.existBucket(bucketName);
	}
}
