package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.custom.CustomStreamers;
import r01f.objectstreamer.util.TypeScan;
import r01f.objectstreamer.util.TypeScan.TypeAnnotation;

@NoArgsConstructor(access=AccessLevel.PRIVATE) 
abstract class MarshallerTypeResolverBuilderDelegates {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, 
												   		  final AnnotatedClass ac,final JavaType baseType) {		
		JavaType javaType = ac.getType();		
		if (javaType == null) return null;

		// inject the @JsonTypeIdResolver
		return _typeResolverBuilderFor(javaType,BuilderFor.TYPE);
	}
	public static TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config,
													   			  final AnnotatedMember am,final JavaType baseType) {
		JavaType javaType = am.getType();		
		if (javaType == null) return null; 
		
		// inject the @JsonTypeIdResolver
		return _typeResolverBuilderFor(javaType,BuilderFor.PROPERTY);
	}
	public static TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, 
																  		 final AnnotatedMember am,final JavaType containerType) {
		JavaType javaType = containerType.getContentType();		
		if (javaType == null) return null;		
				
		// inject the @JsonTypeIdResolver
		return _typeResolverBuilderFor(javaType,BuilderFor.PROPERTY_CONTENT_TYPE);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GENERIC
/////////////////////////////////////////////////////////////////////////////////////////
	private enum BuilderFor {
		TYPE,
		PROPERTY,
		PROPERTY_CONTENT_TYPE;
	}
	/**
	 * If an abstract / interface java type, a type resolver is needed: 
	 * 		a property that gives a hint about the "real" type is "injected" in the 
	 * 		serialized format and used by the deserializer to create the "real" type instance
	 * @param javaType
	 * @return
	 */
	private static TypeResolverBuilder<?> _typeResolverBuilderFor(final JavaType javaType,
																  final BuilderFor builderFor) {
		TypeResolverBuilder<?> outTypeResolverBuilder = null;
		
		// tries to find a type annotated with @MarshallPholymorphicTypeInfo at the type hierarchy
		TypeAnnotation<MarshallPolymorphicTypeInfo> typeWithPolyAnn = TypeScan.findTypeAnnotaion(MarshallPolymorphicTypeInfo.class,
												  			 		 				 		 	 javaType);
		if (typeWithPolyAnn == null) {
			// no @MarshallPholymorphicTypeInfo was found
			if (builderFor == BuilderFor.PROPERTY 
			 && javaType.getRawClass() == Object.class) {
				// this is the case where a field's type is erased at runtime:
				//  [1] - Fields defined with an interface
				//				public interface Vehicle {
				//					...
				//				}
				//				public class Car 
				//				  implements Vehicle {
				//					...
				//				}
				//				@MarshallType(as="bean")
				//				public class VehicleContainerBean {
				//					@MarshallField(as="vehicle")
				//					@Getter @Setter private Vehicle _vehicle;	<-- the concrete type is unknown
				//				}
				//	[2] - willcard-parameterized fields
				//				@MarshallType(as="person")
				//				public class Person<T> {
				//					@MarshallField(as="id")
				//					@Getter @Setter private T _id;					<--
				//				}
				//				@MarshallType(as="bean")
				//				public class PersonContainerBean {
				//					@MarshallField(as="thePerson")
				//					@Getter @Setter private Person<?> _person;		<-- the type of the parameter is NOT known (object)
				//				}
				//
				// ... the only "mean" to generate the typeId property is to use the CONCRETE object type that's only known when 
				//     serializing a CONCRETE object
				//     (the default typeIdResolver uses the type-available info: does NOT need to check the concrete object type)
						
				// [2] - Create the type resolver builder and initialize it
				outTypeResolverBuilder = _buildBuilderForGenericTypeResolverUsingProperty(new MarshallerTypeIdResolverBasedOnInstanceRunTimeType(),	// custo TypeIdResolver
																						  "typeId",	// the typeId property name
																						  true);	// is the typeId property available at the deserializer?
			}
		}
		else {
			// Found @MarshallPholymorphicTypeInfo
			MarshallPolymorphicTypeInfo polyAnn = typeWithPolyAnn.getAnnotation();
			if (builderFor == BuilderFor.TYPE
			 && _hasToDefineTypeResolverFor(javaType,polyAnn.includeTypeInfo().type())) {
				// inject @JsonTypeIdResolver to the TYPE
				outTypeResolverBuilder =  _buildBuilderForGenericTypeResolverUsingProperty(polyAnn.typeIdPropertyName(),					// the typeId property name
																					  	   polyAnn.typeInfoAvailableWhenDeserializing());	// is the typeId property available at the deserializer?
			}
			else if ((builderFor == BuilderFor.PROPERTY || builderFor == BuilderFor.PROPERTY_CONTENT_TYPE)
				   && _hasToDefineTypeResolverFor(javaType,polyAnn.includeTypeInfo().property())) {
				// inject @JsonTypeIdResolver to the FIELD
				// TODO maybe if the type was injected with @JsonTypeIdResolver, this is NOT necessary
				outTypeResolverBuilder =  _buildBuilderForGenericTypeResolverUsingProperty(polyAnn.typeIdPropertyName(),					// the typeId property name
																					  	   polyAnn.typeInfoAvailableWhenDeserializing());	// is the typeId property available at the deserializer?
			}
		}
		return outTypeResolverBuilder;
	}
	private static boolean _hasToDefineTypeResolverFor(final JavaType javaType,
													   final MarshalTypeInfoIncludeCase typeInfoInclude) {
		boolean outHasToDefineTypeResolver = false;
		if ( (
				typeInfoInclude != MarshalTypeInfoIncludeCase.NEVER)
			 && 
			  (
				typeInfoInclude == MarshalTypeInfoIncludeCase.ALWAYS
					 	|| 
				(typeInfoInclude == MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE && _isNotInstanciable(javaType))
			  )
			) {
			outHasToDefineTypeResolver = true;
		}
		return outHasToDefineTypeResolver;
	}
	private static TypeResolverBuilder<?> _buildBuilderForGenericTypeResolverUsingProperty(final String typeIdPropName,
																						   final boolean typeIdPropVisibleByDeserializer) { 
		return _buildBuilderForGenericTypeResolverUsingProperty(null,typeIdPropName,	// use default TypeIdResolver
																typeIdPropVisibleByDeserializer);
	}
	private static TypeResolverBuilder<?> _buildBuilderForGenericTypeResolverUsingProperty(final TypeIdResolver typeIdResolver,
																						   final String typeIdPropName,
																						   final boolean typeIdPropVisibleByDeserializer) { 
		TypeResolverBuilder<?> outTypeResolverBuilder;
				
		// [1] - Manually create the JsonTypeInfo data 
//		JsonTypeInfo typeInfo = _createJsonTypeInfo();
		
		// [2] - Create the type resolver builder and initialize it
		outTypeResolverBuilder = new StdTypeResolverBuilder();
		
		outTypeResolverBuilder = typeIdResolver == null 
										// default TypeIdResolver that uses jackson's TypeNameIdResolver
										// (this is the usual case)
										? outTypeResolverBuilder.init(JsonTypeInfo.Id.NAME,		// typeInfo.use(), 
															 		  null)						// TypeIdResolver
										// custom TypeIdResolver
										: outTypeResolverBuilder.init(JsonTypeInfo.Id.CUSTOM,	// typeInfo.use(), 
															 		  typeIdResolver);			// TypeIdResolver
														
		outTypeResolverBuilder = outTypeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);					// typeInfo.include()
		outTypeResolverBuilder = outTypeResolverBuilder.typeProperty(typeIdPropName); 							// typeInfo.property()
		outTypeResolverBuilder = outTypeResolverBuilder.typeIdVisibility(typeIdPropVisibleByDeserializer);		// typeInfo.visible()
		outTypeResolverBuilder = outTypeResolverBuilder.defaultImpl(null);										// typeInfo.defaultImpl()
		
		return outTypeResolverBuilder;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static JsonTypeInfo _createJsonTypeInfo() {
		return new JsonTypeInfo() {
						@Override
						public As include() {
							return As.PROPERTY;			// type info is included as a json property
						}
						@Override
						public String property() {
							return CustomStreamers.TYPE_ID_PROPERTY_NAME;	// type property name is always 'typeId'
						}
						@Override
						public Id use() {
							return Id.NAME;				// type property contains the type name 
						}
						@Override
						public boolean visible() {
							return true;				// typeId is visible to concrete instances (it's needed at the custom deserializer!)
						}
						@Override
						public Class<?> defaultImpl() {
							return null;
						}
						@Override
						public Class<? extends Annotation> annotationType() {
							return JsonTypeInfo.class;
						}
				};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static boolean _isNotInstanciable(final JavaType javaType) {
		return javaType.isAbstract();
	}
	private static boolean _isNotInstanciable(final Class<?> type) {
		return type.isAnnotation()
			|| Modifier.isAbstract(type.getModifiers())	// is abstract
			|| Modifier.isInterface(type.getModifiers());	// is interface
	}
}
