package r01f.model.persistence;

import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.guids.OID;
import r01f.guids.OIDForVersionableModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.DBEntityToModelObjectTransformerBuilder;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.collections.CollectionUtils;

/**
 * Used from {@link CRUDResultBuilder} when composing the {@link PersistenceOperationResult} for a
 * multiple entity operation (ie delete all versions)
 * @param <O>
 * @param <M>
 */
@GwtIncompatible
@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
public class CRUDResultForMultipleEntityBuilder<M extends PersistableModelObject<? extends OIDForVersionableModelObject> & HasVersionableFacet> {
/////////////////////////////////////////////////////////////////////////////////////////
//  OK
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
	public static class CRUDResultForMultipleEntitiesBuilderResultStep<T> {
		
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		
		public CRUDOnMultipleResult<T> allExecuted(final PersistenceRequestedOperation reqOp,
												   final Collection<CRUDOK<T>> execOKs) {
			CRUDOnMultipleResult<T> outMultipleCRUDOKs = new CRUDOnMultipleResult<T>(reqOp,
																					 _entityType);
			for (CRUDOK<T> crudOK : execOKs) {
				outMultipleCRUDOKs.addOperationOK(crudOK);
			}
			return outMultipleCRUDOKs;
		}		
		public CRUDOnMultipleResult<T> allUpdated(final Collection<T> updateOKs) {
			CRUDOnMultipleResult<T> outMultipleCRUDOKs = new CRUDOnMultipleResult<T>(PersistenceRequestedOperation.UPDATE,
																					_entityType);
			outMultipleCRUDOKs.addOperationsOK(updateOKs,
											   PersistenceRequestedOperation.UPDATE);
			return outMultipleCRUDOKs;
		}
		public <DB extends DBEntity> CRUDResultForMultipleBuilderTransformerStep<DB,T> allDBEntitiesUpdated(final Collection<DB> okDBEntities) {
			return new CRUDResultForMultipleBuilderTransformerStep<DB,T>(_securityContext,
																		 _entityType,
																		 okDBEntities,
																		 PersistenceRequestedOperation.UPDATE);
		}		
		public CRUDOnMultipleResult<T> allDeleted(final Collection<T> delOKs) {
			CRUDOnMultipleResult<T> outMultipleCRUDOKs = new CRUDOnMultipleResult<T>(PersistenceRequestedOperation.DELETE,
																					 _entityType);
			outMultipleCRUDOKs.addOperationsOK(delOKs,
											   PersistenceRequestedOperation.DELETE);
			return outMultipleCRUDOKs;
		}
		public <DB extends DBEntity> CRUDResultForMultipleBuilderTransformerStep<DB,T> allDBEntitiesDeleted(final Collection<DB> okDBEntities) {
			return new CRUDResultForMultipleBuilderTransformerStep<DB,T>(_securityContext,
																		 _entityType,
																		 okDBEntities,
																		 PersistenceRequestedOperation.DELETE);
		}
		public CRUDOnMultipleResult<T> noneExcecuted(final PersistenceRequestedOperation reqOp,
												 	 final Collection<CRUDError<T>> execErrs) {
			CRUDOnMultipleResult<T> outMultipleCRUDOKs = new CRUDOnMultipleResult<T>(reqOp,
																					 _entityType);
			for (CRUDError<T> crudErr : execErrs) {
				outMultipleCRUDOKs.addOperationNOK(crudErr);
			}
			return outMultipleCRUDOKs;
		}
		public CRUDOnMultipleResult<T> executed(final PersistenceRequestedOperation reqOp,
												final Collection<CRUDResult<T>> execResults) {
			CRUDOnMultipleResult<T> outMultipleCRUDResults = new CRUDOnMultipleResult<T>(reqOp,
																						 _entityType);
			if (CollectionUtils.hasData(execResults)) {
				for (CRUDResult<T> execResult : execResults) {
					outMultipleCRUDResults.addOperationResult(execResult);
				}
			}
			return outMultipleCRUDResults;
		}
		public CRUDOnMultipleResult<T> partilly(final PersistenceRequestedOperation reqOp,
												final Collection<CRUDOK<T>> execOKs,
												final Collection<CRUDError<T>> execNOKs) {
			CRUDOnMultipleResult<T> outMultipleCRUDOKs = new CRUDOnMultipleResult<T>(reqOp,
																					 _entityType);
			if (CollectionUtils.hasData(execOKs)) {
				for (CRUDOK<T> crudOk : execOKs) {
					outMultipleCRUDOKs.addOperationOK(crudOk);
				}
			}
			if (CollectionUtils.hasData(execNOKs)) {
				for (CRUDError<T> crudNOK : execNOKs) {
					outMultipleCRUDOKs.addOperationNOK(crudNOK);
				}
			}
			return outMultipleCRUDOKs;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class CRUDResultForMultipleBuilderTransformerStep<DB extends DBEntity,
																	T> {
		
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;		
		protected final Collection<DB> _dbEntities;
		protected final PersistenceRequestedOperation _requestedOp;
	
		public <M extends PersistableModelObject<? extends OID>> CRUDOnMultipleResult<M> transformedToModelObjectUsing(final TransformsDBEntityIntoModelObject<DB,M> dbEntityToModelObjectTransformer) {			
			return this.transformedToModelObjectUsing(DBEntityToModelObjectTransformerBuilder.createFor(_securityContext,
																										dbEntityToModelObjectTransformer));
		}
		@SuppressWarnings("unchecked")
		public <M extends PersistableModelObject<? extends OID>> CRUDOnMultipleResult<M> transformedToModelObjectUsing(final Function<DB,M> transformer) {
			// [1] - Transform from a db entity to a model object
			Function<DB,M> dbEntityToModelObjConverter = DBEntityToModelObjectTransformerBuilder.createFor(_securityContext,
																										   transformer);
			
			Collection<M> okModelObjs = Lists.newArrayListWithExpectedSize(_dbEntities.size());
			for (DB dbEntity : _dbEntities) {
				okModelObjs.add(dbEntityToModelObjConverter.apply(dbEntity));
			}
			// [2] - Build the result
			CRUDOnMultipleResult<M> outMultipleCRUDOKs = new CRUDOnMultipleResult<M>(_requestedOp,
																					 (Class<M>)_entityType);
			outMultipleCRUDOKs.addOperationsOK(okModelObjs,
											   _requestedOp);
			return outMultipleCRUDOKs;
		}
	}
}