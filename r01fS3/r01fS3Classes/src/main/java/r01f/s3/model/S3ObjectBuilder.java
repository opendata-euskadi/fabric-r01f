package r01f.s3.model;

import java.io.InputStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;
import r01f.s3.model.metadata.ObjectMetaData;
import r01f.types.url.UrlPath;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class S3ObjectBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static S3ObjectBuilderKeySetterStep create() {
		S3Object request = new S3Object();
		return new S3ObjectBuilder() { /* nothing */ }
					.new S3ObjectBuilderKeySetterStep(request);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderLastStep {
		private final S3Object _object;

		public S3Object build() {
			return _object;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class S3ObjectBuilderKeySetterStep {
		private final S3Object _object;
		public S3ObjectBuilderBucketSetterStep forObject(final S3ObjectKey key) {
			_object.setKey(key);
			return new S3ObjectBuilderBucketSetterStep(_object);
		 }
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class S3ObjectBuilderBucketSetterStep {
		private final S3Object _object;
		public S3ObjectBuilderContentStep onBucket(final S3BucketName name) {
			_object.setBucketName(name);
			return new S3ObjectBuilderContentStep(_object);
		 }
		public S3ObjectBuilderContentStep noVersion() {
			return new S3ObjectBuilderContentStep(_object);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class S3ObjectBuilderContentStep {
		private final S3Object _object;

		public S3ObjectBuilderMetadataStep withContent(final InputStream stream) {
			_object.setObjectContent(stream);
			return new S3ObjectBuilderMetadataStep(_object);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
   	public class S3ObjectBuilderMetadataStep {
       	private final S3Object _object;

   		public S3ObjectBuilderRedirectURLStep withMetadata(final ObjectMetaData data){
   			_object.setMetadata(data);
   			return new S3ObjectBuilderRedirectURLStep(_object);
   		}
   		public S3ObjectBuilderRedirectURLStep withNoMetadata(){
   			return new S3ObjectBuilderRedirectURLStep(_object);
   		}
   	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class S3ObjectBuilderRedirectURLStep {
		private final S3Object _object;

		public S3ObjectBuilderTaggingStep andRedirectLocation(final UrlPath url){
			if ( url !=  null ) {
				_object.setRedirectLocation(url);
			}
			return new S3ObjectBuilderTaggingStep(_object);
		}
		public S3Object build(){
   			return new BuilderLastStep(_object).build();
   		}
	}
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class S3ObjectBuilderTaggingStep {
    	private final S3Object _object;

		public S3ObjectBuilderRequestChargedStep taggingCount(final Integer taggingCount){
			_object.setTaggingCount(taggingCount);
			return new S3ObjectBuilderRequestChargedStep(_object);
		}
	}
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
   	public class S3ObjectBuilderRequestChargedStep {
       	private final S3Object _object;

   		public BuilderLastStep isCharged(final boolean requestCharged){
   			_object.setRequesterCharged(requestCharged);
   			return new BuilderLastStep(_object);
   		}
   	}
}
