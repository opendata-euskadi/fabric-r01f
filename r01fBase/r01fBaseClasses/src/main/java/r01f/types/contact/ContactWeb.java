package r01f.types.contact;

import com.google.common.annotations.GwtIncompatible;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Url;


/**
 * Contact's web sites
 * <pre class='brush:java'>
 *	ContactWeb user = ContactWeb.createToBeUsedFor(ContactInfoUsage.PERSONAL)
 *								.url(WebUrl.of("www.futuretelematics.net"));
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="webChannel")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class ContactWeb 
     extends ContactMeanDataBase<ContactWeb>
  implements ContactMean,
  			 HasLanguage {
	
	private static final long serialVersionUID = -4012809208590547328L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The language
	 */
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _language;
	/**
	 * Web
	 */
	@MarshallField(as="url",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private Url _web;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactWeb createToBeUsedFor(final ContactInfoUsage usage) {
		ContactWeb outNetwork = new ContactWeb();
		outNetwork.usedFor(usage);
		return outNetwork;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactWeb url(final Url web) {
		_web = web;
		return this;
	}
	@GwtIncompatible("Url NOT usable in GWT")
	public ContactWeb url(final String web) {
		_web = Url.from(web);
		return this;
	}
	public ContactWeb in(final Language lang) {
		_language = lang;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _web != null ? _web.asString() : null;
	}
}
