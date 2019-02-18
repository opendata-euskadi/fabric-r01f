package r01f.filestore.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum FileStoreType
 implements EnumWithCode<String,FileStoreType> {
	LOCAL("local"),
	HDFS("hdfs"),
	TEAM_SITE("team-site"),
	KEY_VALUE("key-value");
	
	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumWithCodeWrapper<String,FileStoreType> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(FileStoreType.class);

	@Override
	public boolean isIn(final FileStoreType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final FileStoreType el) {
		return WRAPPER.is(this,el);
	}
	public boolean isNOTIn(final FileStoreType... els) {
		return WRAPPER.isNOTIn(this,els);
	}
	public boolean isNOT(final FileStoreType el) {
		return WRAPPER.isNOT(this,el);
	}
	
}
