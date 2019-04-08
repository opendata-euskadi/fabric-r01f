package r01f.reflection;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;

@Immutable

@Accessors(prefix="_")
public class TypeMethodRefByName 
  implements Serializable {
	private static final long serialVersionUID = 2502317609370430460L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final TypeRefByName _type;
	/**
	 * Method name
	 */
	@Getter private final String _name;
	/**
	 * Method parameter types
	 */
	@Getter private final TypeMethodParamRefByName[] _params;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TypeMethodRefByName(final TypeRefByName type,
							   final String name,final TypeMethodParamRefByName... params) {
		_type = type;
		_name = name;
		_params = params;
	}
	public TypeMethodRefByName(final TypeRefByName type,
							   final String name,final Set<TypeMethodParamRefByName> params) {
		_type = type;
		_name = name;
		_params = params != null ? params.toArray(new TypeMethodParamRefByName[params.size()])
								 : null;
	}
}
