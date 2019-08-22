package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
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
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import r01f.guids.CommonOIDs.AppCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.JavaPackage;
import r01f.util.types.Strings;

/**
 * The jackson annotation introspector type hierarchy is like:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[JacksonAnnotationIntrospector]	<-- this is where jackson json annotations are detected
 * 			|		|--[JacksonXmlAnnotationIntrospector] 	<-- this is where jackson xml annotations are detected
 * 			|--[JaxbAnnotationIntrospector]	<-- this is where Jaxb annotations are detected
 * 					|-[XmlJaxbAnnotationIntrospector] (deprecated!!)
 * </pre>
 *
 * when it comes to xml annotation introspection:
 * <pre>
 * 		 [AnnotationIntrospector]
 * 			|
 * 			|--[JaxbAnnotationIntrospector] <-- does NOT implements XmlAnnotationIntrospector
 *
 * 		 [XmlAnnotationIntrospector]
 * 			|
 * 			|--[JacksonXmlAnnotationIntrospector] extends JacksonAnnotationIntrospector that also extends AnnotationIntrospector
 * 			|
 * 			|--[XmlJaxbAnnotationIntrospector] (deprecated!!!) > use [JaxbAnnotationIntrospector] which does NOT implements [XmlAnnotationIntrospector]
 * 			|
 * 			|
 * 			|--[Pair]	<-- it's used to pair an [XmlAnnotationIntrospector] and a [JaxbAnnotationIntrospector]
 * 							... since [JaxbAnnotationIntrospector DOES NOT implements [XmlAnnotationIntrospector],
 * 								the [JaxbWrapper] is used to wrap [JaxbAnnotationIntrospector] into a [XmlAnnotationIntrospector]
 * </pre>
 *
 * Here, delegation is used instead inheritance:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[MarshallerXmlAnnotationIntrospector]
 * 						|--(delegates)-> (1) [JacksonXmlAnnotationIntrospector]
 * 										 (2) [JaxbAnnotationIntrospector]
 * 										 (3) [MarshallerAnnotationIntrospector]		(the usual introspector)
 * <pre>
 */
