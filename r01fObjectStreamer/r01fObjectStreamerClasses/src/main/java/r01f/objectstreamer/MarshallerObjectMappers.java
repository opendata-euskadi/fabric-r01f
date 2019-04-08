package r01f.objectstreamer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

abstract class MarshallerObjectMappers {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static void setDefaultConfig(final ObjectMapper mapper) {
		// use only field annotations
		mapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
//		mapper.setVisibility(mapper.getSerializationConfig()
//										// visibility
//										.getDefaultVisibilityChecker()
//		                					.withFieldVisibility(JsonAutoDetect.Visibility.ANY)			// field 
//		                					.withGetterVisibility(JsonAutoDetect.Visibility.NONE)		// getter
//		                					.withSetterVisibility(JsonAutoDetect.Visibility.NONE)		// setter
//		                					.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));	// creator
		
		// Pretty print
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		// ------ Features: https://github.com/FasterXML/jackson-databind/wiki/Mapper-Features
		
		// include only not-null
		mapper.setSerializationInclusion(Include.NON_EMPTY);	// NON_NULL + other
		
		// problems with lombok's noargs constructors: https://github.com/FasterXML/jackson-databind/issues/1197
		mapper.configure(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES,false);

		// do NOT ignore final on field declarations
		mapper.configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS,false);
		
		// polymorphic
		mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS,false);
		
		// do NOT fail on unknown properties
		// case: 
		//		a) An object with a polymorphic field:
		//					@MarshallType(as="qualifiedClause")
		//					@Accessors(prefix="_")
		//					public static class QualifiedQueryClause<Q extends QueryClause> {
		//						@MarshallField(as="clause")
		//						@Getter private final Q _clause;
		//						
		//						@MarshallField(as="occur",
		//									   whenXml=@MarshallXmlField(attr=true))
		//						@Getter private final QueryClauseOccur _occur;
		//					}
		//		a) The serialized representation includes a typeId:
		//				{
		//				  "clause" : {
		//				    "typeId" : "hasDataClause",
		//				    "forMetaData" : "theValue"
		//				  },
		//				  "occur" : "MUST"
		//				}
		//		b) The serialization works if the json stream is readed as:
		//				QualifiedQueryClause<?> clause = mapper.readValue(json,
		//						  			   							  QualifiedQueryClause.class); <-- NO "hint" about the real type of _clause field
		//		   ... since there's NO hint about the QualifiedQueryClause's _clause field, the "typeId" 
		//			   is NEEDED to know the real type of the _clause field
		//		c) BUT if the json stream is readed as:
		//				QualifiedQueryClause<HasDataQueryClause> clause = mapper.readValue(json,
		//						  			   							  				   new TypeReference<QualifiedQueryClause<HasDataQueryClause>>() { /* empty */ });
		//		   ... now the deserializer "knows" that the QualifiedQueryClause's _clause field has HasDataQueryClause type
		//		   ... so when the deserializer finds the "typeId" property, FAILS:
		//					com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "typeId" 
		//			   meaning that HasDataQueryClause does NOT have a "typeId" field
		//			   (the deserializer does NOT need the "typeId" field to guess the _clause field's type since it's known beforehand)
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}
}
