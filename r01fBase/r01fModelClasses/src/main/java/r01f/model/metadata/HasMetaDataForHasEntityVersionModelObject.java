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

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_ENTITY_VERSION_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene identificador de version de la entidad"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has an entity version"),
						@DescInLang(language=Language.ENGLISH, value="A model object that has an entity version")
			     })
@GwtIncompatible
public interface HasMetaDataForHasEntityVersionModelObject
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
		ENTITY_VERSION ("entityVersion");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Versión persistida (BBDD) del objeto (optimistic locking)"),
							@DescInLang(language=Language.BASQUE, value="[eu] Versión persistida (BBDD) del objeto (optimistic locking)"),
							@DescInLang(language=Language.ENGLISH, value="Model object's persisted version (BBDD) (used for optimistic locking)")
					  },
					  storage = @Storage(indexed=false,tokenized=false,
					  					 stored=true))
	public long getEntityVersion();
}
