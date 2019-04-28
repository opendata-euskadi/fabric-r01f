package r01f.model.search.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.generics.TypeRef;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.TypeMetaData;
import r01f.model.search.query.ContainsTextQueryClause.ContainedTextAt;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;



/**
 * A fluent-api to build a brunch of query predicates / clauses
 * Usage:
 * <pre class='brush:java'>
 *		BooleanQueryClause boolQry = BooleanQueryClause.create()
 *												   			.field("myField").must().beEqualTo(new Date())
 *												   			.field("myField2").should().beInsideLast(5).minutes()
 *												   			.field("myField3").mustNOT().beWithin(10D,12D,11D)
 *												   		.build();
 * </pre>
 */
@MarshallType(as="booleanQuery")
@GwtIncompatible
@Accessors(prefix="_")
public class BooleanQueryClause 
  implements QueryClause {	

	private static final long serialVersionUID = -240591382802340581L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// a field used to identify the boolean query (not mandatory)
	@MarshallIgnoredField
	@Getter @Setter private transient String _internalId; 
	
	@MarshallField(as="clauses")
	@Getter @Setter private Set<QualifiedQueryClause<? extends QueryClause>> _clauses;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public BooleanQueryClause() {
		// default no-args constructo
	}
	public BooleanQueryClause(final Set<QualifiedQueryClause<? extends QueryClause>> clauses) {
		_clauses = clauses;
	}
	public BooleanQueryClause(final String id,
							  final Set<QualifiedQueryClause<? extends QueryClause>> clauses) {
		this(clauses);
		_internalId = id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public BooleanQueryClauseStep0Builder predicates() {
		return new BooleanQueryClauseStep0Builder(_internalId,_clauses);
	}
	public <Q extends QueryClause> void add(final QualifiedQueryClause<Q> clause) {
		if (_clauses == null) _clauses = new HashSet<QualifiedQueryClause<? extends QueryClause>>();
		_clauses.add(clause);
	}
	public <Q extends QueryClause> void add(final Q clause,final QueryClauseOccur occur) {
		QualifiedQueryClause<Q> qClause = new QualifiedQueryClause<Q>(clause,occur);
		this.add(qClause);
	}
	public boolean removeAllFor(final FieldID fieldId) {
		if (CollectionUtils.isNullOrEmpty(_clauses)) return false;
		Set<QualifiedQueryClause<? extends QueryClause>> clausesToBeRemoved = Sets.newHashSet();
		for (QualifiedQueryClause<? extends QueryClause> clause : _clauses) {
			if (clause.getClause().getFieldId().is(fieldId)) clausesToBeRemoved.add(clause);
		}
		_clauses.removeAll(clausesToBeRemoved);
		return CollectionUtils.hasData(clausesToBeRemoved);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FieldID getFieldId() {
		throw new UnsupportedOperationException("BooleanQueryClauses are NOT aplicable to fields");
	}
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.BOOLEAN;
	}	
	@Override @SuppressWarnings("unchecked")
	public <Q extends QueryClause> Q as(final Class<Q> type) {
		return (Q)this;
	}
	@Override @SuppressWarnings("unchecked")
	public <Q extends QueryClause> Q as(final TypeRef<Q> type) {
		return (Q)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <V> V getValue() {
		throw new UnsupportedOperationException();
	}
	@Override
	public <V> Class<V> getValueType() {
		throw new UnsupportedOperationException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes the clauses in a {@link String}
	 * @return
	 */
	public String encodeAsString() {
		String outEncodedClauses = null;
		if (CollectionUtils.hasData(_clauses)) {
			StringBuilder sb = new StringBuilder(_clauses.size()*50);
			for (Iterator<QualifiedQueryClause<? extends QueryClause>> clauseIt = _clauses.iterator(); clauseIt.hasNext(); ) {
				QualifiedQueryClause<? extends QueryClause> qClause = clauseIt.next();
				String serializedClause = QueryClauseStringEncoderDecoder.encode(qClause);	// encode
				sb.append(serializedClause);
				if (clauseIt.hasNext()) sb.append(";");
			}
			outEncodedClauses = sb.toString();
		}
		return outEncodedClauses;
	}
	/**
	 * Decodes the clauses from a {@link String}
	 * @param str
	 * @param modelObjMetaData
	 * @return
	 */
	public static BooleanQueryClause fromString(final String str,
												final TypeMetaData<?> modelObjMetaData) {
		BooleanQueryClause outQryClause = null;
		if (Strings.isNOTNullOrEmpty(str)) {
			String[] clauses = str.split(";");
			if (CollectionUtils.hasData(clauses)) {
				outQryClause = new BooleanQueryClause(new HashSet<QualifiedQueryClause<? extends QueryClause>>(clauses.length));
				for (String clauseStr : clauses) {
					QualifiedQueryClause<? extends QueryClause> clause = QueryClauseStringEncoderDecoder.decode(clauseStr,
																												modelObjMetaData);	// decode
					outQryClause.add(clause);
				}
			}
		}
		return outQryClause;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds a BooleanQueryClause identified by the given id
	 * (note that the BooleanQueryClause id is NOT mandatory)
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public QualifiedQueryClause<BooleanQueryClause> findBooleanQueryClause(final String id) {
		if (CollectionUtils.isNullOrEmpty(_clauses)) return null;
		QualifiedQueryClause<BooleanQueryClause> outQClause = null;
		for (QualifiedQueryClause<? extends QueryClause> qClause : _clauses) {
			if (!(qClause.getClause() instanceof BooleanQueryClause)) continue;	
			
			BooleanQueryClause boolQryClause = (BooleanQueryClause)qClause.getClause();
			if (boolQryClause.getInternalId() != null
			 && boolQryClause.getInternalId().equals(id)) {
				outQClause = (QualifiedQueryClause<BooleanQueryClause>)qClause;
				break;
			}
		}
		return outQClause;
	}
	/**
	 * Finds a qualified query clause
	 * @param fieldId
	 * @return
	 */
	public QualifiedQueryClause<? extends QueryClause> findQualifiedQueryClause(final FieldID fieldId) {
		if (CollectionUtils.isNullOrEmpty(_clauses)) return null;
		QualifiedQueryClause<? extends QueryClause> outQClause = null;
		for (QualifiedQueryClause<? extends QueryClause> qClause : _clauses) {
			if (qClause.getClause() instanceof BooleanQueryClause) continue;	// boolean query clauses DO NOT have id
			
			if (qClause.getClause().getFieldId().equals(fieldId)) {
				outQClause = qClause;
				break;
			}
		}
		return outQClause;
	}
	/**
	 * Finds a qualified query clause with an occur 
	 * @param fieldId
	 * @param occur
	 * @return
	 */
	public QualifiedQueryClause<? extends QueryClause> findQualifiedQueryClause(final FieldID fieldId,final QueryClauseOccur occur) {
		if (CollectionUtils.isNullOrEmpty(_clauses)) return null;
		QualifiedQueryClause<? extends QueryClause> outQClause = null;
		for (QualifiedQueryClause<? extends QueryClause> qClause : _clauses) {
			if (qClause.getClause() instanceof BooleanQueryClause) continue;	// boolean query clauses DO NOT have id
			
			if (qClause.getClause().getFieldId().equals(fieldId) && qClause.getOccur().is(occur)) {
				outQClause = qClause;
				break;
			}
		}
		return outQClause;
	}
	/**
	 * Finds a qualified clause by it's name and type
	 * @param fieldId
	 * @param clauseType
	 * @return
	 */
	public QualifiedQueryClause<? extends QueryClause> findQualifiedQueryClauseOfType(final FieldID fieldId,
											 								 		  final Class<? extends QueryClause> clauseType) {
		if (CollectionUtils.isNullOrEmpty(_clauses)) return null;
		QualifiedQueryClause<? extends QueryClause> outQClause = null;
		for (QualifiedQueryClause<? extends QueryClause> qClause : _clauses) {
			if (qClause.getClause().getFieldId().equals(fieldId)
			 && qClause.getClause().getClass() == clauseType) {
				outQClause = qClause;
				break;
			}
		}
		return outQClause;		
	}
	/**
	 * Finds a qualified clause by it's name and type
	 * @param fieldId
	 * @param clauseType
	 * @return
	 */
	public QualifiedQueryClause<? extends QueryClause> findQualifiedQueryClauseOfType(final FieldID fieldId,
											 										  final Class<? extends QueryClause> clauseType,
											 										  final QueryClauseOccur occur) {
		if (CollectionUtils.isNullOrEmpty(_clauses)) return null;
		QualifiedQueryClause<? extends QueryClause> outQClause = null;
		for (QualifiedQueryClause<? extends QueryClause> qClause : _clauses) {
			if (qClause.getClause().getFieldId().equals(fieldId)
			 && qClause.getClause().getClass() == clauseType
			 && qClause.getOccur().is(occur)) {
				outQClause = qClause;
				break;
			}
		}
		return outQClause;		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds a clause by it's name
	 * @param fieldId
	 * @return
	 */
	public QueryClause findQueryClause(final FieldID fieldId) {
		QualifiedQueryClause<? extends QueryClause> qClause = this.findQualifiedQueryClause(fieldId);
		return qClause != null ? qClause.getClause() : null;
	}
	/**
	 * Finds a clause by it's name and occurrence
	 * @param fieldId
	 * @param occur
	 * @return
	 */
	public QueryClause findQueryClause(final FieldID fieldId,final QueryClauseOccur occur) {
		QualifiedQueryClause<? extends QueryClause> qClause = this.findQualifiedQueryClause(fieldId,occur);
		return qClause != null ? qClause.getClause() : null;
	}
	/**
	 * Finds a clause by it's name and type
	 * @param fieldId
	 * @param clauseType
	 * @return
	 */
	public QueryClause findQueryClauseOfType(final FieldID fieldId,
											 final Class<? extends QueryClause> clauseType) {
		QualifiedQueryClause<? extends QueryClause> qClause = this.findQualifiedQueryClauseOfType(fieldId,
																								  clauseType);
		return qClause != null ? qClause.getClause() : null;
	}
	/**
	 * Finds a clause by it's name and type
	 * @param fieldId
	 * @param clauseType
	 * @return
	 */
	public QueryClause findQueryClauseOfType(final FieldID fieldId,
											 final Class<? extends QueryClause> clauseType,
											 final QueryClauseOccur occur) {
		QualifiedQueryClause<? extends QueryClause> qClause = this.findQualifiedQueryClauseOfType(fieldId,
																								  clauseType,
																								  occur);
		return qClause != null ? qClause.getClause() : null;
	}
	/**
	 * Returns the language filter clause if it exists
	 * @param fieldId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public EqualsQueryClause<Language> findLanguageQueryClause(final FieldID fieldId) {
		EqualsQueryClause<Language> outLangClause = null;
		QueryClause langClause = this.findQueryClause(fieldId);
		if (langClause instanceof EqualsQueryClause) {
			outLangClause = (EqualsQueryClause<Language>)langClause;
		}
		return outLangClause;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof BooleanQueryClause)) return false;
		
		BooleanQueryClause otherBool = (BooleanQueryClause)obj;
		if (!(_internalId == null && otherBool.getInternalId() == null)) {
			boolean idEq = _internalId != null && otherBool.getInternalId() != null
								? _internalId.equals(otherBool.getInternalId())
								: false;		// one null anothoer not
			if (idEq) return true;	// if same id, both equals
		}
		return _clauses != null ? otherBool.getClauses() != null ? _clausesEqs(_clauses,otherBool.getClauses())
							    							     : false
							    : true;		// both null
	}
	private boolean _clausesEqs(final Set<QualifiedQueryClause<? extends QueryClause>> a,final Set<QualifiedQueryClause<? extends QueryClause>> b) {
		if (a.size() != b.size()) return false;
		boolean outEqs = true;
		
		// TODO: item order is NOT guaranteed so how to compare both sets???
		
		return outEqs;
	}
	@Override
	public int hashCode() {
		return _internalId != null ? _internalId.hashCode()					// use the id hash
								   : Objects.hashCode(this.getClauses());	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static BooleanQueryClauseStep0Builder create() {
		return BooleanQueryClause.create(null);		// no id
	}
	public static BooleanQueryClauseStep0Builder create(final String id) {
		return new BooleanQueryClauseStep0Builder(id,
												  new LinkedHashSet<QualifiedQueryClause<? extends QueryClause>>());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static class BooleanQueryClauseStep0Builder {
		private final String _id;
		private final Set<QualifiedQueryClause<? extends QueryClause>> _clauses;
		
		public BooleanQueryClauseStep1Builder field(final FieldID fieldId) {
			return new BooleanQueryClauseStep1Builder(_id,_clauses,fieldId);
		}
		public BooleanQueryClause build() {
			return new BooleanQueryClause(_id,
										  _clauses);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class BooleanQueryClauseStep1Builder {
		private final String _id;
		private final Set<QualifiedQueryClause<? extends QueryClause>> _clauses;
		private final FieldID _fieldId;
		public BooleanQueryClauseStep2Builder must() {
			return new BooleanQueryClauseStep2Builder(_id,_clauses,
													  _fieldId,QueryClauseOccur.MUST);
		}
		public BooleanQueryClauseStep2Builder should() {
			return new BooleanQueryClauseStep2Builder(_id,_clauses,
													  _fieldId,QueryClauseOccur.SHOULD);
		}
		public BooleanQueryClauseStep2Builder mustNOT() {
			return new BooleanQueryClauseStep2Builder(_id,_clauses,
													  _fieldId,QueryClauseOccur.MUST_NOT);
		}
		public BooleanQueryClauseStep2Builder occur(final QueryClauseOccur occur) {
			return new BooleanQueryClauseStep2Builder(_id,_clauses,
													  _fieldId,occur);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class BooleanQueryClauseStep2Builder {
		private final String _id;
		private final Set<QualifiedQueryClause<? extends QueryClause>> _clauses;
		
		private final FieldID _fieldId;
		private final QueryClauseOccur _occur;
		
		// ----- Sub
		public BooleanQueryClauseStep0Builder applyTo(final BooleanQueryClause other) {
			if (other != null) _clauses.add(new QualifiedQueryClause<BooleanQueryClause>(other,
																			  		     _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		
		// ----- Contained in
		public BooleanQueryClauseStep0Builder beWithin(final OID... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<OID>>(ContainedInQueryClause.<OID>forField(_fieldId)
																									 .within(spectrum),
																			   _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public <E extends Enum<E>> BooleanQueryClauseStep0Builder beWithin(final E... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<E>>(ContainedInQueryClause.<E>forField(_fieldId)
																								   .within(spectrum),
																			 _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beWithin(final Integer... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<Integer>>(ContainedInQueryClause.<Integer>forField(_fieldId)
																										 .within(spectrum),
																				   _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beWithin(final Long... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<Long>>(ContainedInQueryClause.<Long>forField(_fieldId)
																									  .within(spectrum),
																				 _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beWithin(final Double... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<Double>>(ContainedInQueryClause.<Double>forField(_fieldId)
																										.within(spectrum),
																				  _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beWithin(final Float... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<Float>>(ContainedInQueryClause.<Float>forField(_fieldId)
																									   .within(spectrum),
																				 _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beWithin(final String... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<String>>(ContainedInQueryClause.<String>forField(_fieldId)
																										.within(spectrum),
																				  _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beWithin(final Character... spectrum) {
			_clauses.add(new QualifiedQueryClause<ContainedInQueryClause<Character>>(ContainedInQueryClause.<Character>forField(_fieldId)
																										   .within(spectrum),
																				     _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		
		// ----- Equals
		public <E extends Enum<E>> BooleanQueryClauseStep0Builder beEqualTo(final E en) {
			if (en != null) _clauses.add(new QualifiedQueryClause<EqualsQueryClause<E>>(EqualsQueryClause.forField(_fieldId)
																								 .of(en),
																						_occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public <O extends OID> BooleanQueryClauseStep0Builder beEqualTo(final O oid) {
			if (oid != null) _clauses.add(new QualifiedQueryClause<EqualsQueryClause<O>>(EqualsQueryClause.forField(_fieldId)
																					     				  .of(oid),
																					     _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beEqualTo(final String str) {
			_clauses.add(new QualifiedQueryClause<EqualsQueryClause<String>>(EqualsQueryClause.forField(_fieldId)
																					          .of(str),
																			 _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beEqualTo(final Character character) {
			_clauses.add(new QualifiedQueryClause<EqualsQueryClause<Character>>(EqualsQueryClause.forField(_fieldId)
																					             .of(character),
																			    _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beEqualTo(final Date date) {
			_clauses.add(new QualifiedQueryClause<EqualsQueryClause<Date>>(EqualsQueryClause.forField(_fieldId)
																							.of(date),
																			  _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public <N extends Number> BooleanQueryClauseStep0Builder beEqualTo(final N num) {
			if (num != null) {
				if (num instanceof Integer) {
					return this.beEqualTo(num.intValue());
				} else if (num instanceof Long) {
					return this.beEqualTo(num.longValue());
				} else if (num instanceof Double) {
					return this.beEqualTo(num.doubleValue());
				} else if (num instanceof Float) {
					return this.beEqualTo(num.floatValue());
				}
				throw new IllegalArgumentException();
			}
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beEqualTo(final int number) {
			_clauses.add(new QualifiedQueryClause<EqualsQueryClause<Integer>>(EqualsQueryClause.forField(_fieldId)
																							   .of(number),
																			  _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beEqualTo(final long number) {
			_clauses.add(new QualifiedQueryClause<EqualsQueryClause<Long>>(EqualsQueryClause.forField(_fieldId)
																							.of(number),
																		   _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beEqualTo(final double number) {
			_clauses.add(new QualifiedQueryClause<EqualsQueryClause<Double>>(EqualsQueryClause.forField(_fieldId)
																							  .of(number),
																			 _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beEqualTo(final float number) {
			_clauses.add(new QualifiedQueryClause<EqualsQueryClause<Float>>(EqualsQueryClause.forField(_fieldId)
																							 .of(number),
																			_occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		
		// ------ Range
		public BooleanQueryClauseStep0Builder beInsideIntRange(final Range<Integer> range) {
			_clauses.add(new QualifiedQueryClause<RangeQueryClause<Integer>>(RangeQueryClause.forField(_fieldId)
																							 .of(range),
																			 _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beInsideLongRange(final Range<Long> range) {
			_clauses.add(new QualifiedQueryClause<RangeQueryClause<Long>>(RangeQueryClause.forField(_fieldId)
																						  .of(range),
																		  _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beInsideDateRange(final Range<Date> range) {
			_clauses.add(new QualifiedQueryClause<RangeQueryClause<Date>>(RangeQueryClause.forField(_fieldId)
																				    	  .of(range),
																		  _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder beInsideMilisecondsRange(final Range<Long> rangeMilis) {
			_clauses.add(new QualifiedQueryClause<RangeQueryClause<Date>>(RangeQueryClause.forField(_fieldId)
																						  .ofMilis(rangeMilis),
																				 _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public InsideLastXDateQueryClauseStepBuilder beInsideLast(final int ammount) {
			return new InsideLastXDateQueryClauseStepBuilder(_id,_clauses,
															 _fieldId,_occur,ammount);
		}
		// ------ Contains Text
		public ContainsTextQueryClauseStep1Builder beginWithText(final String str) {
			return new ContainsTextQueryClauseStep1Builder(_id,_clauses,
														   _fieldId,_occur,str,ContainedTextAt.BEGINING);
		}
		public ContainsTextQueryClauseStep1Builder endWithText(final String str) {
			return new ContainsTextQueryClauseStep1Builder(_id,_clauses,
														   _fieldId,_occur,str,ContainedTextAt.ENDING);
		}
		public ContainsTextQueryClauseStep1Builder containText(final String str) {
			return new ContainsTextQueryClauseStep1Builder(_id,_clauses,
														   _fieldId,_occur,str,ContainedTextAt.CONTENT);
		}
		public ContainsTextQueryClauseStep1Builder containFullText(final String str) {
			return new ContainsTextQueryClauseStep1Builder(_id,_clauses,
														   _fieldId,_occur,str,ContainedTextAt.FULL);
		}
		public ContainsFullTextQueryClauseStep1Builder containsAnyTextOf(final String... terms) {
			return new ContainsFullTextQueryClauseStep1Builder(_id,_clauses,
															   _fieldId,_occur,CollectionUtils.of(terms));
		}
		// ------ Has Data
		public BooleanQueryClauseStep0Builder haveData() {
			_clauses.add(new QualifiedQueryClause<HasDataQueryClause>(HasDataQueryClause.forField(_fieldId),
																	  _occur));
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STEP2 FOR CONTAINSTEXT
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainsTextQueryClauseStep1Builder {
		private final String _id;
		private final Set<QualifiedQueryClause<? extends QueryClause>> _clauses;
		
		private final FieldID _fieldId;
		private final QueryClauseOccur _occur;
		private final String _text;
		private final ContainedTextAt _position;
		
		public BooleanQueryClauseStep0Builder in(final Language lang) {
			if (Strings.isNOTNullOrEmpty(_text)) {
				_clauses.add(new QualifiedQueryClause<ContainsTextQueryClause>(ContainsTextQueryClause.forField(_fieldId)
																									  .at(_position)
																  					  	  			  .text(_text)
																  					  	  			  .in(lang),
																				_occur));
			}
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder languageIndependent() {
			return this.in(null);	// no language 
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainsFullTextQueryClauseStep1Builder {
		private final String _id;
		private final Set<QualifiedQueryClause<? extends QueryClause>> _clauses;
		
		private final FieldID _fieldId;
		private final QueryClauseOccur _occur;
		private final Collection<String> _texts;
		
		public BooleanQueryClauseStep0Builder in(final Language lang) {
			if (CollectionUtils.isNullOrEmpty(_texts)) {
				// [1] - Create a "child" BooleanQueryClause that "joins" individual NumberEqualsQueryClauses as SHOULD
				BooleanQueryClauseStep0Builder childBoolQryClause = BooleanQueryClause.create();			// all the individual items -globally-
				for (String term : _texts) {
					childBoolQryClause.field(_fieldId)
									  .occur(QueryClauseOccur.SHOULD)	// OR = any --> individual item	
									  .containText(term)
									  .in(lang);
				}
				// [2] - Add the "child" BooleanQueryClause to the "parent" BooleanQueryClause
				_clauses.add(new QualifiedQueryClause<BooleanQueryClause>(childBoolQryClause.build(),
						 											  	  _occur));
			}
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder languageIndependent() {
			return this.in(null);	// no language
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STEP2 FOR INSIDE LAST X DATES
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class InsideLastXDateQueryClauseStepBuilder {
		private final String _id;
		private final Set<QualifiedQueryClause<? extends QueryClause>> _clauses;
		
		private final FieldID _fieldId;
		private final QueryClauseOccur _occur;
		private final int _ammount;
		
		public BooleanQueryClauseStep0Builder minutes() {
			if (_ammount > 0) {
				DateTime now = new DateTime();
				DateTime nowMinusX = now.minusMinutes(_ammount);
				Range<Date> range = Range.closed(nowMinusX.toDate(),now.toDate());
				return new BooleanQueryClauseStep2Builder(_id,_clauses,
														  _fieldId,_occur)
								.beInsideDateRange(range);
			}
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder days() {
			if (_ammount > 0) {
				DateTime now = new DateTime();
				DateTime nowMinusX = now.minusDays(_ammount);
				Range<Date> range = Range.closed(nowMinusX.toDate(),now.toDate());
				return new BooleanQueryClauseStep2Builder(_id,_clauses,
														  _fieldId,_occur)
								.beInsideDateRange(range);
			}
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder months() {
			if (_ammount > 0) {
				DateTime now = new DateTime();
				DateTime nowMinusX = now.minusMonths(_ammount);
				Range<Date> range = Range.closed(nowMinusX.toDate(),now.toDate());
				return new BooleanQueryClauseStep2Builder(_id,_clauses,
														  _fieldId,_occur)
								.beInsideDateRange(range);
			}
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
		public BooleanQueryClauseStep0Builder years() {
			if (_ammount > 0) {
				DateTime now = new DateTime();
				DateTime nowMinusX = now.minusYears(_ammount);
				Range<Date> range = Range.closed(nowMinusX.toDate(),now.toDate());
				return new BooleanQueryClauseStep2Builder(_id,_clauses,
														  _fieldId,_occur)
								.beInsideDateRange(range);
			}
			return new BooleanQueryClauseStep0Builder(_id,_clauses);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public enum QueryClauseOccur 
	 implements EnumExtended<QueryClauseOccur> { 
		MUST,		// = and
		MUST_NOT,	// = NOT
		SHOULD; 	// = or
		
		private static EnumExtendedWrapper<QueryClauseOccur> _enums = EnumExtendedWrapper.wrapEnumExtended(QueryClauseOccur.class);

		public static QueryClauseOccur fromName(final String name) {
			return _enums.fromName(name);
		}
		@Override
		public boolean isIn(final QueryClauseOccur... els) {
			return _enums.isIn(this,els);
		}
		@Override
		public boolean is(QueryClauseOccur el) {
			return _enums.is(this,el);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="qualifiedClause")
	@Accessors(prefix="_")
	public static class QualifiedQueryClause<Q extends QueryClause> 
			 implements Serializable {
		private static final long serialVersionUID = -465425719935758069L;
		
		@MarshallField(as="clause")
		@Getter private final Q _clause;
		
		@MarshallField(as="occur",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final QueryClauseOccur _occur;
		
		public QualifiedQueryClause(@MarshallFrom("clause") final Q clause,
									@MarshallFrom("occur") 	final QueryClauseOccur occur) {
			_clause = clause;
			_occur = occur;
		}
		public static <C extends QueryClause> QualifiedQueryClause<C> create(final C clause,final QueryClauseOccur occur) {
			return new QualifiedQueryClause<C>(clause,occur);
		}
		@Override
		public boolean equals(final Object obj) {
			if (obj == null) return false;
			if (obj == this) return true;
			if (!(obj instanceof QualifiedQueryClause)) return false;
			
			@SuppressWarnings("unchecked")
			QualifiedQueryClause<? extends QueryClause> otherQClause = (QualifiedQueryClause<? extends QueryClause>)obj;
			boolean occursEq = _occur != null ? otherQClause.getOccur() != null ? _occur.equals(otherQClause.getOccur())
																				: false
											  : true;	// both null
			boolean clauseEq = _clause != null ? otherQClause.getClause() != null ? _clause.equals(otherQClause.getClause())
																				  : false
											   : true;
			return occursEq && clauseEq;
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(_clause,
									_occur);
		}
	}
}
