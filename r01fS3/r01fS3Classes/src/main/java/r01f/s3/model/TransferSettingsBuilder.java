package r01f.s3.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class TransferSettingsBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static TransferSettingsBuilderFieldSetterStep create() {
		TransferSettings transfer = new TransferSettings();
		return new TransferSettingsBuilder() { /* nothing */ }
					.new TransferSettingsBuilderFieldSetterStep(transfer);
	}
	public static TransferSettingsBuilderFieldSetterStep createWithDefaultValues() {
		TransferSettings transfer = new TransferSettings();
		transfer.setMinimumUploadPartSize(TransferSettings.DEFAULT_MINIMUM_UPLOAD_PART_SIZE);
		transfer.setMultipartUploadThreshold(TransferSettings.DEFAULT_MULTIPART_UPLOAD_THRESHOLD);
		transfer.setMultipartCopyPartSize(TransferSettings.DEFAULT_MINIMUM_UPLOAD_PART_SIZE);
		transfer.setMultipartCopyThreshold(TransferSettings.DEFAULT_MULTIPART_COPY_THRESHOLD);
		transfer.setDisableParallelDownloads(TransferSettings.DISABLE_PARALLEL_DOWNLOADS);
		transfer.setShutDownThreadPools(TransferSettings.SHUT_DOWN_THEAD_POOLS);
		transfer.setThreadPoolSize(TransferSettings.DEFAULT_THREAD_POOL_SIZE);

		return new TransferSettingsBuilder() { /* nothing */ }
					.new TransferSettingsBuilderFieldSetterStep(transfer);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class TransferSettingsBuilderLastStep {
		private final TransferSettings _transfer;

		public TransferSettings build() {
			return _transfer;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class TransferSettingsBuilderFieldSetterStep {
		private final TransferSettings _transfer;
		public TransferSettings build() {
			return new TransferSettingsBuilderLastStep(_transfer).build();
		}
		public TransferSettingsBuilderFieldSetterStep withMinimumUploadPartSize(final long value) {
			_transfer.setMinimumUploadPartSize(value);
			return this;
		}
		public TransferSettingsBuilderFieldSetterStep withMultipartUploadThreshold(final long value) {
			_transfer.setMultipartUploadThreshold(value);
			return this;
		}
		public TransferSettingsBuilderFieldSetterStep withMultipartCopyPartSize(final long value) {
			_transfer.setMultipartCopyPartSize(value);
			return this;
		}
		public TransferSettingsBuilderFieldSetterStep withMultipartCopyThreshold(final long value) {
			_transfer.setMultipartCopyThreshold(value);
			return this;
		}
		public TransferSettingsBuilderFieldSetterStep disablingParallelDownloads() {
			_transfer.setDisableParallelDownloads(true);
			return this;

		}
		public TransferSettingsBuilderFieldSetterStep enablingParallelDownloads() {
			_transfer.setDisableParallelDownloads(false);
			return this;
		}
		public TransferSettingsBuilderFieldSetterStep shutingDownThreadPools() {
			_transfer.setShutDownThreadPools(true);
			return this;
		}
		public TransferSettingsBuilderFieldSetterStep NotShutingDownThreadPools() {
			_transfer.setShutDownThreadPools(false);
			return this;
		}
		public TransferSettingsBuilderFieldSetterStep withThreadPoolSize(int thSize) {
			_transfer.setThreadPoolSize(thSize);
			return this;
		}
	}
}
