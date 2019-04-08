package r01f.marshalling.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.guids.OID;
import r01f.types.Range;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class JacksonSerializers {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonSerialize(using = OIDJacksonSerializer.class)
	public static abstract class OIDJacksonAnnotationsMixin {
		@JsonUnwrapped abstract Object getId();
		@JsonIgnore abstract Object getRaw(); // we don't need it!
	}
	// Serialize oids as id:value
	// Jackson by default serializes OIDs like:
	// {
	// 		oid {
	// 			id : "xxxx"
	//      }
	// }
	// ... but it's more elegant to serialize as:
	// {
	// 		oid : "xxx"
	// }
	@NoArgsConstructor
	public static class OIDJacksonSerializer 
		        extends JsonSerializer<OID> {
	    @Override
	    public void serialize(final OID value,
	    					  final JsonGenerator jgen,final SerializerProvider provider) throws IOException,
	    																						 JsonProcessingException {
	        jgen.writeString(value.asString());
	    }
	}
///////////////////////////////////////////////////////////////////////////////
// 	RANGE
///////////////////////////////////////////////////////////////////////////////	
	@JsonSerialize(using = RangeJacksonSerializer.class)
	public static abstract class RangeJacksonAnnotationsMixin {
		// nothing
	}
	@NoArgsConstructor
	public static class RangeJacksonSerializer 
		        extends JsonSerializer<Range<?>> {
	    @Override
	    public void serialize(final Range<?> value,
	    					  final JsonGenerator jgen,final SerializerProvider provider) throws IOException,
	    																						 JsonProcessingException {
	    	jgen.writeStartObject();
	    	if (value.hasLowerBound()) jgen.writeStringField("start",value.getLowerBound().toString());
	    	if (value.hasUpperBound()) jgen.writeStringField("end",value.getUpperBound().toString());
	    	jgen.writeEndObject();
	    }
	}
}
