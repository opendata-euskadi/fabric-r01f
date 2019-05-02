package r01f.s3.model;

import java.io.File;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class GetRequestBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static GetRequestBuilderSettingsStep create() {
		GetRequest request = new GetRequest();
		return new GetRequestBuilder() { /* nothing */ }
					.new GetRequestBuilderSettingsStep(request);
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderLastStep {
		private final GetRequest _request;

		public GetRequest build() {
			return _request;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class GetRequestBuilderSettingsStep {
		private final GetRequest _request;

		public GetRequestBuilderBucketStep usingOperationsSettings(final OperationSettings operationSettings) {
			_request.setOperationSettings(operationSettings);
			return new GetRequestBuilderBucketStep(_request);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class GetRequestBuilderBucketStep {
		private final GetRequest _request;

		public GetRequestBuilderObjectNameStep onBucket(final S3BucketName bucketName) {

			_request.setBucketName(bucketName);
			return new GetRequestBuilderObjectNameStep(_request);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class GetRequestBuilderObjectNameStep {
		private final GetRequest _request;

		public GetRequestBuilderFileStep namedObject(final S3ObjectKey objectKey){
			_request.setKey(objectKey);
			return new GetRequestBuilderFileStep(_request);
		}
	}
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class GetRequestBuilderFileStep {
			private final GetRequest _request;

		public BuilderLastStep onFile(final File file){
			_request.setFile(file);
			return new BuilderLastStep(_request);
		}
	}
}
