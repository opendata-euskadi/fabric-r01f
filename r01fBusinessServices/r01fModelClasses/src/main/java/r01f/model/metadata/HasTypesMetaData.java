package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

/**
 * Holds info about model object types metadata
 */
@GwtIncompatible
public interface HasTypesMetaData {
	/**
	 * Finds the {@link TypeMetaData} for the given model object type
	 * @param hasMetaData
	 * @return
	 */
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final Class<M> hasMetaData);
	/**
	 * Finds the {@link TypeMetaData} for the given model objet type code
	 * @param typeCode
	 * @return
	 */
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final long typeCode);
}