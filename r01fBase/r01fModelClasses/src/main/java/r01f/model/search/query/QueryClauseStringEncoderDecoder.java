package r01f.model.search.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.FieldMetaDataForCollection;
import r01f.model.metadata.FieldMetaDataForPolymorphicType;
import r01f.model.metadata.FieldMetaDataForRange;
import r01f.model.metadata.TypeFieldMetaData;
import r01f.model.metadata.TypeMetaData;
import r01f.model.search.query.BooleanQueryClause.QualifiedQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.QueryClauseSerializerUtils.ContainedTextSpec;
import r01f.model.search.query.QueryClauseSerializerUtils.STRING_ESCAPE;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Encodes a {@link BooleanQueryClause} in the url as:
 * <pre class='brush:java'>
 *		BooleanQueryClause boolQry = BooleanQueryClause.create()
 *												   			.field("myField").must().beEqualTo(myOid)
 *												   			.field("myField2").should().beInsideLast(5).minutes()
 *												   			.field("myField3").mustNOT().beWithin(10D,12D,11D)
 *															.field("myField4").must().haveData()
 *												   		.build();
 * </pre>
 * Is encoded in an URL as:
 * <pre>
 * 		myField.MUST.beEqualTo(r01132qfa12341) {r01.model.oid.MyOID};
 * 		myField2.SHOULD.beInsideRange([124123..123413]) {java.util.Date};
 * 		myField3.MUST_NOT.beWithin(10,12,11) {java.lang.Integer}
 * 		myField4.MUST.haveData() {}
 * </pre>
 * Each of the predicates has the following structure:
 * <pre>
 * 		{field}.{CONDITION}.{predicate}({value}) {DataType}
 */
@GwtIncompatible
class QueryClauseStringEncoderDecoder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static <Q extends QueryClause> String encode(final QualifiedQueryClause<Q> qry) {
//		MetaDataConfig mdConfig = metaData.getMetaDataConfigFor(qry.getClause().getFieldId()
//																			   .asMetaDataId());
//		mdConfig.getDataType().getName();
		
