package r01f.locale;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasLang;
import r01f.facets.HasLanguage;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public abstract class ObjectInLangBase
		   implements HasLang, 
	 		 		  HasLanguage,
	 		 		  Serializable {

	private static final long serialVersionUID = 5553558037787053060L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _lang;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ObjectInLangBase() {
		// no-args constructor
	}
	public ObjectInLangBase(final Language lang) {
		_lang = lang;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Language getLanguage() {
		return _lang;
	}
	@Override
	public void setLanguage(final Language lang) {
		_lang = lang;
	}
}
