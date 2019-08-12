package r01f.reflection;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.patterns.Memoized;

/**
 * Wraps a type by it's name
 */
@Immutable

@Accessors(prefix="_")
public class TypeRefByName 
  implements Serializable {
	private static final long serialVersionUID = -2891958240264085388L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final String _name;
			private final Memoized<Class<?>> _type;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TypeRefByName(final String typeName) {
		_name = typeName;
		_type = new Memoized<Class<?>>() {
						@Override
						public Class<?> supply() {
							return ReflectionUtils.typeFromClassName(_name);
						}
				};
	}
	public static TypeRefByName forTypeName(final String fullTypeName) {
		return new TypeRefByName(fullTypeName);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the type from the name of the wrapped name
	 */
	public Class<?> type() {
		return this.getType();
	}
	public Class<?> getType() {
		return _type.get();
	}
	public String getTypeName() {
		return _name;
	}
	/**
	 * @return an instance of the wrapped type
	 */
	public <T> T createInstance() {
		return ReflectionUtils.<T>createInstanceOf(this.type());
	}
	/**
	 * @return a bean reflection access to the wrapped type
	 */
	public BeanReflection reflectionAccess() {
		return Reflection.wrap(this.type());
	}
}
