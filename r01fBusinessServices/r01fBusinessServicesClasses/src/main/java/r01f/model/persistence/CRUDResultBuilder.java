package r01f.model.persistence;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.OIDForVersionableModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.CRUDResultForMultipleEntityBuilder.CRUDResultForMultipleEntitiesBuilderResultStep;
import r01f.model.persistence.CRUDResultForSingleEntityBuilder.CRUDResultBuilderForCreateError;
import r01f.model.persistence.CRUDResultForSingleEntityBuilder.CRUDResultBuilderForErrorAboutStep;
import r01f.model.persistence.CRUDResultForSingleEntityBuilder.CRUDResultBuilderForErrorStep;
import r01f.model.persistence.CRUDResultForSingleEntityBuilder.CRUDResultBuilderForOKStep;
import r01f.model.persistence.CRUDResultForSingleEntityBuilder.CRUDResultBuilderForUpdateError;
import r01f.patterns.IsBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;

/**
 * Builder type for {@link CRUDResult}-implementing types:
 * <ul>
 * 		<li>A successful CRUD operation result on a single entity: {@link CRUDOK}</li>
 * 		<li>An error on a CRUD operation execution on a single entity: {@link CRUDError}</li>
 * </ul>
 * If the operation execution was successful:
 * <pre class='brush:java'>
 * 		CRUDOK<MyEntity> opOK = CRUDResultBuilder.using(securityContext)
 * 											     .on(MyEntity.class)
 * 												 .loaded()
 * 														.entity(myEntityInstance);
 * 		CRUDOK<MyEntity> opOK = CRUDResultBuilder.using(securityContext)
 * 											     .on(MyEntity.class)
 * 												 .created()
 * 													.entity(myEntityInstance);
 * </pre>
 * If the client requested to load an entity BUT it was NOT found:
 * <pre class='brush:java'>
 * 		CRUDError<MyEntity> opError = CRUDResultBuilder.using(securityContext)
 * 													   .on(MyEntity.class)
 * 													   .notLoaded()
 * 															.becauseClientRequestedEntityWasNOTFound()
 * 																.about(requestedEntityOid);
 * </pre>
 * If an error is raised while executing the persistence operation:
 * <pre class='brush:java'>
 * 		CRUDError<MyEntity> opError = CRUDResultBuilder.using(securityContext)
 * 													   .on(MyEntity.class)
 * 													   .notLoaded()
 * 													   .because(error)
 * 														 	.about(myEntityOid);
 * </pre>
 * If multiple entities are affected by the operation (ie: the deletion of all entity versions)
 * <pre class='brush:java'>
 * 		CRUDResultOnMultipleEntities<MyEntity> opResult = CRUDResultBuilder.using(securityContext)
 * 																		   .on(MyEntity.class)
 * 																		   .versions()
 * 																				.deleted(aDeletedEntitiesCol);
 * </pre>
 */
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CRUDResultBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static CRUDResultBuilderEntityStep using(final SecurityContext securityContext) {
		return new CRUDResultBuilder() {/* nothing */}
						.new CRUDResultBuilderEntityStep(securityContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class CRUDResultBuilderEntityStep {
		private final SecurityContext _securityContext;
		
		public <T> CRUDResultBuilderOperationStep<T> on(final Class<T> entityType) {
			return new CRUDResultBuilderOperationStep<T>(_securityContext,
														 entityType);
		}
		@SuppressWarnings("unchecked")
		public <T> CRUDResultBuilderOperationStep<T> on(final TypeToken<T> typeRef) {
			return new CRUDResultBuilderOperationStep<T>(_securityContext,
														 (Class<T>)typeRef.getRawType());
		}
		public <T extends PersistableModelObject<? extends OIDForVersionableModelObject> & HasVersionableFacet> 
			   CRUDResultForMultipleEntitiesBuilderResultStep<T> onVersionable(final Class<T> entityType) {
			return new CRUDResultForMultipleEntitiesBuilderResultStep<T>(_securityContext,
																	     entityType);
		}
		@SuppressWarnings("unchecked")
		public <T extends PersistableModelObject<? extends OIDForVersionableModelObject> & HasVersionableFacet> 
			   CRUDResultForMultipleEntitiesBuilderResultStep<T> onVersionable(final TypeToken<T> typeRef) {
			return new CRUDResultForMultipleEntitiesBuilderResultStep<T>(_securityContext,
																		 (Class<T>)typeRef.getRawType());
		}
		public <T> CRUDResultForMultipleEntitiesBuilderResultStep<T> onMultiple(final Class<T> entityType) {
			return new CRUDResultForMultipleEntitiesBuilderResultStep<T>(_securityContext,
																 		 entityType);
		}
		@SuppressWarnings("unchecked")
		public <T> CRUDResultForMultipleEntitiesBuilderResultStep<T> onMultiple(final TypeToken<T> typeRef) {
			return new CRUDResultForMultipleEntitiesBuilderResultStep<T>(_securityContext,
																 		 (Class<T>)typeRef.getRawType());
		}		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private abstract class CRUDResultBuilderOperationStepBase<T> {
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		
		//  --------- ERROR
		public CRUDResultBuilderForErrorAboutStep<T> badClientRequestData(final PersistenceRequestedOperation reqOp,
																		  final String msg,final Object... vars) {
			CRUDError<T> err = new CRUDError<T>(reqOp,
												_entityType,
												Strings.customized(msg,vars),PersistenceErrorType.BAD_REQUEST_DATA);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
														     err);
		}
		public CRUDResultBuilderForErrorStep<T> not(final PersistenceRequestedOperation reqOp) {
			return new CRUDResultBuilderForErrorStep<T>(_securityContext,
														_entityType,
														reqOp);
		}
		public CRUDResultBuilderForErrorStep<T> notLoaded() {
			return new CRUDResultBuilderForErrorStep<T>(_securityContext,
														_entityType,
														PersistenceRequestedOperation.LOAD);	
		}
		public CRUDResultBuilderForCreateError<T> notCreated() {
			return new CRUDResultBuilderForCreateError<T>(_securityContext,
														  _entityType);	
		}
		public CRUDResultBuilderForUpdateError<T>  notUpdated() {
			return new CRUDResultBuilderForUpdateError<T>(_securityContext,
														  _entityType);	
		}
		public CRUDResultBuilderForErrorStep<T> notDeleted() {
			return new CRUDResultBuilderForErrorStep<T>(_securityContext,
														_entityType,
														PersistenceRequestedOperation.DELETE);	
		}
		// --------- SUCCESS
		public CRUDResultBuilderForOKStep<T> executed(final PersistenceRequestedOperation requestedOp,
													  final PersistencePerformedOperation performedOp) {
			return new CRUDResultBuilderForOKStep<T>(_securityContext,
													 _entityType,
													 requestedOp,performedOp);
		}
		public CRUDResultBuilderForOKStep<T> loaded() {
			return new CRUDResultBuilderForOKStep<T>(_securityContext,
													 _entityType,
													 PersistenceRequestedOperation.LOAD,PersistencePerformedOperation.LOADED);
		}
		public CRUDResultBuilderForOKStep<T> created() {
			return new CRUDResultBuilderForOKStep<T>(_securityContext,
													 _entityType,
													 PersistenceRequestedOperation.CREATE,PersistencePerformedOperation.CREATED);
		}
		public CRUDResultBuilderForOKStep<T> updated() {
			return new CRUDResultBuilderForOKStep<T>(_securityContext,
													 _entityType,
													 PersistenceRequestedOperation.UPDATE,PersistencePerformedOperation.UPDATED);
		}
		public CRUDResultBuilderForOKStep<T> deleted() {
			return new CRUDResultBuilderForOKStep<T>(_securityContext,
													 _entityType,
													PersistenceRequestedOperation.DELETE,PersistencePerformedOperation.DELETED);
		}
	}	
	public class CRUDResultBuilderOperationStep<T> 
		 extends CRUDResultBuilderOperationStepBase<T> {
		public CRUDResultBuilderOperationStep(final SecurityContext securityContext,
											  final Class<T> entityType) {
			super(securityContext,
				  entityType);
		}
	}
}