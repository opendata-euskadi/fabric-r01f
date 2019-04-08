package r01f.httpclient;

import static r01f.httpclient.HttpRequestFormParameterForMultiPartBinaryData.HttpRequestFormBinaryParameterTransferEncoding.BASE64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import r01f.util.types.collections.CollectionUtils;

/**
 * Creates a param to be send to the server using a POSTed form which could be:
 * <ol>
 * 		<li>a param in a form-url-encoded post</li>
 * 		<li>a form param in a multi-part post</li>
 * </ol>
 * The creation of a param is like:
 * <pre class='brush:java'>
 * 		HttpRequestFormParameter param = HttpRequestFormParameterForMultiPartBinaryData.of(HttpRequestPayloadForFileParameter.wrap(new File("d:/myFile.txt"))
 * 																											 	  		   	 .withFileName("myFile.txt"),
 * 																		   				   HttpRequestPayloadForFileParameter.wrap(new File("d:/myOtherFile.gif")
 * 																											 	  		   	 .withFileName("myImage.gif")
 * 																											 	  		  	 .mimeType(...))
 * 																	   					.withName("myFiles");
 * </pre>
 */
@Accessors(prefix="_")
public class HttpRequestFormParameterForFileBinaryData
     extends HttpRequestFormParameterForMultiPartBinaryData {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpRequestFormParameterForFileBinaryData(final String name,
													 final List<HttpRequestPayloadForFileParameter> fileParts) {
		super(name,
			  fileParts);
	}
	public static HttpRequestFormParameterForMultiPartBinaryDataBuilderNameStep of(final HttpRequestPayloadForFileParameter... parts) {
		return new HttpRequestFormParameterForMultiPartBinaryDataBuilderNameStep(Lists.newArrayList(parts),
																				 new HttpRequestFormParameterFactory<HttpRequestFormParameterForFileBinaryData>() {
																						@Override
																						public HttpRequestFormParameterForFileBinaryData createFrom(final String name,
																																					final List<HttpRequestPayloadForFileParameter> theParts) {
																							return new HttpRequestFormParameterForFileBinaryData(name,
																																			     theParts);
																						}
																				 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public byte[] _serializeFormParam(final Charset targetServerCharset,
									  final boolean multiPart) throws IOException {
		if (!multiPart) throw new IOException("A file parameter must be POSTed in a multi-part form");

		final Charset theTargetServerCharset = targetServerCharset == null ? Charset.defaultCharset()
																	 : targetServerCharset;

		byte[] outBytes = null;
		if (_binaryParts.size() == 1) {
			// FilePart header
			final HttpRequestPayloadForFileParameter filePart = CollectionUtils.of(_binaryParts)
																         .pickOneAndOnlyElement();

			// File Part contents
			byte[] contentBytes = null;
			contentBytes = filePart.mustEncodeToTargetServerCharset() ? filePart.getContent(theTargetServerCharset)		// encode the source String to the target server encoding
																      : filePart.getContent();
			contentBytes = filePart.getTransferEncoding() == BASE64 ? Base64.encodeBase64(contentBytes)
													   			    : contentBytes;

			final ByteArrayOutputStream bos = new ByteArrayOutputStream(contentBytes.length);
			bos.write(contentBytes);
			bos.flush();
			bos.close();
			outBytes = bos.toByteArray();



		} else {
			throw new IllegalArgumentException("Se esperaba un unico fichero");
		}

		return outBytes != null ? outBytes
								: null;
	}
}
