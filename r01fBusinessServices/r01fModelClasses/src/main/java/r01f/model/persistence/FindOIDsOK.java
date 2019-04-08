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

@MarshallType(as="findResult",typeId="FINDOIDsOK")
@Accessors(prefix="_")
public class FindOIDsOK<O extends PersistableObjectOID>
	 extends FindOK<O>
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
	public FindOIDsOK() {
		super();
	}
	<M extends PersistableModelObject<O>> FindOIDsOK(final Class<M> modelObjectType,final Class<O> oidType) {
		super(oidType); 
		_modelObjectType = modelObjectType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsError<O> asCRUDError() {
		throw new ClassCastException();
	}
	@Override
	public FindOIDsOK<O> asCRUDOK() {
		return this;
	}
}
