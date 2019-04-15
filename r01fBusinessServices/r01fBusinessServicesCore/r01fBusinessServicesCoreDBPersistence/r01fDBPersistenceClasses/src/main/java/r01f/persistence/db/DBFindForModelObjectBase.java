package r01f.persistence.db;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.facets.util.Facetables;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.OID;
import r01f.guids.OIDs;
import r01f.guids.PersistableObjectOID;
import r01f.model.HasTrackingInfo;
import r01f.model.PersistableModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindOIDsResultBuilder;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.model.persistence.FindSummariesResult;
import r01f.model.persistence.FindSummariesResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.FactoryFrom;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.reflection.ReflectionUtils;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.util.types.collections.CollectionUtils;

/**
 * Base type for every persistence layer type
 * @param <O>
 * @param <M>
 * @param <PK>
 * @param <DB>
 */
@Slf4j
@Accessors(prefix="_")
public abstract class DBFindForModelObjectBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>,
							     			   PK extends DBPrimaryKeyForModelObject,DB extends DBEntity & DBEntityForModelObject<PK>>
			  extends DBBaseForModelObject<O,M,
			  				 			   PK,DB>
	       implements DBFindForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBFindForModelObjectBase(final Class<M> modelObjectType,final Class<DB> dbEntityType,
									final DBModuleConfig dbCfg,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(modelObjectType,dbEntityType,
			  dbCfg,
			  entityManager,
			  marshaller);
	}
	public DBFindForModelObjectBase(final Class<M> modelObjectType,final Class<DB> dbEntityType,
								    final TransformsDBEntityIntoModelObject<DB,M> dbEntityIntoModelObjectTransformer,
									final DBModuleConfig dbCfg,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(modelObjectType,dbEntityType,
			  dbEntityIntoModelObjectTransformer,
			  dbCfg,
			  entityManager,
			  marshaller);
	}
	public DBFindForModelObjectBase(final Class<M> modelObjectType,final Class<DB> dbEntityType,
								    final Function<DB,M> dbEntityIntoModelObjectTransformer,
									final DBModuleConfig dbCfg,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(modelObjectType,dbEntityType,
			  new TransformsDBEntityIntoModelObject<DB,M>() {
						@Override
						public M dbEntityToModelObject(final SecurityContext securityContext,
													   final DB dbEntity) {
							return dbEntityIntoModelObjectTransformer.apply(dbEntity);
						}
			  },
			  dbCfg,
			  entityManager,
			  marshaller);
	}
	@Deprecated
	public DBFindForModelObjectBase(final DBModuleConfig dbCfg,
									final Class<M> modelObjectType,final Class<DB> dbEntityType,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  entityManager,
			  marshaller);
	}
	@Deprecated
	public DBFindForModelObjectBase(final DBModuleConfig dbCfg,
									final Class<M> modelObjectType,final Class<DB> dbEntityType,
									final TransformsDBEntityIntoModelObject<DB,M> dbEntityIntoModelObjectTransformer,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  dbEntityIntoModelObjectTransformer,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<O> findAll(final SecurityContext securityContext) {
		return new QueryWrapper()
						.findOidsUsing(securityContext);
	}
	@Override
	public FindOIDsResult<O> findByCreateDate(final SecurityContext securityContext,
											  final Range<Date> createDate) {
		return new QueryWrapper()
						.addFilterByDateRangePredicate(createDate,"_createDate")
						.findOidsUsing(securityContext);
	}
	@Override
	public FindOIDsResult<O> findByLastUpdateDate(final SecurityContext securityContext,
												  final Range<Date> lastUpdateDate) {
		return new QueryWrapper()
					.addFilterByDateRangePredicate(lastUpdateDate,"lastUpdateDate")
					.findOidsUsing(securityContext);
	}
	@Override
	public FindOIDsResult<O> findByCreator(final SecurityContext securityContext,
										   final UserCode creatorUserCode) {
		return new QueryWrapper()
					.addFilterByUserPredicate(creatorUserCode,"_creator")
					.findOidsUsing(securityContext);
	}
	@Override
	public FindOIDsResult<O> findByLastUpdator(final SecurityContext securityContext,
											   final UserCode lastUpdatorUserCode) {
		return new QueryWrapper()
						.addFilterByUserPredicate(lastUpdatorUserCode,"_lastUpdator")
						.findOidsUsing(securityContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract class QueryWrapperBase<SELF_TYPE extends QueryWrapperBase<SELF_TYPE>> {

		public abstract CriteriaBuilder getCriteriaBuilder();
		public abstract <T> CriteriaQuery<T> getCriteriaQuery();
		public abstract <RDB extends DB> Root<RDB> getRoot();

		protected Collection<Predicate> _filterPredicates;

		public CriteriaQuery<? extends DB> composeCriteriaQuery() {
			if (CollectionUtils.hasData(_filterPredicates)) this.getCriteriaQuery()
																.where(_filterPredicates.toArray(new Predicate[_filterPredicates.size()]));
			return this.getCriteriaQuery();
		}

		@SuppressWarnings("unchecked")
		public SELF_TYPE addPredicate(final Predicate pred) {
			if (_filterPredicates == null) _filterPredicates = Lists.newArrayList();
			_filterPredicates.add(pred);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addFilterByIsNull(final String dbEntityCol) {
			Predicate oidPredicate = this.getCriteriaBuilder().isNull(this.getRoot().<String>get(dbEntityCol));
			this.addPredicate(oidPredicate);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addFilterByEntityTypePredicate(final Class<? extends DBEntity> dbEntityType) {
			// type(e) = dbEntityType
			Predicate oidPredicate = this.getCriteriaBuilder().equal(this.getRoot().type(),
																	 this.getCriteriaBuilder().literal(dbEntityType));
			this.addPredicate(oidPredicate);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addFilterByOidPredicate(final OID oid,final String dbEntityCol) {
			Predicate oidPredicate = this.getCriteriaBuilder().equal(this.getRoot().<String>get(dbEntityCol),
										 							 oid.asString());
			this.addPredicate(oidPredicate);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addFilterByDateRangePredicate(final Range<Date> dateRange,final String dbEntityCol) {
			Predicate dateRangePredicate = _buildDateRangePredicate(this.getCriteriaBuilder(),this.getRoot(),dbEntityCol,
													   				dateRange);
			this.addPredicate(dateRangePredicate);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addFilterByUserPredicate(final UserCode userCode,final String dbEntityCol) {
			Predicate userCodePredicate = _buildUserPredicate(this.getCriteriaBuilder(),this.getRoot(),
															  dbEntityCol,userCode);
			this.addPredicate(userCodePredicate);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addFilterByNumberPredicate(final Number num,final String dbEntityCol) {
			Predicate oidPredicate = this.getCriteriaBuilder().equal(this.getRoot().<Number>get(dbEntityCol),
										 							 num);
			this.addPredicate(oidPredicate);
			return (SELF_TYPE)this;
		}
		protected Predicate _buildDateRangePredicate(final CriteriaBuilder builder,
													 final Root<? extends DB> root,final String dbColName,
													 final Range<Date> dateRange) {
			Predicate outPredicate = null;
			if (dateRange != null) {
				if (dateRange.hasLowerBound() && dateRange.hasUpperBound()) {
					outPredicate = builder.between(root.<Date>get(dbColName),
												   dateRange.getLowerBound(),dateRange.getUpperBound());
				} else if (dateRange.hasLowerBound()) {
					outPredicate = builder.greaterThanOrEqualTo(root.<Date>get(dbColName),
																dateRange.getLowerBound());
				} else if (dateRange.hasUpperBound()) {
					outPredicate = builder.lessThanOrEqualTo(root.<Date>get(dbColName),
															 dateRange.getLowerBound());
				}
			}
			return outPredicate;
		}
		protected Predicate _buildUserPredicate(final CriteriaBuilder builder,
												final Root<? extends DB> root,final String dbColName,
												final UserCode userCode) {
			Predicate outPredicate = null;
			if (userCode != null) {
				outPredicate = builder.equal(root.<String>get(dbColName),
											 userCode.asString());
			}
			return outPredicate;
		}
		protected Predicate _buildIntegerRangePredicate(final CriteriaBuilder builder,
												   	    final Root<? extends DB> root,final String dbColName,
												   	    final Range<Integer> intRange) {

			Predicate outPredicate = null;
			if (intRange != null) {
				if (intRange.hasLowerBound() && intRange.hasUpperBound()) {
					if (intRange.getLowerBound().equals(intRange.getUpperBound())) {
						outPredicate = builder.equal(root.<Integer>get(dbColName),
													 intRange.getLowerBound());
					} else {
						outPredicate = builder.between(root.<Integer>get(dbColName),
													   intRange.getLowerBound(),intRange.getUpperBound());
					}
				} else if (intRange.hasLowerBound()) {
					outPredicate = builder.greaterThanOrEqualTo(root.<Integer>get(dbColName),
																intRange.getLowerBound());
				} else if (intRange.hasUpperBound()) {
					outPredicate = builder.lessThanOrEqualTo(root.<Integer>get(dbColName),
															 intRange.getLowerBound());
				}
			}
			return outPredicate;
		}
	}
	@Accessors(prefix="_")
	protected class QueryDBEntityWrapper
			extends QueryWrapperBase<QueryDBEntityWrapper> {
		@Getter protected final CriteriaBuilder _criteriaBuilder;
		@Getter protected final CriteriaQuery<? extends DB> _criteriaQuery;
		@Getter protected final Root<? extends DB> _root;

		public QueryDBEntityWrapper() {
			this(_DBEntityType);
		}
		public QueryDBEntityWrapper(final Class<? extends DB> dbEntityType) {
			_criteriaBuilder = _entityManager.getCriteriaBuilder();
			_criteriaQuery = _criteriaBuilder.createQuery(dbEntityType);
			_root = _criteriaQuery.from(dbEntityType);
		}
		@Deprecated
		public FindResult<M> exec(final SecurityContext securityContext) {
			return this.findUsing(securityContext);
		}
		@Deprecated
		public FindOIDsResult<O> execFindOids(final SecurityContext securityContext) {
			return this.findOidsUsing(securityContext);
		}
		public FindResult<M> findUsing(final SecurityContext securityContext) {
			this.composeCriteriaQuery();
			Collection<? extends DB> dbEntities = _entityManager.createQuery(_criteriaQuery)
																	.setHint(QueryHints.READ_ONLY,HintValues.TRUE)
																.getResultList();
			FindResult<M> outObjs = _buildResultsFromDBEntities(securityContext,
															    dbEntities);
			return outObjs;
		}
		public FindOIDsResult<O> findOidsUsing(final SecurityContext securityContext) {
			this.composeCriteriaQuery();
			Collection<? extends DB> dbEntities = _entityManager.createQuery(_criteriaQuery)
																	.setHint(QueryHints.READ_ONLY,HintValues.TRUE)
																.getResultList();
			FindOIDsResult<O> outOids = _buildOIDsResultsFromDBEntities(securityContext,
																		dbEntities);
			return outOids;
		}
	}
	@Accessors(prefix="_")
	protected class QueryWrapper
			extends QueryWrapperBase<QueryWrapper> {

		@Getter protected final CriteriaBuilder _criteriaBuilder;
		@Getter protected final CriteriaQuery<Tuple> _criteriaQuery;
		@Getter protected final Root<? extends DB> _root;

		public QueryWrapper(final String... colNames) {
			this(_DBEntityType,
				 colNames);
		}
		public QueryWrapper(final Class<? extends DB> dbEntityType,
							final String... colNames) {
			_criteriaBuilder = _entityManager.getCriteriaBuilder();
			_criteriaQuery = _criteriaBuilder.createTupleQuery();
			_root = _criteriaQuery.from(dbEntityType);

			// compose the sel cols
			Set<String> theColNames = Sets.newLinkedHashSet();
			theColNames.add("_oid");
			if (Facetables.hasFacet(_modelObjectType,HasVersionableFacet.class)) {
				theColNames.add("_version");
			}
			if (CollectionUtils.hasData(colNames)) {
				for (String colName : colNames) theColNames.add(colName);
			}
			// use projections to return ONLY the given cols (see http://stackoverflow.com/questions/12618489/jpa-criteria-api-select-only-specific-columns)
			List<Selection<?>> multisel = FluentIterable.from(theColNames)
														.transform(new Function<String,Selection<?>>() {
																		@Override
																		public Selection<?> apply(final String colName) {
																			return _root.get(colName);
																		}
																   })
														.toList();
			if (CollectionUtils.hasData(multisel)) _criteriaQuery.multiselect(multisel);
		}
		@Deprecated
		public FindOIDsResult<O> exec(final SecurityContext securityContext) {
			return this.findOidsUsing(securityContext);
		}
		@Deprecated
		public <S extends SummarizedModelObject<M>> FindSummariesResult<M> exec(final SecurityContext securityContext,
										   										final Function<Object[],S> dbTupleToSummaryConverter) {
			return this.findSummariesUsing(securityContext)
					   .convertingTupleValuesUsing(dbTupleToSummaryConverter);
		}
		public FindOIDsResult<O> findOidsUsing(final SecurityContext securityContext) {
			this.composeCriteriaQuery();
			List<Tuple> tupleResult = _entityManager.createQuery(_criteriaQuery)
															.setHint(QueryHints.READ_ONLY,HintValues.TRUE)
												    .getResultList();
			FindOIDsResult<O> outOids = _buildOIDsResultsFromDBTuples(securityContext,
															      	  tupleResult);
			return outOids;
		}
		public QueryWrapperExecResultTransform findSummariesUsing(final SecurityContext securityContext) {
			if (CollectionUtils.hasData(_filterPredicates)) _criteriaQuery.where(_filterPredicates.toArray(new Predicate[_filterPredicates.size()]));
			List<Tuple> tupleResult = _entityManager.createQuery(_criteriaQuery)
														.setHint(QueryHints.READ_ONLY,HintValues.TRUE)
												    .getResultList();
			return new QueryWrapperExecResultTransform(securityContext,
													   tupleResult);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class QueryWrapperExecResultTransform {
		private final SecurityContext _securityContext;
		private final List<Tuple> _tupleResult;
		
		public <S extends SummarizedModelObject<M>> FindSummariesResult<M> convertingTupleValuesUsing(final Function<Object[],S> dbTupleToSummaryConverter) {
			FindSummariesResult<M> outSummaries = _buildSummariesResultFromTupleValues(_securityContext,
															 					 	   _tupleResult,dbTupleToSummaryConverter);
			return outSummaries;
		}
		public <S extends SummarizedModelObject<M>> FindSummariesResult<M> convertingTuplesUsing(final Function<Tuple,S> dbTupleToSummaryConverter) {
			FindSummariesResult<M> outSummaries = _buildSummariesResultFromTuple(_securityContext,
															 					 _tupleResult,dbTupleToSummaryConverter);
			return outSummaries;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RESULTS BUILDING
/////////////////////////////////////////////////////////////////////////////////////////
	protected Function<DB,M> _dbEntityToModelObjTransformUsingDescriptor =
			new Function<DB,M>() {
					@Override
					public M apply(final DB dbEntity) {
						DBEntityHasModelObjectDescriptor hasXmlDescriptor = (DBEntityHasModelObjectDescriptor)dbEntity;
						String xmlDescriptorAsString = hasXmlDescriptor.getDescriptor();
						M outM = _modelObjectsMarshaller.forReading().fromXml(xmlDescriptorAsString,
																			  _modelObjectType);
						// do not forget to copy the tracking info and entity version info
						if (dbEntity instanceof HasEntityVersion) {
							outM.setEntityVersion(((HasEntityVersion)dbEntity).getEntityVersion());
						} else {
							log.debug("{} does NOT has entity version (does NOT implements {})",
									  outM.getClass().getSimpleName(),HasTrackingInfo.class.getName());
						}
						if (dbEntity instanceof HasTrackingInfo) {
							outM.setTrackingInfo(((HasTrackingInfo)dbEntity).getTrackingInfo());
						} else {
							log.debug("{} does NOT has tracking info (does NOT implements {})",
									  outM.getClass().getSimpleName(),HasTrackingInfo.class.getName());
						}
						return outM;
					}
			};
	protected FindResult<M> _buildResultsFromDBEntities(final SecurityContext securityContext,
													    final Collection<? extends DB> dbEntities) {
		if (!ReflectionUtils.isImplementing(_DBEntityType,DBEntityHasModelObjectDescriptor.class)) {
			throw new IllegalStateException("The DBEntity " + _DBEntityType + " DOES NOT implements " + DBEntityHasModelObjectDescriptor.class + "; " +
											"the only option to build the model object from the DBEntity is to override this method and build the find results by hand");
		}
		FindResult<M> outResult = null;
		if (CollectionUtils.hasData(dbEntities)) {
			Collection<M> modelObjs = FluentIterable.from(dbEntities)
													.transform(_dbEntityToModelObjTransformUsingDescriptor)
													.toList();
			outResult = FindResultBuilder.using(securityContext)
										 .on(_modelObjectType)
										 .foundEntities(modelObjs);
		} else {
			outResult = FindResultBuilder.using(securityContext)
										 .on(_modelObjectType)
										 .noEntityFound();
		}
		return outResult;
	}
	protected FindOIDsResult<O> _buildOIDsResultsFromDBEntities(final SecurityContext securityContext,
													    		final Collection<? extends DB> dbEntities) {
		FindOIDsResult<O> outResult = null;
		if (CollectionUtils.hasData(dbEntities)) {
			Collection<O> oids = FluentIterable.from(dbEntities)
													.transform(new Function<DB,O>() {
																		@Override
																		public O apply(final DB dbEntity) {
																			return OIDs.<O,M>createOIDFor(_modelObjectType,
																									 	  dbEntity.getOid());
																		}
															   })
													.toList();
			outResult = FindOIDsResultBuilder.using(securityContext)
										 	 .on(_modelObjectType)
										 	 .foundEntitiesWithOids(oids);
		} else {
			outResult = FindOIDsResultBuilder.using(securityContext)
										 	 .on(_modelObjectType)
										 	 .noEntityFound();
		}
		return outResult;
	}
	protected FindOIDsResult<O> _buildOIDsResultsFromDBTuples(final SecurityContext securityContext,
													      	  final Collection<Tuple> dbTuples) {
		FindOIDsResult<O> outOids = null;
		if (CollectionUtils.hasData(dbTuples)) {
			Class<O> oidType = OIDs.oidTypeFor(_modelObjectType);
			boolean isVersionableOid = Facetables.hasFacet(_modelObjectType,HasVersionableFacet.class);
			FactoryFrom<Tuple,O> oidFactory = _createDefaultOidFromTupleFactory(oidType,isVersionableOid);
			outOids = _buildOIDsResultsFromDBTuples(securityContext,
													dbTuples,
													oidFactory);
		} else {
			outOids = FindOIDsResultBuilder.using(securityContext)
										   .on(_modelObjectType)
										   .noEntityFound();
		}
		return outOids;
	}
	protected FindOIDsResult<O> _buildOIDsResultsFromDBTuples(final SecurityContext securityContext,
													      	  final Collection<Tuple> dbTuples,
													      	  final FactoryFrom<Tuple,O> oidFromStringFactory) {

		FindOIDsResult<O> outOids = null;
		if (CollectionUtils.hasData(dbTuples)) {
			Collection<O> oids = Lists.newArrayListWithExpectedSize(dbTuples.size());
			for (Tuple tuple : dbTuples) {
				O oid = oidFromStringFactory.from(tuple);
				oids.add(oid);
			}
			outOids = FindOIDsResultBuilder.using(securityContext)
										   .on(_modelObjectType)
										   .foundEntitiesWithOids(oids);
		} else {
			outOids = FindOIDsResultBuilder.using(securityContext)
										   .on(_modelObjectType)
										   .noEntityFound();
		}
		return outOids;
	}
	protected <S extends SummarizedModelObject<M>> FindSummariesResult<M> _buildSummariesResultFromTupleValues(final SecurityContext securityContext,
																										 	   final Collection<Tuple> dbTuples,final Function<Object[],S> dbTupleToSummaryConverter) {
		FindSummariesResult<M> outSummaries = null;
		if (CollectionUtils.hasData(dbTuples)) {
			Collection<S> summaries = Lists.newArrayListWithExpectedSize(dbTuples.size());
			for (Tuple tuple : dbTuples) {
				S summary = dbTupleToSummaryConverter.apply(tuple.toArray());
				summaries.add(summary);
			}
			outSummaries = FindSummariesResultBuilder.using(securityContext)
													 .on(_modelObjectType)
													 .foundSummaries(summaries);
		} else {
			outSummaries = FindSummariesResultBuilder.using(securityContext)
													 .on(_modelObjectType)
													 .noSummaryFound();
		}
		return outSummaries;
	}
	protected <S extends SummarizedModelObject<M>> FindSummariesResult<M> _buildSummariesResultFromTuple(final SecurityContext securityContext,
																										 final Collection<Tuple> dbTuples,final Function<Tuple,S> dbTupleToSummaryConverter) {
		FindSummariesResult<M> outSummaries = null;
		if (CollectionUtils.hasData(dbTuples)) {
			Collection<S> summaries = Lists.newArrayListWithExpectedSize(dbTuples.size());
			for (Tuple tuple : dbTuples) {
				S summary = dbTupleToSummaryConverter.apply(tuple);
				summaries.add(summary);
			}
			outSummaries = FindSummariesResultBuilder.using(securityContext)
													 .on(_modelObjectType)
													 .foundSummaries(summaries);
		} else {
			outSummaries = FindSummariesResultBuilder.using(securityContext)
													 .on(_modelObjectType)
													 .noSummaryFound();
		}
		return outSummaries;
	}
	private FactoryFrom<Tuple,O> _createDefaultOidFromTupleFactory(final Class<O> oidType,final boolean isVersionableOid) {
		return new FactoryFrom<Tuple,O>() {
						@Override
						public O from(final Tuple dbTuple) {
							O oid = null;
							if (isVersionableOid) {
								String versionIndependentOidAsString = (String)dbTuple.get(0);
								String versionOid = (String)dbTuple.get(1);
								oid = OIDs.createVersionableOIDFromString(oidType,
										 					 		      versionIndependentOidAsString,versionOid);
							} else {
								String oidAsString = (String)dbTuple.get(0);
								oid = OIDs.createOIDFromString(oidType,
															   oidAsString);
							}
							return oid;
						}
			   };
	}
//	private O _tupleOid(final Class<O> oidType,final boolean isVersionableOid,
//						final Tuple dbTuple) {
//		O oid = null;
//		if (isVersionableOid) {
//			String versionIndependentOidAsString = (String)dbTuple.get(0);
//			String versionOid = (String)dbTuple.get(1);
//			oid = OIDs.createVersionableOIDFromString(oidType,
//					 					 		      versionIndependentOidAsString,versionOid);
//		} else {
//			String oidAsString = (String)dbTuple.get(0);
//			oid = OIDs.createOIDFromString(oidType,
//										   oidAsString);
//		}
//		return oid;
//	}
}
