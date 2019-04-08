package r01f.types;

import java.io.Serializable;

/**
 * Represents a java type name
 */
public class JavaTypeName
  implements CanBeRepresentedAsString,
			 Serializable {
	private static final long serialVersionUID = 7316410643001225993L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _typeName;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaTypeName(final String name) {
		_typeName = name;
	}
	public JavaTypeName(final Class<?> type) {
		_typeName = type.getName();
	}
	public static JavaTypeName of(final String name) {
		return new JavaTypeName(name);
	}
	public static JavaTypeName of(final Class<?> type) {
		return new JavaTypeName(type);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _typeName;
	}
	@Override
	public String toString() {
		return _typeName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object other) {
		if (other == this) return true;
		if (other instanceof JavaTypeName) {
			JavaTypeName otherJavaTypeName = (JavaTypeName)other;
			return otherJavaTypeName.asString().equals(_typeName);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return _typeName.hashCode();
	}


}
