package r01f.types.contact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;



@Immutable
@NoArgsConstructor @AllArgsConstructor
@Accessors(prefix="_")
abstract class ValidatedContactMeanBase
    implements ValidatedContactMean {

	private static final long serialVersionUID = 7691819813799837148L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _id;

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
