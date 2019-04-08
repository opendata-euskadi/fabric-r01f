package r01f.reflection;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;

/**
 * Method param
 */
@Immutable

@Accessors(prefix="_")
@RequiredArgsConstructor
public class TypeMethodParamRefByName implements Serializable {
	private static final long serialVersionUID = -9058364756169461226L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Parameter type
	 */
	@Getter private final TypeRefByName _type;
	/**
	 * Parameter value
	 */
	@Getter private final Object _paramValue;
}
