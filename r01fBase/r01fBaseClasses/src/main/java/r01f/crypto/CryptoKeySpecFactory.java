package r01f.crypto;

import java.security.spec.KeySpec;


public interface CryptoKeySpecFactory {

	public KeySpec from(final byte[] byteArray);

}
