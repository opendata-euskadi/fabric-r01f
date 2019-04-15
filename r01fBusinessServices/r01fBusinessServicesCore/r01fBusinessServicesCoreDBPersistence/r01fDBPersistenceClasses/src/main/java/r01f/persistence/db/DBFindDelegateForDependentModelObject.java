package r01f.persistence.db;

import com.google.common.base.Function;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.persistence.db.DBFindForModelObjectBase.QueryDBEntityWrapper;
import r01f.persistence.db.DBFindForModelObjectBase.QueryWrapper;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.annotations.DBEntitySummaryField;
import r01f.persistence.db.entities.annotations.ParentOidDBEntityField;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.securitycontext.SecurityContext;
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
public abstract class DBFindDelegateForDependentModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>,P extends PersistableModelObject<?>,
							     			   				PK extends DBPrimaryKeyForModelObject,DB extends DBEntity & DBEntityForModelObject<PK>>
	       implements DBFindForDependentModelObject<O,M,P> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Class<P> _parentObjType;
	@Getter protected final DBFindForModelObjectBase<O,M,PK,DB> _dbFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBFindDelegateForDependentModelObject(final Class<P> parentModelObjType,
												 final DBFindForModelObjectBase<O,M,PK,DB> dbFind) {
		_parentObjType = parentModelObjType;
		_dbFind = dbFind;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({ "unchecked","rawtypes" })
	public <PO extends OID> FindOIDsResult<O> findOidsOfDependentsOf(final SecurityContext securityContext,
											  						 final PO parentOid) {
		QueryWrapper qry = _dbFind.new QueryWrapper();
		qry.addFilterByOidPredicate(parentOid,_getParentOidDBEntityFieldName());
		return qry.findOidsUsing(securityContext);
	}
	@Override @SuppressWarnings({ "unchecked","rawtypes" })
	public <PO extends OID> FindResult<M> findDependentsOf(final SecurityContext securityContext,
														   final PO parentOid) {
		QueryDBEntityWrapper qry = _dbFind.new QueryDBEntityWrapper();
		qry.addFilterByOidPredicate(parentOid,_getParentOidDBEntityFieldName());
		return qry.exec(securityContext);
	}
	@Override @SuppressWarnings({ "unchecked","rawtypes" })
	public <PO extends OID> FindSummariesResult<M> findSummariesOfDependentsOf(final SecurityContext securityContext,
															  				   final PO parentOid) {
		QueryWrapper qry = _dbFind.new QueryWrapper(_summarizedDBRowCols());
		qry.addFilterByOidPredicate(parentOid,this._getParentOidDBEntityFieldName());
		return qry.findSummariesUsing(securityContext)
				  .convertingTuplesUsing(_dbRowToSummarizedModelObjectTranformFunction(securityContext));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the name of the {@link DBEntity} field that acts as parent {@link DBEntity} reference
	 * @return
	 */
	protected String _getParentOidDBEntityFieldName() {
		// use reflection to find the field annotated with @ParentOidDBEntityField
		FieldAnnotated<ParentOidDBEntityField>[] fields = ReflectionUtils.fieldsAnnotated(_dbFind.getDBEntityType(),
																						  ParentOidDBEntityField.class);
		if (CollectionUtils.isNullOrEmpty(fields)) throw new IllegalStateException("The db entity " + _dbFind.getDBEntityType() + " DOES NOT have a field annotated with @" + ParentOidDBEntityField.class.getSimpleName() + " that stores the parent entity's oid");
		if (fields.length > 1) throw new IllegalStateException("The db entity " + _dbFind.getDBEntityType() + " HAS MORE THAN A SINGLE FIELD annotated with @" + ParentOidDBEntityField.class.getSimpleName() + " that stores the parent entity's oid");
		
		return fields[0].getField().getName();
	}
	/**
	 * Returns the cols to be returned when returning a summarized object
	 * @return
	 */
	protected String[] _summarizedDBRowCols() {
		// use reflection to find the fields annotated with @ParentOidDBEntityField	
		FieldAnnotated<ParentOidDBEntityField>[] parentFields = ReflectionUtils.fieldsAnnotated(_dbFind.getDBEntityType(),
																								ParentOidDBEntityField.class);
		
		// use reflection to find the fields annotated with @DBEntitySummaryField
		FieldAnnotated<DBEntitySummaryField>[] fields = ReflectionUtils.fieldsAnnotated(_dbFind.getDBEntityType(),
																						DBEntitySummaryField.class);
		if (CollectionUtils.isNullOrEmpty(fields)) log.warn("The db entity {} DOES NOT have any field annotated with @{} to be included at the SELECT clause when querying for summaries",
															_dbFind.getDBEntityType(),DBEntitySummaryField.class.getSimpleName());
		
		int colNum = (CollectionUtils.hasData(parentFields) ? parentFields.length : 0) +
				   	 fields.length;
		String[] outCols = new String[colNum];		
		int i=0;
		
		// the cols for the fields annotated with @DBEntitySummaryField
		for (FieldAnnotated<DBEntitySummaryField> field : fields) {
			outCols[i] = field.getField().getName();
			i++;
		}
		// the cols for the fields annotated with @ParentOidDBEntityField
		if (CollectionUtils.hasData(parentFields)) {
			for (FieldAnnotated<ParentOidDBEntityField> parentField : parentFields) {
				outCols[i] = parentField.getField().getName();
				i++;
			}
		}
		return outCols;
	}
	/**
	 * Returns a function that transforms a db row to a summarized model object
	 * @return
	 */
	protected abstract <S extends SummarizedModelObject<M>> Function<Object[],S> _dbRowToSummarizedModelObjectTranformFunction(SecurityContext securityContext);
}
