package r01f.types.contact;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

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
	@Getter @Setter private Url _url;
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
		_url = web;
		return this;
	}
	@GwtIncompatible("Url NOT usable in GWT")
	public ContactWeb url(final String web) {
		_url = Url.from(web);
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
	public void updateFrom(final ContactWeb other) {
		super.updateFrom(other);
		_language = other.getLanguage();
		_url = other.getUrl();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _url != null ? _url.asString() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContactWeb)) return false;
		ContactWeb other = (ContactWeb)obj;
		return super.equals(other)
			&& Objects.equal(this.getLanguage(),other.getLanguage())
			&& Objects.equal(this.getUrl(),other.getUrl());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_private,
								_usage,
							    _usageDetails,
							    _default,
							    _language,
							    _url);
	}
}
