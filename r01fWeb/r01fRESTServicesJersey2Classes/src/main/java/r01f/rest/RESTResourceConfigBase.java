package r01f.rest;

import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.server.ResourceConfig;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import r01f.util.types.collections.CollectionUtils;


/**
 * Rest Resource referenced at {@link org.glassfish.jersey.server.ResourceConfig} (Jersey 2  is in use)
 *  see {@link https://stackoverflow.com/questions/45625925/what-exactly-is-the-resourceconfig-class-in-jersey-2/45627178#45627178 }
 * in order to load the REST resources and the request / response received / sent objects mappers / exeption mappers
 * See also {@link RESTResourceConfigBootstrapBase } with DI Boostraping logic.
 *
 * <pre>
 *   NOTE: REST Resource should be defined in WEB-INF/web.xml
 * </pre>
 */
@Slf4j
@Singleton
public abstract class RESTResourceConfigBase
              extends ResourceConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public RESTResourceConfigBase() {
        // rest Resources
        Set<Class<? extends RESTResource>> restResourceTypes = this.getRESTResourceTypes();
        if (CollectionUtils.hasData(restResourceTypes)) {
            restResourceTypes.stream()
                                .forEach(r -> {  log.warn("Registering [ Jersey 2 Resource  {} ]", r.getCanonicalName());
                                                 register(r);
                                              });
        }
        // Request received objects mappers: transforms Java->XML for REST methods param types
        Set<Class<? extends MessageBodyReader<?>>> reqReceivedTypesMappers = this.getRequestReceivedTypesMappers();
        if (CollectionUtils.hasData(reqReceivedTypesMappers)) {
              reqReceivedTypesMappers.stream()
                                		.forEach(r -> {  log.warn("Registering [ Jersey 2 Reequest Type ] {} ]", r.getCanonicalName());
                                                 register(r);
                                              });
        }
        // Response sent objects mappers: transforms Java->XML,JSON, etx... for REST methods return types
        Set<Class<? extends MessageBodyWriter<?>>> respSentTypesMappers = this.getResponseSentTypesMappers();
        if (CollectionUtils.hasData(respSentTypesMappers)) {
             respSentTypesMappers.stream()
                                 .forEach(r -> {  log.warn("Registering [  Jersey 2 Response Type  {} ]", r.getCanonicalName());
                                                 register(r);
                                              });
        }
        // Exception Mappers
        Set<Class<? extends ExceptionMapper<?>>> expsMappers = this.getExceptionsMappers();
        if (CollectionUtils.hasData(expsMappers)) {
               expsMappers.stream()
               				.forEach(r -> {  log.warn("Registering [ Jersey 2  Exception Mapper Type  {} ]", r.getCanonicalName());
                                                 register(r);
                                      });
        }

        // Filters
        Set<Class<? extends RESTFilter>> filters = this.getFilterTypes();
        if (CollectionUtils.hasData(filters)) {
    			filters.stream()
               				.forEach(r -> {  log.warn("Registering [ Jersey 2  Filter  {} ]", r.getCanonicalName());
                                                 register(r);
                                      });
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return the {@link RESTResource} implementing types
     */
    public abstract Set<Class<? extends RESTResource>> getRESTResourceTypes();
    /**
     * @return the {@link MessageBodyReader} types that maps request received objects
     */
    public Set<Class<? extends MessageBodyReader<?>>> getRequestReceivedTypesMappers() {
        Set<Class<? extends MessageBodyReader<?>>> outMappers = Sets.newHashSet();
        return outMappers;
    }
    /**
     * @return the {@link MessageBodyWriter} types that maps response sent objects
     */
    public Set<Class<? extends MessageBodyWriter<?>>> getResponseSentTypesMappers() {
        Set<Class<? extends MessageBodyWriter<?>>> outMappers = Sets.newHashSet();
        return outMappers;
    }
    /**
     * @return the {@link ExceptionMapper} types that maps exceptions
     */
    public Set<Class<? extends ExceptionMapper<?>>> getExceptionsMappers() {
        Set<Class<? extends ExceptionMapper<?>>> outMappers = Sets.newHashSet();
        return outMappers;
    }
      /**
     * @return the {@link RESTFilter} types that maps exceptions
     */
    public Set<Class<? extends RESTFilter >> getFilterTypes() {
        Set<Class<? extends RESTFilter>> outMappers = Sets.newHashSet();
        return outMappers;
    }
///////////////////////////////////////////////////////////////////////////////
// METHODS
///////////////////////////////////////////////////////////////////////////////
}
