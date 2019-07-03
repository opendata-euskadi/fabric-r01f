package r01f.api.interfaces.s3.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.s3.S3ServiceForObjects;
import r01f.exceptions.Throwables;
import r01f.io.Streams;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;
import r01f.s3.model.DeleteResult;
import r01f.s3.model.DeleteResultBuilder;
import r01f.s3.model.PutResult;
import r01f.s3.model.PutResultBuilder;
import r01f.s3.model.S3Object;
import r01f.s3.model.S3ObjectBuilder;
import r01f.s3.model.metadata.ObjectMetaData;
import r01f.s3.model.metadata.ObjectMetaDataTransformer;
import r01f.types.url.UrlPath;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class S3ServiceForObjectsImpl
	 extends S3ServiceBaseImpl
  implements S3ServiceForObjects {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public S3ServiceForObjectsImpl(final AmazonS3 s3Client)  {
		super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// PUT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PutResult putObject(final S3BucketName bucketName,final S3ObjectKey key,
							   final InputStream stream){
		return putObject(bucketName,key,stream,null);
	}
	@Override
	public PutResult putObject(final S3BucketName bucketName,final S3ObjectKey key,
							   final InputStream streamToUpload ,final ObjectMetaData objectMetadata){
		try {			
			if (streamToUpload == null) {
				throw new IllegalArgumentException(Strings.customized(" The object of key  {}  CANNOT be null to store!!!", key.asString()));
			}
			byte[]  contentBytes = Streams.inputStreamBytes(streamToUpload); // This Streams.inputStreamBytes method closes the stream.
		    ByteArrayInputStream  stream = new ByteArrayInputStream(contentBytes);
			log.warn(" > Put input stream  {}  of size {} on bucket {}",
					 key,  contentBytes.length, bucketName );
			
			// All systems compatible with S3 should provide a metadata system, but there are some that don't, f.e  MINIO
			ObjectMetadata metadata = null;
			if (objectMetadata != null
			 && CollectionUtils.hasData(objectMetadata.getItems())) {
				log.warn(" Metadata externally provided {}",
						 objectMetadata.debugInfo());
				metadata = ObjectMetaDataTransformer.toS3ObjectMetaData(objectMetadata);
			} else {			
				metadata = new ObjectMetadata();
			}
		    metadata.setContentLength( contentBytes.length);
			PutObjectResult result = _s3Client.putObject(new PutObjectRequest(bucketName.asString(),key.asString(),
																			  stream, metadata));
			return PutResultBuilder.create()
									   .forObject(key)
						               .withVersionId(result.getVersionId())
						               .etag(result.getETag())
						               .contentMD5(result.getContentMd5())
						               .andExpirationTime(result.getExpirationTime())
						               .withMetadata(ObjectMetaDataTransformer.fromS3ObjectMetaData(result.getMetadata()))
					               .build();

		} catch (final Throwable e) {
			log.error( " Error {}  creating object of key '{}'  on bucket '{}'" , e.getLocalizedMessage(), key, bucketName);
			throw Throwables.throwUnchecked((Exception) e);
		}
	}

	@Override
	public PutResult putObject(final S3BucketName bucketName,final S3ObjectKey key,
							  final File file) {
		if (file == null) {
			throw new IllegalArgumentException(Strings.customized(" The object of key  {} "
																	+ " CANNOT be null to store!!!", key.asString()));
		}
		log.warn("Put file {} on bucket {}", key, bucketName );
		PutObjectResult result =  _s3Client.putObject(new PutObjectRequest(bucketName.asString(),key.asString(),file));

		return PutResultBuilder.create()
								.forObject(key)
					               .withVersionId(result.getVersionId())
					               .etag(result.getETag())
					               .contentMD5(result.getContentMd5())
					               .andExpirationTime(result.getExpirationTime())
					               .withMetadata(ObjectMetaDataTransformer.fromS3ObjectMetaData(result.getMetadata()))
				               .build();

	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	GET METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public S3Object getObject(final S3BucketName bucketName,final S3ObjectKey key) {
		com.amazonaws.services.s3.model.S3Object remoteObject  = null;
		try {
			GetObjectRequest objectRequest = new GetObjectRequest(bucketName.asString(), key.asString());
			remoteObject = _s3Client.getObject(objectRequest);
			
			//Content reading.
			byte[]  contentBytes = Streams.inputStreamBytes(remoteObject.getObjectContent());
		    ByteArrayInputStream stream = new ByteArrayInputStream(contentBytes);
	
			return S3ObjectBuilder.create()
		               .forObject(key)
		               .onBucket(bucketName)
		               .withContent(stream)
		               .withMetadata(ObjectMetaDataTransformer.fromS3ObjectMetaData(remoteObject.getObjectMetadata()))
		               .andRedirectLocation( (remoteObject.getRedirectLocation() != null) ?
		            		   													 UrlPath.from(remoteObject.getRedirectLocation()) : null)
		               .taggingCount(remoteObject.getTaggingCount())
	
		               .isCharged(remoteObject.isRequesterCharged())
	               .build();
		
		} catch (final IOException e) {			
			e.printStackTrace();
			log.error( " Error {}  loading object of key '{}'  on bucket '{}'" , e.getLocalizedMessage(), key, bucketName);
			throw Throwables.throwUnchecked(e);
		} finally {
			if ( remoteObject != null ) {
				try {
					remoteObject.close();
				} catch (final IOException e){				
					e.printStackTrace();
				}
			}
			
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DELETE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public DeleteResult deleteObject(final S3BucketName bucketName,final S3ObjectKey key){
		DeleteObjectsRequest deleteObjectsRequest = (new DeleteObjectsRequest(bucketName.toString())
																.withKeys(key.asString()));
		DeleteObjectsResult results = _s3Client.deleteObjects(deleteObjectsRequest);
		if (results.getDeletedObjects().size() > 1 ) {
			throw new  IllegalStateException(Strings.customized(" Deleted objects more " +
															    " than one for key  {} ",key.asString()));

		} else {
			DeletedObject deletedObject = results.getDeletedObjects().get(0);
			return DeleteResultBuilder.create()
										  .forObject(S3ObjectKey.forId(deletedObject.getKey()))
										  .withVersionId(deletedObject.getVersionId())
										  .deleteMarker(deletedObject.isDeleteMarker())
										  .withDeleteMarkerId(deletedObject.getDeleteMarkerVersionId())
									  .build();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS AND INNER CLASSES
/////////////////////////////////////////////////////////////////////////////////////////
}
