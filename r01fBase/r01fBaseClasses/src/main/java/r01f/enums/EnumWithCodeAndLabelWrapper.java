package r01f.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;




/**
 * Encapsulates usual operations with enums implementing {@link EnumWithCodeAndLabel}
 * Usage:
 * <pre class='brush:java'>
 * @Accessors(prefix="_")
 * @RequiredArgsConstructor
 * public enum MyEnum implements EnumWithCodeAndLabel<MyEnum> {
 *		MyEnumValue1("oid1","MyEnumValue1Description"),
 *		MyEnumValue2("oid2","MyEnumValue2Description"),
 *		MyEnumValue3("oid3","MyEnumValue3Description");
 *				
 *		@Getter private String _code;		// dos NOT have to be an String... it can be an int or anything
 *		@Getter private String _label;
 *
 *		// Static enum wrapper implementing CodeEnum functions
 *		private static EnumWithCodeAndLabelWrapper<String,MyEnum> _enums = new EnumWithCodeAndLabelWrapper<String,MyEnum>(MyEnum.values());
 *		
 *		@Override
 *		public boolean isIn(MyEnum... other) {
 *			return _enums.isIn(this,other);
 *		}
 *		@Override
 *		public boolean is(MyEnum other) {
 *			return _enums.is(this,other);
 *		}
 *		public static MyEnum fromCode(int code) {
 *			return _enums.fromCode(code);
 *		}
 *		public static MyEnum fromName(String name) {
 *			return _enums.fromName(name);
 *		}
 *		public static MyEnum fromLabel(String label) {
 *			return _enums.fromDescription(label);
 *		}
 * }
 * </pre> 
 * @param <T>s
 */
public class EnumWithCodeAndLabelWrapper<C,T extends EnumWithCodeAndLabel<C,T>>
     extends EnumWithCodeWrapper<C,T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * enum values-based constructor
	 * @param values 
	 */
	public EnumWithCodeAndLabelWrapper(final T[] values) {
		super(values);
	}
	/**
	 * Factory
	 * @param enumType
	 * @return
	 */
	public static <C,T extends EnumWithCodeAndLabel<C,T>> EnumWithCodeAndLabelWrapper<C,T> wrapEnumWithCodeAndLabel(final Class<T> enumType) {
		return new EnumWithCodeAndLabelWrapper<C,T>(enumType.getEnumConstants());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public EnumWithCodeAndLabelWrapper<C,T> strict() {	// must be overridden to adapt the returned type
		super.strict();
		return this;
	}
	/**
	 * Checks if an enum element can be set from a label
	 * @param label 
	 * @return true if the enum element can be assigned
	 */
	public boolean canBeFrom(final String label) {
		T el = null;
		for (T ty : _values) {
			if (ty.getLabel().equals(label)) {
				el = ty;
				break;
			}
		}
		return el != null ? true : false;
	}
	/**
	 * Gets the enum element from the label
	 * @param label 
	 * @return 
	 */
	public T from(final String label) {
		T outT = null;
		for (T ty : _values) {
			if (ty.getLabel().equals(label)) {
				outT = ty;
				break;
			}
		}
		if (_strict && outT == null)  throw new IllegalArgumentException("There does NOT exists an enum element with label = " + label);
		return outT;
	}
	/**
	 * Gets the enum element by means of applying a regexp to the labels
	 * @param matchingRegEx
	 * @return 
	 */	
	@GwtIncompatible
	public T elementMatching(final Pattern matchingRegEx) {
		T outT = null;
		//Pattern p = Pattern.compile(matchingRegEx);
		Matcher m = null;
		for (T ty : _values) {
			m = matchingRegEx.matcher(ty.getLabel());
			if (m.matches()) {
				outT = ty;
				break;
			}
		}
		if (_strict && outT == null) throw new IllegalArgumentException("Threre does NOT exists an enum element matching " + matchingRegEx.toString());
		return outT;
	}	
}
