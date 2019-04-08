package r01f.model.persistence;

import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.model.ModelObject;
import r01f.model.persistence.FindError;
import r01f.model.persistence.FindOK;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.PersistenceErrorType;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.patterns.IsBuilder;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.DBEntityToModelObjectTransformerBuilder;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Builder type for {@link FindResult}-implementing types:
 * <ul>
 * 		<li>A successful FIND operation result: {@link FindOK}</li>
 * 		<li>An error on a FIND operation execution: {@link FindError}</li>
 * </ul>
 * If the find operation execution was successful and entities are returned:
 * <pre class='brush:java'>
 * 		FindOK<MyEntity> opOK = FindResultBuilder.using(securityContext)
 * 											     .on(MyEntity.class)
 * 												  	   .foundEntities(myEntityInstances);
 * </pre>
 * If an error is raised while executing an entity find operation:
 * <pre class='brush:java'>
 * 		FindError<MyEntity> opError = FindResultBuilder.using(securityContext)
 * 													   .on(MyEntity.class)
 * 														   	.errorFindingEntities()
 * 																.causedBy(error);
 * </pre>
 */
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FindResultBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static FindResultBuilderEntityStep using(final SecurityContext securityContext) {
		return new FindResultBuilder() {/* nothing */}
						.new FindResultBuilderEntityStep(securityContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class FindResultBuilderEntityStep {
		private final SecurityContext _securityContext;
		
		public <T> FindResultBuilderOperationStep<T> on(final Class<T> entityType) {
			return new FindResultBuilderOperationStep<T>(_securityContext,
														 entityType);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class FindResultBuilderOperationStep<T> {
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		
		
		//  --------- ERROR
		public FindResultBuilderForError<T> errorFindingEntities() {
			return new FindResultBuilderForError<T>(_securityContext,
													_entityType);	
		}
		// ---------- SUCCESS FINDING 
		public FindOK<T> foundEntities(final Collection<T> entities) {
			return _buildFoundEntitiesCollection(entities,
												 _entityType);
		}
		public <DB extends DBEntity> FindResultBuilderDBEntityTransformerStep<DB,T> foundDBEntities(final Collection<DB> dbEntities) {
			return new FindResultBuilderDBEntityTransformerStep<DB,T>(_securityContext,
																	  _entityType,
																	  dbEntities);
		}
		public FindOK<T> noEntityFound() {
			FindOK<T> outFoundEntities = new FindOK<T>();
			outFoundEntities.setFoundObjectType(_entityType);
			outFoundEntities.setRequestedOperation(PersistenceRequestedOperation.FIND);
			outFoundEntities.setPerformedOperation(PersistencePerformedOperation.FOUND);
			outFoundEntities.setOperationExecResult(Lists.<T>newArrayList());	// no data found
			return outFoundEntities;
		}
	}	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class FindResultBuilderDBEntityTransformerStep<DB extends DBEntity,
														  T> {
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		protected final Collection<DB> _dbEntities;
		
		public <M extends ModelObject> FindOK<M> transformedToModelObjectsUsing(final TransformsDBEntityIntoModelObject<DB,M> dbEntityToModelObjectTransformer) {
			return this.transformedToModelObjectsUsing(DBEntityToModelObjectTransformerBuilder.<DB,M>createFor(_securityContext,
													  													  		 dbEntityToModelObjectTransformer));
		}
		@SuppressWarnings("unchecked")
		public <M extends ModelObject> FindOK<M> transformedToModelObjectsUsing(final Function<DB,M> transformer) {
			Collection<M> entities = null;
			if (CollectionUtils.hasData(_dbEntities)) {
				Function<DB,M> dbEntityToModelObjectTransformer = DBEntityToModelObjectTransformerBuilder.createFor(_securityContext,
																								  					transformer);
				entities = FluentIterable.from(_dbEntities)
										 .transform(dbEntityToModelObjectTransformer)
										  .filter(Predicates.notNull())
										  	.toList();
			} else {
				entities = Sets.newHashSet();
			}
			return _buildFoundEntitiesCollection(entities,
												 (Class<M>)_entityType);
		}
		
	}
	private static <T> FindOK<T> _buildFoundEntitiesCollection(final Collection<T> entities,
															   final Class<T> entityType) {
		FindOK<T> outFoundEntities = new FindOK<T>();
		outFoundEntities.setFoundObjectType(entityType);
		outFoundEntities.setRequestedOperation(PersistenceRequestedOperation.FIND);
		outFoundEntities.setPerformedOperation(PersistencePerformedOperation.FOUND);
		outFoundEntities.setOperationExecResult(entities);
		return outFoundEntities;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class FindResultBuilderForError<T> {
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		
		public FindError<T> causedBy(final Throwable th) {
			return new FindError<T>(_entityType,
									th);
		}
		public FindError<T> causedBy(final String cause) {
			return new FindError<T>(_entityType,
									cause,
									PersistenceErrorType.SERVER_ERROR);
		}
		public FindError<T> causedBy(final String cause,final Object... vars) {
			return this.causedBy(Strings.customized(cause,vars));
		}
		public FindError<T> causedByClientBadRequest(final String msg,final Object... vars) {
			FindError<T> outError = new FindError<T>(_entityType,
											     	 Strings.customized(msg,vars),			// the error message
											     	 PersistenceErrorType.BAD_REQUEST_DATA);	// is a client error?
			return outError;
		}
	}
}
