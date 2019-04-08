package r01f.model.metadata;

import java.util.Date;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;

@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.HAS_TRACKING_INFO_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Un objeto de modelo que tiene información de tracking"),
						@DescInLang(language=Language.BASQUE, value="[eu] A model object that has tracking info"),
						@DescInLang(language=Language.ENGLISH, value="A model object that has tracking info")
			     })
@GwtIncompatible
public interface HasMetaDataForHasTrackableFacetForModelObject
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
		CREATE_DATE ("createDate"),
		LAST_UPDATE_DATE ("lastUpdateDate"),
		CREATOR ("creator"),
		LAST_UPDATOR ("lastUpdator");
		
		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Fecha de creación"),
							@DescInLang(language=Language.BASQUE, value="[eu] Fecha de creación"),
							@DescInLang(language=Language.ENGLISH, value="Create date")
					  },
					  storage = @Storage(indexed=true,tokenized=false,
					  					 stored=true))
	public Date getCreateDate();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Fecha de última actualización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Fecha de última actualización"),
							@DescInLang(language=Language.ENGLISH, value="Last update date")
					  },
					  storage = @Storage(indexed=true,tokenized=false,
					  					 stored=true))
	public Date getLastUpdateDate();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Usuario/a que ha creado el objeto"),
							@DescInLang(language=Language.BASQUE, value="[eu] Usuario/a que ha creado el objeto"),
							@DescInLang(language=Language.ENGLISH, value="The user who created the model object")
					  },
					  storage = @Storage(indexed=true,tokenized=false,
							  			 stored=true))
	public UserCode getCreator();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Usuario/a que ha actualizado el objeto por última vez"),
							@DescInLang(language=Language.BASQUE, value="[eu] Usuario/a que ha actualizado el objeto por última vez"),
							@DescInLang(language=Language.ENGLISH, value="The user who last updated the model object")
					  },
					  storage = @Storage(indexed=true,tokenized=false,
							  			 stored=true))
	public UserCode getLastUpdator();
}
