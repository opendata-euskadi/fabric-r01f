package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
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
		
		TypeAnnotation<MarshallPolymorphicTypeInfo> typeWithPolyAnn = TypeScan.findTypeAnnotaion(MarshallPolymorphicTypeInfo.class,
												  			 		 				 		 	 javaType);
		if (typeWithPolyAnn == null) {
			if (builderFor == BuilderFor.PROPERTY 
			 && javaType.getRawClass() == Object.class) {
				// this is the case of willcard-parameterized fields
				//			public class TestPersonContainerBean {
				//				@MarshallField(as="thePerson")
				//				@Getter @Setter private Person<?> _person;		<-- the type of the parameter is NOT known (object)
				//			}
				//	where Person type is like:
				//		public class Person<T> {
				//			@MarshallField(as="id")
				//			@Getter @Setter private T _id;
				//		}
//				outTypeResolverBuilder =  _buildBuilderForGenericTypeResolverUsingProperty(CustomStreamers.TYPE_ID_PROPERTY_NAME,	// the typeId property name
//																					  	   false);									// is the typeId property available at the deserializer?
			}
		}
		else {
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
		// System.out.println(">> type resolver builder for " + builderFor + " at " + javaType + " > " + outTypeResolverBuilder);
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
        TypeResolverBuilder<?> outTypeResolverBuilder;
    	    	
    	// [1] - Manually create the JsonTypeInfo data 
//        JsonTypeInfo typeInfo = _createJsonTypeInfo();
        
		// [2] - Create the type resolver builder and initialize it
        outTypeResolverBuilder = new StdTypeResolverBuilder();
        
        outTypeResolverBuilder = outTypeResolverBuilder.init(JsonTypeInfo.Id.NAME,								// typeInfo.use(), 
        													 null); 											// TypeIdResolver
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
