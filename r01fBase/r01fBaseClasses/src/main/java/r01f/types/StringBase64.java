package r01f.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import com.google.common.base.Function;

import lombok.SneakyThrows;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.StringEncodeUtils;

@MarshallType(as="stringBase64")
public class StringBase64
  implements Serializable,
  			 CanBeRepresentedAsString {

	private static final long serialVersionUID = -4115901126395440560L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,String> DEFAULT_ENCODER_FUNCION = new Function<String,String>() {
																					@Override
																					public String apply(final String input) {
																						return StringEncodeUtils.encodeBase64AsString(input.getBytes());
																					}
																		  };
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _stringBase64;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public StringBase64(final String stringBase64) {
		_stringBase64 = stringBase64;
	}
	public static StringBase64 fromBase64EncodedString(final String stringBase64) {
		return new StringBase64(stringBase64);
	}
	public static StringBase64 encode(final String rawString) {
		return StringBase64.encode(rawString,
								   DEFAULT_ENCODER_FUNCION);
	}
	public static StringBase64 encode(final String rawString,
							  		  final Function<String,String> encoder) {
		if (rawString == null || encoder == null) throw new IllegalArgumentException();
		return new StringBase64(encoder.apply(rawString));
	}
	public static StringBase64 encode(final byte[] rawBytes) {
		if (rawBytes == null || rawBytes.length == 0) throw new IllegalArgumentException();
 		return new StringBase64(StringEncodeUtils.encodeBase64AsString(rawBytes));
	}
	@SneakyThrows
	public static StringBase64 deflateAndEncode(final String rawString) {
		return StringBase64.deflateAndEncode(rawString,
								   			 DEFAULT_ENCODER_FUNCION);
	}
	@SneakyThrows
	public static StringBase64 deflateAndEncode(final String rawString,
												final Function<String,String> encoder) {
		if (rawString == null || encoder == null) throw new IllegalArgumentException();

		// [1] - Deflate
		byte[] deflatedRawBytes = _deflate(rawString.getBytes());

		// [2] - Encode base 64
		return StringBase64.encode(deflatedRawBytes);
	}
	@SneakyThrows
	public static StringBase64 deflateAndEncode(final byte[] rawBytes) {
		// [1] - Deflate
		byte[] deflatedRawBytes = _deflate(rawBytes);

		// [2] - Encode base 64
		return StringBase64.encode(deflatedRawBytes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _stringBase64;
	}
	public byte[] getBytes() {
		return _stringBase64.getBytes();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DECODE
/////////////////////////////////////////////////////////////////////////////////////////
	public String decode() {
		return StringEncodeUtils.decodeBase64(_stringBase64)
								.toString();
	}
	public byte[] decodeAsBytes() {
		return StringEncodeUtils.decodeBase64(_stringBase64.getBytes());
	}
	@SneakyThrows
	public String decodeAndInflate() {
		// [1] - Decode
		String deflatedString = this.decode();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedString.getBytes());
		return new String(inflatedBytes);
	}
	@SneakyThrows
	public byte[] decodeAsBytesAndInflate() {
		// [1] - Decode
		byte[] deflatedBytes = this.decodeAsBytes();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedBytes);
		return inflatedBytes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INFLATE / DEFLATE
/////////////////////////////////////////////////////////////////////////////////////////
	private static byte[] _deflate(final byte[] inflatedBytes) throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION,
										 true);	// use a GZIP compatible version
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream,
																			 deflater);
		deflaterOutputStream.write(inflatedBytes);
		deflaterOutputStream.close();
		byte[] outDeflatedStringBytes = outputStream.toByteArray();

		return outDeflatedStringBytes;
	}
	private static byte[] _inflate(final byte[] deflatedBytes) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Inflater inflater = new Inflater(true);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(outputStream,
																			 inflater);
		inflaterOutputStream.write(deflatedBytes);
		inflaterOutputStream.close();
		byte[] outInflatedStringBytes = outputStream.toByteArray();

		return outInflatedStringBytes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof StringBase64)) return false;
		StringBase64 b = (StringBase64)obj;
		return _stringBase64.equals(b.asString());
	}
	@Override
	public int hashCode() {
		return _stringBase64.hashCode();
	}
}
