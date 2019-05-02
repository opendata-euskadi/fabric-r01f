package r01f.s3.model.metadata;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.patterns.IsBuilder;
import r01f.s3.S3ObjectMetadataItemId;
import r01f.s3.model.metadata.ObjectMetaData;
import r01f.s3.model.metadata.ObjectMetaDataItem;
import r01f.s3.model.metadata.S3SystemMetaDatas;

/**
 *
 * ObjectMetadaBuilders.
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ObjectMetaDataBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static ObjectMetaDataBuilderNameSetterStep create() {
		ObjectMetaData metadata = new ObjectMetaData();
		return new ObjectMetaDataBuilder() { /* nothing */ }
					.new ObjectMetaDataBuilderNameSetterStep(metadata);
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderLastStep {
		private final ObjectMetaData _metadata;

		public ObjectMetaData build() {
			return _metadata;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	@Accessors(prefix="_")
	public class ObjectMetaDataBuilderNameSetterStep {
		@Getter @Setter private  ObjectMetaData _object;
		@Getter @Setter private  String _name;
		@Getter @Setter private  boolean  _isForSystem;

		public ObjectMetaDataBuilderNameSetterStep(ObjectMetaData object) {
			_object = object;
		}
		public ObjectMetaData build() {
			return new BuilderLastStep(_object).build();
		}

		public ObjectMetaDataBuilderFieldValueSetterStep with(final S3SystemMetaDatas common) {
			setForSystem(true);
			setName(common.getCode().asString());
			return  new ObjectMetaDataBuilderFieldValueSetterStep(this);
		}
		public ObjectMetaDataBuilderFieldValueSetterStep withCustom(final String forName) {
			setForSystem(false);
			setName(forName);
			return  new ObjectMetaDataBuilderFieldValueSetterStep(this);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ObjectMetaDataBuilderFieldValueSetterStep {
		private final ObjectMetaDataBuilderNameSetterStep _fieldNameSetter;

		public ObjectMetaDataBuilderNameSetterStep ofValue(final String value) {
			ObjectMetaDataItem item = new ObjectMetaDataItem() ;
			item.setId(S3ObjectMetadataItemId.forId(_fieldNameSetter.getName()));
			item.setValue(value);
			item.setUserDefinedCustomMetadata(!_fieldNameSetter.isForSystem());
			_fieldNameSetter.getObject().put(item);
			return _fieldNameSetter;
		}
	}
}
