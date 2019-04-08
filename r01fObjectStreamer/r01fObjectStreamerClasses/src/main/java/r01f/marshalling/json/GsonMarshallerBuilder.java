package r01f.marshalling.json;

import java.lang.reflect.Field;
import java.util.Date;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import r01f.marshalling.json.GsonTypeAdapterFactories.EnumWithCodeTypeAdapterFactory;
import r01f.marshalling.json.GsonTypeAdapterFactories.OIDTypeAdapterFactory;
import r01f.marshalling.json.GsonTypeAdapterFactories.YearTypeAdapterFactory;
import r01f.patterns.IsBuilder;

public class GsonMarshallerBuilder 
  implements IsBuilder {
///////////////////////////////////////////////////////////////////////////////
//  BUILDER
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Builder
	 * <b>BEWARE!</b>
	 * It's advisable to inject the JSonMarshaller using GUICE since the created object
	 * is cached as a singleton
	 * @return el objeto {@link GsonMarshaller}
	 */
	public static Gson createGson() {		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class,new DateTypeAdapter());
//				gsonBuilder.serializeNulls();
		gsonBuilder.registerTypeAdapterFactory(new OIDTypeAdapterFactory());
		gsonBuilder.registerTypeAdapterFactory(new YearTypeAdapterFactory());
		gsonBuilder.registerTypeAdapterFactory(new EnumWithCodeTypeAdapterFactory());
		gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {
													@Override
													public String translateName(final Field f) {
														return f.getName().startsWith("_") ? f.getName().substring(1) : f.getName();
													}
										   });
		return gsonBuilder.create();
	}
	public static GsonMarshaller create() {
		return new GsonMarshaller(GsonMarshallerBuilder.createGson());
	}
}
