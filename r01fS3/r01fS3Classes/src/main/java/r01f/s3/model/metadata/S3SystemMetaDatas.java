package r01f.s3.model.metadata;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.s3.S3ObjectMetadataItemId;

//////////////////////////////////////////////////////////////////////////
/// SOME COMMON IDENTIFIED OBJECT METADATA ITEMS
///////////////////////////////////////////////////////////////////////////
 @Accessors(prefix="_")
 @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
 public enum S3SystemMetaDatas
  implements EnumWithCode<S3ObjectMetadataItemId,S3SystemMetaDatas> {

		CACHE_CONTROL("Cache-Control"),
		CONTENT_DISPOSITION("Content-Disposition"),
		CONTENT_ENCODING("Content-Encoding"),
		CONTENT_LENGTH("Content-Length"),
		CONTENT_RANGE("Content-Range"),
		CONTENT_MD5("Content-MD5"),
		CONTENT_TYPE("Content-Type"),
		CONTENT_LANGUAGE("Content-Language"),
		DIMENSION("dimension");												// XXX: Image metadata

		@Getter private final Class<S3ObjectMetadataItemId> _codeType = S3ObjectMetadataItemId.class;
		@Getter private final S3ObjectMetadataItemId _code;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
		S3SystemMetaDatas(final String codeAsString){
			this(S3ObjectMetadataItemId.forId(codeAsString));
		}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
		private static EnumWithCodeWrapper<S3ObjectMetadataItemId,S3SystemMetaDatas> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(S3SystemMetaDatas.class);
		@Override
		public boolean isIn(final S3SystemMetaDatas... els) {
			return WRAPPER.isIn(this,els);

		}
		@Override
		public boolean is(final S3SystemMetaDatas el) {
			return WRAPPER.is(this,el);
		}
/////////////////////////////////////////////////////////////////////////////////////////
// 	exposed methods
/////////////////////////////////////////////////////////////////////////////////////////
		public static S3SystemMetaDatas from(final String codeAsString) {
			return WRAPPER.fromCode(S3ObjectMetadataItemId.forId(codeAsString));
		}
		public static S3SystemMetaDatas fromName(final String name) {
			return WRAPPER.fromName(name);
		}
		public Collection<S3ObjectMetadataItemId> codes() {
			return WRAPPER.codes();
		}
	}