		String outEncodedQry = null;
		if (qry.getClause() instanceof BooleanQueryClause) {
			BooleanQueryClause boolQry = (BooleanQueryClause)qry.getClause();
			String boolQryStr = boolQry.encodeAsString();
			outEncodedQry = boolQryStr;
			//throw new UnsupportedOperationException("Nested BooleanQueries are NOT supported... for the moment");
			
		} else if (qry.getClause() instanceof HasDataQueryClause) {
			// fieldId.MUST.hasData{} --> NOT a boolean query clause
			outEncodedQry = Strings.customized("{}.{}.{}({})",
								   			   qry.getClause().getFieldId(),			// [1] field id
										  	   qry.getOccur().name(),					// [2] must / must_not / should
										  	   QueryCondition.fromQuery(qry.getClause()),	// [3] hasData
										  	   "");										// [4] NO value (hasData)
										  	   //"{}")									// [5] NO datatype (hasData)
		} else {
			// fieldId.MUST.beEqualTo(5) --> NOT a boolean query clause
			outEncodedQry = Strings.customized("{}.{}.{}({})",
								    		   qry.getClause().getFieldId(),													// [1] field id
										  	   qry.getOccur().name(),															// [2] must / must_not / should
										  	   QueryCondition.fromQuery(qry.getClause()),											// [3] beEqualTo / beInsideRange / beInsideRange / beginWith / endWith / contain / fullText
										  	   QueryClauseSerializerUtils.serializeValue(qry.getClause(),STRING_ESCAPE.NONE));	// [4] value
										  	   //"{" + qry.getClause().getValueType().getName() + "}")							// [5] dataType
		}
		return outEncodedQry;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	 
/////////////////////////////////////////////////////////////////////////////////////////
	// ie: (.+)\.(MUST|MUST_NOT|SHOULD)\.beEqualTo\((.+)\)
	private static final Pattern CLAUSE_URL_PATTERN = Pattern.compile("(.+)" + 								// [1] fieldName
																	  "\\.(MUST|MUST_NOT|SHOULD)\\." +		// [2] must / must_not / should
																	  "(" + QueryCondition.pattern() + ")" +		// [3] beEqualTo / beInsideRange / beInsideRange / contain 
																	  "\\((.*)\\)");						// [4] value
																	  //"\\{([^}]*)\\}");					// [5] dataType
	@SuppressWarnings({ "unchecked","rawtypes" })
	public static <Q extends QueryClause> QualifiedQueryClause<Q> decode(final String encodedQry,
																		 final TypeMetaData metaData) {
		QualifiedQueryClause<Q> outQry = null;
		Matcher m = CLAUSE_URL_PATTERN.matcher(encodedQry);
		if (m.find()) {
			FieldID fieldId = new FieldID(m.group(1));
			QueryClauseOccur occur = QueryClauseOccur.fromName(m.group(2));
			QueryCondition condition = QueryCondition.fromName(m.group(3));
			String value = m.group(4);
			
			if (Strings.isNullOrEmpty(value) && condition != QueryCondition.haveData) throw new IllegalArgumentException(encodedQry + " is NOT a valid encoded query clause");
			// Class<?> dataType = Strings.isNOTNullOrEmpty(m.group(5)) ? ReflectionUtils.typeFromClassName(m.group(5))
			//														 : null;
			TypeFieldMetaData typeField = metaData.findFieldByIdOrThrow(fieldId);
			if (typeField == null) throw new IllegalStateException(Throwables.message("There's NO field with name {} at {} model object metadata; available fields are {}",
																					  fieldId,metaData.getRawType(),CollectionUtils.of(metaData.getFieldsMetaDataIds()).toStringCommaSeparated()));
			FieldMetaData fieldMetaData = typeField.asFieldMetaData();
			Class<?> dataType = fieldMetaData.getDataType();
			
			Q clause = null;
			switch(condition) {
			case beEqualTo:
				if (fieldMetaData.isCollectionField()) {
					// A multi-valued field
					FieldMetaDataForCollection colFieldMetaData = (FieldMetaDataForCollection)fieldMetaData;
					clause = (Q)EqualsQueryClause.forField(fieldId)
												 .of(QueryClauseSerializerUtils.instanceFromString(value,colFieldMetaData.getComponentsType()));
				} if (fieldMetaData.isPolymorphicField()) {
					// A polymorphic field: the concrete datatype depends upon the filtered model object type
					FieldMetaDataForPolymorphicType polyFieldMetaData = (FieldMetaDataForPolymorphicType)fieldMetaData;
					Class<?> polyDataType = polyFieldMetaData.getFieldDataTypeMap().get(metaData.getRawType());
					clause = (Q)EqualsQueryClause.forField(fieldId)
											  	 .of(QueryClauseSerializerUtils.instanceFromString(value,polyDataType));
				} else {
					// a single-valued field
					clause = (Q)EqualsQueryClause.forField(fieldId)
											  	 .of(QueryClauseSerializerUtils.instanceFromString(value,dataType));
				}
				break;				
			case beInsideRange:
				Class<? extends Comparable> comparableDataType = (Class<? extends Comparable>)dataType;
				Range<? extends Comparable> range = Range.parse(value,
										  						comparableDataType);
				clause = (Q)RangeQueryClause.forField(fieldId)
											.of((Range)range);
				break;
			case beWithin:
				Class<?> type = null;
				if (fieldMetaData instanceof FieldMetaDataForCollection) {
					FieldMetaDataForCollection fieldMetaDataForCollection = (FieldMetaDataForCollection)fieldMetaData;
					type = fieldMetaDataForCollection.getComponentsType();
				}
				else if (fieldMetaData instanceof FieldMetaDataForRange) {
					FieldMetaDataForRange fieldMetaDataForRange = (FieldMetaDataForRange)fieldMetaData;
					type = fieldMetaDataForRange.getRangeDataType();
				}
				else {
					type = fieldMetaData.getDataType();
				}
				clause = (Q)ContainedInQueryClause.forField(fieldId)
												  .within(QueryClauseSerializerUtils.spectrumArrayFromString(value,type));
				break;
			case haveData:
				clause = (Q)HasDataQueryClause.forField(fieldId);
				break;
			case contain:
				ContainedTextSpec containedTextSpec = ContainedTextSpec.fromSerializedFormat(value);
				clause = (Q)ContainsTextQueryClause.forField(fieldId)
												   .forSpec(containedTextSpec);
				break;
			default:
				break;
			}
			// Create the query
			outQry = new QualifiedQueryClause<Q>(clause,occur);
		} else { 
			throw new IllegalArgumentException(encodedQry + " is NOT a valid encoded query clause");
		}
		return outQry;
	}
}
