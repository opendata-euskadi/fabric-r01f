package r01f.model.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.common.reflect.TypeToken.TypeSet;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.outline.TypeOutline;
import r01f.reflection.scanner.ScannerFilter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@GwtIncompatible
@Accessors(prefix="_")
public class TypeMetaDataInspector
  implements HasTypesMetaData {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TypeMetaDataInspector() {
		// nothing
	}
	public TypeMetaDataInspector(final AppCode appCode) {
		this();
		this.init(appCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SINGLETON
/////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    private static enum TypeMetaDataInspectoringletonHolder {
    	SINGLETON;

    	@Getter private final TypeMetaDataInspector _instance;

    	TypeMetaDataInspectoringletonHolder() {
    		_instance = new TypeMetaDataInspector();
    	}
    }
    /**
     * @return the {@link TypeMetaDataInspector} singleton instance
     */
    public static TypeMetaDataInspector singleton() {
    	return TypeMetaDataInspectoringletonHolder.SINGLETON.getInstance();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private ConcurrentMap<Class<? extends MetaDataDescribable>,TypeMetaData<? extends MetaDataDescribable>> _inspectedTypes = Maps.newConcurrentMap();

	@Getter private Map<AppCode,Boolean> _initialized = Maps.newHashMapWithExpectedSize(2);

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static Reflections buildAnnotationsScanner(final AppCode appCode) {
		final String modelObjPackage = Strings.customized("{}.model",
														  appCode);
		List<URL> modelObjTypesUrl = new ArrayList<URL>();
		modelObjTypesUrl.addAll(ClasspathHelper.forPackage(modelObjPackage));	// xxx.model.*
		Reflections ref = new Reflections(new ConfigurationBuilder()
													.setUrls(// org.reflections.ClasspathHelper seems to return ONLY the jar or path containing the given package
															 // ... so the package MUST be added back to the url to minimize scan time and unneeded class loading
															 FluentIterable.from(modelObjTypesUrl)
																   .transform(new Function<URL,URL>() {
																						@Override
																						public URL apply(final URL url) {
																							try {
																								return new URL(url.toString() + modelObjPackage.replace(".", "/")
																																			   .replace("\\", "/"));
																							} catch (MalformedURLException ex) {
																								ex.printStackTrace();
																							}
																							return url;
																						}
																   			   })
																   .toList())
													.filterInputsBy(ScannerFilter.DEFAULT_TYPE_FILTER)
													.setScanners(new SubTypesScanner(true),	// true=exclude object class
																 new TypeAnnotationsScanner()));
		return ref;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds all @ModelObjectData annotated types at package {appCode}.model.*
	 * for each found type, the {@link #inspect(Class)} method is called
	 * A CACHE is used to avoid inspecting types all over again
	 *
	 * BEWARE! all types annotated with @ModelObjectData MUST implement {@link MetaDataDescribable}
	 * @param appCode
	 */
	@SuppressWarnings("unchecked")
	public void init(final AppCode appCode) {
		log.info("Finding model objects for {} api at {}.model.*",
				 appCode,appCode);

		// [0] - Avoid duplicate initialization
		Boolean initialized = _initialized.get(appCode);
		if (initialized != null
		&& initialized == true) {
			log.info("The inspector was already initialized! for {}",appCode);
//			throw new IllegalStateException("The inspector was already initialized! for " + apiAppAndModule);
		}


		// [1] - Find every type annotated with ModelObjectData
		Reflections reflections = TypeMetaDataInspector.buildAnnotationsScanner(appCode);
		Set<Class<?>> modelObjTypes = reflections.getTypesAnnotatedWith(ModelObjectData.class);

		// [2] - For every found type, look at the @ModelObjectData annotation and load the ModelObjectMetaData
		if (CollectionUtils.hasData(modelObjTypes)) {
			for (Class<?> modelObjType : modelObjTypes) {
				// if (ReflectionUtils.isAbstract(modelObjType)) continue;		// skip base types??
				if (!ReflectionUtils.isImplementing(modelObjType,MetaDataDescribable.class)) {
					log.warn("Type {} is annotated with @{} BUT it's NOT implementing {}",
							 modelObjType,ModelObjectData.class,MetaDataDescribable.class);
				}
				this.inspect((Class<? extends MetaDataDescribable>)modelObjType);
			}
		}

		// [3] - The inspector is initialized
		_initialized.put(appCode,true);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final Class<M> hasMetaData) {
		TypeMetaData<M> outTypeMetaData = this.inspect(hasMetaData);
		if (outTypeMetaData == null) throw new IllegalStateException("There's NO type metadata info for type " + hasMetaData);
		return outTypeMetaData;
	}

	@Override @SuppressWarnings("unchecked")
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final long typeCode) {
		TypeMetaData<M> outTypeMetaData = null;
		for (TypeMetaData<? extends MetaDataDescribable> typeMetaData : _inspectedTypes.values()) {
			if (typeMetaData.getTypeMetaData().modelObjTypeCode() == typeCode) {
				outTypeMetaData = (TypeMetaData<M>)typeMetaData;
				break;
			}
		}
		return outTypeMetaData;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inspects all {@link HasMetaDataForModelObject} types returning a {@link TypeMetaData} object that encapsulates
	 * the metadata info about the model object type
	 * The model object type (or one of the types in it's hierarchy) MUST be annotated with @ModelObjectData
	 * setting the type that holds the meta-data about the type
	 * <pre class='brush:java'>
	 * 		@ModelObjectData(MyBusinessObjecTypeMetaData.class)
	 * 		@Accessors(prefix="_")
	 * 		public class MyBusinessObjectType
	 * 		  implements ModelObject {		// extends MetaDataDescribable
	 * 			@Getter @Setter private MyOID _oid;
	 * 			@Getter @Setter private MyOtherModelObject _myOtherField;
	 * 		}
	 * </pre>
	 * The type that holds the metadata is roughly (not mandatory) similar to the model object
	 * BUT with meta-data information
	 * <pre class='brush:java'>
	 * 		@MetaDataForType(modelObjTypeCode = 100,
	 *					     description = {
	 *							@DescInLang(language=Language.SPANISH,value="My model object type"),
	 *							@DescInLang(language=Language.BASQUE,value="[eu] my model object type"),
	 *							@DescInLang(language=Language.ENGLISH,value="My model object type")
	 *						 })
	 * 		public class MyBusinessObjectType
	 * 		  implements HasModelObjectMetaData {
	 *				@MetaDataForField(description = {
	 *									@DescInLang(language=Language.SPANISH,value="Unique identifier"),
	 *									@DescInLang(language=Language.BASQUE,value="[eu] Unique identifier"),
	 *									@DescInLang(language=Language.ENGLISH,value="Unique identifier")
	 *							     },
	 *			   				     storage = @Storage(indexed=false))
	 * 				private MyOID _oid;
	 *
	 *				@MetaDataForField(description = {
	 *									@DescInLang(language=Language.SPANISH,value="Other field"),
	 *									@DescInLang(language=Language.BASQUE,value="[eu] Other field"),
	 *									@DescInLang(language=Language.ENGLISH,value="Other field")
	 *							     },
	 *			   				     storage = @Storage(indexed=false))
	 * 				private MyOtherModelObject _myOtherField;
	 * 		}
	 * </pre>
	 *
	 * @param type
	 * @return
	 */
	public <M extends MetaDataDescribable> TypeMetaData<M> inspect(final Class<M> type) {
		return _inspect(null,
						type);
	}
	@SuppressWarnings("unchecked")
	private <M extends MetaDataDescribable> TypeMetaData<M> _inspect(final FieldID fieldId,
								  									 final Class<M> type) {
		// [0] - check the cache
		TypeMetaData<M> outMetaData = (TypeMetaData<M>)_inspectedTypes.get(type);
		if (outMetaData != null) return outMetaData;

		// [1] - Build the type hierarchy outline
		TypeOutline typeOutline = TypeOutline.from(type);
		Collection<Class<?>> flatHierarchy = typeOutline.getNodesFromSpezializedToGeneralization();

		// [2] - find the @ModelObjectData on the given type
		ModelObjectData typeMetaDataRefAnnot = _findModelObjectMetaDataAnnot(flatHierarchy);
		if (typeMetaDataRefAnnot == null) {
			log.warn("The {} model object type or a type in it's hierarchy (super types or implemented interfaces) MUST be annotated with {} specifying the model object's metadata",
					 type,ModelObjectData.class);
			return null;
		}

		// [2] - Build the type metadata info using the @MetaDataForType annotation on the
		// 		 type containing the metadata
		final Class<? extends HasMetaDataForModelObject> hasMetaDataType = typeMetaDataRefAnnot.value();
		final MetaDataForType typeMetaDataAnnot = hasMetaDataType.getAnnotation(MetaDataForType.class);
//		if (typeMetaDataAnnot == null) throw new IllegalStateException(String.format("%s is supposed to contain metadata about %s BUT it's not annotated with %s",
//																					 hasMetaDataType,type,
//																					 MetaDataForType.class));

		// [3] - Build the facets inspecting the hierarchy recursively
		// 3.1 - Filter all types annotated with @ModelObjectData up in the hierarchy of the given type
		Set<Class<?>> facetTypes = FluentIterable.from(flatHierarchy)
												 .filter(new Predicate<Class<?>>() {
																@Override
																public boolean apply(final Class<?> superType) {
																	return type != superType	//  beware to not include the type: stack overflow!!
																		&& ReflectionUtils.typeAnnotation(superType,
																										  ModelObjectData.class) != null;	// has ModelObjectData annotation
																}
														})
												 .toSet();

		// 3.2 - Transform the Set<Class<?>> of type facets to Set<TypeMetaData> recursively
		//		 calling inspect(...)
		Set<TypeMetaData<? extends MetaDataDescribable>> facetsTypeMetaData = FluentIterable.from(facetTypes)
																				   .transform(new Function<Class<?>,TypeMetaData<? extends MetaDataDescribable>>() {
																										@Override
																										public TypeMetaData<? extends MetaDataDescribable> apply(final Class<?> superType) {
																											return TypeMetaDataInspector.this.inspect((Class<? extends MetaDataDescribable>)superType);
																										}
																					 			})
																					.toSet();

		final TypeMetaData<M> typeMetaData = new TypeMetaData<M>(type,					// the metadata-described model object type
														   		 TypeToken.of(hasMetaDataType),		// the annotated type that contains the model object's metadata
														   		 typeMetaDataAnnot,		// the annotation info
														   		 facetsTypeMetaData);	// the type facets' metadata

		// [3] - Introspect fields of the type containing the metadata
		//		 (consider all fields, including those in the type hierarchy)
		TypeSet hasMetaDataTypeSet = TypeToken.of(hasMetaDataType)
											  .getTypes();
		Iterator<TypeToken<?>> typeTokenIt = hasMetaDataTypeSet.iterator();
		for (; typeTokenIt.hasNext(); ) {
			final TypeToken<?> hasMetaDataTypeToken = typeTokenIt.next();

			// 3.1 - Fields
			final Field[] fields = hasMetaDataTypeToken.getRawType()
													   .getDeclaredFields();
			Collection<TypeFieldMetaData> nodeFieldsMetaData = FluentIterable.<Field>from(fields)
																	 // filter fields annotated with @MetaDataForField
																	 .filter(FIELD_ANNOTATED_FILTER)
																	 // transform to TypeFieldMetaData
																	 .transform(new Function<Field,TypeFieldMetaData>() {
																						@Override
																						public TypeFieldMetaData apply(final Field field) {
																							FieldMetaDataAnnotated<Field> an = new FieldMetaDataAnnotated<Field>(hasMetaDataTypeToken,
																																								 field);
																							return _fieldMetaDataFor(fieldId,		// the type that contains the type being inspected
																													 typeMetaData,	// metadata about the type that contains the field
																													 hasMetaDataTypeToken,an);		// the type containing the field + field
																						}
																	 			})
																	 .toList();
			if (CollectionUtils.hasData(nodeFieldsMetaData)) typeMetaData.getFieldsMetaData()
																		 .addAll(nodeFieldsMetaData);
			// 3.2 - Getter methods (used at interfaces)
			boolean isIface = ReflectionUtils.isInterface(hasMetaDataTypeToken.getRawType());
			boolean isAbstract = ReflectionUtils.isAbstract(hasMetaDataTypeToken.getRawType());
			if (!isIface && !isAbstract) continue;
			
			final Method[] methods = hasMetaDataTypeToken.getRawType()
														 .getDeclaredMethods();
			Collection<TypeFieldMetaData> nodeMethodsMetaData = FluentIterable.from(methods)
																	 // filter getter methods annotated with @MetaDataForField
																	 .filter(METHOD_ANNOTATED_FILTER)
																	 // transform to TypeFieldMetaData
																	 .transform(new Function<Method,TypeFieldMetaData>() {
																						@Override
																						public TypeFieldMetaData apply(final Method method) {
																							FieldMetaDataAnnotated<Method> an = new FieldMetaDataAnnotated<Method>(hasMetaDataTypeToken,
																																								   method);
																							return _fieldMetaDataFor(fieldId,		// the field that contains the type being inspected
																													 typeMetaData,	// metadata about the type that contains the field
																													 hasMetaDataTypeToken,an);		// the meta-data type containing the field + field
																						}
																	 			})
																	 .toList();
			if (CollectionUtils.hasData(nodeMethodsMetaData)) typeMetaData.getFieldsMetaData()
																		  .addAll(nodeMethodsMetaData);

		}	// for

		// [4] - Cache
		_inspectedTypes.putIfAbsent(type,typeMetaData);

		// [5] - Return
		return typeMetaData;
	}
	@SuppressWarnings("unchecked")
	private TypeFieldMetaData _fieldMetaDataFor(final FieldID parentFieldId,		// recursive calls only: the field that contains a MetaDataDescribable object one of whose fields is being processed
												final TypeMetaData<? extends MetaDataDescribable> typeMetaData,
											    final TypeToken<?> fieldContainerMetaDataType,final FieldMetaDataAnnotated<?> field) {
		// [1] - field id
		//			Patterns:
		//				- id={fieldId} 				> fields directly defined at the type that contains the meta-data
		//											  or at an r01 meta-data container type for a facet (ie: HasMetaDataForHasOIDModelObject)
		//				- id={facetId}.{fieldId}	> fields defined at a meta-data container type for a facet
		FieldID id = null;
//		MetaDataForType fieldContainerMetaDataTypeAnnot = fieldContainerMetaDataType.getRawType()
//																					.getAnnotation(MetaDataForType.class);
//		if (fieldContainerMetaDataTypeAnnot == null) throw new IllegalStateException(String.format("%s MUST be annotated with @%s",
//																								   fieldContainerMetaDataType,MetaDataForType.class.getSimpleName()));
		if (parentFieldId != null) {
			// it's a recursive call (a MetaDataDescribable field of another MetaDataDescribable object)
			id = FieldID.forId(Strings.customized("{}.{}",
														   parentFieldId,
														   field.getId()));
		} else {
			id = FieldID.forId(Strings.customized("{}",
														   field.getId()));
		}
		// [2] - field type
		Type fieldType = field.getType();
		TypeMetaData<? extends MetaDataDescribable> fieldTypeMetaData = fieldType instanceof Class
																				? ReflectionUtils.isImplementing((Class<?>)fieldType,MetaDataDescribable.class)
																							? _inspect(id,
																								       (Class<? extends MetaDataDescribable>)fieldType)		// BEWARE!! recursive call
																							: null
																				: null;
		// [3] - Return
		return new TypeFieldMetaData(typeMetaData,
									 id,
									 field.getMetadataForFieldAnnotation(),
									 fieldType,
									 fieldTypeMetaData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	WRAPS A Field or Method
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	private class FieldMetaDataAnnotated<T> {
		@Getter private final TypeToken<?> _containerType;
		@Getter private final T _annotatedElement;	// can be either a Field or a getter Method
		@Getter private final MetaDataForField _metadataForFieldAnnotation;

		public FieldMetaDataAnnotated(final TypeToken<?> containerType,final T annotated) {
			_containerType = containerType;
			_annotatedElement = annotated;
			if (annotated instanceof Field) {
				_metadataForFieldAnnotation = ((Field)annotated).getAnnotation(MetaDataForField.class);
			}
			else if (annotated instanceof Method) {
				_metadataForFieldAnnotation = ((Method)annotated).getAnnotation(MetaDataForField.class);
			}
			else {
				throw new IllegalArgumentException(String.format("%s is NOT a %s or %s",
															     annotated.getClass(),
															     Field.class,Method.class));
			}
		}
		public String getId() {
			String outId = null;
			if (_annotatedElement instanceof Field) {
				String fieldName = ((Field)_annotatedElement).getName();
				outId = fieldName.startsWith("_") ? fieldName.substring(1) : fieldName;
			}
			else if (_annotatedElement instanceof Method) {
				String fieldName = null;
				String methodName = ((Method)_annotatedElement).getName();
				if (methodName.startsWith("get")) {
					fieldName = methodName.substring("get".length());
				} else if (methodName.startsWith("is")) {
					fieldName = methodName.substring("is".length());
				} else {
					throw new IllegalStateException(String.format("%s is NOT a valid method name",methodName));
				}
				// ensure the first letter is lower-case
		        int strLen = fieldName.length();
		        outId = new StringBuilder(strLen)
					            .append(Character.toLowerCase(fieldName.charAt(0)))
					            .append(fieldName.substring(1))
					            .toString();
			}
			return outId;
		}
		public Type getType() {
			Type outType = null;

			if (_annotatedElement instanceof Field) {
				Field field = (Field)_annotatedElement;
				if (field.getType() == Class.class) {
					outType = Class.class;
				} else {
					outType = _containerType.resolveType(field.getGenericType()).getType();
				}
			}
			else if (_annotatedElement instanceof Method) {
				Method method = (Method)_annotatedElement;
				outType = _containerType.resolveType(method.getGenericReturnType()).getType();
			}
//			System.out.println(">>>> " + this.getId() + ": " + _containerType + " > " + outType);
			return outType;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ANNOTATION INSTROSPECTION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds the @ModelObjectData annotation in a type's hierarchy
	 * @param typeOutline
	 * @return
	 */
	private static ModelObjectData _findModelObjectMetaDataAnnot(final Collection<Class<?>> flatHierarchy) {
		// recursively finds a ModelObjectData annotation:
		//		- If the object is NOT annotated with ModelObjectData annotation, all it's directly implemented interfaces are checked
		//		- ... if the ModelObjectData annotation is NOT found, it's super-type is checked recursively
		ModelObjectData outModelObjectData = null;
		for (Class<?> hierarchyType : flatHierarchy) {
			if (hierarchyType.isAnnotationPresent(ModelObjectData.class)) {
				outModelObjectData = hierarchyType.getAnnotation(ModelObjectData.class);
				break;
			}
		}
		return outModelObjectData;
	}
	public static final Predicate<Field> FIELD_ANNOTATED_FILTER = new Predicate<Field>() {
																			@Override
																			public boolean apply(final Field field) {
																				return field.getAnnotation(MetaDataForField.class) != null;
																			}
																  };
	public static final Predicate<Method> METHOD_ANNOTATED_FILTER = new Predicate<Method>() {
																			@Override
																			public boolean apply(final Method method) {
																				boolean isGetter = (method.getName().startsWith("get") && method.getName().length() > "get".length())
																								|| (method.getName().startsWith("is") && method.getName().length() > "is".length());
																				boolean isAnnotated = method.getAnnotation(MetaDataForField.class) != null;
																				return isGetter && isAnnotated;
																			}
															 		 };
}
