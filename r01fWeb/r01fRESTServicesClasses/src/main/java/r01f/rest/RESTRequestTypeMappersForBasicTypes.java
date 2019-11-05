package r01f.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.io.util.StringPersistenceUtils;
import r01f.reflection.ReflectionUtils;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Strings;

/**
 * Type mappers for user types
 */
@Slf4j
public class RESTRequestTypeMappersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Date (only usable for Dates sent in the BODY (POST / PUT)
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class DateRequestTypeMapperBase 
		  	          implements MessageBodyReader<Date> {
		@Override
		public boolean isReadable(final Class<?> type,final Type genericType,
								  final Annotation[] annotations,
								  final MediaType mediaType) {
			boolean outReadable = false;
			if (type.equals(Date.class)) {
			     outReadable = true;
			} 
			return outReadable;
		}
		@Override
		public Date readFrom(final Class<Date> type,final Type genericType,
							 final Annotation[] annotations,
							 final MediaType mediaType,
							 final MultivaluedMap<String,String> httpHeaders,
							 final InputStream entityStream) throws IOException,
							   										WebApplicationException {
			log.trace("reading {} type",type.getName());
			String dateMillisStr = StringPersistenceUtils.load(entityStream);
			Date outDate = null;
			if (Strings.isNOTNullOrEmpty(dateMillisStr)) {
				long millis = Long.parseLong(dateMillisStr);
				outDate = Dates.fromMillis(millis);
			}
			return outDate;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Range (only usable for Dates sent in the BODY (POST / PUT)
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class DateRangeRequestTypeMapperBase 
		  	          implements MessageBodyReader<Range<Date>> {
		@Override
		public boolean isReadable(final Class<?> type,final Type genericType,
								  final Annotation[] annotations,
								  final MediaType mediaType) {
			boolean outReadable = false;
			if (type.equals(Range.class)) {
			     outReadable = true;
			} 
			return outReadable;
		}
		@Override
		public Range<Date> readFrom(final Class<Range<Date>> type,final Type genericType,
							 	 	final Annotation[] annotations,
							 	 	final MediaType mediaType,
							 	 	final MultivaluedMap<String,String> httpHeaders,
							 	 	final InputStream entityStream) throws IOException,
							   											   WebApplicationException {
			log.trace("reading {} type",type.getName());
			String rangeAsString = StringPersistenceUtils.load(entityStream);
			Range<Date> outRange = null;
			if (Strings.isNOTNullOrEmpty(rangeAsString)) {
				outRange = Range.parse(rangeAsString,Date.class);
			}
			return outRange;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public static abstract class MarshalledObjectRequestTypeMapper<T> 
		  	 		  implements MessageBodyReader<T> {
		
		private final Class<?> _mappedType;
		private final MediaType _mediaType;
		
		public abstract r01f.objectstreamer.Marshaller getObjectsMarshaller();
		
		@Override
		public boolean isReadable(final Class<?> type,final Type genericType,
								  final Annotation[] annotations,
								  final MediaType mediaType) {
			// every application/xml received params are transformed to java in this type
			return mediaType.isCompatible(_mediaType)
				&& ReflectionUtils.isImplementing(type,_mappedType);
		}
		@Override
		public T readFrom(final Class<T> type,final Type genericType,
						  final Annotation[] annotations,
						  final MediaType mediaType,
						  final MultivaluedMap<String,String> httpHeaders,
						  final InputStream entityStream) throws IOException,
							  								     WebApplicationException {
			log.trace("reading {} type",type.getName());
			// xml -> java
			String xml = StringPersistenceUtils.load(entityStream);
			T outObj = null;
			if (Strings.isNOTNullOrEmpty(xml)) {
				if(mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
					outObj = this.getObjectsMarshaller().forReading().fromJson(xml, type);
				} else if(mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
					outObj = this.getObjectsMarshaller().forReading().fromXml(xml, type);
				} else {
					throw new IllegalArgumentException("Received media type is not compatible");
				}
			}
			return outObj;
		}
	}
}
