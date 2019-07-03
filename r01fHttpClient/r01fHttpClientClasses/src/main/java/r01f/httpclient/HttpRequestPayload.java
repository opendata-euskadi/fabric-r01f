package r01f.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;

import lombok.Cleanup;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class HttpRequestPayload 
     extends HttpRequestPayloadBase<HttpRequestPayload> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public HttpRequestPayload(final byte[] content,
							  final boolean mustEncodeToTargetServerCharset) {
		super(content,
			  mustEncodeToTargetServerCharset);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//   BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpRequestPayload wrap(final InputStream is) throws IOException {
		return new HttpRequestPayload(IOUtils.toByteArray(is),
									  false);
	}
	public static HttpRequestPayload wrap(final byte[] bytes) {
		return new HttpRequestPayload(bytes,
									  false);
	}

	public static HttpRequestPayload wrap(final File file) throws IOException {
		@Cleanup InputStream fis = new FileInputStream(file);
		byte[] fileBytes = IOUtils.toByteArray(fis);
		return new HttpRequestPayload(fileBytes,
									  false);
	}

	@SuppressWarnings("deprecation")
	public static HttpRequestPayload wrap(final Reader reader) throws IOException {
		@Cleanup ReaderInputStream ris = new ReaderInputStream(reader);
		byte[] readerBytes = IOUtils.toByteArray(ris);
		return new HttpRequestPayload(readerBytes,
									  true);
	}
	public static HttpRequestPayload wrap(final String str) {
		return HttpRequestPayload.wrap(str,
									   Charset.defaultCharset());
	}
	public static HttpRequestPayload wrap(final String str,
										  final Charset charset) {
		byte[] strBytes = str.getBytes(charset);
		return new HttpRequestPayload(strBytes,
									  true);
	}
}
