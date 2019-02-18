package r01f.types.geo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoID;
import r01f.util.types.Strings;

/**
 * Geo info base type
 * <pre class='brush:java'>
 * R01MGeoLocationPart<R01MGeoCountry> country = new R01MGeoLocationPart<R01MGeoCountry>(34,
 * 																					    "Spain",
 * 																						GeoPosition2D.usingStandard(GOOGLE).setLocation(lat,long));
 * </pre>
 * @param <GID>
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public abstract class GeoLocationBase<GID extends GeoID,
								      SELF_TYPE extends GeoLocationBase<GID,SELF_TYPE>> 
           implements Serializable {

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
	 * location name 
	 */
	@MarshallField(as="name")
	@Getter @Setter private LanguageTexts _name;
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
		if (_name == null) _name = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL,null);
		_name.add(lang,name);
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withNameForAll(final String name) {
		_name = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL,null);
		for (Language lang : Language.values()) {
			_name.add(lang,name);
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
	public String getNameIn(final Language lang) {
		return _name != null ? _name.get(lang) : null;
	}
}
