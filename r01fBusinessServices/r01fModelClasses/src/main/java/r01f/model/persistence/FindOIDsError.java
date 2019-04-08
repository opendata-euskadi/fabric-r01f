package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="FINDOIDsError")
@Accessors(prefix="_")
public class FindOIDsError<O extends PersistableObjectOID>
	 extends FindError<O>
  implements FindOIDsResult<O> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The model object type
	 * Beware that {@link FindOIDsOK} extends {@link FindOK} parameterized with 
	 * the oid type NOT the model object type 
	 */
	@MarshallField(as="modelObjType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<? extends PersistableModelObject<? extends OID>> _modelObjectType;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindOIDsError() {
		super();
	}
	<M extends PersistableModelObject<O>> FindOIDsError(final Class<M> modelObjType,final Class<O> oidType,
			  	  										final Throwable th) {
		super(oidType,
			  th);
		_modelObjectType = modelObjType;
	}
	<M extends PersistableModelObject<O>> FindOIDsError(final Class<M> entityType,final Class<O> oidType,
			  	  										final String errMsg,final PersistenceErrorType errorCode) {
		super(oidType,
			  errMsg,errorCode);
		_modelObjectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsError<O> asCRUDError() {
		return this;
	}
	@Override
	public FindOIDsOK<O> asCRUDOK() {
		throw new ClassCastException();
	}
}
