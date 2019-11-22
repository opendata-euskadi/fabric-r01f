package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.PackageVersion;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.objectstreamer.util.TypeScan;
import r01f.objectstreamer.util.TypeScan.TypeAnnotation;
import r01f.types.JavaPackage;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Annotation introspector
 *
 * The jackson annotation introspector type hierarchy is like:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[JacksonAnnotationIntrospector]	<-- this is where jackson json annotations are detected
 * 			|		|--[JacksonXmlAnnotationIntrospector] 	<-- this is where jackson xml annotations are detected
 * 			|--[XmlJaxbAnnotationIntrospector]	<-- this is where Jaxb annotations are detected
 * </pre>
 *
 * Here, delegation is used instead inheritance:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[MarshallerAnnotationIntrospector]
 * 						|--(delegates)-> [JacksonAnnotationIntrospector]
 * <pre>
 */
@Slf4j
public class MarshallerAnnotationIntrospector
     extends AnnotationIntrospector {

	private static final long serialVersionUID = -8142099488664415041L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	 // The java packages that contains objects to be marshalled
	 // (it's used to scan for subtypes of abstract types -see TypeScan.java-)
	protected final Collection<JavaPackage> _javaPackagess;

	// module setup context
	private final Module.SetupContext _moduleSetupContext;

	// delegated jackson introspector
	private final JacksonAnnotationIntrospector _delegatedJacksonAnnotationIntrospector;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerAnnotationIntrospector(final Collection<JavaPackage> javaPackages,
											final Module.SetupContext context) {
		_javaPackagess = javaPackages;
		_moduleSetupContext = context;
		_delegatedJacksonAnnotationIntrospector = new JacksonAnnotationIntrospector();
	}
	@Override
	public Version version() {
		return PackageVersion.VERSION; // VersionUtil.versionFor(MarshallerAnnotationIntrospector.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IGNORE PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember am) {
		// [0] - only check fields!
		if ( !(am instanceof AnnotatedField) ) return false; // _delegatedJacksonAnnotationIntrospector.hasIgnoreMarker(am);

		boolean isIgnorable = false;

		// [1] - if transient field, ignore
		AnnotatedField field = (AnnotatedField)am;
		isIgnorable = Modifier.isTransient(field.getModifiers());

		// [2] - check @FieldMarshallIgnored annotation
		if (isIgnorable == false) isIgnorable = am.hasAnnotation(MarshallIgnoredField.class);
		//		 ... or the default Jackson annotations
		if (isIgnorable == false) isIgnorable = _delegatedJacksonAnnotationIntrospector.hasIgnoreMarker(am);


        return isIgnorable;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	NAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PropertyName findRootName(final AnnotatedClass ac) {
		return _findName(ac,
						 NameFor.ROOT);
	}
	@Override
	public PropertyName findNameForSerialization(final Annotated ann) {
    	return _findName(ann,
    					 NameFor.SERIALIZATION);
	}
	@Override
	public PropertyName findNameForDeserialization(final Annotated ann) {
    	return _findName(ann,
    					 NameFor.DESERIALIZATION);
	}
    private enum NameFor {
    	SERIALIZATION,
    	DESERIALIZATION,
    	ROOT;
    }
	private PropertyName _findName(final Annotated ann,
								   final NameFor whatFor) {
		PropertyName propertyName = null;

		// [0] - Do NOT mind annotated getters
		if (ann instanceof AnnotatedMethod) return null;

		// [1] - @MarshallFrom annotated constructor parameter
		if (ann instanceof AnnotatedParameter
		 && ann.hasAnnotation(MarshallFrom.class)) {
			MarshallFrom m = ann.getAnnotation(MarshallFrom.class);
			if (!MarshallFrom.MARKER_FOR_DEFAULT.equals(m.value())) {
				propertyName = PropertyName.construct(m.value());
			} else {
				propertyName = PropertyName.construct(ann.getName());
			}
		}

		// [2] - @TypeMarshall annotated type
		else if (ann instanceof AnnotatedClass
			  && ann.hasAnnotation(MarshallType.class)) {
			MarshallType m = ann.getAnnotation(MarshallType.class);
			if (!MarshallType.MARKER_FOR_DEFAULT.equals(m.as())) {
				propertyName = PropertyName.construct(m.as());
			} else {
				propertyName = PropertyName.construct(_normalizeField(ann.getName()));
			}
		}
		// [3] - @FieldMarshall annotated field
		else if (ann instanceof AnnotatedField
			  && ann.hasAnnotation(MarshallField.class)) {
			MarshallField m = ann.getAnnotation(MarshallField.class);
			if (!MarshallField.MARKER_FOR_DEFAULT.equals(m.as())) {
				propertyName = PropertyName.construct(m.as());
			} else {
				propertyName = PropertyName.construct(_normalizeField(ann.getName()));
			}
		}

		// [5] - if name could not be found delegate to native jackson annotations
		if (propertyName == null
		 || propertyName == PropertyName.USE_DEFAULT) {
			switch(whatFor) {
			case SERIALIZATION:
				propertyName = _delegatedJacksonAnnotationIntrospector.findNameForSerialization(ann);
				break;
			case DESERIALIZATION:
				propertyName = _delegatedJacksonAnnotationIntrospector.findNameForDeserialization(ann);
				break;
			case ROOT:
				propertyName = _delegatedJacksonAnnotationIntrospector.findRootName((AnnotatedClass)ann);
				break;
			}
		}
		// return
		return propertyName;
	}
	private String _normalizeField(final String name) {
		if (name.startsWith("_")) return name.substring(1);
		return name;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FORMAT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public JsonFormat.Value findFormat(final Annotated ann) {
		JsonFormat.Value outFormat = null;

		// [0] - Do NOT mind annotated getters
		if ( !(ann instanceof AnnotatedField) ) return null;

		// [1] - Check MarshallField annotation
		AnnotatedField annF = (AnnotatedField)ann;

		if (_isDateType(annF.getRawType())) {
			if (ann.hasAnnotation(MarshallField.class)) {
				MarshallField mf = ann.getAnnotation(MarshallField.class);
				if (mf.dateFormat() != null) {
					DateFormat dateFormat = mf.dateFormat().use();
					switch(dateFormat) {
					case CUSTOM:
						String pattern = mf.dateFormat().format();
						String timezone = mf.dateFormat().timezone();
						if (MarshallField.MARKER_FOR_DEFAULT.equals(pattern)) throw new AnnotationFormatError(String.format("Field %s of %s is a Date field with CUSTOM format: the pattern is mandatory",
																															annF.getName(),annF.getDeclaringClass().getName()));
						if (!"".equals(timezone)) {
							TimeZone tz = TimeZone.getTimeZone(timezone);
							outFormat = JsonFormat.Value.forShape(Shape.STRING)
														.withTimeZone(tz)
														.withPattern(pattern);
						} else {
							outFormat = JsonFormat.Value.forShape(Shape.STRING)
														.withPattern(pattern);
						}
						break;
					case EPOCH:
						outFormat = JsonFormat.Value.forShape(Shape.STRING)
													.withPattern(Dates.EPOCH);
						break;
					case ISO8601:
						outFormat = JsonFormat.Value.forShape(Shape.STRING)
													.withPattern(Dates.ISO8601);
						break;
					case TIMESTAMP:
					default:
						outFormat = JsonFormat.Value.forShape(Shape.NUMBER);
						break;
					}
				}
			}
		}
		// warn if annotating a no-date-typed field
		else if (ann.hasAnnotation(MarshallField.class)) {
			MarshallField mf = ann.getAnnotation(MarshallField.class);
			if (mf.dateFormat() != null
			 && _isDateType(annF.getRawType())) {
				log.warn("Field {} of type {} was annotated with @{}(dateFormat=...) BUT the field is NOT a date-type field!",
						 annF.getName(),annF.getDeclaringClass(),MarshallField.class.getSimpleName());
			}
		}

		// [2] - Delegate
		if (outFormat == null) outFormat = _delegatedJacksonAnnotationIntrospector.findFormat(ann);

		// [3] - Return
		return outFormat;
	}
	private static final boolean _isDateType(final Class<?> type) {
		return type == Date.class
			|| type == java.sql.Date.class;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TYPE RESOLVING
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config,
												   final AnnotatedClass ac,final JavaType baseType) {
    	TypeResolverBuilder<?> outTypeResolverBuilder = null;

    	// try custom type resolver builders
    	outTypeResolverBuilder = MarshallerTypeResolverBuilderDelegates.findTypeResolver(config,
    																					 ac,baseType);

    	// just delegate
    	if (outTypeResolverBuilder == null) outTypeResolverBuilder = _delegatedJacksonAnnotationIntrospector.findTypeResolver(config,
																															  ac,baseType);
    	return outTypeResolverBuilder;
	}
	@Override
	public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config,
														   final AnnotatedMember am,final JavaType baseType) {
		TypeResolverBuilder<?> outTypeResolverBuilder = null;

        // As per definition of @JsonTypeInfo, should only apply to contents of container types (collection, map),
		// not container types themselves:
        if (baseType.isContainerType()
         || baseType.isReferenceType()) return null;

    	// try custom type resolver builders
    	outTypeResolverBuilder = MarshallerTypeResolverBuilderDelegates.findPropertyTypeResolver(config,
    																							 am,baseType);

    	// just delegate
    	if (outTypeResolverBuilder == null) outTypeResolverBuilder = _delegatedJacksonAnnotationIntrospector.findPropertyTypeResolver(config,
																															  		  am,baseType);
    	return outTypeResolverBuilder;
	}
	@Override
	public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config,
																  final AnnotatedMember am,final JavaType containerType) {
		TypeResolverBuilder<?> outTypeResolverBuilder = null;

        // ensure property is a container type
        if (containerType.getContentType() == null) throw new AnnotationFormatError("Must call method with a container or reference type (got " + containerType + ")");

    	// try custom type resolver builders
    	outTypeResolverBuilder = MarshallerTypeResolverBuilderDelegates.findPropertyContentTypeResolver(config,
    																									am,containerType);

    	// just delegate
    	if (outTypeResolverBuilder == null) outTypeResolverBuilder = _delegatedJacksonAnnotationIntrospector.findPropertyContentTypeResolver(config,
																															  		  		 am,containerType);
    	return outTypeResolverBuilder;
	}

    // A cache of sub-types by abstract type
    private final Map<Class<?>,List<NamedType>> _subTypesByAbstractType = Maps.newHashMap();

	@Override
    public List<NamedType> findSubtypes(final Annotated ann) {
    	List<NamedType> outTypes = null;

    	if ( !(ann instanceof AnnotatedClass) ) return _delegatedJacksonAnnotationIntrospector.findSubtypes(ann);

    	// [0] - Find the type to be handled
    	//		 (when collections, handle the collection elements type)
		Class<?> type = null;
		if (ann.getType() != null) {
			if (ann.getType().isCollectionLikeType() || ann.getType().isArrayType()) {
				type = ann.getType().getContentType().getRawClass();
			} else {
				type = ann.getType().getRawClass();
			}
		} else {
			type = ann.getRawType();
		}

		// [1] - Find the type for which the subtypes might have to be found
		Class<?> keyType = _keyTypeFor(type);

		// [2] - Find subtypes
    	if (keyType != null) {
    		outTypes = _subTypesByAbstractType.get(keyType);	// check the cache
    		if (outTypes == null) {		// BEWARE!! do NOT use CollectionUtils.isNullOrEmpty()!!!!
    			Set<?> subTypes = TypeScan.findSubTypesOfInJavaPackages(keyType,
    													  				_javaPackagess);
    			log.trace("{} has {} subtypes",
    					  keyType,(subTypes != null ? subTypes.size() : 0));

    			if (CollectionUtils.hasData(subTypes)) {
    				outTypes = Lists.newArrayListWithExpectedSize(subTypes.size());
    			} else {
    				outTypes = Lists.newArrayListWithExpectedSize(0);
    			}
    			for (Object subTypeObj : subTypes) {
    				Class<?> subType = (Class<?>)subTypeObj;

    				// find the typeId from the @TypeMarshall annotation or @JsonRootName
    				// ... if none of these annotations is present, use the full class name
    				String typeId = _findTypeIdFor(subType);

    				// register the type with it's id
    				outTypes.add(new NamedType(subType,
    										   typeId));
    			}
	    		// cache
	    		_subTypesByAbstractType.put(keyType,outTypes);

		    	// debug
//		    	System.out.println("====>" + " > " + keyType + " > " + outTypes);
		    	log.trace("SubType Info for {}: {}",
		    			  keyType,outTypes);
    		}
    	}

    	// if not found, delegate
    	if (outTypes == null) outTypes = _delegatedJacksonAnnotationIntrospector.findSubtypes(ann);
    	return outTypes;
    }
	private Class<?> _keyTypeFor(final Class<?> type) {
		Class<?> outKeyType = null;
		if (_isCollectionOrArrayType(type)) return null;
		if (_isNotInstanciable(type)) {
			outKeyType = type;
		} else {
			// find the @MarshallPolymorphicTypeInfo in the hierarchy
			TypeAnnotation<MarshallPolymorphicTypeInfo> typeWithPolyAnn = TypeScan.findTypeAnnotaion(MarshallPolymorphicTypeInfo.class,
													  			 		 							 type);
			// if found, the type annotated is the key
			if (typeWithPolyAnn != null) {
				outKeyType = typeWithPolyAnn.getType();
			} else if (type == Object.class) {
				// object!! cannot know the subtypes
				throw new IllegalStateException("Connot find subtypes of Object.class: this is usually originated at a willcard-parameterized field like final Person<?> _person; a more type specific parameter like Person<? extends PersonID> _person is needed");
			}
		}
		return outKeyType;
	}
	private String _findTypeIdFor(final Class<?> subType) {
		String typeId = null;
		MarshallType typeMarshallAnn = subType.getAnnotation(MarshallType.class);
		if (typeMarshallAnn != null) {
			typeId = MarshallType.MARKER_FOR_DEFAULT.equals(typeMarshallAnn.typeId()) ? typeMarshallAnn.as()
																					  : typeMarshallAnn.typeId();
		} else {
			JsonRootName jsonRootNameAnn = subType.getAnnotation(JsonRootName.class);
			if (jsonRootNameAnn != null) typeId = jsonRootNameAnn.value();
		}
		if (Strings.isNullOrEmpty(typeId)) {
			log.error("Type {} does NOT have the @{} annotation, the full class name will be used as typeId",
					  subType,MarshallType.class.getSimpleName());
			typeId = subType.getName();
		}
		return typeId;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static boolean _isNotInstanciable(final Class<?> type) {
    	return type.isAnnotation()
    		|| Modifier.isAbstract(type.getModifiers())		// is abstract
    		|| Modifier.isInterface(type.getModifiers());	// is interface
    }
	private static boolean _isCollectionOrArrayType(final Class<?> type) {
		return Map.class.isAssignableFrom(type)
			|| Collection.class.isAssignableFrom(type)
			|| type.isArray();
	}
	private static boolean _isInstanciable(final Class<?> type) {
		return !_isNotInstanciable(type);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE
//  =====================================================================================
// 	Beware that since this type extends AnnotationIntrospector, ONLY methods overridden
//	at JacksonAnnotationIntrospector are delegated
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isAnnotationBundle(final Annotation ann) {
		return _delegatedJacksonAnnotationIntrospector.isAnnotationBundle(ann);
	}
	@Override @Deprecated // since 2.8
	public String findEnumValue(final Enum<?> value) {
		return _delegatedJacksonAnnotationIntrospector.findEnumValue(value);
	}
	@Override
	public String[] findEnumValues(final Class<?> enumType,
								   final Enum<?>[] enumValues,final String[] names) {
		return _delegatedJacksonAnnotationIntrospector.findEnumValues(enumType,
																	  enumValues,names);
	}
	@Override
	public Enum<?> findDefaultEnumValue(final Class<Enum<?>> enumCls) {
		return _delegatedJacksonAnnotationIntrospector.findDefaultEnumValue(enumCls);
	}
	@Override
	public Value findPropertyIgnorals(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyIgnorals(a);
	}
	@Override
	public Boolean isIgnorableType(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.isIgnorableType(ac);
	}
	@Override
	public Object findFilterId(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findFilterId(a);
	}
	@Override
	public Object findNamingStrategy(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findNamingStrategy(ac);
	}
	@Override
	public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac,
														 final VisibilityChecker<?> checker) {
		return _delegatedJacksonAnnotationIntrospector.findAutoDetectVisibility(ac,
																				checker);
	}
	@Override
	public String findTypeName(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findTypeName(ac);
	}
	@Override
	public Boolean isTypeId(final AnnotatedMember member) {
		return _delegatedJacksonAnnotationIntrospector.isTypeId(member);
	}
	@Override
	public String findClassDescription(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findClassDescription(ac);
	}
	@Override
	public String findImplicitPropertyName(final AnnotatedMember m) {
		return _delegatedJacksonAnnotationIntrospector.findImplicitPropertyName(m);
	}
	@Override
	public List<PropertyName> findPropertyAliases(Annotated m) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyAliases(m);
	}
	@Override
	public Boolean hasRequiredMarker(final AnnotatedMember m) {
		return _delegatedJacksonAnnotationIntrospector.hasRequiredMarker(m);
	}
	@Override
	public Access findPropertyAccess(final Annotated m) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyAccess(m);
	}
	@Override
	public String findPropertyDescription(final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyDescription(ann);
	}
	@Override
	public Integer findPropertyIndex(final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyIndex(ann);
	}
	@Override
	public String findPropertyDefaultValue(final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyDefaultValue(ann);
	}
	@Override
	public ReferenceProperty findReferenceType(final AnnotatedMember member) {
		return _delegatedJacksonAnnotationIntrospector.findReferenceType(member);
	}
	@Override
	public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
		return _delegatedJacksonAnnotationIntrospector.findUnwrappingNameTransformer(member);
	}
	@Override
	public JacksonInject.Value findInjectableValue(final AnnotatedMember m) {
		return _delegatedJacksonAnnotationIntrospector.findInjectableValue(m);
	}
	@Override @Deprecated
	public Object findInjectableValueId(final AnnotatedMember m) {
		return _delegatedJacksonAnnotationIntrospector.findInjectableValueId(m);
	}
	@Override
	public Class<?>[] findViews(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findViews(a);
	}
	@Override
	public AnnotatedMethod resolveSetterConflict(final MapperConfig<?> config,
												 final AnnotatedMethod setter1,final AnnotatedMethod setter2) {
		return _delegatedJacksonAnnotationIntrospector.resolveSetterConflict(config,
																			 setter1,setter2);
	}
	@Override
	public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findObjectIdInfo(ann);
	}
	@Override
	public ObjectIdInfo findObjectReferenceInfo(final Annotated ann,
												final ObjectIdInfo objectIdInfo) {
		return _delegatedJacksonAnnotationIntrospector.findObjectReferenceInfo(ann,
																			   objectIdInfo);
	}
	@Override
	public Object findSerializer(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findSerializer(a);
	}
	@Override
	public Object findKeySerializer(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findKeySerializer(a);
	}
	@Override
	public Object findContentSerializer(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findContentSerializer(a);
	}
	@Override
	public Object findNullSerializer(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findNullSerializer(a);
	}
	@Override
	public JsonInclude.Value findPropertyInclusion(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyInclusion(a);
	}
	@Override
	public Typing findSerializationTyping(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationTyping(a);
	}
	@Override
	public Object findSerializationConverter(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationConverter(a);
	}
	@Override
	public Object findSerializationContentConverter(final AnnotatedMember a) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationContentConverter(a);
	}
	@Override
	public JavaType refineSerializationType(final MapperConfig<?> config,
											final Annotated a,final JavaType baseType)
			throws JsonMappingException {
		return _delegatedJacksonAnnotationIntrospector.refineSerializationType(config,
																			   a,baseType);
	}
	@Override @Deprecated
	public Class<?> findSerializationKeyType(final Annotated am,final JavaType baseType) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationKeyType(am,baseType);
	}
	@Override @Deprecated
	public Class<?> findSerializationContentType(final Annotated am,final JavaType baseType) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationContentType(am,baseType);
	}
	@Override
	public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationPropertyOrder(ac);
	}
	@Override
	public Boolean findSerializationSortAlphabetically(final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationSortAlphabetically(ann);
	}
	@Override
	public void findAndAddVirtualProperties(final MapperConfig<?> config,
											final AnnotatedClass ac,final List<BeanPropertyWriter> properties) {
		_delegatedJacksonAnnotationIntrospector.findAndAddVirtualProperties(config,
																			ac,properties);
	}
	@Override
	public Boolean hasAsValue(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.hasAsValue(a);
	}
	@Override
	public Boolean hasAnyGetter(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.hasAnyGetter(a);
	}
	@Override @Deprecated
	public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
		return _delegatedJacksonAnnotationIntrospector.hasAnyGetterAnnotation(am);
	}
	@Override @Deprecated
	public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
		return _delegatedJacksonAnnotationIntrospector.hasAsValueAnnotation(am);
	}
	@Override
	public Object findDeserializer(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializer(a);
	}
	@Override
	public Object findKeyDeserializer(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findKeyDeserializer(a);
	}
	@Override
	public Object findContentDeserializer(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findContentDeserializer(a);
	}
	@Override
	public Object findDeserializationConverter(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializationConverter(a);
	}
	@Override
	public Object findDeserializationContentConverter(final AnnotatedMember a) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializationContentConverter(a);
	}
	@Override
	public JavaType refineDeserializationType(final MapperConfig<?> config,
											  final Annotated a,final JavaType baseType) throws JsonMappingException {
		return _delegatedJacksonAnnotationIntrospector.refineDeserializationType(config,
																				 a,baseType);
	}
	@Override @Deprecated
	public Class<?> findDeserializationContentType(final Annotated am,final JavaType baseContentType) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializationContentType(am,baseContentType);
	}
	@Override @Deprecated
	public Class<?> findDeserializationType(final Annotated am,final JavaType baseType) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializationType(am, baseType);
	}
	@Override @Deprecated
	public Class<?> findDeserializationKeyType(final Annotated am,final JavaType baseKeyType) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializationKeyType(am,baseKeyType);
	}
	@Override
	public Object findValueInstantiator(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findValueInstantiator(ac);
	}
	@Override
	public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findPOJOBuilder(ac);
	}
	@Override
	public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findPOJOBuilderConfig(ac);
	}
	@Override
	public Boolean hasAnySetter(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.hasAnySetter(a);
	}
	@Override
	public JsonSetter.Value findSetterInfo(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findSetterInfo(a);
	}
	@Override
	public Boolean findMergeInfo(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findMergeInfo(a);
	}
	@Override @Deprecated
	public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
		return _delegatedJacksonAnnotationIntrospector.hasAnySetterAnnotation(am);
	}
	@Override @Deprecated
	public boolean hasCreatorAnnotation(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.hasCreatorAnnotation(a);
	}
	@Override @Deprecated
	public Mode findCreatorBinding(final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findCreatorBinding(a);
	}
	@Override
	public Mode findCreatorAnnotation(final MapperConfig<?> config,
									  final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.findCreatorAnnotation(config,
																			 a);
	}
}
