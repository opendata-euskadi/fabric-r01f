package r01f.s3.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.s3.S3ObjectKey;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DeleteResultBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static DeleteResultBuilderKeySetterStep create() {
		DeleteResult request = new DeleteResult();
		return new DeleteResultBuilder() { /* nothing */ }
					.new DeleteResultBuilderKeySetterStep(request);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderLastStep {
		private final DeleteResult _result;

		public DeleteResult build() {
			return _result;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class DeleteResultBuilderKeySetterStep {
		private final DeleteResult _result;

		public DeleteResultBuilderVersionStep forObject(final S3ObjectKey key) {
			_result.setKey(key);
			return new DeleteResultBuilderVersionStep(_result);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class DeleteResultBuilderVersionStep {
		private final DeleteResult _result;

		public DeleteResultBuilderDeleteMarkerStep withVersionId(final String versionId ) {
			_result.setVersionId(versionId);
			return new DeleteResultBuilderDeleteMarkerStep(_result);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class DeleteResultBuilderDeleteMarkerStep {
		private final DeleteResult _result;

		public DeleteResultBuilderFileStep deleteMarker(final boolean deleteMarker){
			_result.setDeleteMarker(deleteMarker);
			return new DeleteResultBuilderFileStep(_result);
		}
	}
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class DeleteResultBuilderFileStep {
		private final DeleteResult _result;

		public BuilderLastStep withDeleteMarkerId(final String deleteMarkerId){
			_result.setDeleteMarkerVersionId(deleteMarkerId);
			return new BuilderLastStep(_result);
		}
	}
}
