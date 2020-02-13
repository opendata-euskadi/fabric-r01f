package r01f.types.contact;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;

@MarshallType(as="phoneExtension")
@Immutable
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class PhoneExtension
  implements CanBeRepresentedAsString,
   		   	 Serializable{
	private static final long serialVersionUID = 4252111531973738528L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _id;
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public static PhoneExtension create(final String ext) {
		return new PhoneExtension(ext);
	}
	public static PhoneExtension from(final String ext) {
		return new PhoneExtension(ext);
	}
	public static PhoneExtension of(final String ext) {
		return new PhoneExtension(ext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.asString();
	}
	@Override
	public String asString() {
		return _id != null ? _id.toString() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static <I extends ValidatedContactMean> String asStringOrNull(final I id) {
		return id != null ? id.asString()
						  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		return _id.hashCode();
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof ValidatedContactMeanBase) {
			ValidatedContactMeanBase id = (ValidatedContactMeanBase)obj;
			return id.getId().equals(_id);
		}
		return false;
	}
}
