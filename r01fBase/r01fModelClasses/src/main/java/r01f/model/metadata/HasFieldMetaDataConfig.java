package r01f.model.metadata;

import java.util.Iterator;

import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public interface HasFieldMetaDataConfig {
	public Iterator<FieldMetaData> metaDataConfigIterator();
}
