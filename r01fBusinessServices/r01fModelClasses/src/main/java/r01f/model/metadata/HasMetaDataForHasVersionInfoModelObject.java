package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_VERSION_INFO_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene información de versionado"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has version info"),
						@DescInLang(language=Language.ENGLISH, value="A model object that has version info")
			     })
@GwtIncompatible
public interface HasMetaDataForHasVersionInfoModelObject<O extends VersionIndependentOID,V extends VersionOID>
		 extends HasFieldsMetaData {
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTANTS
// 	Usually it's a bad practice to put constants at interfaces since they're exposed
// 	alongside with the interface BUT this time this is the deliberately desired behavior
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA 
	 implements FieldIDToken {
		VERSION_INDEPENDENT_OID("versionIndependentOid"),
		VERSION("versionOid");
		
		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único independiente de la versión"),
							@DescInLang(language=Language.BASQUE, value="[eu] Identificador único independiente de la versión"),
							@DescInLang(language=Language.ENGLISH, value="Version-independent unique identifier")
				   	  })
	public O getVersionIndependentOid();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de la versión"),
							@DescInLang(language=Language.BASQUE, value="[eu] Identificador único de la versión"),
							@DescInLang(language=Language.ENGLISH, value="Version unique identifier")
				   	  })
	public V getVersionOid();
	
}
