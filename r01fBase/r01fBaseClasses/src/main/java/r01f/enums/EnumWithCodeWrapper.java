package r01f.enums;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import r01f.util.types.Strings;

/**
 * Encapsulates operations with an {@link Enum} implementing {@link EnumWithCode}
 * Usage
 * <pre class='brush:java'>
 * @Accessors(prefix="_")
 * @RequiredArgsConstructor
 * public enum MyEnum 
 *  implements EnumWithCode<MyEnum>
 *		MyEnumValue1(1),
 *		MyEnumValue2(2),
 *		MyEnumValue3(3);
 *		
 *		@Getter private Integer _code;			// this do not mandatory have to be an integer
 *		@Getter private Class<Integer> _codeType = Integer.class;
 *
 *		// Wrapper that encapsulates the EnumWithCode behavior
 *		private static EnumWithCodeWrapper<Integer,MyEnum> _enums = EnumWithCodeWrapper.create(MyEnum.class)
 *
 *		@Override
 *		public boolean isIn(MyEnum... other) {
 *			return _enums.isIn(this,other);
 *		}
  *		@Override
 *		public boolean is(R01MPublishRequestType other) {
 *			return _enums.is(this,other);
 *		}
 *		// Static factory
 *		public static DataType fromCode(int code) {
 *			return enums.fromCode(code);
 *		}
 * }
 * </pre> 
 * @param <C> the code type
 * @param <E> the {@link Enum}
 */
public class EnumWithCodeWrapper<C,E extends EnumWithCode<C,E>> 
     extends EnumExtendedWrapper<E> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor from enum values
	 * @param values 
	 */
	public EnumWithCodeWrapper(final E[] values) {
		super(values);
	}
	/**
	 * Factory
	 * @param enumType
	 * @return
	 */
	public static <C,T extends EnumWithCode<C,T>> EnumWithCodeWrapper<C,T> wrapEnumWithCode(final Class<T> enumType) {
		return new EnumWithCodeWrapper<C,T>(enumType.getEnumConstants());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the enum element from the code
	 * @param code 
	 * @return 
	 */
	public E fromCode(final C code) {
		E outT = null;
		for (E ty : _values) {
			if (ty.getCode().equals(code)) {
				outT = ty;
				break;
			}
		}		
		if (_strict && outT == null) throw new IllegalArgumentException("It does NOT exist an element of enum with code = " + code);
		return outT;
	}
	/**
	 * Return true if the code can be an enum element
	 * @param code 
	 * @return  
	 */
	public boolean canBeFromCode(final C code) {
		E outT = null;
		try {
			outT = this.fromCode(code);
		} catch(IllegalArgumentException illArgEx) {
			/* Nothing to do */
		}
		return outT != null;
	}
	/**
	 * @return a list of the codes
	 */
	public Collection<C> codes() {
		return FluentIterable.from(_values)
	 			   .transform(new Function<E,C>() {
									@Override
									public C apply(final E val) {
										return val.getCode();
									}
	 			   			  })
	 			   .toList();
	}
	/**
	 * Returns the enum names quouted
	 * @return
	 */
	public Collection<String> codesQuoted() {
		return FluentIterable.from(_values)
	 			   .transform(new Function<E,String>() {
									@Override
									public String apply(final E val) {
										return Strings.customized("'{}'",
															 	  val.getCode());
									}
	 			   			  })
	 			   .toList();
	}
}
