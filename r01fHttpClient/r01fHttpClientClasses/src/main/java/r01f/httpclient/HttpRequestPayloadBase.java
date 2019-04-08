package r01f.httpclient;

import java.nio.charset.Charset;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.httpclient.HttpRequestFormParameterForMultiPartBinaryData.HttpRequestFormBinaryParameterTransferEncoding;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.util.types.StringEncodeUtils;


@Accessors(prefix="_")
abstract class HttpRequestPayloadBase<SELF_TYPE extends HttpRequestPayloadBase<SELF_TYPE>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  STATUS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter protected final byte[] _content;
	@Getter protected MimeType _mimeType;
	@Getter protected HttpRequestFormBinaryParameterTransferEncoding _transferEncoding;
	
			protected transient boolean _mustEncodeToTargetServerCharset = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected HttpRequestPayloadBase(final byte[] content) {
		_content = content;
		_mimeType = MimeTypes.OCTECT_STREAM;	// default
		_transferEncoding = HttpRequestFormBinaryParameterTransferEncoding.BINARY;
	}
	protected HttpRequestPayloadBase(final byte[] content,
									 final boolean mustEncodeToTargetServerCharset) {
		this(content);
		_mustEncodeToTargetServerCharset = mustEncodeToTargetServerCharset;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-APIs
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static class HttpRequestPayloadParameterBuilderNameStep<P extends HttpRequestPayloadBase<P>> {
		private final byte[] _content;
		private final boolean _mustEncodeToTargetServerCharset;
		private final HttpRequestPayloadParameterFactory<P> _reqPayloadParamFactory;
		
		public P withFileName(final String name) {
			return _reqPayloadParamFactory.createFrom(name,
													  _content,
													  _mustEncodeToTargetServerCharset);
		}
	}
	protected interface HttpRequestPayloadParameterFactory<P extends HttpRequestPayloadBase<P>> {
		public P createFrom(final String name,
							final byte[] content,
							final boolean mustEncodeToTargetServerCharset);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public byte[] getContent(final Charset charset) {
		return StringEncodeUtils.encode(new String(_content),
										charset)
								.toString()
								.getBytes();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE mimeType(final MimeType mime) {
		_mimeType = mime;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE transferedEncodedAs(final HttpRequestFormBinaryParameterTransferEncoding transferEncoding) {
		_transferEncoding = transferEncoding;
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean mustEncodeToTargetServerCharset() {
		return _mustEncodeToTargetServerCharset;
	}
}