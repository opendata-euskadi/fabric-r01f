package r01f.marshalling.json;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.RequiredArgsConstructor;
import r01f.enums.EnumWithCode;
import r01f.guids.OID;
import r01f.reflection.ReflectionUtils;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.DayOfWeek;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

public class GsonTypeAdapterFactories {
/////////////////////////////////////////////////////////////////////////////////////////
//  Single field objects
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @see http://www.javacreed.com/gson-typeadapter-example/
	 * Gson TypeAdapterFactory used to avoid the OID's id field wrapping
	 * Gson by default serializes OIDs like:
	 * {
	 * 		oid {
	 * 			id : "xxxx"
	 *      }
	 * }
	 * ... but it's more elegant to serialize as:
	 * {
	 * 		oid : "xxx"
	 * }
	 */
	@RequiredArgsConstructor
	static class SingleFieldTypeAdapterFactoryBase
	  implements TypeAdapterFactory  {
		private final Class<?> _type;
		private final String _fieldName;
		
		@Override
		public <T> TypeAdapter<T> create(final Gson gson,
										 final TypeToken<T> type) {
			if (!ReflectionUtils.isImplementing(type.getRawType(),_type)) return null;
			
		    final TypeAdapter<T> delegate = gson.getDelegateAdapter(this,type);
		    final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
		    return new TypeAdapter<T>() {
					      @Override 
					      public void write(final JsonWriter out,final T value) throws IOException {
					    	  JsonElement tree = delegate.toJsonTree(value);
					    	  if (!(tree instanceof JsonObject)) {
					    		  elementAdapter.write(out,tree);
					    	  } else {
					    		  // instead of the object, serialize the id
					    		  elementAdapter.write(out,((JsonObject)tree).get(_fieldName));
					    	  }
					      }
					      @Override 
					      public T read(final JsonReader in) throws IOException {
					          JsonElement tree = elementAdapter.read(in);
					          return delegate.fromJsonTree(tree);
					      }
		    };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OIDs
/////////////////////////////////////////////////////////////////////////////////////////
	public static class OIDTypeAdapterFactory
		        extends SingleFieldTypeAdapterFactoryBase {
		public OIDTypeAdapterFactory() {
			super(OID.class,
				  "id");
		}
	}
	public static class YearTypeAdapterFactory
		        extends SingleFieldTypeAdapterFactoryBase {
		public YearTypeAdapterFactory() {
			super(Year.class,
				  "year");
		}
	}
	public static class MonthOfYearTypeAdapterFactory
		        extends SingleFieldTypeAdapterFactoryBase {
		public MonthOfYearTypeAdapterFactory() {
			super(MonthOfYear.class,
				  "monthOfYear");
		}
	}
	public static class DayOfMonthTypeAdapterFactory
		        extends SingleFieldTypeAdapterFactoryBase {
		public DayOfMonthTypeAdapterFactory() {
			super(DayOfMonth.class,
				  "dayOfMonth");
		}
	}
	public static class DayOfWeekTypeAdapterFactory
		        extends SingleFieldTypeAdapterFactoryBase {
		public DayOfWeekTypeAdapterFactory() {
			super(DayOfWeek.class,
				  "dayOfWeek");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @see http://www.javacreed.com/gson-typeadapter-example/
	 * Gson TypeAdapterFactory used to serialize EnumWithCode enums as their code
	 * (gson by default serializes Enums as their names)
	 */
	public static class EnumWithCodeTypeAdapterFactory
		     implements TypeAdapterFactory  {

		@Override
		public <T> TypeAdapter<T> create(final Gson gson,
										 final TypeToken<T> type) {
			if (!ReflectionUtils.isImplementing(type.getRawType(),EnumWithCode.class)) return null;
			
			final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
		    final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
		    return new TypeAdapter<T>() {
					      @Override 
					      public void write(final JsonWriter out,final T value) throws IOException {
					    	  JsonElement tree = delegate.toJsonTree(value);
					    	  if (!(tree instanceof JsonPrimitive)) {
					    		  elementAdapter.write(out,tree);
					    	  } else {
					    		  String codeAsString = ((EnumWithCode<?,?>)value).getCode().toString();
					    		  elementAdapter.write(out,new JsonPrimitive(codeAsString));
					    	  }
					      }
					      @Override 
					      public T read(final JsonReader in) throws IOException {
					          JsonElement tree = elementAdapter.read(in);
					          return delegate.fromJsonTree(tree);
					      }
		    };
		}
	}	
}
