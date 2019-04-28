package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.reflection.ReflectionUtils;

@MarshallType(as="metaDataConfigForOIDField")
@GwtIncompatible
public class FieldMetaDataForOID
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 8974728672923211198L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForOID() {
		super();
	}
	@SuppressWarnings("unchecked")
	public FieldMetaDataForOID(final FieldMetaDataForOID other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),			 
			 (Class<? extends OID>)other.getDataType());
	}
	public FieldMetaDataForOID(final FieldID fieldId,
							   final LanguageTexts name,final LanguageTexts description,
							   final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
							   final Class<? extends OID> oidType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  oidType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof OID 
				 		  && (ReflectionUtils.isSameClassAs(value.getClass(),_dataType)		// same type
				 		      || 														    // or
				 		      ReflectionUtils.isSubClassOf(value.getClass(),_dataType));	// a subtype
				 		      
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,_dataType.getSimpleName(),value.getClass())); 
	}
}
