package r01f.types;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.guids.CommonOIDs.PropertyID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="property")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class Property 
  implements Serializable {

	private static final long serialVersionUID = 1431856501223713110L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final PropertyID _id;
	
	@MarshallField(as="value",escape=true,
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter private final String _value;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Property(@MarshallFrom("id") final PropertyID id,@MarshallFrom("value") final String value) {
		_id = id;
		_value = value;
	}
	public static Property from(final PropertyID id,final String value) {
		return new Property(id,value);
	}
}
