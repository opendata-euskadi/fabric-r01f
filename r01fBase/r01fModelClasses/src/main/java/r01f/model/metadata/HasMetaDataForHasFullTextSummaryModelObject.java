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
import r01f.types.summary.Summary;

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_FULL_TEXT_SUMMARY_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene un resumen para ser indexado a texto completo"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has full-text summary"),
						@DescInLang(language=Language.ENGLISH, value="A model object that has full-text summary")
			     })
@GwtIncompatible
public interface HasMetaDataForHasFullTextSummaryModelObject<S extends Summary>
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
		FULL_TEXT("fullTextSummary");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Resumen para indexación / búsqueda en texto libre"),
							@DescInLang(language=Language.BASQUE, value="[eu] Full-text index / search summary"),
							@DescInLang(language=Language.ENGLISH, value="Full-text index / search summary")
					  },
					  storage = @Storage(indexed=true,tokenized=true,
							  			 stored=false))
	public S getFullTextSummary();
}
