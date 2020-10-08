package r01f.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.io.util.StringPersistenceUtils;
import r01f.reflection.ReflectionUtils;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

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
	public static abstract class MarshalledObjectRequestTypeMapper<T>
		  	 		  implements MessageBodyReader<T> {

		private final Class<?> _mappedType;
		private final Collection<MediaType> _mediaTypes;

		public MarshalledObjectRequestTypeMapper(final Class<?> mappedType,
												 final MediaType... mediaTypes) {
			this(mappedType,
				 CollectionUtils.hasData(mediaTypes) ? Lists.newArrayList(mediaTypes) : null);
		}
		public MarshalledObjectRequestTypeMapper(final Class<?> mappedType,
												 final Collection<MediaType> mediaTypes) {
			_mappedType = mappedType;
			_mediaTypes = mediaTypes;
		}

		public abstract r01f.objectstreamer.Marshaller getObjectsMarshaller();

		@Override
		public boolean isReadable(final Class<?> type,final Type genericType,
								  final Annotation[] annotations,
								  final MediaType mediaType) {
			// every application/xml received params are transformed to java in this type
			return this.isCompatible(mediaType)
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
				if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
					outObj = this.getObjectsMarshaller().forReading().fromJson(xml, type);
				} else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
					outObj = this.getObjectsMarshaller().forReading().fromXml(xml, type);
				} else {
					throw new IllegalArgumentException("Received media type is not compatible");
				}
			}
			return outObj;
		}
		protected boolean isCompatible(final MediaType mediaType) {
			if (CollectionUtils.isNullOrEmpty(_mediaTypes)) return true;
			boolean outCompatible = false;
			for (MediaType m : _mediaTypes) {
				if (mediaType.isCompatible(m)) {
					outCompatible = true;
					break;
				}
			}
			return outCompatible;
		}
	}
	@Deprecated
	public static abstract class XMLMarshalledObjectRequestTypeMapper<T>
						 extends MarshalledObjectRequestTypeMapper<T> {
		public XMLMarshalledObjectRequestTypeMapper(final Class<?> mappedType) {
			super(mappedType,
				  MediaType.APPLICATION_XML_TYPE,MediaType.APPLICATION_JSON_TYPE);
		}
	}
}
