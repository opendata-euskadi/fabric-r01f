package r01f.model.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.ModelObject;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.Storage;

/**
 * Interface for types that describes {@link ModelObject}s
 */
public interface HasMetaDataForPersistableModelObject<O extends OID> 
		 extends HasMetaDataForModelObject,
		 		 HasMetaDataForHasOIDModelObject<O>,
		 		 HasMetaDataForHasEntityVersionModelObject,
		 		 HasMetaDataForHasTrackableFacetForModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCHABLE METADATAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA
	 implements FieldIDToken {
		DOCID ("DOCID");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único del documento indexado"),
							@DescInLang(language=Language.BASQUE, value="[eu] Identificador único del documento indexado"),
							@DescInLang(language=Language.ENGLISH, value="Document unique identifier")
					  },
					  storage = @Storage(indexed=true, stored=true, tokenized=false))
	public OID getDocId();
}
