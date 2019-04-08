package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

/**
 * A marker interface for types that contains metadata about type fields
 */
@GwtIncompatible
public interface HasFieldsMetaData {
	public final long MODEL_OBJECT_TYPE_CODE = 100;
	public final long PERSISTABLE_MODEL_OBJECT_TYPE_CODE = 200;
	
	public final long HAS_OID_MODEL_OBJECT_TYPE_CODE = 110;
	public final long HAS_ID_MODEL_OBJECT_TYPE_CODE = 111;
	
	public final long HAS_ENTITY_VERSION_MODEL_OBJECT_TYPE_CODE = 112;
	public final long HAS_TRACKING_INFO_MODEL_OBJECT_TYPE_CODE = 113;

	public final long HAS_LANGUAGE_MODEL_OBJECT_TYPE_CODE = 114;
	
	public final long HAS_SUMMARY_MODEL_OBJECT_TYPE_CODE = 115;
	public final long HAS_FULL_TEXT_SUMMARY_MODEL_OBJECT_TYPE_CODE = 116;
	
	public final long HAS_VERSION_INFO_MODEL_OBJECT_TYPE_CODE = 117;
	
	public final long HAS_LANGUAGE_DEPENDENT_MODEL_OBJECT_TYPE_CODE = 118;
	public final long HAS_LANGUAGE_INDDEPENDENT_MODEL_OBJECT_TYPE_CODE = 119;
}
