package r01f.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;

import lombok.Cleanup;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class HttpRequestPayloadForFileParameter 
     extends HttpRequestPayloadBase<HttpRequestPayloadForFileParameter> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final String _fileName;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected HttpRequestPayloadForFileParameter(final String fileName,
												 final byte[] content) {
		super(content);
		_fileName = fileName;
	}
	protected HttpRequestPayloadForFileParameter(final String fileName,
												 final byte[] content,
												 final boolean mustEncodeToTargetServerCharset) {
		super(content,
			  mustEncodeToTargetServerCharset);
		_fileName = fileName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//   BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter> wrap(final InputStream is) throws IOException {
		return new HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter>(IOUtils.toByteArray(is),
													  				 							  false,
													  				 							  _createReqPayloadParamFactory());
	}
	public static HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter> wrap(final byte[] bytes) {
		return new HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter>(bytes,
																								  false,
																								   _createReqPayloadParamFactory());
	}
	
	public static HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter> wrap(final File file) throws IOException {
		@Cleanup InputStream fis = new FileInputStream(file);
		byte[] fileBytes = IOUtils.toByteArray(fis);
		return new HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter>(fileBytes,
																	 							  false,
																	 							  _createReqPayloadParamFactory());
	}
	
	@SuppressWarnings("deprecation")
	public static HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter> wrap(final Reader reader) throws IOException {
		@Cleanup ReaderInputStream ris = new ReaderInputStream(reader);
		byte[] readerBytes = IOUtils.toByteArray(ris);
		return new HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter>(readerBytes,
																	 							  true,
																	 							  _createReqPayloadParamFactory());
	}
	public static HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter> wrap(final String str) {
		byte[] strBytes = str.getBytes();
		return new HttpRequestPayloadParameterBuilderNameStep<HttpRequestPayloadForFileParameter>(strBytes,
																	 							  true,
																	 							  _createReqPayloadParamFactory());
	}
	private static HttpRequestPayloadParameterFactory<HttpRequestPayloadForFileParameter> _createReqPayloadParamFactory() {
		return new HttpRequestPayloadParameterFactory<HttpRequestPayloadForFileParameter>() {
						@Override
						public HttpRequestPayloadForFileParameter createFrom(final String name, 
																			 final byte[] content,
																			 final boolean mustEncodeToTargetServerCharset) {
							return new HttpRequestPayloadForFileParameter(name,
																		  content,
																		  mustEncodeToTargetServerCharset);
						}
			   };
	}
}
