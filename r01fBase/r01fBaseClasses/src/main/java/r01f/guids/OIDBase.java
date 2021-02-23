package r01f.guids;

import java.util.Arrays;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;

/**
 * Models an oid by encapsulating an id that can be either a String, an int, a long, etc
 * The OID could be based on an iImmutable OID if it extends {@link OIDBaseImmutable}
 * or on an mutable OID if it extends {@link OIDBaseMutable}
 *
 * {@link OIDBaseMutable} should be used as OID base type if the OID must have a zero-argument
 * constructor. This is the case of objects that must be serialized using GWT serialization
 * mechanism: if a no-arg constructor is missing a compilation error is raised
 *
 * @param <T> the type of the id
 */
@Immutable
@NoArgsConstructor
public abstract class OIDBase<T>
		   implements OIDTyped<T> {

	private static final long serialVersionUID = 3253497735245445342L;
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract T getId();
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <O extends OID> boolean is(final O other) {
		return this.equals(other);
	}
	@Override
	public <O extends OID> boolean isNOT(final O other) {
		return !this.is(other);
	}
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> boolean isContainedIn(final O... oids) {
		return oids != null ? this.isContainedIn(Arrays.asList(oids))
							: false;
	}
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> boolean isNOTContainedIn(final O... oids) {
		return !this.isContainedIn(oids);
	}
	@Override
	public <O extends OID> boolean isContainedIn(final Iterable<O> oids) {
		boolean outIsContained = false;
		if (oids != null) {
			for (O oid : oids) {
				if (oid.equals(this)) {
					outIsContained = true;
					break;
				}
			}
		}
		return outIsContained;
	}
	@Override
	public <O extends OID> boolean isNOTContainedIn(final Iterable<O> oids) {
		return !this.isContainedIn(oids);
	}
	@Override @SuppressWarnings("null")
	public <O extends OID> boolean isIgnoringCase(final O other) {
		if (other == null) return false;
		String thisStr = this.asString();
		String otherStr = other.asString();
		if (thisStr == null && otherStr == null) return true;
		if (thisStr != null && otherStr == null) return false;
		if (thisStr == null && otherStr != null) return false;
		if (thisStr.equalsIgnoreCase(otherStr)) return true;
		return true;
	}
	@Override
	public T getRaw() {
		return this.getId();
	}
	@Override
	public String asString() {
		return this.toString();
	}
///////////////////////////////////////////////////////////////////////////////
//  Serializable Cloneable y Comparable
///////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.getId() != null ? this.getId().toString()
									: null;
	}
	@Override @GwtIncompatible("not supported by gwt")
	protected Object clone() throws CloneNotSupportedException {
		if (this.getId() == null) throw new IllegalStateException(String.format("The oid of type %s has NO state",this.getClass()));
		Object id = ObjectUtils.clone(this.getId());
		return _createOIDFromString(this.getClass(),
									id.toString());
	}
	@Override
	public int compareTo(final OID o) {
		return this.getId().toString().compareTo(o.toString());
	}
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof OID) {
			OIDTyped<?> oidTyped = (OIDTyped<?>)obj;
			return oidTyped.getRaw().equals(this.getRaw());
		} else if (obj instanceof String
				|| obj instanceof Number) {
			return obj.toString().equals(this.getId().toString());
		}
		return super.equals(obj);
	}
	@Override
	public boolean is(final T otherId) {
		if (otherId == null) return false;
		if (otherId == this.getId()) return true;
		return otherId.equals(this.getId());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends OID> String asStringOrNull(final O oid) {
		return oid != null ? oid.asString()
						   : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O cast() {
		return (O)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isValid() {
		return this.getId() != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@GwtIncompatible("gwt does NOT supports reflection") @SuppressWarnings("unchecked")
	private static <O extends OID> O _createOIDFromString(final Class<O> oidType,
												   		  final String oidAsString) {
		Preconditions.checkArgument(oidAsString != null,
									"an oid cannot have a null id");
		O outOid = null;
		try {
			outOid = (O)MethodUtils.invokeStaticMethod(oidType,
				   									   "forId",
				   									   new Object[] {oidAsString},new Class<?>[] {String.class});
		} catch (NoSuchMethodException nsmEx) {
			throw new IllegalArgumentException(String.format("Type %s is NOT a valid OID: it does NOT have a forId(String) static builder method",
															 oidType));
		} catch (Throwable th) {
			th.printStackTrace();
			throw new IllegalStateException(String.format("Could NOT create an OID instance of type %s: %s",oidType,th.getMessage()),
											th);
		}
		return outOid;
	}
}
