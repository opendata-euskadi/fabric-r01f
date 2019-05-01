package r01f.s3.model;

import java.io.File;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class PutRequestBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static PutRequestBuilderUploadConfigSetterStep create() {
		PutRequest request = new PutRequest();
		return new PutRequestBuilder() { /* nothing */ }
					.new PutRequestBuilderUploadConfigSetterStep(request);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderLastStep {
		private final PutRequest _request;

		public PutRequest build() {
			return _request;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutRequestBuilderUploadConfigSetterStep {
		private final PutRequest _request;
		public PutRequestBuilderBucketStep usingOperationsSettings(final OperationSettings operationSettings) {
			_request.setOperationSettings(operationSettings);
			return new PutRequestBuilderBucketStep(_request);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutRequestBuilderBucketStep {
		private final PutRequest _request;

		public PutRequestBuilderObjectNameStep onBucket(final S3BucketName bucketName) {
			_request.setBucketName(bucketName);
			return new PutRequestBuilderObjectNameStep(_request);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutRequestBuilderObjectNameStep {
			private final PutRequest _request;

		public PutRequestBuilderFileStep namedObject(final S3ObjectKey objectKey){
			_request.setKey(objectKey);
			return new PutRequestBuilderFileStep(_request);
		}
	}
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutRequestBuilderFileStep {
		private final PutRequest _request;

		public BuilderLastStep fromFile(final File file){
			_request.setFile(file);
			return new BuilderLastStep(_request);
		}
	}
}
