package r01f.api.interfaces.s3.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.ExecutorFactory;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.s3.S3ServiceForMultipartOperationsHighLevel;
import r01f.s3.S3ProgressListener;
import r01f.s3.S3ProgressListener.S3ProgressEvent;
import r01f.s3.model.GetRequest;
import r01f.s3.model.PutRequest;
import r01f.s3.model.TransferSettings;

/**
 * The AWS SDK for Java exposes a high-level API, called TransferManager, that simplifies multipart uploads
 *  (see Uploading Objects Using Multipart Upload API).  You can upload data from a file or a stream.
 *  You can also set advanced options, such as the part size you want to use for the multipart upload,
 *  or the number of concurrent threads you want to use when uploading the parts.
 *  You can also set optional object properties, the storage class, or the ACL.
 *   You use the PutObjectRequest and the TransferManagerConfiguration classes to set these advanced options.
 * @author  PCI
 *
 */
@Slf4j
public class S3ServiceForMultipartOperationsHighLevelImpl
	 extends S3ServiceBaseImpl
  implements S3ServiceForMultipartOperationsHighLevel {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public S3ServiceForMultipartOperationsHighLevelImpl(final AmazonS3 s3Client){
		 super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PUT OBJECTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void putObject(final PutRequest uploadRequest){
		log.warn("Upload request with settings {}",
				 uploadRequest.getOperationSettings().debugInfo());

		TransferManager transferManager =
				_buildTransferManagerFrom(uploadRequest.getOperationSettings()
													   .getTranferConfig());

		Upload myUpload = transferManager.upload(uploadRequest.getBucketName().asString(),
												  uploadRequest.getKey().asString(),
												  uploadRequest.getFile());

		// You can poll your transfer's status to check its progress
		if (myUpload.isDone() == false) {
		       log.warn("Transfer: {}",myUpload.getDescription());
		       log.warn("  - State: {}",myUpload.getState());
		       log.warn("  - Progress {}",myUpload.getProgress().getBytesTransferred());
		 }

		 ProgressListener myProgressListener = new ProgressListenerImpl(transferManager,
				 														uploadRequest.getOperationSettings()
				                                                                     .getProgressListener());

		// Transfers also allow you to set a <code>ProgressListener</code> to receive
		// asynchronous notifications about your transfer's progress.
		 myUpload.addProgressListener(myProgressListener);

		// Or you can block the current thread and wait for your transfer to
		// to complete. If the transfer fails, this method will throw an
		// AmazonClientException or AmazonServiceException detailing the reason.
		try {
			myUpload.waitForCompletion();
		} catch (final AmazonServiceException e) {
			e.printStackTrace();
		} catch (final AmazonClientException e){
			e.printStackTrace();
		} catch (final Throwable e){
			e.printStackTrace();
		}
		// After the upload is complete, call shutdownNow to release the resources.
		//tx.shutdownNow();
	}

	@Override
	public void getObject(final GetRequest downloadRequest){
		log.warn("Download request with settings {}",
				  downloadRequest.getOperationSettings().debugInfo());

		TransferManager transferManager =
				_buildTransferManagerFrom(downloadRequest.getOperationSettings().getTranferConfig());


		Download myDownload = transferManager.download(downloadRequest.getBucketName().asString(),
												       downloadRequest.getKey().asString(),
												       downloadRequest.getFile());

		// You can poll your transfer's status to check its progress
		if (myDownload.isDone() == false) {
		       log.warn("Transfer: " + myDownload.getDescription());
		       log.warn("  - State: " + myDownload.getState());
		       log.warn("  - Progress: "
		                       + myDownload.getProgress().getBytesTransferred());
		 }

		 ProgressListener myProgressListener = new ProgressListenerImpl(transferManager,
				 														downloadRequest.getOperationSettings()
				                                                                       .getProgressListener());

		// Transfers also allow you to set a <code>ProgressListener</code> to receive
		// asynchronous notifications about your transfer's progress.
		 myDownload.addProgressListener(myProgressListener);

		// Or you can block the current thread and wait for your transfer to
		// to complete. If the transfer fails, this method will throw an
		// AmazonClientException or AmazonServiceException detailing the reason.
		try {
			myDownload.waitForCompletion();
		} catch (final AmazonServiceException e) {
			e.printStackTrace();
		} catch (final AmazonClientException e){
			e.printStackTrace();
		} catch (final Throwable e){
			e.printStackTrace();
		}
		// After the upload is complete, call shutdownNow to release the resources.
		//tx.shutdownNow();

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private TransferManager _buildTransferManagerFrom(final TransferSettings transferSettings) {
		return TransferManagerBuilder.standard()
			        .withS3Client(_s3Client)
			            .withMinimumUploadPartSize(transferSettings.getMinimumUploadPartSize())
			            .withMultipartUploadThreshold(transferSettings.getMultipartUploadThreshold())
			            .withMultipartCopyPartSize(transferSettings.getMultipartCopyPartSize())
			            .withMultipartCopyThreshold(transferSettings.getMultipartCopyThreshold())
			            .withDisableParallelDownloads(transferSettings.getDisableParallelDownloads())
			            .withShutDownThreadPools(transferSettings.getShutDownThreadPools())
			            .withExecutorFactory(new ExecutorFactory() {
												@Override
												public ExecutorService newExecutor() {
													return  Executors.newFixedThreadPool(transferSettings.getThreadPoolSize());
						}})
		            .build();
			}

	@RequiredArgsConstructor
    @Accessors(prefix="_")
	 static class ProgressListenerImpl
	   implements ProgressListener{

		@SuppressWarnings("unused")
		private final TransferManager _tx;
		private final S3ProgressListener _s3ProgressListenerImpl;

		@Override
		public void progressChanged(final ProgressEvent progressEvent) {
			   log.debug("\n\nTransferred bytes: {}",progressEvent.getBytesTransferred());
			   log.debug("\n\n Even Type: {}",progressEvent.getEventType());
			  /* if (progressEvent.getEventType()
					     .equals(ProgressEventType.CLIENT_REQUEST_SUCCESS_EVENT) ||
				   progressEvent.getEventType()
					      .equals(ProgressEventType.CLIENT_REQUEST_FAILED_EVENT)
					      ) {
				    log.warn("..................shutdown TransferManager");
				   _tx.shutdownNow();
			    }*/
			    _s3ProgressListenerImpl.progressChanged(S3ProgressEvent.valueOf(progressEvent.getEventType().toString()));
		}
	}

}
