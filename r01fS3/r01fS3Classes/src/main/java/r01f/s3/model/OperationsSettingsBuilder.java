package r01f.s3.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.s3.S3ProgressListener;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class OperationsSettingsBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static OperationsSettingsBuilderTransferSetterStep create() {
		OperationSettings transfer = new OperationSettings();
		return new OperationsSettingsBuilder() { /* nothing */ }
					.new OperationsSettingsBuilderTransferSetterStep(transfer);
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderLastStep {
		private final OperationSettings _uploadConfig;

		public OperationSettings build() {
			return _uploadConfig;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class OperationsSettingsBuilderTransferSetterStep {
		private final OperationSettings _uploadConfig;
		public OperationsSettingsBuilderWaitStep usingTransferSettings(final TransferSettings tranferConfig) {
			;
			_uploadConfig.setTranferConfig(tranferConfig);
			return new OperationsSettingsBuilderWaitStep(_uploadConfig);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class OperationsSettingsBuilderWaitStep {
		private final OperationSettings _uploadConfig;
		public OperationsSettingsBuilderProgressListenerStep waitUntilFinish() {
			_uploadConfig.setWait(true);
			return new OperationsSettingsBuilderProgressListenerStep(_uploadConfig);
		}
		public OperationsSettingsBuilderProgressListenerStep nowait() {
			_uploadConfig.setWait(false);
			return new OperationsSettingsBuilderProgressListenerStep(_uploadConfig);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class OperationsSettingsBuilderProgressListenerStep {
		private final OperationSettings _uploadConfig;

		public BuilderLastStep usingProgressListener(final S3ProgressListener progressListener){
			_uploadConfig.setProgressListener(progressListener);
			return new BuilderLastStep(_uploadConfig);
		}
	}
}
