package r01f.model.persistence;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasOID;
import r01f.model.services.COREServiceMethodExecError;
import r01f.model.services.COREServiceMethodExecOK;
import r01f.model.services.COREServiceMethodExecResultBase;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="coreServiceMethodExecResult",typeId="crudOnMultiple")
@Accessors(prefix="_")
public class CRUDOnMultipleResult<T>
	 extends COREServiceMethodExecResultBase<Collection<T>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE DATA
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The model object type
	 * (beware that {@link PersistenceOperationOnObjectOK} wraps a {@link Collection}
	 *  of this objects)
	 */
	@MarshallField(as="modelObjType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _objectType;
	/**
	 * The result
	 */
	@MarshallField(as="methodExecResult",
				   whenXml=@MarshallFieldAsXml(collectionElementName="resultItem"))		// only when the result is a Collection (ie: find ops)
	@Getter @Setter protected Collection<CRUDResult<T>> _methodExecResult;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDOnMultipleResult(final PersistenceRequestedOperation reqOp) {
		super(reqOp.getCOREServiceMethod());
		_methodExecResult = Sets.newHashSet();
	}
	public CRUDOnMultipleResult(final Class<T> objectType,
								final PersistenceRequestedOperation reqOp) {
		super(reqOp.getCOREServiceMethod());
		_objectType = objectType;
		_methodExecResult = Sets.newHashSet();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ADD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a {@link PersistenceOperationResultForSingleRecord} (a successful {@link PersistenceOperationOK})
	 * @param opOK
	 */
	public void addOperationResult(final CRUDResult<T> opResult) {
//		if (_requestedOperation != opResult.getRequestedOperation()) throw new IllegalArgumentException("Unexpected requested operation: received " + opResult.getRequestedOperation() + " - " +
//						 																	            "expected " + _requestedOperationName);
		_methodExecResult.add(opResult);
	}
	/**
	 * Adds a {@link PersistenceOperationResultForSingleRecord} (a successful {@link PersistenceOperationOK})
	 * @param opOK
	 */
	public void addOperationOK(final CRUDOK<T> opOK) {
		this.addOperationResult(opOK);
	}
	/**
	 * Adds a {@link PersistenceOperationNOK} (a failed {@link PersistenceOperationOK})
	 * @param opNOK
	 */
	public void addOperationNOK(final CRUDError<T> opNOK) {
		this.addOperationResult(opNOK);
	}
	/**
	 * Adds a collection of performed operations
	 * @param okEntities
	 * @param reqOp
	 */
	public void addOperationsOK(final Collection<T> okEntities,
								final PersistenceRequestedOperation reqOp) {
		if (CollectionUtils.hasData(okEntities)) {
			for (T okEntity : okEntities) {
				CRUDOK<T> ok = new CRUDOK<T>(_objectType,
											 reqOp,PersistencePerformedOperation.from(reqOp),
										     okEntity);
				this.addOperationOK(ok);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the number of successful operations
	 */
	public int getNumberOfOperationsOK() {
		Collection<CRUDOK<T>> opsOK = this.getOperationsOK();
		return CollectionUtils.hasData(opsOK) ? opsOK.size() : 0;
	}
	/**
	 * Gets the {@link CRUDResult}s that were successful (ie the {@link CRUDOK} instances)
	 * @return the {@link CRUDOK} operations
	 */
	public Collection<CRUDOK<T>> getOperationsOK() {
		Collection<CRUDOK<T>> outOps = null;
		if (CollectionUtils.hasData(_methodExecResult)) {
			outOps = FluentIterable.from(_methodExecResult)
										.filter(new Predicate<CRUDResult<T>>() {
															@Override
															public boolean apply(final CRUDResult<T> op) {
																return op.hasSucceeded();	// only successful operations
															}
												})
									    .transform(new Function<CRUDResult<T>,CRUDOK<T>>() {
															@Override
															public CRUDOK<T> apply(final CRUDResult<T> op) {
																return op.asCRUDOK();	//as(CRUDOK.class);
															}
												   })
									    .toSet();
		}
		return outOps;
	}
	/**
	 * @return the number of unsuccessful operations
	 */
	public int getNumberOfOperationsNOK() {
		Collection<CRUDError<T>> opsNOK = this.getOperationsNOK();
		return CollectionUtils.hasData(opsNOK) ? opsNOK.size() : 0;
	}
	/**
	 * Gets the {@link CRUDResult}s that failed (ie the {@link CRUDError} instances)
	 * @return the {@link CRUDOK} operations
	 */
	public Collection<CRUDError<T>> getOperationsNOK() {
		Collection<CRUDError<T>> outOps = null;
		if (CollectionUtils.hasData(_methodExecResult)) {
			outOps = FluentIterable.from(_methodExecResult)
										.filter(new Predicate<CRUDResult<T>>() {
															@Override
															public boolean apply(final CRUDResult<T> op) {
																return op.hasFailed();	// only failed operations
															}
												})
									    .transform(new Function<CRUDResult<T>,CRUDError<T>>() {
															@Override
															public CRUDError<T> apply(final CRUDResult<T> op) {
																return op.asCRUDError();	//as(CRUDError.class);
															}
												   })
									    .toSet();
		}
		return outOps;
	}
	@Override
	public boolean hasFailed() {
		return this.haveAllFailed();
	}

	@Override
	public boolean hasSucceeded() {
		return this.haveAllSucceeded();
	}
	/**
	 * @return true if all persistence operations failed
	 */
	public boolean haveAllFailed() {
		return CollectionUtils.hasData(_methodExecResult) ? this.getOperationsNOK().size() == _methodExecResult.size()
														  : false;
	}
	/**
	 * @return true if all persistence operations succeeded
	 */
	public boolean haveAllSucceeded() {
		return CollectionUtils.hasData(_methodExecResult) ? this.getOperationsOK().size() == _methodExecResult.size()
														  : false;
	}
	/**
	 * @return true if there's any failed operation
	 */
	public boolean haveSomeFailed() {
		return CollectionUtils.hasData(_methodExecResult) ? this.getOperationsNOK().size() <= _methodExecResult.size()
														  : false;
	}
	/**
	 * @return true if there's any successful operation
	 */
	public boolean haveSomeSucceeded() {
		return CollectionUtils.hasData(_methodExecResult) ? this.getOperationsOK().size() <= _methodExecResult.size()
														  : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the successful persistence operations or throws a {@link PersistenceException}
	 * if any operation failed
	 * @return
	 * @throws PersistenceException
	 */
	public Collection<CRUDOK<T>> getSuccessfulResultsOrThrow() throws PersistenceException {
		Collection<CRUDError<T>> nokResults = this.getOperationsNOK();
		if (CollectionUtils.hasData(nokResults)) FluentIterable.from(nokResults)
																	.first().orNull()					// throw first exception
																	.throwAsPersistenceException();
		return this.getOperationsOK();
	}
	/**
	 * Gets the {@link CRUDResult}s that were successful (ie the {@link CRUDOK} instances)
	 * <ul>
	 * <li>If some operations were successful and other failed, this method returns ONLY the successful ones
	 *     and does NOT throws an exception for the failed ones</li>
	 * <li>If all operations failed, this method throws a {@link PersistenceException} with the {@link CRUDError}
	 *     for the first failed operation</li>
	 * </ul>
	 * On the contrary, the {@link #getStrict()} method throws a {@link PersistenceException} if
	 * there's any failed operation
	 * @return a {@link Set} of the records after being processed
	 * @throws PersistenceException if there's a general error or ALL of the operations failed
	 */
	public Collection<T> getSuccessfulNoThrow() {
		if (this.haveAllFailed()) {
			CRUDError<T> err = this.getFirstError();
			err.throwAsPersistenceException();
		}
		return this.getEntitiesOK();
	}
	/**
	 * Gets the multiple operation results or throws a {@link PersistenceException} if one of the
	 * operations has failed
	 * @return a {@link Set} of the records after being processed
	 * @throws PersistenceException if there's a general error or any of the operations failed
	 */
	public Collection<T> getAllSuccessfulOrThrow() throws PersistenceException {
		Collection<T> outResults = null;
		if (this.haveSomeSucceeded()) {
			// Returns all successful entities
			outResults = this.getEntitiesOK();
		} else {
			// Find the first error and throw it
			CRUDError<T> firstError = this.getFirstError();
			if (firstError != null) firstError.throwAsPersistenceException();
		}
		return outResults;
	}
	@Override
	public Collection<T> getOrThrow() throws PersistenceException {
		return this.getAllSuccessfulOrThrow();
	}
	/**
	 * @return the entities that were successfully processed
	 */
	public Collection<T> getEntitiesOK() {
		Set<T> outEntities = null;
		if (CollectionUtils.hasData(this.getOperationsOK())) {
			outEntities = FluentIterable.from(_methodExecResult)
									    .transform(new Function<CRUDResult<T>,T>() {
															@Override
															public T apply(final CRUDResult<T> op) {
																T entity = op.asCRUDOK()		//.as(CRUDOK.class)
																			 .getOrThrow();	// sure it won't throw
																return entity;
															}
												   })
									    .toSet();
		}
		return outEntities;
	}
	/**
	 * @return the first failed operation
	 */
	public CRUDError<T> getFirstError() {
		Collection<CRUDError<T>> opError = this.getOperationsNOK();
		return CollectionUtils.hasData(opError) ? CollectionUtils.of(opError)
																 .pickOneElement()
												: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecError<Collection<T>> asCOREServiceMethodExecError() {
		throw new UnsupportedOperationException();
	}
	@Override
	public COREServiceMethodExecOK<Collection<T>> asCOREServiceMethodExecOK() {
		throw new UnsupportedOperationException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		Collection<CRUDOK<T>> opsOK = this.getOperationsOK();
		Collection<CRUDError<T>> opsNOK = this.getOperationsNOK();

		int size = (opsOK != null ? opsOK.size() * 200 : 0) +
				   (opsNOK != null ? opsNOK.size() * 200 : 0);
		StringBuilder sb = new StringBuilder(size);
		String debugErrors = this.haveSomeFailed() ? _debugErrors(opsNOK) : null;
		String debugNonErrors = this.haveSomeSucceeded()  ? _debugNonErrors(opsOK) : null;
		if (Strings.isNOTNullOrEmpty(debugNonErrors)) sb.append(debugNonErrors);
		if (Strings.isNOTNullOrEmpty(debugErrors)) {
			if (Strings.isNOTNullOrEmpty(debugErrors)) sb.append("\n");
			sb.append(debugErrors);
		}
		return sb;
	}
	private String _debugNonErrors(final Collection<CRUDOK<T>> opsOK) {
		StringBuilder sb = null;
		if (CollectionUtils.hasData(opsOK)) {
			sb = new StringBuilder(50);
			sb.append("Persistence Operations OK: ")
			  .append(opsOK.size());
		}
		return sb != null ? sb.toString() : null;
	}
	/**
	 * Returns a debug string with all the erroneous items
	 * @return
	 */
	private String _debugErrors(final Collection<CRUDError<T>> opsNOK) {
		StringBuilder sb = null;
		if (CollectionUtils.hasData(opsNOK)) {
			sb = new StringBuilder(opsNOK.size()*200);
			sb.append("Persistence Operations with ERROR: ")
			  .append(opsNOK.size()).append("\n");
			for (CRUDError<T> opErr : opsNOK) {
				sb.append("\t")
				  .append(opErr.getTargetEntityIdInfo())
				  .append(": ")
				  .append(opErr.getErrorMessage())
				  .append("\n");
			}
		}
		return sb != null ? sb.toString() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private static String _oksToCommaSeparatedOids(final Collection<CRUDOK<?>> ops) {
		Set<String> oids = null;
		if (CollectionUtils.hasData(ops)) {
			oids = FluentIterable.from(ops)
								 .transform(new Function<CRUDOK<?>,String>() {
									 				@Override
													public String apply(final CRUDOK<?> op) {
									 					Object obj = op.getOrThrow();
									 					if (obj instanceof HasOID) return ((HasOID<?>)obj).getOid().asString();
									 					return "unknownOID";
													}
								 			})
								 .toSet();
		}
		return CollectionUtils.hasData(oids) ? CollectionUtils.of(oids).toStringCommaSeparated()
											 : null;
	}
	@SuppressWarnings("unused")
	private static String _noksToCommaSeparatedOids(final Collection<CRUDError<?>> ops) {
		Set<String> oids = null;
		if (CollectionUtils.hasData(ops)) {
			oids = FluentIterable.from(ops)
								 .transform(new Function<CRUDError<?>,String>() {
									 				@Override
													public String apply(final CRUDError<?> op) {
									 					return op.getTargetEntityIdInfo();
													}
								 			})
								 .toSet();
		}
		return CollectionUtils.hasData(oids) ? CollectionUtils.of(oids).toStringCommaSeparated()
											 : null;
	}
	@Override
	public String getDetailedMessage() {
		return "not implemented!!";		// TODO implement me!!
	}
}