public class MarshallerXmlAnnotationIntrospector
     extends AnnotationIntrospector
  implements XmlAnnotationIntrospector {

	private static final long serialVersionUID = 4120643020074142400L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    private static final String JAXB_MARKER_FOR_DEFAULT = "##default";

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	 // The java packages that contains objects to be marshalled
	 // (it's used to scan for subtypes of abstract types -see TypeScan.java-)
	protected final Collection<JavaPackage> _javaPackages;

	// module setup context
	private final Module.SetupContext _moduleSetupContext;

    // Delegates
	private final JacksonXmlAnnotationIntrospector _jacksonXmlAnnotationIntrospector;
	private final JaxbAnnotationIntrospector _jaxbAnnotationIntrospector;
	private final MarshallerAnnotationIntrospector _marshallerAnnotationIntrospector;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerXmlAnnotationIntrospector(final Collection<JavaPackage> javaPackages,
											   final Module.SetupContext context) {
		// app codes
		_javaPackages = javaPackages;

		// context
		_moduleSetupContext = context;

		// delegates
		_jacksonXmlAnnotationIntrospector = new JacksonXmlAnnotationIntrospector();
		_jaxbAnnotationIntrospector = new JaxbAnnotationIntrospector(context.getTypeFactory());
		_marshallerAnnotationIntrospector = new MarshallerAnnotationIntrospector(javaPackages,
																				 context);
	}
	@Override
	public Version version() {
		return VersionUtil.versionFor(MarshallerAnnotationIntrospector.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDefaultUseWrapper(final boolean b) {
		_jacksonXmlAnnotationIntrospector.setDefaultUseWrapper(b);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ATTRIBUTE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Boolean isOutputAsAttribute(final Annotated ann) {
		Boolean outAttr = null;

		// [0] - do not mind annotated types
		if (ann instanceof AnnotatedClass) return false;

		// [1] - @FieldMarshall annotated fields
		if (ann.hasAnnotation(MarshallField.class)) {
			MarshallField fmAnn = ann.getAnnotation(MarshallField.class);
			outAttr = fmAnn.whenXml() != null ? fmAnn.whenXml().attr()
										  	  : false;
			if (fmAnn.whenXml() != null
			 && fmAnn.whenXml().asParentElementValue() == true
			 && fmAnn.whenXml().attr() == true) throw new AnnotationFormatError(String.format("Field %s of type %s is set to be serialized as xml attribute and text: both modifiers cannot be set at the same time",
																					    	  ((AnnotatedField)ann).getAnnotated().getName(),
																					    	  ((AnnotatedField)ann).getDeclaringClass()));
		}
		// [2] - Use Jackson xml annotations
		if (outAttr == null) {
			outAttr = _jacksonXmlAnnotationIntrospector.isOutputAsAttribute(ann);
		}
		// [3] - Finally use Jaxb annotations
		if (outAttr == null) {
			outAttr = _jaxbAnnotationIntrospector.isOutputAsAttribute(ann);
		}

		// [4] - check
		if (outAttr != null
		 && outAttr == true
		 && ann.getType().isAbstract()
		 && !ann.getType().isPrimitive()) {
			AnnotatedField annF = (AnnotatedField)ann;
			throw new AnnotationFormatError(String.format("Field %s of type %s is defined using an abstract/interface type (%s): it cannot be mapped as an xml attribute",
				 										  ann.getName(),annF.getDeclaringClass(),annF.getRawType()));

		}
		// return
		return outAttr;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NAME
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public PropertyName findRootName(final AnnotatedClass ac) {
    	PropertyName outPropName = null;

		// [1] - @TypeMarshall annotated
		if (ac.hasAnnotation(MarshallType.class)) {
			MarshallType m = ac.getAnnotation(MarshallType.class);
			outPropName = _combineNames(m.as(),
										m.namespace());
		}
		// [2] - native annotations
		if (outPropName == null
		 || outPropName.equals(PropertyName.USE_DEFAULT)) {
			outPropName = _findNameWithIntrospectorDelegates(ac,
															 NameFor.ROOT);
		}
		// return
		return outPropName;
    }
	@Override
	public PropertyName findNameForSerialization(final Annotated ann) {
		PropertyName outPropName = _findNameForSerializationOrDeSerialization(ann,
															 	 			  NameFor.SERIALIZATION);
		return outPropName;
	}
	@Override
	public PropertyName findNameForDeserialization(final Annotated ann) {
		PropertyName outPropName = _findNameForSerializationOrDeSerialization(ann,
															 	 			  NameFor.DESERIALIZATION);
		return outPropName;
	}
	@Override
	public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
		NameTransformer outTransformer = _marshallerAnnotationIntrospector.findUnwrappingNameTransformer(member);
		return outTransformer;
	}
    @Override
    public PropertyName findWrapperName(final Annotated ann) {
    	PropertyName outPropName = null;

    	// [1] - @MarshallField annotated
    	if (ann instanceof AnnotatedField
    	 && ann.hasAnnotation(MarshallField.class)) {
    		MarshallField fm = ann.getAnnotation(MarshallField.class);
    		// ... do NOT wrap
    		if (fm.whenCollectionLike() != null
    		 && fm.whenCollectionLike().useWrapping() == false) {
    			outPropName = PropertyName.NO_NAME;		// DO NOT WRAP
    		}
    		else if (fm.whenXml() != null
    			  && fm.whenXml().asParentElementValue() == true) {
    			outPropName = PropertyName.NO_NAME;		// DO NOT WRAP
    		}
    		else {
    			outPropName = new PropertyName(fm.as());
    		}
    	}
    	// [2] - jackson xml native annotations
		if (outPropName == null
		 || outPropName == PropertyName.USE_DEFAULT) {
			outPropName = _findNameWithIntrospectorDelegates(ann,
															 NameFor.WRAPPER);
		}
		// return
		return outPropName;
    }
    private enum NameFor {
    	SERIALIZATION,
    	DESERIALIZATION,
    	ROOT,
    	WRAPPER;
    }
    private PropertyName _findNameForSerializationOrDeSerialization(final Annotated ann,
    																final NameFor whatFor) {
    	if ( ann instanceof AnnotatedMethod ) return null;

		PropertyName outPropName = null;

		// [1] - if it's a collection-like field or type, see if an item name is set
		if (ann instanceof AnnotatedField
		 && ann.hasAnnotation(MarshallField.class)	// do NOT move!! (problem with aspects inter-type injected fields)
		 && (ann.getType().isCollectionLikeType() || ann.getType().isArrayType())) {
    		MarshallField fm = ann.getAnnotation(MarshallField.class);
    		if (fm.whenXml() != null
    		 && !fm.whenXml().collectionElementName().equals(MarshallField.MARKER_FOR_DEFAULT)) {
    			// annotated with @MarshallField and explicitly setting the collection element names
    			// 	@MarshallField(as="wrapper")
				//				   whenXml=@MarshallXmlField(collectionElementName="item"))
    			//	@Getter private Collection<MyType> _items;
    			outPropName = PropertyName.construct(fm.whenXml().collectionElementName());
    		} else {
    			// not explicitly setting the collection element names: use the name set at the collection's items
    			JavaType contentType = ann.getType().getContentType();
    			MarshallType mtAnn = contentType.getRawClass().getAnnotation(MarshallType.class);
    			if (mtAnn != null) outPropName = PropertyName.construct(mtAnn.as());
    		}
		}
		// [2] - jackson xml native annotations
		if (outPropName == null
		 || outPropName.equals(PropertyName.USE_DEFAULT)) {
			outPropName = _findNameWithIntrospectorDelegates(ann,
															 whatFor);
		}

		// return
		return outPropName;
    }
    private PropertyName _findNameWithIntrospectorDelegates(final Annotated ann,
								   				  	 		final NameFor whatFor) {
    	PropertyName outPropName = null;
		// [1] - jackson xml native annotations
		if (outPropName == null
		 || outPropName.equals(PropertyName.USE_DEFAULT)) {
			outPropName = _delegateFindName(_jacksonXmlAnnotationIntrospector,
											ann,
											whatFor);
		}
		// [2] - jaxb annotations
		if (outPropName == null
		 || outPropName.equals(PropertyName.USE_DEFAULT)) {
			outPropName = _delegateFindName(_jaxbAnnotationIntrospector,
										  	ann,
											whatFor);
		}
		// [3] - finally the marshaller
		if (outPropName == null
		 || outPropName.equals(PropertyName.USE_DEFAULT)) {
			outPropName = _delegateFindName(_marshallerAnnotationIntrospector,
											ann,
											whatFor);
		}
		// [4] - default
		if (outPropName == null) outPropName = PropertyName.USE_DEFAULT;

		// [5] - Return
		return outPropName;
    }
    private static PropertyName _delegateFindName(final AnnotationIntrospector annIntrospector,
    											  final Annotated ann,
    											  final NameFor whatFor) {
    	PropertyName outPropName = null;
		if (outPropName == null
		 || outPropName == PropertyName.USE_DEFAULT) {
			switch(whatFor) {
			case SERIALIZATION:
				outPropName = annIntrospector.findNameForSerialization(ann);
				break;
			case DESERIALIZATION:
				outPropName = annIntrospector.findNameForDeserialization(ann);
				break;
			case ROOT:
				outPropName = annIntrospector.findRootName((AnnotatedClass)ann);
				break;
			case WRAPPER:
				outPropName = annIntrospector.findWrapperName(ann);
			}
		}
		return outPropName;
    }
    private static PropertyName _combineNames(final String localName,
    										  final String namespace) {
        if (Strings.isNullOrEmpty(namespace)) {
            return new PropertyName(localName);
        }
        return new PropertyName(localName,namespace);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	TEXT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Boolean isOutputAsText(final Annotated ann) {
		Boolean outText = null;

		// Allows specifying that value of one property is to be serialized as "unwrapped" text, and not in an element.
		if (ann instanceof AnnotatedField) {
			AnnotatedField annF = (AnnotatedField)ann;
			MarshallField fmAnn = ann.getAnnotation(MarshallField.class);
			if (fmAnn != null
			 && fmAnn.whenXml() != null) {

				outText = fmAnn.whenXml().asParentElementValue();

				if (fmAnn.whenXml().attr() == true
				 && outText == true) throw new AnnotationFormatError(String.format("Field %s of type %s is set to be serialized as xml attribute and text: both modifiers cannot be set at the same time",
																				   ((AnnotatedField)ann).getAnnotated().getName(),
																				   ((AnnotatedField)ann).getDeclaringClass()));
			}
			else if ((annF.getType().isCollectionLikeType() || annF.getType().isCollectionLikeType())
				  && fmAnn != null
				  && fmAnn.whenCollectionLike() != null
    		      && fmAnn.whenCollectionLike().useWrapping() == false) {
    			outText = true;
    		}
		}
		return outText != null ? outText
							   : _jacksonXmlAnnotationIntrospector.isOutputAsText(ann);		// just delegate
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CDATA
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Boolean isOutputAsCData(final Annotated ann) {
    	Boolean outCDATA = null;
		// [1] - @FieldMarshall annotated
		if (ann.hasAnnotation(MarshallField.class)) {
			MarshallField fm = ann.getAnnotation(MarshallField.class);
			outCDATA = fm.escape();
		}
		if (outCDATA == null
		 && ann.hasAnnotation(MarshallField.class)) {
			MarshallField fm = ann.getAnnotation(MarshallField.class);
			outCDATA = fm.escape();
		}
		// [2] - jackson xml native annotations
		if (outCDATA == null) {
			outCDATA = _jacksonXmlAnnotationIntrospector.isOutputAsCData(ann);
		}
		// [3] - jaxb annotations
		if (outCDATA == null) {
            //There is no CData annotation in JAXB
            return null;
		}
		// return
		return outCDATA;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NAMESPACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public String findNamespace(final Annotated ann) {
		String outNameSpace = null;

		// [1] - @FieldMarshall annotated fields
		if (ann.hasAnnotation(MarshallField.class)) {
			MarshallField m = ann.getAnnotation(MarshallField.class);
			outNameSpace = m.namespace();
		}
    	// [2] - @TypeMarshall annotated types
		if (Strings.isNullOrEmpty(outNameSpace)
		 && ann.hasAnnotation(MarshallType.class)) {
    		MarshallType t = ann.getAnnotation(MarshallType.class);
    		outNameSpace = t.namespace();
	    }

    	// [3] - jackson xml annotations
    	if (Strings.isNullOrEmpty(outNameSpace)) {
    		outNameSpace = _jacksonXmlAnnotationIntrospector.findNamespace(ann);
    	}
    	// [4] - jaxb annotations
    	if (Strings.isNullOrEmpty(outNameSpace)) {
    		outNameSpace = _jaxbAnnotationIntrospector.findNamespace(ann);
    	}

    	// checkings
    	if (JAXB_MARKER_FOR_DEFAULT.equals(outNameSpace)) outNameSpace = null;
    	if (Strings.isNullOrEmpty(outNameSpace)) outNameSpace = null;

    	// return
    	return outNameSpace;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE
//  =====================================================================================
// 	Beware that since this type extends AnnotationIntrospector, ONLY methods overridden
//	at JacksonAnnotationIntrospector are delegated
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember am) {
    	return _marshallerAnnotationIntrospector.hasIgnoreMarker(am);
    }
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config,
    											   final AnnotatedClass ac,final JavaType baseType) {
		return _marshallerAnnotationIntrospector.findTypeResolver(config, ac, baseType);
	}
    @Override
	public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config,
														   final AnnotatedMember am,final JavaType baseType) {
		return _marshallerAnnotationIntrospector.findPropertyTypeResolver(config, am, baseType);
	}
	@Override
	public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config,
																  final AnnotatedMember am,final JavaType containerType) {
		return _marshallerAnnotationIntrospector.findPropertyContentTypeResolver(config, am, containerType);
	}
	@Override
	public List<NamedType> findSubtypes(final Annotated ann) {
		return _marshallerAnnotationIntrospector.findSubtypes(ann);
	}
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isAnnotationBundle(final Annotation ann) {
		return _marshallerAnnotationIntrospector.isAnnotationBundle(ann);
	}
	@Override @Deprecated // since 2.8
	public String findEnumValue(final Enum<?> value) {
		return _marshallerAnnotationIntrospector.findEnumValue(value);
	}
	@Override
	public String[] findEnumValues(final Class<?> enumType,
								   final Enum<?>[] enumValues,final String[] names) {
		return _marshallerAnnotationIntrospector.findEnumValues(enumType,
															    enumValues,names);
	}
	@Override
	public Enum<?> findDefaultEnumValue(final Class<Enum<?>> enumCls) {
		return _marshallerAnnotationIntrospector.findDefaultEnumValue(enumCls);
	}
	@Override
	public Value findPropertyIgnorals(final Annotated a) {
		return _marshallerAnnotationIntrospector.findPropertyIgnorals(a);
	}
	@Override
	public Boolean isIgnorableType(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.isIgnorableType(ac);
	}
	@Override
	public Object findFilterId(final Annotated a) {
		return _marshallerAnnotationIntrospector.findFilterId(a);
	}
	@Override
	public Object findNamingStrategy(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.findNamingStrategy(ac);
	}
	@Override
	public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac,
														 final VisibilityChecker<?> checker) {
		return _marshallerAnnotationIntrospector.findAutoDetectVisibility(ac,
																		  checker);
	}
	@Override
	public String findTypeName(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.findTypeName(ac);
	}
	@Override
	public Boolean isTypeId(final AnnotatedMember member) {
		return _marshallerAnnotationIntrospector.isTypeId(member);
	}
	@Override
	public String findClassDescription(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.findClassDescription(ac);
	}
	@Override
	public String findImplicitPropertyName(final AnnotatedMember m) {
		return _marshallerAnnotationIntrospector.findImplicitPropertyName(m);
	}
	@Override
	public List<PropertyName> findPropertyAliases(Annotated m) {
		return _marshallerAnnotationIntrospector.findPropertyAliases(m);
	}
	@Override
	public Boolean hasRequiredMarker(final AnnotatedMember m) {
		return _marshallerAnnotationIntrospector.hasRequiredMarker(m);
	}
	@Override
	public Access findPropertyAccess(final Annotated m) {
		return _marshallerAnnotationIntrospector.findPropertyAccess(m);
	}
	@Override
	public String findPropertyDescription(final Annotated ann) {
		return _marshallerAnnotationIntrospector.findPropertyDescription(ann);
	}
	@Override
	public Integer findPropertyIndex(final Annotated ann) {
		return _marshallerAnnotationIntrospector.findPropertyIndex(ann);
	}
	@Override
	public String findPropertyDefaultValue(final Annotated ann) {
		return _marshallerAnnotationIntrospector.findPropertyDefaultValue(ann);
	}
	@Override
	public JsonFormat.Value findFormat(final Annotated ann) {
		return _marshallerAnnotationIntrospector.findFormat(ann);
	}
	@Override
	public ReferenceProperty findReferenceType(final AnnotatedMember member) {
		return _marshallerAnnotationIntrospector.findReferenceType(member);
	}
	@Override
	public JacksonInject.Value findInjectableValue(final AnnotatedMember m) {
		return _marshallerAnnotationIntrospector.findInjectableValue(m);
	}
	@Override @Deprecated
	public Object findInjectableValueId(final AnnotatedMember m) {
		return _marshallerAnnotationIntrospector.findInjectableValueId(m);
	}
	@Override
	public Class<?>[] findViews(final Annotated a) {
		return _marshallerAnnotationIntrospector.findViews(a);
	}
	@Override
	public AnnotatedMethod resolveSetterConflict(final MapperConfig<?> config,
												 final AnnotatedMethod setter1,final AnnotatedMethod setter2) {
		return _marshallerAnnotationIntrospector.resolveSetterConflict(config,
																	   setter1,setter2);
	}
	@Override
	public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
		return _marshallerAnnotationIntrospector.findObjectIdInfo(ann);
	}
	@Override
	public ObjectIdInfo findObjectReferenceInfo(final Annotated ann,
												final ObjectIdInfo objectIdInfo) {
		return _marshallerAnnotationIntrospector.findObjectReferenceInfo(ann,
																		 objectIdInfo);
	}
	@Override
	public Object findSerializer(final Annotated a) {
		return _marshallerAnnotationIntrospector.findSerializer(a);
	}
	@Override
	public Object findKeySerializer(final Annotated a) {
		return _marshallerAnnotationIntrospector.findKeySerializer(a);
	}
	@Override
	public Object findContentSerializer(final Annotated a) {
		return _marshallerAnnotationIntrospector.findContentSerializer(a);
	}
	@Override
	public Object findNullSerializer(final Annotated a) {
		return _marshallerAnnotationIntrospector.findNullSerializer(a);
	}
	@Override
	public JsonInclude.Value findPropertyInclusion(final Annotated a) {
		return _marshallerAnnotationIntrospector.findPropertyInclusion(a);
	}
	@Override
	public Typing findSerializationTyping(final Annotated a) {
		return _marshallerAnnotationIntrospector.findSerializationTyping(a);
	}
	@Override
	public Object findSerializationConverter(final Annotated a) {
		return _marshallerAnnotationIntrospector.findSerializationConverter(a);
	}
	@Override
	public Object findSerializationContentConverter(final AnnotatedMember a) {
		return _marshallerAnnotationIntrospector.findSerializationContentConverter(a);
	}
	@Override
	public JavaType refineSerializationType(final MapperConfig<?> config,
											final Annotated a,final JavaType baseType)
			throws JsonMappingException {
		return _marshallerAnnotationIntrospector.refineSerializationType(config,
																		 a,baseType);
	}
	@Override @Deprecated
	public Class<?> findSerializationKeyType(final Annotated am,final JavaType baseType) {
		return _marshallerAnnotationIntrospector.findSerializationKeyType(am,baseType);
	}
	@Override @Deprecated
	public Class<?> findSerializationContentType(final Annotated am,final JavaType baseType) {
		return _marshallerAnnotationIntrospector.findSerializationContentType(am,baseType);
	}
	@Override
	public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.findSerializationPropertyOrder(ac);
	}
	@Override
	public Boolean findSerializationSortAlphabetically(final Annotated ann) {
		return _marshallerAnnotationIntrospector.findSerializationSortAlphabetically(ann);
	}
	@Override
	public void findAndAddVirtualProperties(final MapperConfig<?> config,
											final AnnotatedClass ac,final List<BeanPropertyWriter> properties) {
		_marshallerAnnotationIntrospector.findAndAddVirtualProperties(config,
																	  ac,properties);
	}
	@Override
	public Boolean hasAsValue(final Annotated a) {
		return _marshallerAnnotationIntrospector.hasAsValue(a);
	}
	@Override
	public Boolean hasAnyGetter(final Annotated a) {
		return _marshallerAnnotationIntrospector.hasAnyGetter(a);
	}
	@Override @Deprecated
	public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
		return _marshallerAnnotationIntrospector.hasAnyGetterAnnotation(am);
	}
	@Override @Deprecated
	public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
		return _marshallerAnnotationIntrospector.hasAsValueAnnotation(am);
	}
	@Override
	public Object findDeserializer(final Annotated a) {
		return _marshallerAnnotationIntrospector.findDeserializer(a);
	}
	@Override
	public Object findKeyDeserializer(final Annotated a) {
		return _marshallerAnnotationIntrospector.findKeyDeserializer(a);
	}
	@Override
	public Object findContentDeserializer(final Annotated a) {
		return _marshallerAnnotationIntrospector.findContentDeserializer(a);
	}
	@Override
	public Object findDeserializationConverter(final Annotated a) {
		return _marshallerAnnotationIntrospector.findDeserializationConverter(a);
	}
	@Override
	public Object findDeserializationContentConverter(final AnnotatedMember a) {
		return _marshallerAnnotationIntrospector.findDeserializationContentConverter(a);
	}
	@Override
	public JavaType refineDeserializationType(final MapperConfig<?> config,
											  final Annotated a,final JavaType baseType) throws JsonMappingException {
		return _marshallerAnnotationIntrospector.refineDeserializationType(config,
																		   a,baseType);
	}
	@Override @Deprecated
	public Class<?> findDeserializationContentType(final Annotated am,final JavaType baseContentType) {
		return _marshallerAnnotationIntrospector.findDeserializationContentType(am,baseContentType);
	}
	@Override @Deprecated
	public Class<?> findDeserializationType(final Annotated am,final JavaType baseType) {
		return _marshallerAnnotationIntrospector.findDeserializationType(am, baseType);
	}
	@Override @Deprecated
	public Class<?> findDeserializationKeyType(final Annotated am,final JavaType baseKeyType) {
		return _marshallerAnnotationIntrospector.findDeserializationKeyType(am,baseKeyType);
	}
	@Override
	public Object findValueInstantiator(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.findValueInstantiator(ac);
	}
	@Override
	public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.findPOJOBuilder(ac);
	}
	@Override
	public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
		return _marshallerAnnotationIntrospector.findPOJOBuilderConfig(ac);
	}
	@Override
	public Boolean hasAnySetter(final Annotated a) {
		return _marshallerAnnotationIntrospector.hasAnySetter(a);
	}
	@Override
	public JsonSetter.Value findSetterInfo(final Annotated a) {
		return _marshallerAnnotationIntrospector.findSetterInfo(a);
	}
	@Override
	public Boolean findMergeInfo(final Annotated a) {
		return _marshallerAnnotationIntrospector.findMergeInfo(a);
	}
	@Override @Deprecated
	public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
		return _marshallerAnnotationIntrospector.hasAnySetterAnnotation(am);
	}
	@Override @Deprecated
	public boolean hasCreatorAnnotation(final Annotated a) {
		return _marshallerAnnotationIntrospector.hasCreatorAnnotation(a);
	}
	@Override @Deprecated
	public Mode findCreatorBinding(final Annotated a) {
		return _marshallerAnnotationIntrospector.findCreatorBinding(a);
	}
	@Override
	public Mode findCreatorAnnotation(final MapperConfig<?> config,
									  final Annotated a) {
		return _marshallerAnnotationIntrospector.findCreatorAnnotation(config,
																	   a);
	}
}
