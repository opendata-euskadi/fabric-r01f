package r01f.types.geo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.LangDependentNamed;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.facets.delegates.LangDependentNamedDelegate;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.locale.LanguageTextsWrapper;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCountry;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;
import r01f.types.summary.SummaryBuilder;
import r01f.util.types.Strings;

/**
 * Geo info base type
 * <pre>
 * Country
 *   |_Territory
 *   	 |_State
 *   		 |_County
 *   		 	|_Locality
 *   				|_Municipality
 *   					|_District
 *   						|_Street
 *   							|_portal
 * </pre>
 * @param <GID>
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public abstract class GeoLocationBase<GID extends GeoID,
								      SELF_TYPE extends GeoLocationBase<GID,SELF_TYPE>> 
           implements GeoLocation<GID>,
           			  HasSummaryFacet {

	private static final long serialVersionUID = -1497083216318413697L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * geo location oid
	 */
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GID _id;
	/**
	 * Official name
	 */
	@MarshallField(as="officialName") 
	@Getter @Setter private String _officialName;
	/**
	 * location name 
	 */
	@MarshallField(as="nameByLang")
	@Getter @Setter private LanguageTexts _nameByLanguage;
	/**
	 * Position 2D (lat/long)
	 */
	@MarshallField(as="geoPosition2D")
	@Getter @Setter private GeoPosition2D _position2D;
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE withNameInLang(final Language lang,final String name) {
		if (Strings.isNullOrEmpty(name)) return (SELF_TYPE)this;
		if (_nameByLanguage == null) _nameByLanguage = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL,null);
		_nameByLanguage.add(lang,name);
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withNameForAll(final String name) {
		_nameByLanguage = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL,null);
		for (Language lang : Language.values()) {
			_nameByLanguage.add(lang,name);
		}
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE positionedAt(final GeoPosition2D geoPosition) {
		_position2D = geoPosition;
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	@Getter private final transient LanguageTextsWrapper<SELF_TYPE> _name = LanguageTextsWrapper.atHasLang((SELF_TYPE)this);
	
	@SuppressWarnings("unchecked")
	private final transient LangDependentNamed _langDepNamedDelegate = new LangDependentNamedDelegate<SELF_TYPE>((SELF_TYPE)this);
	
	@Override
	public LangDependentNamed asLangDependentNamed() {
		return _langDepNamedDelegate;
	}
	public String getNameIn(final Language lang) {
		return _nameByLanguage != null ? _nameByLanguage.get(lang) : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUMMARIZABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return SummarizableBuilder.summarizableFrom(SummaryBuilder.languageDependent()
																  .create(this));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACETS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isBelongsToCountry() {
		return (this instanceof GeoLocationBelongsToCountry);
	}
	@Override
	public GeoLocationBelongsToCountry asBelongsToCountry() {
		if (!this.isBelongsToCountry()) throw new IllegalStateException();
		return (GeoLocationBelongsToCountry)this;
	}
	@Override
	public boolean isBelongsToTerritory() {
		return (this instanceof GeoLocationBelongsToTerritory);
	}
	@Override
	public GeoLocationBelongsToTerritory asBelongsToTerritory() {
		if (!this.isBelongsToTerritory()) throw new IllegalStateException();
		return (GeoLocationBelongsToTerritory)this;
	}
	@Override
	public boolean isBelongsToState() {
		return (this instanceof GeoLocationBelongsToState);
	}
	@Override
	public GeoLocationBelongsToState asBelongsToState() {
		if (!this.isBelongsToState()) throw new IllegalStateException();
		return (GeoLocationBelongsToState)this;
	}
	@Override
	public boolean isBelongsToCounty() {
		return (this instanceof GeoLocationBelongsToCounty);
	}
	@Override
	public GeoLocationBelongsToCounty asBelongsToCounty() {
		if (!this.isBelongsToCounty()) throw new IllegalStateException();
		return (GeoLocationBelongsToCounty)this;
	}
	@Override
	public boolean isBelongsToRegion() {
		return (this instanceof GeoLocationBelongsToRegion);
	}
	@Override
	public GeoLocationBelongsToRegion asBelongsToRegion() {
		if (!this.isBelongsToRegion()) throw new IllegalStateException();
		return (GeoLocationBelongsToRegion)this;
	}
	@Override
	public boolean isBelongsToMunicipality() {
		return (this instanceof GeoLocationBelongsToMunicipality);
	}
	@Override
	public GeoLocationBelongsToMunicipality asBelongsToMunicipality() {
		if (!this.isBelongsToMunicipality()) throw new IllegalStateException();
		return (GeoLocationBelongsToMunicipality)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder path = new StringBuilder();
		if (this instanceof GeoLocationBelongsToCountry) {
			GeoCountryID countryId = this.asBelongsToCountry().getCountryId();
			if (countryId != null) path.append(" country=").append(countryId);
		}
		if (this instanceof GeoLocationBelongsToTerritory) {
			GeoTerritoryID territoryId = this.asBelongsToTerritory().getTerritoryId();
			if (territoryId != null) path.append(" territory=").append(territoryId);
		}
		if (this instanceof GeoLocationBelongsToState) {
			GeoStateID stateId = this.asBelongsToState().getStateId();
			if (stateId != null) path.append(" state=").append(stateId);
		}
		if (this instanceof GeoLocationBelongsToRegion) {
			GeoRegionID regionId = this.asBelongsToRegion().getRegionId();
			if (regionId != null) path.append(" region=").append(regionId);
		}
		if (this instanceof GeoLocationBelongsToMunicipality) {
			GeoMunicipalityID munId = this.asBelongsToMunicipality().getMunicipalityId();
			if (munId != null) path.append(" municipality=").append(munId);
		}
		return Strings.customized("{} id={}{} name={}",
								  this.getClass().getSimpleName(),
								  this.getId(),
								  path.toString(),
								  this.getOfficialName() != null ? this.getOfficialName() 
										  						 : this.getNameByLanguage() != null ? this.getNameByLanguage().getAny(Language.ENGLISH,Language.SPANISH,Language.BASQUE)
										  								 				  			: "no name");
	}

}
