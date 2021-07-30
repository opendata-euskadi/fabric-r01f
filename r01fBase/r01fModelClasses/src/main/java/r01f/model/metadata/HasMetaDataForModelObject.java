package r01f.model.metadata;

import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.ModelObject;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;

/**
 * Interface for types that describes {@link ModelObject}s
 */
@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Objeto del modelo"),
						@DescInLang(language=Language.BASQUE, value="[eu] Model object"),
						@DescInLang(language=Language.ENGLISH, value="Model object")
				 })
public interface HasMetaDataForModelObject 
		 extends HasFieldsMetaData {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCHABLE METADATAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA
	 implements FieldIDToken {
		TYPE_CODE ("typeCode"),
		TYPE ("javaType"),
		TYPE_FACETS ("typeFacets"),
		NUMERIC_ID ("typeNumericId");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	HasMetaDataForModelObject
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Tipo del objeto"),
							@DescInLang(language=Language.BASQUE, value="[eu] Tipo del objeto"),
							@DescInLang(language=Language.ENGLISH, value="Model object's Type")
					  },
				      storage = @Storage(indexed=true,
				      					 stored=true))
	public long getTypeCode();

	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Nombre del tipo del objeto"),
							@DescInLang(language=Language.BASQUE, value="[eu] Nombre del tipo del objeto"),
							@DescInLang(language=Language.ENGLISH, value="Model object's type's name")
					  },
					  storage = @Storage(indexed=false,
					  					 stored=true))
	public Class<?> getJavaType();

	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Facets del objeto"),
							@DescInLang(language=Language.BASQUE, value="[eu] Facets del objeto"),
							@DescInLang(language=Language.ENGLISH, value="Model object's Facets")
					  },
					  storage = @Storage(indexed=true,tokenized=false,
					  					 stored=true))
	public Collection<Long> getTypeFacets();

	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador numérico del objeto"),
							@DescInLang(language=Language.BASQUE, value="[eu] Identificador numérico del objeto"),
							@DescInLang(language=Language.ENGLISH, value="Model Object's numeric identifier")
					  },
					  storage = @Storage(indexed=true,
					  					 stored=true))
	public long getNumericId();
}
