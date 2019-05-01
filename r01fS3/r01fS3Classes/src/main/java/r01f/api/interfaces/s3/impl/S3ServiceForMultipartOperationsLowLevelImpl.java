package r01f.api.interfaces.s3.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.s3.S3ServiceForMultipartOperationsLowLevel;
import r01f.s3.model.GetRequest;
import r01f.s3.model.PutRequest;

@Slf4j
public class S3ServiceForMultipartOperationsLowLevelImpl
	 extends S3ServiceBaseImpl
  implements S3ServiceForMultipartOperationsLowLevel {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public S3ServiceForMultipartOperationsLowLevelImpl(final AmazonS3 s3Client) {
		super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PUT OBJECTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void putObject(final PutRequest uploadRequest){
		log.debug(" Upload Object...low level impl");
		File file = uploadRequest.getFile();
        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB

	     // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
        // then, after each individual part has been uploaded, pass the list of ETags to
        // the request to complete the upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Initiate the multipart upload.
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(uploadRequest.getBucketName().asString(),
        																				uploadRequest.getKey().asString());
        InitiateMultipartUploadResult initResponse = _s3Client.initiateMultipartUpload(initRequest);

        // Upload the file parts.
        long filePosition = 0;
        for (int i = 1; filePosition < contentLength; i++) {
            // Because the last part could be less than 5 MB, adjust the part size as needed.
            partSize = Math.min(partSize, (contentLength - filePosition));

            // Create the request to upload a part.
            UploadPartRequest uploadPartRequest = new UploadPartRequest()
										                    .withBucketName(uploadRequest.getBucketName().asString())
										                    .withKey(uploadRequest.getKey().asString())
										                    .withUploadId(initResponse.getUploadId())
										                    .withPartNumber(i)
										                    .withFileOffset(filePosition)
										                    .withFile(file)
										                    .withPartSize(partSize);

            // Upload the part and add the response's ETag to our list.
            UploadPartResult uploadResult = _s3Client.uploadPart(uploadPartRequest);
            partETags.add(uploadResult.getPartETag());

            filePosition += partSize;
        }

        // Complete the multipart upload.
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(uploadRequest.getBucketName().asString(),
        		                                                                        uploadRequest.getKey().asString(),
        																				initResponse.getUploadId(),
        																				partETags);
        _s3Client.completeMultipartUpload(compRequest);

	}
	@Override
	public void getObject(final GetRequest downloadRequest){
		throw  new UnsupportedOperationException("No implemented yet");
	}

}
