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

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_ID_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene un identificador de negocio"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has a business identifier"),
						@DescInLang(language=Language.ENGLISH, value="A model object that has a business identifier")
			     })
@GwtIncompatible
public interface HasMetaDataForHasIDModelObject<ID extends OID>
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
		ID ("id");
		
		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de negocio del objeto"),
							@DescInLang(language=Language.BASQUE, value="[eu] Model Object's unique business identifier"),
							@DescInLang(language=Language.ENGLISH, value="Model Object's unique business identifier")
					  },
					  storage = @Storage(indexed=true,tokenized=false,
							  			 stored=true))
	public ID getId();
}
