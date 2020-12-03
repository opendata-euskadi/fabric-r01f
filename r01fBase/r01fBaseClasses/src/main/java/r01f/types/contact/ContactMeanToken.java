package r01f.types.contact;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.SystemID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.SecurityToken;

@MarshallType(as="contactMeanToken")
@Accessors(prefix="_")
public class ContactMeanToken
  implements Serializable {

	private static final long serialVersionUID = 5700150378385730558L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="system",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private SystemID _system;

	@MarshallField(as="token",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private SecurityToken _token;
}
