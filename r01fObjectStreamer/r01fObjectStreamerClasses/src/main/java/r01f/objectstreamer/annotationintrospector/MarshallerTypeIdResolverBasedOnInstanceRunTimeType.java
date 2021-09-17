package r01f.objectstreamer.annotationintrospector;

import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

/**
 * A Jackson {@link TypeIdResolver} that uses an instance RUN-TIME type to create the [typeId] attribute value when 
 * serializing polymorphic types
 * 
 * When serializing a polymorphic FIELD Jackson usually uses the interface type annotations to infer the [typeId] attribute value.
 * But there are circumstances where the interface type is NOT known due to java's type erasure:
 *		 [1] Fields defined with an interface
 *						public interface Vehicle {
 *							...
 *						}
 *						public class Car 
 *						  implements Vehicle {
 *							...
 *						}
 *						@MarshallType(as="bean")
 *						public class VehicleContainerBean {
 *							@MarshallField(as="vehicle")
 *							@Getter @Setter private Vehicle _vehicle;	<-- the concrete type is unknown
 *						}
 *			[ - willcard-parameterized fields
 *						@MarshallType(as="person")
 *						public class Person<T> {
 *							@MarshallField(as="id")
 *							@Getter @Setter private T _id;					<--
 *						}
 *						@MarshallType(as="bean")
 *						public class PersonContainerBean {
 *							@MarshallField(as="thePerson")
 *							@Getter @Setter private Person<?> _person;		<-- the type of the parameter is NOT known (object)
 *						}
 *		
 *	... the only "mean" to generate the typeId property is to use the CONCRETE object type that's only known when 
 *		serializing a CONCRETE object
 *		(the default typeIdResolver uses the type-available info: does NOT need to check the concrete object type)
 * 
 * This {@link TypeIdResolver} uses RUN-TIME type information to guess the [typeId] attribute value
 */
public class MarshallerTypeIdResolverBasedOnInstanceRunTimeType 
	 extends TypeIdResolverBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final ConcurrentHashMap<String,String> _typeToId = new ConcurrentHashMap<>();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String idFromValue(final Object instance) {
		return instance != null ? this.idFromClass(instance.getClass())
								: null;
	}
	public String idFromClass(final Class<?> clazz) {
		String key = clazz.getName();
		String id = _typeToId.get(key);
		if (id != null) return id;
		
		// use the run-time object instance type
		String typeId = MarshallerAnnotationIntrospector.findPolymorphicTypeIdFor(clazz);

		// cache
		_typeToId.put(key,typeId);
		
		return typeId;
	}
	@Override
	public String idFromValueAndType(final Object value,final Class<?> suggestedType) {
		return this.idFromValue(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Id getMechanism() {
		return JsonTypeInfo.Id.CUSTOM;
	}
}
