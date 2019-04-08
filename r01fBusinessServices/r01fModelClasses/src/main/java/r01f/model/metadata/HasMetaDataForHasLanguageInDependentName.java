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

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_LANGUAGE_DEPENDENT_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene un nombre independiente del idioma"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has a language-independent name"),
						@DescInLang(language=Language.ENGLISH, value="A model object that has a language-independent name")
			     })
@GwtIncompatible
@Accessors(prefix="_")
public interface HasMetaDataForHasLanguageInDependentName
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
		NAME ("name");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Nombre"),
							@DescInLang(language=Language.BASQUE, value="[eu] Name"),
							@DescInLang(language=Language.ENGLISH, value="Name")
					  },
					  storage = @Storage(indexed=true,tokenized=true,
							  			 stored=true))
	public String getName();
}
