package r01f.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.model.search.SearchModelObject;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.index.IndexManagementCommand;
import r01f.reflection.ReflectionUtils;
import r01f.rest.RESTResponseTypeMappersForBasicTypes.XMLMarshalledObjectResultTypeMapperBase;
import r01f.types.jobs.EnqueuedJob;
import r01f.util.types.Strings;

/**
 * Types in charge of convert the {@link Response} of a REST method form the business type returned from the method (ie {@link R01MStructureLabel})
 * to the bytes returned by the servlet in the {@link OutputStream}
 * ie: if inside a REST module exists a method like
 * <pre class='brush:java'>
 * 		@DELETE @Path("record/{id}") 
 *		@Produces(application/xml)
 *		public Record deleteRecord(@PathParam("id") final String id)  {
 *			....
 *		}
 * </pre>
 * In order to return in the OutputStream an instanceof Record a serialization to bytes of this java object must be done
 * This kind of serialization is done at the type-mappers which implements the {@link MessageBodyWriter} or {@link MessageBodyReader}
 * interfaces, whether it:
 * <ul>
 * 		<li>serializes the method return type TO the {@link Response} {@link OutputStream}</li>
 * 		<li>... or serializes a method param FROM the {@link Request} {@link InputStream}</li>
 * </ul>
 * JAX-RS scans the classpath searching the types that must be published as REST resources and also scans searching
 * types implementing {@link MessageBodyReader} or {@link MessageBodyWriter}
 * <pre>
 * NOTE:	As an alternative of JAX-RS scanning the classpath for the types, these can be issued at the
 * 			getClasses() method of the REST {@link Application} instance
 * </pre>
 * 			<pre class='brush:java'>
 * 					@Override
 *					public Set<Class<?>> getClasses() {
 *						Set<Class<?>> s = new HashSet<Class<?>>();
 *						s.add(LongResponseTypeMapper.class);
 *						...
 *						return s;
 *					}
 * 			</pre>
 *
 * For example, the {@link MessageBodyWriter} interface has three methods:
 * 	<table>
 * 		<tr>
 * 			<td>isWriteable</td>
 * 			<td>
 * 				<p>In this method a decision is made about the possibility of serialization of a received type using this {@link MessageBodyWriter} instance</p>
 * 				<p>Every {@link MessageBodyWriter} types are iterated one after another calling it's isWriteable method until one returning true is found</p>
 * 				<p>In order to make a decision to serialize or not some type, some methods can be used_</p>
 * 				<ul>
 * 					<li>Using the type of the object to serialize; this can be useful if the {@link MessageBodyWriter} instance is used for a concrete type</li>
 * 					<li>Using the MIME-TYPE: The method is annotated with @Produces(SOME-MIME-TYPE) and this MIME-TYPE is used to make the decision</li>
 * 					<li>Using some annotation: 
 * 						<ul>
 * 							<li>if a REST module method return type is to be serialized, the method can be annotated with a custom annotation.</li>
 * 							<li>If a REST module method param is to be serialized, the param can be annotated with a custom annotation</li>
 *						</ul>
 *					</li>
 * 				</ul>
 * 			</td>
 * 		</tr>
 * 		<tr>
 * 			<td>getSize</td>
 * 			<td>
 * 				It the response size in bytes is known, this size must be returned; otherwise, return -1
 * 			</td>
 * 		</tr>
 * 		<tr>
 * 			<td>writeTo</td>
 * 			<td>
 * 				Performs the java object serialization to bytes written in the {@link Response} {@link OutputStream}
 * 			</td>
 * 		</tr>
 * 	</table>
 * 
 * (see http://stackoverflow.com/questions/8194408/how-to-access-parameters-in-a-restful-post-method)
 *
 */
@Slf4j
public class RESTResponseTypeMappersForModelObjects {

/////////////////////////////////////////////////////////////////////////////////////////
//	ModelObject
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * MessageBodyWriter for all {@link ModelObject}s
	 */
	@Accessors(prefix="_")
	public static abstract class ModelObjectResponseTypeMapperBase<M extends ModelObject> 
		        		 extends XMLMarshalledObjectResultTypeMapperBase<M> {
		public ModelObjectResponseTypeMapperBase(final Marshaller marshaller) {
			super(ModelObject.class,
				  marshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SearchModelObject
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * MessageBodyWriter for all {@link SearchModelObject}s
	 */
	@Accessors(prefix="_")
	public static abstract class SearchModelObjectResponseTypeMapperBase 
		     			 extends XMLMarshalledObjectResultTypeMapperBase<SearchModelObject> {
		
		public SearchModelObjectResponseTypeMapperBase(final Marshaller marshaller) {
			super(SearchModelObject.class,
				  marshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RecordPersistenceOperationResult
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * MessageBodyWriter for all {@link PersistenceOperationResult}
	 */
	public static abstract class PersistenceOperationResultTypeMapperBase 
		                 extends XMLMarshalledObjectResultTypeMapperBase<PersistenceOperationResult> {		
		public PersistenceOperationResultTypeMapperBase(final Marshaller modelObjectsMarshaller) {
			super(PersistenceOperationResult.class,
				  modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * MessageBodyWriter for all {@link R01MModelObject}s
	 */
	public static abstract class OIDResponseTypeMapperBase 
		     		  implements MessageBodyWriter<OID> {
		
		@Override
		public boolean isWriteable(final Class<?> type,final Type genericType,
								   final Annotation[] annotations,
								   final MediaType mediaType) {
			boolean outWriteable = false;
			if (mediaType.equals(MediaType.APPLICATION_XML_TYPE) 
			 && ReflectionUtils.isImplementingAny(type,OID.class)) {
			     outWriteable = true;
			}
			log.trace("{} type is {} writeable with {}",type.getName(),
														outWriteable ? "" : "NOT",
														this.getClass().getName());
			return outWriteable;
		}
		@Override
		public long getSize(final OID modelObj,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations,
							final MediaType mediaType) {
			return -1;
		}
		@Override
		public void writeTo(final OID modelObj,
							final Class<?> type,final Type genericType,
							final Annotation[] annotations, 
							final MediaType mediaType,
							final MultivaluedMap<String,Object> httpHeaders,
							final OutputStream entityStream) throws IOException,
																	WebApplicationException {
			log.trace("writing {} type",type.getName());
			String oid = modelObj.asString(); 
			// write 
			if (Strings.isNOTNullOrEmpty(oid)) entityStream.write(oid.getBytes());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IndexManagementCommand & EnqueuedJob
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * MessageBodyWriter for all {@link IndexManagementCommand}s
	 */
	@Accessors(prefix="_")
	public static abstract class IndexManagementCommandResponseTypeMapperBase 
		     			 extends XMLMarshalledObjectResultTypeMapperBase<IndexManagementCommand> {
		
		
		public IndexManagementCommandResponseTypeMapperBase(final Marshaller marshaller) {
			super(IndexManagementCommand.class,
				  marshaller);
		}
	}
	/**
	 * MessageBodyWriter for all {@link EnqueuedJob}s
	 */
	@Accessors(prefix="_")
	public static abstract class EnqueuedJobResponseTypeMapperBase 
		     			 extends XMLMarshalledObjectResultTypeMapperBase<EnqueuedJob> {
		
		public EnqueuedJobResponseTypeMapperBase(final Marshaller marshaller) {
			super(EnqueuedJob.class,
				  marshaller);
		}
	}

}
