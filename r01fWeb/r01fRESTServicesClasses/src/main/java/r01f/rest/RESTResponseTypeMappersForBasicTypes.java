package r01f.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.reflection.ReflectionUtils;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Strings;

/**
 * {@link MessageBodyWriter} for basic types as Long, Integer, Boolean, etc
 *
 */
@Slf4j
public class RESTResponseTypeMappersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Boolean
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 	Jersey only scans at the locations defined under web.xml com.sun.jersey.config.property.packages
	 * <init-param>
	 *	To overcome this restriction, simply extend this type in a new type at the defined package 
	 */
	public static abstract class BooleanResponseTypeMapperBase
	  		 	      implements MessageBodyWriter<Boolean> {
		@Override
		public boolean isWriteable(final Class<?> type,final Type genericType,
								   final Annotation[] annotations,
								   final MediaType mediaType) {
			boolean outWriteable = false;
			if (type.equals(Boolean.class)) {
			     outWriteable = true;
			}
			return outWriteable;
		}
		@Override
		public long getSize(final Boolean theBoolean,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations,
							final MediaType mediaType) {
			return theBoolean != null ? Boolean.toString(theBoolean).length()
								   : 1;
		}
		@Override
		public void writeTo(final Boolean theBoolean,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations, 
							final MediaType mediaType,
							final MultivaluedMap<String,Object> httpHeaders,
							final OutputStream entityStream) throws IOException,
																	WebApplicationException {
			if (theBoolean != null) {
				entityStream.write(Boolean.toString(theBoolean).getBytes());
			} else {
				entityStream.write("false".getBytes());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Date
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 	Jersey only scans at the locations defined under web.xml com.sun.jersey.config.property.packages
	 * <init-param>
	 *	To overcome this restriction, simply extend this type in a new type at the defined package 
	 */
	public static abstract class DateResponseTypeMapperBase
	  		          implements MessageBodyWriter<Date> {
		@Override
		public boolean isWriteable(final Class<?> type,final Type genericType,
								   final Annotation[] annotations,
								   final MediaType mediaType) {
			boolean outWriteable = false;
			if (type.equals(Date.class)) {
			     outWriteable = true;
			}
			return outWriteable;
		}
		@Override
		public long getSize(final Date theDate,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations,
							final MediaType mediaType) {
			return theDate != null ? Long.toString(Dates.asMillis(theDate)).length()
								   : 1;
		}
		@Override
		public void writeTo(final Date theDate,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations, 
							final MediaType mediaType,
							final MultivaluedMap<String,Object> httpHeaders,
							final OutputStream entityStream) throws IOException,
																	WebApplicationException {
			if (theDate != null) {
				entityStream.write(Long.toString(Dates.asMillis(theDate)).getBytes());
			} else {
				entityStream.write("0".getBytes());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Long
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 	Jersey only scans at the locations defined under web.xml com.sun.jersey.config.property.packages
	 * <init-param>
	 *	To overcome this restriction, simply extend this type in a new type at the defined package 
	 */
	public static abstract class LongResponseTypeMapperBase
	  		 		  implements MessageBodyWriter<Long> {
		@Override
		public boolean isWriteable(final Class<?> type,final Type genericType,
								   final Annotation[] annotations,
								   final MediaType mediaType) {
			boolean outWriteable = false;
			if (type.equals(Long.class)) {
			     outWriteable = true;
			}
			return outWriteable;
		}
		@Override
		public long getSize(final Long theLong,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations,
							final MediaType mediaType) {
			return theLong != null ? Long.toString(theLong).length()
								   : 1;
		}
		@Override
		public void writeTo(final Long theLong,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations, 
							final MediaType mediaType,
							final MultivaluedMap<String,Object> httpHeaders,
							final OutputStream entityStream) throws IOException,
																	WebApplicationException {
			if (theLong != null) {
				entityStream.write(Long.toString(theLong).getBytes());
			} else {
				entityStream.write("0".getBytes());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Long
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 	Jersey only scans at the locations defined under web.xml com.sun.jersey.config.property.packages
	 * <init-param>
	 *	To overcome this restriction, simply extend this type in a new type at the defined package 
	 */
	public static abstract class RangeResponseTypeMapperBase
					  implements MessageBodyWriter<Range<?>> {
		@Override
		public boolean isWriteable(final Class<?> type,final Type genericType,
								   final Annotation[] annotations,
								   final MediaType mediaType) {
			boolean outWriteable = false;
			if (type.equals(Long.class)) {
			     outWriteable = true;
			}
			return outWriteable;
		}
		@Override
		public long getSize(final Range<?> theRange,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations,
							final MediaType mediaType) {
			return theRange != null ? theRange.asString().length()
								    : 1;
		}
		@Override
		public void writeTo(final Range<?> theRange,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations, 
							final MediaType mediaType,
							final MultivaluedMap<String,Object> httpHeaders,
							final OutputStream entityStream) throws IOException,
																	WebApplicationException {
			if (theRange != null) {
				entityStream.write(theRange.asString().getBytes());
			} else {
				entityStream.write("0".getBytes());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Collection
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * MessageBodyWriter for all {@link Collection} types (Map, List and Collection)
	 * This type is used when the REST service has to return a {@link Collection} type
	 */
	@SuppressWarnings("rawtypes")
	public static abstract class CollectionResponseTypeMapperBase 
		                 extends MarshalledObjectResultTypeMapperBase<Collection> {
		public CollectionResponseTypeMapperBase(final Marshaller modelObjectsMarshaller, final MediaType mediaType) {
			super(Map.class,
				  mediaType,
				  modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Map
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * MessageBodyWriter for all {@link Collection} types (Map, List and Collection)
	 * This type is used when the REST service has to return a {@link Collection} type
	 */
	@SuppressWarnings("rawtypes")
	public static abstract class MapResponseTypeMapperBase 
		                 extends MarshalledObjectResultTypeMapperBase<Map> {
		public MapResponseTypeMapperBase(final Marshaller modelObjectsMarshaller, final MediaType mediaType) {
			super(Map.class,
				  mediaType,
				  modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * MessageBodyWriter for all {@link PersistenceOperationResult}
	 */
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static abstract class MarshalledObjectResultTypeMapperBase<T> 
		              implements MessageBodyWriter<T>,
		              			 HasMarshaller {
		
		private final Class<?> _mappedType;
		private final MediaType _mediaType;
		
		@Getter private final Marshaller _modelObjectsMarshaller;
		
		@Override
		public boolean isWriteable(final Class<?> type,final Type genericType,
								   final Annotation[] annotations,
								   final MediaType mediaType) {
			boolean outWriteable = false;
			if (mediaType.equals(_mediaType) 
			 && ReflectionUtils.isImplementingAny(type, _mappedType)) {
			     outWriteable = true;
			}
			log.trace("{} type is {} writeable with {}",type.getName(),
														outWriteable ? "" : "NOT",
														this.getClass().getName());
			return outWriteable;
		}
		@Override
		public long getSize(final T obj,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations,
							final MediaType mediaType) {
			return -1;	// The size of the model object in it's serialized form is not know beforehand
		}
		@Override @SuppressWarnings("null")
		public void writeTo(final T obj,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations, 
							final MediaType mediaType,
							final MultivaluedMap<String,Object> httpHeaders,
							final OutputStream entityStream) throws IOException,
																	WebApplicationException {
			log.trace("writing {} type",type.getName());
			String outString = null;
			
			if(_mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
				outString = obj != null ? this.getModelObjectsMarshaller().forWriting().toJson(obj)	
							  		 	   : null;
			} else if (_mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
				outString = obj != null ? this.getModelObjectsMarshaller().forWriting().toXml(obj)	
							  		 	   : null;
			} else {
				throw new IllegalArgumentException("Received media type is not compatible");
			}
			
			if (Strings.isNOTNullOrEmpty(outString)) entityStream.write(outString.getBytes());
		}
	}
}
