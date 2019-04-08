package r01f.model.metadata;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;


@MarshallType(as="metaDataConfigForDependentObjectField")
@GwtIncompatible
@Accessors(prefix="_")
public class FieldMetaDataForDependentObject 
	 extends FieldMetaDataBase 
  implements HasFieldMetaDataConfig {

	private static final long serialVersionUID = 1281643971770301481L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="childMetaData")
	@Getter @Setter private Collection<FieldMetaData> _childMetaData;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForDependentObject() {
		super();
	}
	public FieldMetaDataForDependentObject(final FieldMetaDataForDependentObject other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 other.getDataType(),
			 other.getChildMetaData());
	}
	public FieldMetaDataForDependentObject(final FieldID fieldId,
								   		   final LanguageTexts name,final LanguageTexts description,
								   		   final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
								   		   final Class<?> type,
								   		   final Collection<FieldMetaData> childMetaData) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  type);
		_childMetaData = childMetaData;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a new child metadata
	 * @param metaData
	 * @return
	 */
	public FieldMetaDataForDependentObject addChildMetaData(final FieldMetaData metaData) {
		if (_childMetaData == null) _childMetaData = Sets.newHashSet();
		_childMetaData.add(metaData);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<FieldMetaData> metaDataConfigIterator() {
		return _childMetaData != null ? _childMetaData.iterator()
									  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FieldID getIndexableFieldId() {
		throw new IllegalStateException(Throwables.message("{} is a compound metaData",FieldMetaDataForDependentObject.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		throw new IllegalArgumentException(Throwables.message("{} is a compound metaData",FieldMetaDataForDependentObject.class));
	}
}
