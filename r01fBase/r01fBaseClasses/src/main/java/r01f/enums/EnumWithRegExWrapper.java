package r01f.enums;

import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;





/**
 * Encapsulates {@link CodeAndDescriptionEnum} operations
 * Usage_
 * <pre class='brush:java'>
 * @Accessors(prefix="_")
 * public enum MyEnum implements EnumWithRexEx<MyEnum> {
 *		IMAGE(".+\\.jpg"),
 *		DOC(".+\\.doc"),
 *				
 *		@Getter private Pattern[] _patterns;
 *
 *		public MyEnum(Pattern... patterns) {
 *			_patterns = patterns;
 *		}
 *
 *		private static EnumWithRegExLabelWrapper<MyEnum> _enums = new EnumWithRegExLabelWrapper<MyEnum>(MyEnum.values());
 *		
 *		@Override
 *		public boolean isIn(MyEnum... other) {
 *			return _enums.isIn(this,other);
 *		}
  *		@Override
 *		public boolean is(MyEnum other) {
 *			return _enums.is(this,other);
 *		}
 *		@Override
 *		public boolean canBeFrom(String label) {
 *			return _enums.canBeFrom(label);
 *		}
 *		public static MyEnum fromName(String name) {
 *			return _enums.fromName(name);
 *		}
 *		public static MyEnum fromCode(int code) {
 *			return _enums.fromCode(code);
 *		}
 *		public static MyEnum fromLabel(String desc) {
 *			return _enums.fromDescription(desc);
 *		}
 * }
 * </pre> 
 * @param <T> 
 */
@GwtIncompatible
public class EnumWithRegExWrapper<T extends EnumWithRegEx<T>> 
     extends EnumExtendedWrapper<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public EnumWithRegExWrapper(final T[] values) {
		super(values);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public EnumWithRegExWrapper<T> strict() {		
		super.strict();
		return this;
	}
	/**
	 * Checks if an enum element can be assigned from a text matching the given expression
	 * @param label
	 * @return true
	 */
	public boolean canBeFrom(final String label) {
		T outT = _fromLabel(label);
		return outT != null ? true : false;
	}
	/**
	 * Gets an enum element from a label
	 * @param label
	 * @return 
	 */
	public T fromLabel(final String label) {
		T outT = _fromLabel(label);
		if (_strict && outT == null)  throw new IllegalArgumentException("There does NOT exists an enum element with label = " + label);
		return outT;
	}
	
	private T _fromLabel(final String label) {
		T outT = null;
		for (T ty : _values) {
			if (ty.getPatterns() != null) {
				for (Pattern p : ty.getPatterns()) {
					if (p.matcher(label).matches()) {
						outT = ty;
						break;
					}
				}
				if (outT != null) break;
			}
		}
		return outT;
	}
}
