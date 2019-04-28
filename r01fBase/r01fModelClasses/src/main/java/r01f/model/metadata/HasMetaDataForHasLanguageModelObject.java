package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_LANGUAGE_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene lenguage"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has language"),
						@DescInLang(language=Language.ENGLISH, value="A model object that has language")
			     })
@GwtIncompatible
public interface HasMetaDataForHasLanguageModelObject 
		 extends HasFieldsMetaData {
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTANTS
// Usually it's a bad practice to put constants at interfaces since they're exposed
// alongside with the interface BUT this time this is the deliberately desired behavior
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static enum SEARCHABLE_METADATA 
			implements FieldIDToken {
		LANGUAGE ("language"),
		LANGUAGE_CODE ("languageCode");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Lenguage"),
							@DescInLang(language=Language.BASQUE, value="[eu] Lenguage"),
							@DescInLang(language=Language.ENGLISH, value="Language")
				   	  },
				   	  storage = @Storage(indexed=false,
				   					  	 stored=true))
	public Language getLanguage();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Código del Lenguage"),
							@DescInLang(language=Language.BASQUE, value="[eu] Código del lenguage"),
							@DescInLang(language=Language.ENGLISH, value="Language Code")
				   	  },
				   	  storage = @Storage(indexed=true,
				   					  	 stored=true))
	public int getLanguageCode();
}
