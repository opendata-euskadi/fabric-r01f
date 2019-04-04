package r01f.httpclient;

import java.io.IOException;
import java.nio.charset.Charset;

public interface HttpRequestFormParameter {
	/**
	 * Serializes a param to be included in the posted data
	 * @param targetServerCharset the target server charset
	 * @param multiPart true if the data is posted in multi-part way
	 * @return the param in it's serialized form 
	 * @throws IOException if an I/O error occurs
	 */
	public byte[] _serializeFormParam(Charset targetServerCharset,
									  boolean multiPart) throws IOException;
}
