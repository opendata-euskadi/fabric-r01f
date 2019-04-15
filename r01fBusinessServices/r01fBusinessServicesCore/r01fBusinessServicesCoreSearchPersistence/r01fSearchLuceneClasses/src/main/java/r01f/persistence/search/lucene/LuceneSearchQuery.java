package r01f.persistence.search.lucene;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import lombok.extern.slf4j.Slf4j;
import r01f.enums.EnumWithCode;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.search.SearchFilter;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.BooleanQueryClause.QualifiedQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainedInQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.QueryClause;
import r01f.model.search.query.RangeQueryClause;
import r01f.persistence.index.document.IndexDocumentFieldConfig;
import r01f.persistence.index.document.IndexDocumentFieldConfigSet;
import r01f.persistence.index.document.IndexDocumentFieldID;
import r01f.persistence.lucene.LuceneConstants;
import r01f.persistence.search.QueryBase;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Creates a Lucene {@link Query} 
 * <pre class='brush:java'>
 *		LuceneQuery qry = LuceneQuery.of(fieldsConfigSet);
 *		qry.predicates()
 *				.field("myIntField").must().beEqualTo(23)
 *				.field("myDateField").should().beInsideDateRange(Range.closed(new Date(),new Date()))
 *				.field("myStrField").must().beEqualTo("555")
 *				.field("myEnumField").mustNOT().beWithin(new Character[] {'a','b','c'})
 *				.field("mySubQueryField").should().applyTo(BooleanQueryClause.create()
 *																.field("mySubInField").should().beEqualTo('a')
 *				 												.field("myOtherSubField1").must().beEqualTo(new Date())
 *				 											  .build())
 *				  .build();
 *		Query qry = qryFactory.getQuery();
 * </pre> 
 */
