package r01f.types;

import java.io.Serializable;

/**
 * Represents a java package
 */
public class JavaPackage
  implements CanBeRepresentedAsString,
			 Serializable {
	private static final long serialVersionUID = 7316410643001225993L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _package;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaPackage(final String pckg) {
		_package = pckg;
	}
	public JavaPackage(final Package pckg) {
		_package = pckg.getName();
	}
	public JavaPackage(final Class<?> type) {
		this(type.getPackage());
	}
	public static JavaPackage of(final String pckg) {
		return new JavaPackage(pckg);
	}
	public static JavaPackage of(final Package pckg) {
		return new JavaPackage(pckg);
	}
	public static JavaPackage of(final Class<?> type) {
		return new JavaPackage(type);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _package;
	}
	@Override
	public String toString() {
		return _package;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isJavaLang() {
		return _package.startsWith("java.lang");
	}
	public boolean isJavax() {
		return _package.startsWith("javax");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object other) {
		if (other == this) return true;
		if (other instanceof JavaPackage) {
			JavaPackage otherJavaTypeName = (JavaPackage)other;
			return otherJavaTypeName.asString().equals(_package);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return _package.hashCode();
	}


}
