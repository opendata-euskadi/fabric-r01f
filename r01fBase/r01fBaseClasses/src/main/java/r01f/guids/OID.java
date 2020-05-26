package r01f.guids;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;
import r01f.types.CanBeRepresentedAsString;

/**
 * Models an oid
 */
@MarshallPolymorphicTypeInfo(typeInfoAvailableWhenDeserializing=true,											// the type info (type id property) is available at the deserializer
				  			 includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.NEVER))	// do NOT include type info for types (only for properties)
public interface OID
         extends Comparable<OID>,
     			 Cloneable,
     			 CanBeRepresentedAsString {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int OID_LENGTH = 50;
	public static final String STATIC_FACTORY_METHOD_NAME = "forId";
	public static final String STATIC_SUPPLIER_METHOD_NAME = "supply";
	public static final String REGEX_NOCAPTURE = "[0-9A-Za-z_-]+";
	public static final String REGEX_CAPTURE = "(" + REGEX_NOCAPTURE + ")";
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if this oid is the same as the provided one
	 * (its similar to equals method but more 'semantic')
	 * @param other
	 * @return true if the two oids are the same oid
	 */
	public <O extends OID> boolean is(final O other);
	/**
	 * Checks if this oid is NOT equal to the provided one
	 * @param other
	 * @return
	 */
	public <O extends OID> boolean isNOT(final O other);
    /**
     * Checks if this oid is included in the provided collection
     * @param oids
     * @return true if the oid is included in the collection, false otherwise
     */
	public <O extends OID> boolean isContainedIn(@SuppressWarnings("unchecked") final O... oids);
    /**
     * Checks if this oid is NOT included in the provided collection
     * @param oids
     * @return true if the oid is NOT included in the collection, false otherwise
     */
	public <O extends OID> boolean isNOTContainedIn(@SuppressWarnings("unchecked") final O... oids);
    /**
     * Checks if this oid is included in the provided collection
     * @param oids
     * @return true if the oid is included in the collection, false otherwise
     */
    public <O extends OID> boolean isContainedIn(final Iterable<O> oids);
    /**
     * Checks if this oid NOT is included in the provided collection
     * @param oids
     * @return true if the oid is NOT included in the collection, false otherwise
     */
    public <O extends OID> boolean isNOTContainedIn(final Iterable<O> oids);
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return true if the oid is valid
     */
    public boolean isValid();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the oid typed
	 */
	public <O extends OID> O cast();
}
