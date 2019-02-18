package r01f.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.util.types.collections.CollectionUtils;

/**
 * An immutable implementation of the {@link ParameterizedType} interface.
 * Based on PrimeFaces implementation of JSonConverter:
 * <ul>
 *  	<li>https://github.com/primefaces-extensions/core/blob/master/src/main/java/org/primefaces/extensions/util/json/ParameterizedTypeImpl.java)</li>
 * 		<li>https://github.com/primefaces-extensions/core/blob/master/src/main/java/org/primefaces/extensions/converter/JsonConverter.java</li>
 * 		<li>http://www.javacodegeeks.com/2013/03/passing-complex-objects-in-url-parameters.html</li>
 * </ul>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class ParameterizedTypeImpl 
  implements ParameterizedType,
  			 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Type _rawType;
	@Getter private final Type[] _actualTypeArguments;
	@Getter private final Type _ownerType;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the type is parameterized
	 */
	public boolean isParameterized() {
		return CollectionUtils.isNullOrEmpty(_actualTypeArguments);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuffer sb = new StringBuffer();
		Class<?> rawTypeClazz = (Class<?>)_rawType;
		sb.append(rawTypeClazz.getName());
		if (CollectionUtils.hasData(_actualTypeArguments)) {
			sb.append("<");
			for (int i=0; i<_actualTypeArguments.length; i++) {
				if (_actualTypeArguments[i] instanceof ParameterizedTypeImpl) {
					ParameterizedTypeImpl argParam = (ParameterizedTypeImpl)_actualTypeArguments[i];
					sb.append(argParam.debugInfo());
					
				} else if (_actualTypeArguments[i] instanceof Class) {
					Class<?> argClazz = (Class<?>)_actualTypeArguments[i];
					if (argClazz.isArray()) {
						sb.append(argClazz.getComponentType()).append("[]");
					} else {
						sb.append(argClazz.getName());
					}
				}
				if (i < _actualTypeArguments.length-1) sb.append(",");
			}
			sb.append(">");
		}
		return sb.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OBJECT OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		if (o == null) return false;
		if (!(o instanceof ParameterizedType)) return false;
		
		// Check that information is equivalent
		ParameterizedType that = (ParameterizedType)o;
		if (this == that) {
			return true;
		}
		Type thatOwner = that.getOwnerType();
		Type thatRawType = that.getRawType();
		
		return (_ownerType == null ? thatOwner == null 
							   : _ownerType.equals(thatOwner))
		    && (_rawType == null ? thatRawType == null 
		    					 : _rawType.equals(thatRawType))
		    && (Arrays.equals(_actualTypeArguments,that.getActualTypeArguments()));
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_actualTypeArguments)
		       ^ (_ownerType == null ? 0 
		    		   			 : _ownerType.hashCode())
		       ^ (_rawType == null ? 0 
		    		   			   : _rawType.hashCode());
	}
}
