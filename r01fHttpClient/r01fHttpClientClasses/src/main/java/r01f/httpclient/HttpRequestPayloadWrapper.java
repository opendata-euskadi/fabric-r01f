package r01f.httpclient;

import static r01f.httpclient.HttpRequestFormParameterForMultiPartBinaryData.HttpRequestFormBinaryParameterTransferEncoding.BASE64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.mime.MimeType;

@Accessors(prefix="_")
@RequiredArgsConstructor
class HttpRequestPayloadWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final HttpRequestPayload _payload;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the contentType header
	 * @return
	 */
	public MimeType payloadContentType() {
		return _payload != null ? _payload.getMimeType()	// .getTypeName()
								: null;
	}
	/**
	 * @return the payload length
	 */
	public long payloadContentLength() {
		long payloadContentLength = -1;
		if (_payload != null) {
			byte[] contentBytes = _payload.getTransferEncoding() == BASE64 ? Base64.encodeBase64(_payload.getContent())
													   			    	   : _payload.getContent();
			payloadContentLength = contentBytes.length;
		}
		return payloadContentLength;
	}
	/**
	 * Puts the payload into the http connection OutputStream
	 * @param dos the {@link OutputStream}
	 * @throws IOException if an I/O error occurs
	 */
	public void payloadToOutputStream(final DataOutputStream dos) throws IOException {
		if (_payload != null) {
			byte[] contentBytes = _payload.getTransferEncoding() == BASE64 ? Base64.encodeBase64(_payload.getContent())
													   			    	   : _payload.getContent();
			ByteArrayOutputStream partBos = new ByteArrayOutputStream(contentBytes.length);
			partBos.write(contentBytes);
			partBos.flush();
			partBos.close();
			dos.write(partBos.toByteArray());
		}
	}
}
