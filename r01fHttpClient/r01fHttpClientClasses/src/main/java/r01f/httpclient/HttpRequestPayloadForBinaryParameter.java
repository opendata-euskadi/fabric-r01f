package r01f.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;

import lombok.Cleanup;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class HttpRequestPayloadForBinaryParameter 
     extends HttpRequestPayloadBase<HttpRequestPayloadForBinaryParameter> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpRequestPayloadForBinaryParameter(final byte[] content) {
		super(content);
	}
	public HttpRequestPayloadForBinaryParameter(final byte[] content,
												final boolean mustEncodeToTargetServerCharset) {
		super(content,
			  mustEncodeToTargetServerCharset);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//   BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpRequestPayloadForBinaryParameter wrap(final InputStream is) throws IOException {
		return new HttpRequestPayloadForBinaryParameter(IOUtils.toByteArray(is),
														false);
	}
	public static HttpRequestPayloadForBinaryParameter wrap(final byte[] bytes) {
		return new HttpRequestPayloadForBinaryParameter(bytes,
														false);
	}
	@SuppressWarnings("resource")
	public static HttpRequestPayloadForBinaryParameter wrap(final File file) throws IOException {
		@Cleanup InputStream fis = new FileInputStream(file);
		byte[] fileBytes = IOUtils.toByteArray(fis);
		return new HttpRequestPayloadForBinaryParameter(fileBytes,
														false);
	}
	@SuppressWarnings("resource")
	public static HttpRequestPayloadForBinaryParameter wrap(final Reader reader) throws IOException {
		@Cleanup ReaderInputStream ris = new ReaderInputStream(reader);
		byte[] readerBytes = IOUtils.toByteArray(ris);
		return new HttpRequestPayloadForBinaryParameter(readerBytes,
														true);
	}
	public static HttpRequestPayloadForBinaryParameter wrap(final String str) {
		byte[] strBytes = str.getBytes();
		return new HttpRequestPayloadForBinaryParameter(strBytes,
														true);
	}
}
