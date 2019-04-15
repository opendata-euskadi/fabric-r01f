package r01f.rest;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.common.collect.Sets;
import com.sun.jersey.guice.JerseyServletModule;

import r01f.util.types.collections.CollectionUtils;


/**
 * Rest app referenced at {@link JerseyServletModule} (Guice is in use)
 * in order to load the REST resources and the request / response received / sent objects mappers
 * 
 * <pre>
 * NOTE:	If Guice was not used, the REST App should be defined in WEB-INF/web.xml
 * </pre>	
 */
@Singleton
public abstract class RESTAppBase 
     		  extends Application {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected RESTAppBase() {
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the {@link RESTResource} implementing types
	 */
	public abstract Set<Class<? extends RESTResource>> getRESTResourceTypes();
	/**
	 * @return the {@link MessageBodyReader} types that maps request received objects
	 */
	@SuppressWarnings("static-method")
	public Set<Class<? extends MessageBodyReader<?>>> getRequestReceivedTypesMappers() {
		Set<Class<? extends MessageBodyReader<?>>> outMappers = Sets.newHashSet();
		return outMappers;
	}
	/**
	 * @return the {@link MessageBodyWriter} types that maps response sent objects
	 */
	@SuppressWarnings("static-method")	
	public Set<Class<? extends MessageBodyWriter<?>>> getResponseSentTypesMappers() {
		Set<Class<? extends MessageBodyWriter<?>>> outMappers = Sets.newHashSet();
		return outMappers;
	}
	/**
	 * @return the {@link ExceptionMapper} types that maps exceptions
	 */
	@SuppressWarnings("static-method")	
	public Set<Class<? extends ExceptionMapper<?>>> getExceptionsMappers() {
		Set<Class<? extends ExceptionMapper<?>>> outMappers = Sets.newHashSet();
		return outMappers;
	}
	
///////////////////////////////////////////////////////////////////////////////
// METHODS
///////////////////////////////////////////////////////////////////////////////	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		
		// rest Resources
		Set<Class<? extends RESTResource>> restResourceTypes = this.getRESTResourceTypes();
		if (CollectionUtils.hasData(restResourceTypes)) s.addAll(restResourceTypes);
		
		// Request received objects mappers: transforms Java->XML for REST methods param types
		Set<Class<? extends MessageBodyReader<?>>> reqReceivedTypesMappers = this.getRequestReceivedTypesMappers();
		if (CollectionUtils.hasData(reqReceivedTypesMappers)) s.addAll(reqReceivedTypesMappers);
		
		// Response sent objects mappers: transforms Java->XML for REST methods return types
		Set<Class<? extends MessageBodyWriter<?>>> respSentTypesMappers = this.getResponseSentTypesMappers();
		if (CollectionUtils.hasData(respSentTypesMappers)) s.addAll(respSentTypesMappers);
		
		// Exception Mappers
		Set<Class<? extends ExceptionMapper<?>>> expsMappers = this.getExceptionsMappers();
		if (CollectionUtils.hasData(expsMappers)) s.addAll(expsMappers);		
		
		return s;
	}
}
