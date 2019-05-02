package r01f.s3.model;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.s3.S3ObjectKey;
import r01f.s3.model.metadata.ObjectMetaData;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class PutResultBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static PutResultBuilderKeySetterStep create() {
		PutResult request = new PutResult();
		return new PutResultBuilder() { /* nothing */ }
					.new PutResultBuilderKeySetterStep(request);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderLastStep {
		private final PutResult _result;

		public PutResult build() {
			return _result;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutResultBuilderKeySetterStep {
		private final PutResult _result;
		public PutResultBuilderVersionSetterStep forObject(final S3ObjectKey key) {
			_result.setKey(key);
			return new PutResultBuilderVersionSetterStep(_result);
		 }
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutResultBuilderVersionSetterStep {
		private final PutResult _result;
		public PutResultBuilderETAGStep withVersionId(final String versionId) {
			_result.setVersionId(versionId);
			return new PutResultBuilderETAGStep(_result);
		 }
		public PutResultBuilderETAGStep noVersion() {
			return new PutResultBuilderETAGStep(_result);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutResultBuilderETAGStep {
		private final PutResult _result;

		public PutResultBuilderContentMD5Step etag(final String eTag) {
			_result.setETag(eTag);
			return new PutResultBuilderContentMD5Step(_result);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutResultBuilderContentMD5Step {
		private final PutResult _result;

		public PutResultBuilderExpirationStep contentMD5(final String contentMD5){
			_result.setContentMd5(contentMD5);
			return new PutResultBuilderExpirationStep(_result);
		}
	}
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PutResultBuilderExpirationStep {
    	private final PutResult _result;

		public PutResultBuilderMetadataStep andExpirationTime(final Date exp){
			_result.setExpirationTime(exp);
			return new PutResultBuilderMetadataStep(_result);
		}
	}
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
   	public class PutResultBuilderMetadataStep {
       	private final PutResult _result;

   		public BuilderLastStep withMetadata(final ObjectMetaData data){
   			_result.setMetadata(data);
   			return new BuilderLastStep(_result);
   		}
   	}
}
