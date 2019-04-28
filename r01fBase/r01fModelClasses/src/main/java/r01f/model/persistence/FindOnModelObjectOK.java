package r01f.model.persistence;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="findResult",typeId="FINDOKOnModelObject")
@Accessors(prefix="_")
public class FindOnModelObjectOK<M extends PersistableModelObject<? extends OID>>
	 extends FindOK<M>
  implements FindOnModelObjectResult<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindOnModelObjectOK() {
		super();
	}
	protected FindOnModelObjectOK(final Class<M> entityType) {
		super(entityType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public Class<M> getModelObjectType() {
		return _foundObjectType;
	}
	@Override @SuppressWarnings("cast")
	public void setModelObjectType(final Class<M> type) {
		_foundObjectType = (Class<M>)type;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the found entities' oids if the persistence find operation was successful or a PersistenteException if not
	 * @throws PersistenceException
	 */
	public <O extends OID> Collection<O> getOidsOrThrow() throws PersistenceException {
		if (CollectionUtils.isNullOrEmpty(_operationExecResult)) return Lists.newArrayList();
		return FluentIterable.from(_operationExecResult)
							 .transform(new Function<M,O>() {
												@Override @SuppressWarnings("unchecked")
												public O apply(final M entity) {
													return (O)entity.getOid();
												}
								 			
							 			})
							 .toList();
	}
	/**
	 * When a single result is expected, this method returns this entity's oid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <O extends OID> O getSingleExpectedOidOrThrow() {
		M outEntity = this.getSingleExpectedOrThrow();
		return (O)(outEntity != null ? outEntity.getOid()
								 	 : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOnModelObjectOK<M> asFindOnModelObjectOK() {
		return this;
	}
	@Override
	public FindOnModelObjectError<M> asFindOnModelObjectError() {
		throw new ClassCastException();
	}
}
