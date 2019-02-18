package r01f.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;

/**
 * Encapsulates usual operations with enums implementing {@link EnumWithCodeAndMultipleLabels}
 * usage:
 * <pre class='brush:java'>
 * @Accessors(prefix="_")
 * @RequiredArgsConstructor
 * public enum MyEnum implements EnumWithCodeAndMultipleLabelsEnum<MyEnum> {
 *		MyEnumValue1(0.5F,"MyEnumValue1Description_11","MyEnumValue1Description_12"),
 *		MyEnumValue2(0.7F,"MyEnumValue2Description_21","MyEnumValue2Description_22"),
 *		MyEnumValue3(0.9F,"MyEnumValue3Description_31","MyEnumValue3Description_32");
 *				
 *		@Getter private Float _code;		// NOTA: if must not be a float... can be an string or anything
 *		@Getter private String[] _labels;
 *
 *		private static EnumWithCodeAndMultipleLabelsWrapper<Float,MyEnum> _enums = new EnumWithCodeAndMultipleLabelsWrapper<Float,MyEnum>(MyEnum.values());
 *		
 *		@Override
 *		public String getDescription() {
 *			return _descriptions != null && _descriptions.length > 0 ? _descriptions[0] 
 *																	 : null;
 *		}
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
 *		public static MyEnum fromLabel(String desc) {
 *			return _enums.fromDescription(desc);
 *		}
 * }
 * </pre> 
 * @param <T> 
 */
public class EnumWithCodeAndMultipleLabelsWrapper<C,T extends EnumWithCodeAndMultipleLabels<C,T>> 
     extends EnumWithCodeAndLabelWrapper<C,T> {
	/**
	 * Constructor using enum values
	 * @param values 
	 */
	public EnumWithCodeAndMultipleLabelsWrapper(final T[] values) {
		super(values);
	}
	
	@Override
	public EnumWithCodeAndMultipleLabelsWrapper<C,T> strict() {		// this method should be overridden to adapt the returned type
		super.strict();
		return this;
	}
	/**
	 * Checks if an enum element can be assigned from a description
	 * @param el 
	 * @param desc 
	 * @return true if the enum element can be assigned
	 */
	public boolean canBeFrom(final T el,final String desc) {
		boolean outCan = false;
		for (String d : el.getLabels()) {
			if (d.equals(desc)) {
				outCan = true;
				break;
			}
		}
		return outCan;
	}
	@Override
	public T from(final String desc) {
		T outT = null;
		for (T ty : _values) {
			for (String d : ty.getLabels()) {
				if (d.equals(desc)) {
					outT = ty;
					break;
				}
				if (outT != null) break;
			}
		}
		if (_strict && outT == null)  throw new IllegalArgumentException("There does NOT exists an enum element with label = " + desc);
		return outT;
	}
	@Override @GwtIncompatible
	public T elementMatching(final Pattern matchingRegEx) {
		T outT = null;
		//Pattern p = Pattern.compile(matchingRegEx);
		Matcher m = null;
		for (T ty : _values) {
			for (String d : ty.getLabels()) {
				m = matchingRegEx.matcher(d);
				if (m.matches()) {
					outT = ty;
					break;
				}
			}
			if (outT != null) break;
		}
		if (_strict && outT == null) throw new IllegalArgumentException("Threre does NOT exists an enum element matching " + matchingRegEx.toString());
		return outT;
	}	
}
