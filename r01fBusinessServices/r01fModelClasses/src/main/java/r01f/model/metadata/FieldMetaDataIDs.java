package r01f.model.metadata;

import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Lists;

import r01f.guids.CommonOIDs.AppCode;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible
public class FieldMetaDataIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Composes a metaData id
	 * @param appCode the app code
	 * @param id the metadata id
	 * @return
	 */
	public static FieldID idFor(final ClientApiAppCode appCode,final String... ids) {
		return FieldMetaDataIDs.idFor(appCode.asAppCode(),ids);
	}
	/**
	 * Composes a metaData id
	 * @param appCode the app code
	 * @param id the metadata id
	 * @return
	 */
	public static FieldID idFor(final CoreAppCode appCode,final String... ids) {
		return FieldMetaDataIDs.idFor(appCode.asAppCode(),ids);
	}
	/**
	 * Composes a metaData id
	 * @param appCode the app code
	 * @param id the metadata id
	 * @return
	 */
	public static FieldID idFor(final AppCode appCode,final String... ids) {
		Collection<String> theIds = ids != null ? Lists.<String>newArrayListWithExpectedSize(ids.length + 1)
												: Lists.<String>newArrayListWithExpectedSize(1);
		return FieldMetaDataIDs.idFor(theIds.toArray(new String[theIds.size()]));
	}
	/**
	 * Composes a metaData id
	 * @param appCode the app code
	 * @param id the metadata id
	 * @return
	 */
	public static FieldID idFor(final String... ids) {
		// [1] join all the ids
		String idsJoined = CollectionUtils.of(ids)
										  .toStringSeparatedWith('.');
		// [2] add the appCode
		return FieldID.forId(idsJoined);
	}
}
