package r01f.locale;

import com.google.common.base.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasLang;
import r01f.facets.HasLanguage;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Wraps a language dependent object
 * Usage: just extend
 * <pre class='brush:java'>
 * 		@MarshallType(as="myType")
 * 		public class MyLangDependentType
 * 			 extends LanguageDependentObjWrapper<Date> {
 * 			// just extend
 * 		}
 * </pre>
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public abstract class LanguageDependentObjWrapper<T>
		   implements HasLang, 
			 		  HasLanguage {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _lang;
	
	@MarshallField(as="obj",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private T _obj;

/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Language getLanguage() {
		return _lang;
	}
	@Override
	public void setLanguage(final Language lang) {
		_lang = lang;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof LanguageDependentObjWrapper) {
			LanguageDependentObjWrapper<?> other = (LanguageDependentObjWrapper<?>)obj;
			boolean langEqs = _lang != null && other.getLang() != null
									? _lang == other.getLang()
									: _lang == null && other.getLang() != null
											? false
											: _lang != null && other.getLang() == null
													? false
													: true;		// both null
			boolean objEqs = _obj != null && other.getObj() != null
									? _obj.equals(other.getObj())
									: _obj == null && other.getObj() != null
											? false
											: _obj != null && other.getObj() == null
													? false		
													: true;		// both null
			return langEqs && objEqs;
		} 
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_lang,_obj);
	}
}
