package r01f.generics;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.common.reflect.TypeToken;

/**
 * Reference to a generic type.
 * Based on Neal Gafter's <code><a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html" target="_blank">TypeReference</a></code>.
 * Usage: When a {@link TypeRef} argument is needed:
 * <pre class="brush:java">
 * 		new TypeRef<TyeType>() {}
 * <pre>
 * see guava's {@link TypeToken}
 * @param <T> the generic type in this reference.
 */
public abstract class TypeRef<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Class<?> _rawType;
	private final Type _runtimeType;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new </code>{@link TypeRef}</code>.
	 * @throws IllegalArgumentException if the generic type of this reference is missing type parameter.
	 */
	public TypeRef() {
		Type superclass = this.getClass().getGenericSuperclass();
		if (superclass instanceof Class<?>) throw new IllegalArgumentException("Missing type parameter. Maybe you have used the generics 'mode' but the type is not generics");
		Type type = ((ParameterizedType)superclass).getActualTypeArguments()[0];
		
		_runtimeType = type;
		if (!(_runtimeType instanceof TypeVariable)) throw new IllegalArgumentException("Cannot construct a TypeToken for a type variable");
		
		_rawType = TypeRef.typeOf(type);		
		if (_rawType == null) throw new IllegalArgumentException("The rawType of type=" + type + " cannot be known!");
	
	}
///////////////////////////////////////////////////////////////////////////////
//	PUBLIC METHODS
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the raw type of the generic type in this reference.
	 * @return the raw type of the generic type in this reference.
	 */
	public final Class<?> rawType() {
		return _rawType;
	}
	/**
	 * Returns the runtime type
	 * @return
	 */
	public final Type getRuntimeType() {
		return _runtimeType;
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the underlying type or null if it's a TypeVariable (remember type erasure)
     * @param type
     * @return 
     */
    public static Class<?> typeOf(final Type type) {
    	Class<?> outClass = null;
    	
        if (type instanceof Class) {
        	// type can be a "normal" class or a generic class
            outClass = (Class<?>)type;
            if (outClass.getTypeParameters() != null) {
            	// It's a generic type
            	//		public class MyType<T> {
            	//			public myMethod() {
            	//				List<T> instance 	<-- Instance is generic
            	//			}
            	//		}
            	// -- do nothing since outClass already contains the correct type
            } else {
            	// It's a "normal" type
            	// -- do nothing since outClass already contains the correct type
            }

        } else if (type instanceof ParameterizedType) {
        	// It's a parameterized type (ie: MyType<String> or Map<String,String>)
        	// Beware that a [parameterized type] is NOT the same as a [type variable]
        	//	 	public class MyType {
        	//			public myMethod() {
        	//				List<T> instance;		<-- Instance is a TypeVariable
        	//			} 
        	ParameterizedType pType = (ParameterizedType)type;
            outClass = TypeRef.typeOf(pType.getRawType());

        } else if (type instanceof TypeVariable) {
        	// It's a type variable (ej: E o T)
        	//	 	public class MyType<T> {
        	//			public myMethod() {
        	//				T instance;		<-- Instance is a TypeVariable
        	//			}
//        	TypeVariable<?> vType = (TypeVariable<?>)type;
        	
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            Class<?> componentClass = TypeRef.typeOf(componentType);
            if (componentClass != null ) outClass = Array.newInstance(componentClass,0).getClass();		// the array must be instantiated to get the class object
        }
        return outClass;
    }
}
