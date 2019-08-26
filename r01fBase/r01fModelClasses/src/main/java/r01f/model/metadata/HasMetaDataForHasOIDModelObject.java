package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_OID_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene un identificador �nico"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has an unique identifier"),
						@DescInLang(language=Language.ENGLISH, value="sA model object that has an unique identifier")
			     })
@GwtIncompatible
public interface HasMetaDataForHasOIDModelObject<O extends OID>
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
		OID ("oid");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único del objeto"),
							@DescInLang(language=Language.BASQUE, value="[eu] Identificador único del objeto"),
							@DescInLang(language=Language.ENGLISH, value="Model Object's unique identifier")
					  },
					  storage = @Storage(indexed=true,tokenized=false,
							  			 stored=true))
	public O getOid();
}
