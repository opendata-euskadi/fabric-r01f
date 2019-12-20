package r01f.types.contact;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Contact person's phone
 * <pre class='brush:java'>
 *	ContactPhone phone = ContactPhone.createToBeUsedFor(ContactInfoUsage.PERSONAL)
 *									 .useAsDefault()
 *									 .withNumber("688671967")
 *									 .availableRangeForCalling(Ranges.closed(0,22));
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="phoneChannel")
@Accessors(prefix="_")
@NoArgsConstructor
public class ContactPhone 
	 extends ContactMeanDataBase<ContactPhone> {
	
	private static final long serialVersionUID = 6677974112128068298L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Phone type (mobile, non-mobile, fax, ...)
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ContactPhoneType _type = ContactPhoneType.MOBILE;
	/**
	 * Phone number
	 */
	@MarshallField(as="number",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Phone _number;
	/**
	 * Hour range when could be contacted
	 * 
	 * It is stored like a String but internally is used like a Range<Integer>. That is to avoid GWT incompatibility.
	 * The lombok Getter and Setter are necessary because the explicit methods have the @GWTIncompatible annotation
	 * and wont be generated in the GWT compilation.
	 */
	@MarshallField(as="availability",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) private String _availableRangeForCallingStr = null;
	@GwtIncompatible("uses Range")
	public r01f.types.Range<Integer> getAvailableRangeForCalling() {
		return r01f.types.Range.parse(_availableRangeForCallingStr,Integer.class);
	}
	@GwtIncompatible("uses Range")
	public void setAvailableRangeForCalling(final r01f.types.Range<Integer> _availableRangeForCalling) {
		_availableRangeForCallingStr = _availableRangeForCalling.asString();
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API: CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactPhone createToBeUsedFor(final ContactInfoUsage usage) {
		ContactPhone outPhone = new ContactPhone();
		outPhone.usedFor(usage);
		return outPhone;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactPhone type(final ContactPhoneType type) {
		_type = type;
		return this;
	}
	public ContactPhone withNumber(final Phone number) {
		_number = number;
		return this;
	}
	public ContactPhone withNumber(final String number) {
		_number = Phone.create(number);
		return this;
	}
	
	@GwtIncompatible("uses Range")
	public ContactPhone availableRangeForCalling(final r01f.types.Range<Integer> range) {
		_availableRangeForCallingStr = range.asString();
		return this;
	}
	public ContactPhone allwaysAvailableForCalling() {
		_availableRangeForCallingStr = null;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateFrom(final ContactPhone other) {
		super.updateFrom(other);
		_type = other.getType();
		_number = other.getNumber();
		_availableRangeForCallingStr = other.getAvailableRangeForCallingStr();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContactPhone)) return false;
		ContactPhone other = (ContactPhone)obj;
		return super.equals(other)
			&& Objects.equal(this.getType(),other.getType())
			&& Objects.equal(this.getNumber(),other.getNumber())
			&& Objects.equal(this.getAvailableRangeForCallingStr(),other.getAvailableRangeForCallingStr());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_private,
								_usage,
							    _usageDetails,
							    _default,
							    _type,
							    _number,
							    _availableRangeForCallingStr);
	}
}