@Slf4j
public class LuceneSearchQuery<F extends SearchFilter>
	 extends QueryBase<LuceneSearchQuery<F>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Lucene fields config
	 */
	private final IndexDocumentFieldConfigSet<?> _fieldsConfigSet;
	/**
	 * A language-dependent analyzer
	 */
	private final Analyzer _luceneAnalyzer;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public LuceneSearchQuery(final IndexDocumentFieldConfigSet<?> fieldsConfigSet,
							 final Analyzer luceneAnalyzer) {
		this(fieldsConfigSet,
			 luceneAnalyzer,
			 null);		// no user interface language specified
	}
	public LuceneSearchQuery(final IndexDocumentFieldConfigSet<?> fieldsConfigSet,
							 final Analyzer luceneAnalyzer,
							 final Language uiLanguage) {
		super(uiLanguage);
		_fieldsConfigSet = fieldsConfigSet;
		_luceneAnalyzer = luceneAnalyzer;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds and returns the lucene query
	 * @return
	 */
	public Query getQuery(final F filter) {
		BooleanQueryClause containerBoolQry = filter != null ? filter.getBooleanQuery()
															 : null;
		Query outQuery = _queryClauseFrom(containerBoolQry);
		return outQuery;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a Lucene {@link BooleanQuery} from the model object query
	 * @param qryClause
	 * @return
	 */
	private Query _queryClauseFrom(final BooleanQueryClause qryClause) {
		if (qryClause == null || CollectionUtils.isNullOrEmpty(qryClause.getClauses())) return null;
		
		BooleanQuery outQry = new BooleanQuery();
		
		Set<QualifiedQueryClause<? extends QueryClause>> clauses = qryClause.getClauses();
		
		for (Iterator<QualifiedQueryClause<? extends QueryClause>> clauseIt = clauses.iterator(); clauseIt.hasNext(); ) {
			QualifiedQueryClause<? extends QueryClause> clause = clauseIt.next();
			
			Query luceneQry = _queryClauseFrom(clause.getClause());
			Occur luceneOccur = _queryClauseJoinFor(clause.getOccur());
			if (luceneQry != null && luceneOccur != null) {
				outQry.add(luceneQry,		// The clause
						   luceneOccur);	// The occurrence
			} else {
				log.error("A null lucene query was returned for field {}",
						  clause.getClause().getFieldId());
			}
		}		
		return outQry;
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	private <Q extends QueryClause> Query _queryClauseFrom(final Q clause) {
		if (clause == null) return null;
		Query outQry = null;
		if (clause instanceof BooleanQueryClause) {
			outQry = _queryClauseFrom((BooleanQueryClause)clause);		// recursion
		} 
		else if (clause instanceof EqualsQueryClause) {
			outQry = _queryClauseFrom((EqualsQueryClause<?>)clause);
		} 
		else if (clause instanceof ContainsTextQueryClause) {
			outQry = _queryClauseFrom((ContainsTextQueryClause)clause);
		} 
		else if (clause instanceof RangeQueryClause) {
			outQry = _queryClauseFrom((RangeQueryClause<? extends Comparable>)clause);
		} 
		else if (clause instanceof ContainedInQueryClause) {
			outQry = _queryClauseFrom((ContainedInQueryClause<?>)clause);
		}
		else {
			throw new IllegalArgumentException("Unsupported clause type: " + clause.getClass());
		}
		return outQry;
	}
	private static Query _queryClauseFrom(final EqualsQueryClause<?> eqQry) {
		if (eqQry == null || eqQry.getValue() == null) return null;
		
		IndexDocumentFieldID luceneFieldId = IndexDocumentFieldID.forId(eqQry.getFieldId().asString());
		
		Query outQry = null;
		if (eqQry.isTextEquals()) {
			outQry = _matchTermQuery(luceneFieldId,
									 eqQry.getValue().toString());
			
		} else if (eqQry.isNumberEquals()) {
			outQry = _numberEqualsQuery(luceneFieldId,
										eqQry.getValueAsNumber(),
										eqQry.getNumberType());
		} else if (eqQry.isDateEquals()) {
			outQry = _numberInRangeQuery(luceneFieldId,
										 _createRange(eqQry.getDateMilis(),BoundType.CLOSED,eqQry.getDateMilis(),BoundType.CLOSED),
										 Long.class);
		} else if (eqQry.isEnumWithCodeEquals()) {
			EnumWithCode<?,?> enumWithCode = (EnumWithCode<?,?>)eqQry.getValue();
			String codeAsString = enumWithCode.getCode().toString();
			outQry = _matchKeywordQuery(luceneFieldId,
							   			codeAsString);
		} else if (eqQry.isEnumEquals()) {
			Enum<?> enumSimple = (Enum<?>)eqQry.getValue();
			String enumAsString = enumSimple.name();
			outQry = _matchKeywordQuery(luceneFieldId,
							   			enumAsString);
		} else if (eqQry.isOIDEquals()) {
			OID oid = (OID)eqQry.getValue();
			String oidAsString = oid.asString();
			outQry = _matchKeywordQuery(luceneFieldId,
										oidAsString);
		} else {
			throw new IllegalArgumentException();
		}
		return outQry;
	}
	private Query _queryClauseFrom(final ContainsTextQueryClause containsTextQry) {
		if (containsTextQry == null || containsTextQry.getText() == null) return null;
		
		Query outQry = null;		
		IndexDocumentFieldID luceneFieldId = containsTextQry.isLanguageDependent() ? IndexDocumentFieldID.fieldIdOf(containsTextQry.getFieldId(),
														  											  containsTextQry.getLang())
														  			        : IndexDocumentFieldID.fieldIdOf(containsTextQry.getFieldId());
		if (containsTextQry.isFullText()) {
			// Full text search query
			outQry = _fullTextQueryClauseFor(luceneFieldId,
											 containsTextQry.getText());
		} else {
			// Normal Term query
			outQry = _matchTermQuery(luceneFieldId,
									 containsTextQry.getText());
		}
		return outQry;
	}
	@SuppressWarnings("resource")
	private Query _fullTextQueryClauseFor(final IndexDocumentFieldID fieldId,
										  final String text) {
		IndexDocumentFieldConfig<?> fieldCfg = _fieldsConfigSet.getConfigOrThrowFor(fieldId);
		Query outQry = null;
		QueryParser qp = new QueryParser(LuceneConstants.VERSION,
										 fieldId.asString(),
										 _luceneAnalyzer);	
		try {
			String luceneTextQry = Strings.customized("{}:{}",
													  fieldId.asString(),text);
			outQry = qp.parse(luceneTextQry);
		} catch(ParseException parseEx) {
			log.error("Error parsing the term text search filter: {}",parseEx.getMessage(),
																	  parseEx);
		}
		return outQry;
	}
	@SuppressWarnings({ "unchecked","rawtypes","static-method" })
	private Query _queryClauseFrom(final RangeQueryClause<? extends Comparable> rangeQry) {
		if (rangeQry == null || rangeQry.getRange() == null) return null;
		
		Class<?> rangeType = rangeQry.getRangeType();
		
		IndexDocumentFieldID luceneFieldId = IndexDocumentFieldID.forId(rangeQry.getFieldId().asString());
		
		Query outQry = null;
		if (ReflectionUtils.isImplementing(rangeType,Integer.class)) {
			outQry = _numberInRangeQuery(luceneFieldId,
										 (Range<Integer>)rangeQry.getGuavaRange(),
										 Integer.class);
		} else if (ReflectionUtils.isImplementing(rangeType,Long.class)) {
			outQry = _numberInRangeQuery(luceneFieldId,
										 (Range<Long>)rangeQry.getGuavaRange(),
										 Long.class);	
		} else if (ReflectionUtils.isImplementing(rangeType,Double.class)) {
			outQry = _numberInRangeQuery(luceneFieldId,
										 (Range<Double>)rangeQry.getGuavaRange(),
										 Double.class);
		} else if (ReflectionUtils.isImplementing(rangeType,Float.class)) {
			outQry = _numberInRangeQuery(luceneFieldId,
										 (Range<Float>)rangeQry.getGuavaRange(),
										 Float.class);
		} else if (ReflectionUtils.isImplementing(rangeType,Date.class)) {
			Range<Long> milisRange = _timeStampRangeFromDateRange((Range<Date>)rangeQry.getGuavaRange());
			outQry = _numberInRangeQuery(luceneFieldId,
										 milisRange,
										 Long.class);
		}
		return outQry;
	}
	@SuppressWarnings("static-method")
	private Query _queryClauseFrom(final ContainedInQueryClause<?> containedInQry) {		
		if (containedInQry == null || CollectionUtils.isNullOrEmpty(containedInQry.getSpectrum())) return null;
		
		BooleanQuery withinQry = new BooleanQuery();
		Class<?> type = containedInQry.getValueType();
		
		IndexDocumentFieldID luceneFieldId = IndexDocumentFieldID.forId(containedInQry.getFieldId().asString());
		
		for (Object spectrumValue : containedInQry.getSpectrum()) {
			Query shouldQry = null;
			if (type.equals(Integer.class)) {
				shouldQry = _numberEqualsQuery(luceneFieldId,
											   (Integer)spectrumValue,
											   Integer.class);
			} else if (type.equals(Long.class)) {
				shouldQry = _numberEqualsQuery(luceneFieldId,
											   (Long)spectrumValue,
											   Long.class);
			} else if (type.equals(Double.class)) {
				shouldQry = _numberEqualsQuery(luceneFieldId,
											   (Double)spectrumValue,
											   Double.class);
			} else if (type.equals(Float.class)) {
				shouldQry = _numberEqualsQuery(luceneFieldId,
											   (Float)spectrumValue,
											   Float.class);
			} else if (type.equals(String.class)) {
				shouldQry = _matchKeywordQuery(luceneFieldId,
											   (String)spectrumValue);
			} else if (type.equals(Character.class)) {
				shouldQry = _matchKeywordQuery(luceneFieldId,
											   ((Character)spectrumValue).toString());
			} else if (type.equals(Date.class)) {
				Date spectrumDateValue = (Date)spectrumValue;
				Long spectrumDateMilis = Dates.asMillis(spectrumDateValue);
				shouldQry = _numberEqualsQuery(luceneFieldId,
											   spectrumDateMilis,
											   Long.class);
			} else if (ReflectionUtils.isImplementing(type,OID.class)) {
				OID spectrumOID = (OID)spectrumValue;
				shouldQry = _matchKeywordQuery(luceneFieldId,
											   spectrumOID.asString());
			} else if (ReflectionUtils.isImplementing(type,EnumWithCode.class)) {
				EnumWithCode<?,?> spectrumEnumWithCode = (EnumWithCode<?,?>)spectrumValue;
				String spectrumEnumCode = spectrumEnumWithCode.getCode().toString();
				shouldQry = _matchKeywordQuery(luceneFieldId,
											   spectrumEnumCode);
			} else if (ReflectionUtils.isImplementing(type,Enum.class)) {
				Enum<?> spectrumEnum = (Enum<?>)spectrumValue;
				String spectrumEnumCode = spectrumEnum.name();
				shouldQry = _matchKeywordQuery(luceneFieldId,
											   spectrumEnumCode);
			}
			withinQry.add(shouldQry,
						  Occur.SHOULD);
		}
		return withinQry;
	}
	private static Occur _queryClauseJoinFor(final QueryClauseOccur occur) {
		Occur outOccur = null;
		switch(occur) {
		case MUST:
			outOccur = Occur.MUST;
			break;
		case MUST_NOT:
			outOccur = Occur.MUST_NOT;
			break;
		case SHOULD:
			outOccur = Occur.SHOULD;
			break;
		default:
			throw new IllegalArgumentException();
		}
		return outOccur;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Query _matchTermQuery(final IndexDocumentFieldID fieldId,
										 final String term) {
		if (Strings.isNullOrEmpty(term)) return null;
		Query termQry = new TermQuery(new Term(fieldId.asString(),
											   term));
		return termQry;
	}

	private static Query _matchKeywordQuery(final IndexDocumentFieldID fieldId,
											final String keyword) {
		if (Strings.isNullOrEmpty(keyword)) return null;
		QueryParser qp = new QueryParser(LuceneConstants.VERSION,
										 fieldId.asString(),
										 new KeywordAnalyzer());
		Query outQry = null;
		try {
			outQry = qp.parse(keyword);
		} catch(ParseException parseEx) {
			log.error("Error parsing the keyword search filter: {}",parseEx.getMessage(),
																    parseEx);
		} 
		return outQry;
	}
	private static <N extends Number> Query _numberEqualsQuery(final IndexDocumentFieldID fieldId,
															   final N number,
															   final Class<N> numberType) {
		Query outQry = null;
		if (numberType.equals(Integer.class)) {
			outQry = _numberInRangeQuery(fieldId,
										 _createRange((Integer)number,BoundType.CLOSED,(Integer)number,BoundType.CLOSED),
										 Integer.class);
		} else if (numberType.equals(Long.class)) {
			outQry = _numberInRangeQuery(fieldId,
										 _createRange((Long)number,BoundType.CLOSED,(Long)number,BoundType.CLOSED),
										 Long.class);
		} else if (numberType.equals(Double.class)) {
			outQry = _numberInRangeQuery(fieldId,
										 _createRange((Double)number,BoundType.CLOSED,(Double)number,BoundType.CLOSED),
										 Double.class);
		} else if (numberType.equals(Long.class)) {
			outQry = _numberInRangeQuery(fieldId,
										 _createRange((Float)number,BoundType.CLOSED,(Float)number,BoundType.CLOSED),
										 Float.class);
		} else {
			throw new IllegalArgumentException();
		}
		return outQry;
	}
	@SuppressWarnings("rawtypes")
	private static <N extends Number & Comparable> Query _numberInRangeQuery(final IndexDocumentFieldID fieldId,
																			 final Range<N> range,
																			 final Class<N> numberType) {
		boolean lowerIncluded = range.hasLowerBound() && range.lowerBoundType() == BoundType.CLOSED;
		boolean upperIncluded = range.hasUpperBound() && range.upperBoundType() == BoundType.CLOSED;
		
		N lowerBound = range.hasLowerBound() ? range.lowerEndpoint() : null;
		N upperBound = range.hasUpperBound() ? range.upperEndpoint() : null;
		
		Query outNumberEqQry = null;
		 if (numberType.equals(Integer.class)) {
			outNumberEqQry = NumericRangeQuery.newIntRange(fieldId.asString(),
														   (Integer)lowerBound,(Integer)upperBound, 
														   lowerIncluded,upperIncluded);
		 } else if (numberType.equals(Long.class)) {
			outNumberEqQry = NumericRangeQuery.newLongRange(fieldId.asString(),
															(Long)lowerBound,(Long)upperBound, 
															lowerIncluded,upperIncluded);
		} else if (numberType.equals(Double.class)) {
			outNumberEqQry = NumericRangeQuery.newDoubleRange(fieldId.asString(),
														      (Double)lowerBound,(Double)upperBound, 
														      lowerIncluded,upperIncluded);			
		} else if (numberType.equals(Float.class)) {
			outNumberEqQry = NumericRangeQuery.newFloatRange(fieldId.asString(),
														      (Float)lowerBound,(Float)upperBound, 
														      lowerIncluded,upperIncluded);
		}
		return outNumberEqQry;
	}
	private static Range<Long> _timeStampRangeFromDateRange(final Range<Date> dateRange) {
		if (dateRange == null
				|| dateRange.lowerEndpoint() == null
				|| dateRange.upperEndpoint() ==  null) return null;
		
		Long lowerTS = dateRange.lowerEndpoint().getTime();
		Long upperTS = dateRange.upperEndpoint().getTime();
		
		Range<Long> outTSRange = Range.range(lowerTS,dateRange.lowerBoundType(),
											 upperTS,dateRange.upperBoundType());
		return outTSRange;
	}
}
