package r01f.types.contact;

import java.io.Serializable;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Base type for every {@link ContactInfo} related object
 * @param <SELF_TYPE>
 */
@Accessors(prefix="_")
abstract class ContactInfoBase<SELF_TYPE extends ContactInfoBase<SELF_TYPE>>   
    implements Serializable {

	private static final long serialVersionUID = -5888226970610575462L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * true if the contact data is private and should not be publicly exposed
	 * (ie: opendata)
	 */
	@MarshallField(as="private",
				   whenXml=@MarshallFieldAsXml(attr=true))		
	@Getter @Setter protected boolean _private;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Do NOT expose contact info data 	
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE doNotExposeData() {
		_private = true;
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public void updateFrom(final SELF_TYPE other) {
		_private = other.isPrivate();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContactInfoBase)) return false;
		SELF_TYPE other = (SELF_TYPE)obj;
		return this.isPrivate() == other.isPrivate();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_private);
	}
}
