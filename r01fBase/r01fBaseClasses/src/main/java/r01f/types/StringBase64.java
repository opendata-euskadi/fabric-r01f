package r01f.types;

import java.io.Serializable;

import com.google.common.base.Function;

import lombok.RequiredArgsConstructor;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.StringEncodeUtils;

@MarshallType(as="stringBase64")
@RequiredArgsConstructor
public class StringBase64 
  implements Serializable,
  			 CanBeRepresentedAsString {

	private static final long serialVersionUID = -4115901126395440560L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _stringBase64;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public String decode() {
		return StringEncodeUtils.decodeBase64(_stringBase64)
								.toString();
	}
	public static StringBase64 encode(final String rawString) {
		if (rawString == null) throw new IllegalArgumentException();
		return StringBase64.encode(rawString,
								   new Function<String,String>() {
											@Override
											public String apply(final String input) {
												return StringEncodeUtils.encodeBase64String(input.getBytes());
											}
								   });
	}
	public static StringBase64 encode(final String rawString,
							  		  final Function<String,String> encoder) {
		if (rawString == null || encoder == null) throw new IllegalArgumentException();
		return new StringBase64(encoder.apply(rawString));
	}
	public static StringBase64 encode(final byte[] bytes) {
		if (bytes.length == 0) throw new IllegalArgumentException();
		return new StringBase64(StringEncodeUtils.encodeBase64String(bytes));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _stringBase64;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
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
