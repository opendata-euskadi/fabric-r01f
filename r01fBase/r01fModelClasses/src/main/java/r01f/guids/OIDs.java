package r01f.guids;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.UUID;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.OidField;
import r01f.exceptions.Throwables;
import r01f.facets.HasOID;
import r01f.model.ModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.metadata.FieldMetaDataForOID;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible("uses reflection")
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class OIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked") 
	public static <O extends OID> Collection<String> toStringCollection(final O... oids) {
		if (CollectionUtils.isNullOrEmpty(oids)) return Lists.newArrayList();
		return OIDs.toStringCollection(Lists.newArrayList(oids));
	}
	public static <O extends OID> Collection<String> toStringCollection(final Collection<O> oids) {
		if (CollectionUtils.isNullOrEmpty(oids)) return Lists.newArrayList();
		return FluentIterable.from(oids)
							 .transform(new Function<O,String>() {
												@Override
												public String apply(final O oid) {
													return oid.asString();
												}
							 			})
							 .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the oid in a {@link HasOID} object
	 * @param hasOid
	 * @param oidType
	 * @param serializedOid
	 */
	public static void setOidFromSerializedFormat(final HasOID<? extends OID> hasOid,
												  final Class<? extends OID> oidType,
												  final String serializedOid) {
		OID oid = ReflectionUtils.invokeStaticMethod(oidType,
									   			     OID.STATIC_FACTORY_METHOD_NAME,
									   			     new Class<?>[] {String.class},new Object[] {serializedOid});
		hasOid.unsafeSetOid(oid);
	}
	/**
	 * Generates a GUID
	 * @return
	 */
	public static String supplyOid() {
		UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
	}
	/**
	 * Invokes the supply() method to generate a new oid
	 * @param oidType
	 * @return
	 */
	public static <O extends OID> O supplyOid(final Class<O> oidType) {
		O oid = ReflectionUtils.<O>invokeStaticMethod(oidType,
									   			   	  OID.STATIC_SUPPLIER_METHOD_NAME,
									   			   	  new Class<?>[] {},new Object[] {});
		return oid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	OIDs
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Returns the model object's oid type or null if no oid type could be found
//	 * @param entityType
//	 * @return
//	 */
//	public static <O extends OID,M extends ModelObject> Class<O> oidTypeOrNullFor(final Class<M> entityType) {
//		return _oidTypeFor(entityType,
//						   false);	// return null if no oid type could be found
//	}
	// Oid type by model object type by oid
	private static Cache<Class<? extends ModelObject>,Class<? extends OID>> MODEL_OBJS_OID_TYPES =  CacheBuilder.newBuilder()	
																											    .softValues()
																											    .maximumSize(200)
																											    .build();
	/**
	 * Returns the model object's oid type
	 * @param entityType
	 * @return
	 * @throws IllegalStateException if the type does NOT have a field annotated with @OidField
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeFor(final Class<M> entityType) {
		return OIDs.oidTypeOrThrowFor(entityType);
	}
	/**
	 * Returns the model object's oid type
	 * @param entityType
	 * @return
	 * @throws IllegalStateException if the type does NOT have a field annotated with @OidField
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeOrThrowFor(final Class<M> entityType) {
		return _oidTypeFor(entityType,
						   true);	// throw an exception
	}
	/**
	 * Returns the model object's oid type
	 * @param entityType
	 * @param defType the default type
	 * @return
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeOrDefaultFor(final Class<M> entityType,
																					 final Class<O> defType) {
		Class<O> outOidType = _oidTypeFor(entityType,
						   				  false);	// do not throw an exception
		return outOidType != null ? outOidType
								  : defType;
	}
	/**
	 * Returns the model object's oid type
	 * @param entityType
	 * @param defType the default type
	 * @return
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeOrNullFor(final Class<M> entityType) {
		return OIDs.oidTypeOrDefaultFor(entityType,
										null);	// default value
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _oidTypeFor(final Class<M> entityType,
																			  final boolean strict) {
		// An oid type by model object type cache is used to avoid excessive Reflection usage
		Class<O> oidType = (Class<O>)MODEL_OBJS_OID_TYPES.getIfPresent(entityType);

		// Try to guess the oid type 
		if (oidType == null) {
			// a) use an annotated field
			oidType = _guessOidTypeFromAnnotatedField(entityType);
		}
		if (oidType == null) {
			// b) use the type info
			oidType = _guessOidTypeFromTypeParameter(entityType);
		}
		if (oidType == null) {	
			// c) use the model object metadata
			oidType = _guessOidTypeFromModelObjectMetaData(entityType);
		}
		
		// If the oid type was NOT found:			
		if (strict && oidType == null) throw new IllegalStateException(Throwables.message("Could NOT guess the oid type for the {} type: either the {} field metadata was NOT present, neither the type has a @{} annotated field",
							 								   		   					  entityType,
							 								   		   					  HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID.getToken(),
							 								   		   					  OidField.class.getSimpleName()));
			
		// Put the oid type in the cache
		if (oidType != null) MODEL_OBJS_OID_TYPES.put(entityType,oidType);
		return oidType;
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _guessOidTypeFromAnnotatedField(final Class<M> entityType) {
		Class<O> oidType = null;
		FieldAnnotated<OidField>[] oidFieldsAnnotated = ReflectionUtils.fieldsAnnotated(entityType,OidField.class);
		if (CollectionUtils.isNullOrEmpty(oidFieldsAnnotated)) return null;
		if (oidFieldsAnnotated.length > 1) throw new IllegalStateException(Throwables.message("The {} type does have more than a singe field annotated with {}",
																							  entityType,OidField.class));
		Field oidField = oidFieldsAnnotated[0].getField();		
		oidType = (Class<O>)ReflectionUtils.fieldType(entityType,oidField);
		return oidType;
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _guessOidTypeFromModelObjectMetaData(final Class<M> entityType) {
		Class<O> oidType = null;
		TypeMetaData<M> metaData = TypeMetaDataInspector.singleton()
													 	.getTypeMetaDataFor(entityType);
		if (metaData == null) throw new IllegalStateException(String.format("Could NOT find the type meta data for %s",entityType));
		FieldMetaDataForOID oidFieldMetaData = metaData.findFieldByIdOrThrow(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID)
															.asFieldMetaData();
		if (oidFieldMetaData != null) {
			oidType = (Class<O>)oidFieldMetaData.getDataType();			
		}
		return oidType;
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _guessOidTypeFromTypeParameter(final Class<M> entityType) {
		Class<O> oidType = null;
		if (ReflectionUtils.isImplementing(entityType,PersistableModelObject.class)) {
		    Type superclassType = entityType.getGenericSuperclass();
		    if (superclassType == null || !ParameterizedType.class.isAssignableFrom(superclassType.getClass())) return null;
		    
			oidType = (Class<O>)((ParameterizedType)entityType.getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return oidType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends OID,M extends ModelObject> O createOIDFor(final Class<M> entityType,
																	   final String oidAsString) {
		Class<O> oidType = OIDs.oidTypeFor(entityType);
		return OIDs.createOIDFromString(oidType,oidAsString);
	}
	/**
	 * Creates an OID from it's serialized as {@link String} value
	 * @param oidType
	 * @param oidAsString
	 * @return
	 */
	public static <O extends OID> O createOIDFromString(final Class<O> oidType,
														final String oidAsString) {
		O outOid = null;
		try {
			outOid = ReflectionUtils.createInstanceFromString(oidType,oidAsString);
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		if (outOid == null) outOid = ReflectionUtils.<O>invokeStaticMethod(oidType,
													   					   "forId",
													   					   new Class<?>[] {String.class},
													   					   new Object[] {oidAsString});
		return outOid;
	}
	/**
	 * Creates a versionable OID from it's serialized as {@link String}s value
	 * @param oidType
	 * @param versionIndependentOidAsString
	 * @param versionOidAsString
	 * @return
	 */
	public static <O extends OID> O createVersionableOIDFromString(final Class<O> oidType,
																   final String versionIndependentOidAsString,
																   final String versionOidAsString) {
		O outOid = ReflectionUtils.<O>invokeStaticMethod(oidType,
													     "forIds",
													     new Class<?>[] {String.class,String.class},
													     new Object[] {versionIndependentOidAsString,versionOidAsString});
		return outOid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an {@link OIDForVersionableModelObject} from the oid and version date
	 * @param entityType
	 * @param oid
	 * @param date
	 * @return
	 */
	public static <O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
				  O createOIDForVersionableModelObject(final Class<M> entityType,
													   final VersionIndependentOID oid,final VersionOID version) {
		// [1] - Guess the oid type
		Class<O> oidType = _guessOidTypeFromModelObjectMetaData(entityType);
		
		
		// [2] - Use the static valueOf method of the OID type
		O outOid = ReflectionUtils.<O>invokeStaticMethod(oidType,
													  "valueOf",
													  new Class<?>[] {String.class,String.class},
													  new Object[] {oid.asString(),version.asString()});
		return outOid;
	}
//	/**
//	 * Creates an {@link OIDForVersionableModelObject} from the oid and version date
//	 * @param entityType
//	 * @param oid
//	 * @param date
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static <O extends OIDForVersionableModelObject,M extends PersistableModelObject<O> & HasVersionableFacet>
//				  O createOIDForVersionableModelObject(final Class<M> entityType,
//													   final VersionIndependentOID oid,
//													   final Date date) {
//		ModelObjectMetaData metaData = ModelObjectMetaDataBuilder.createFor(entityType);
//		Class<O> oidType = (Class<O>)metaData.getOIDFieldMetaData().getDataType();
//		return _createOIDForVersionableModelObject(oidType,
//												   oid,date);
//	}
//	/**
//	 * Creates an {@link OIDForVersionableModelObject} from the oid and version date
//	 * @param entityType
//	 * @param oid
//	 * @param date
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static <O extends OIDForVersionableModelObject,M extends PersistableModelObject<O> & HasVersionableFacet>
//				  O createOIDForVersionableModelObject(final Class<M> entityType,
//													   final VersionIndependentOID oid,
//													   final Object versionInfo) {
//		ModelObjectMetaData metaData = ModelObjectMetaDataBuilder.createFor(entityType);
//		Class<O> oidType = (Class<O>)metaData.getOIDFieldMetaData().getDataType();
//		return _createOIDForVersionableModelObject(oidType,
//												   oid,versionInfo);
//	}
//	/**
//	 * Creates an {@link OIDForVersionableModelObject} from the oid and version as Strings
//	 * When an error stops a request on a versionable object to be processed, the returned oid is 
//	 * a "virtual" one built upon the tarjet object's oid and other data that identifies the version
//	 * like the date
//	 * @param oidType
//	 * @param oid
//	 * @param versionData
//	 * @return
//	 */
//	private static <O extends OIDForVersionableModelObject> O _createOIDForVersionableModelObject(final Class<O> oidType,
//																								  final VersionIndependentOID oid,
//																								  final Object versionData) {
//		String versionAsString = null;
//		if (versionData instanceof VersionOID) {
//			versionAsString = ((VersionOID)versionData).asString();
//		} else if (versionData instanceof Date) {
//			versionAsString = Dates.format((Date)versionData,Dates.DEFAULT_FORMAT);
//		} else if (versionData instanceof String) {
//			versionAsString = (String)versionData;
//		} 
//		// Use the static valueOf method of the OID type
//		O outOid = ReflectionUtils.invokeStaticMethod(oidType,
//													  "valueOf",
//													  new Class<?>[] {String.class,String.class},
//													  new Object[] {oid.asString(),versionAsString});
//		return outOid;
//	}
}
