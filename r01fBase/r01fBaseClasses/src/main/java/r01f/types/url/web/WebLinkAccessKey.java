package r01f.types.url.web;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;



@MarshallType(as="accessKey")
@Immutable
@Accessors(prefix="_")
public class WebLinkAccessKey 
  implements CanBeRepresentedAsString,
  			 Serializable {
	private static final long serialVersionUID = -3951876797922937680L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="accessKey",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _accessKey;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkAccessKey() {
		// default no-args constructor
	}
	public WebLinkAccessKey(final String accessKey) {
		_accessKey = accessKey;
	}
	public WebLinkAccessKey(final WebLinkAccessKey other) {
		_accessKey = other.getAccessKey();
	}
	public static WebLinkAccessKey of(final String accessKey) {
		return Strings.isNOTNullOrEmpty(accessKey) ? new WebLinkAccessKey(accessKey)
											  	   : null;
	}
	public static WebLinkAccessKey valueOf(final String accessKey) {
		return WebLinkAccessKey.of(accessKey);
	}
	public static WebLinkAccessKey create(final String accessKey) {
		return WebLinkAccessKey.of(accessKey);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.asString();
	}
	@Override
	public String asString() {
		return _accessKey != null ? _accessKey.toString() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (o instanceof WebLinkAccessKey) {
			WebLinkAccessKey a = (WebLinkAccessKey)o;
			return a.getAccessKey().equals(_accessKey);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return _accessKey.hashCode();
	}
	
}
