package r01f.s3.model.metadata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.s3.S3ObjectMetadataItemId;

@NoArgsConstructor
@Accessors(prefix="_")
public class ObjectMetaDataItem {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter S3ObjectMetadataItemId _id;
	@Getter @Setter String  _value;
	@Getter @Setter boolean _isUserDefinedCustomMetadata;
}
