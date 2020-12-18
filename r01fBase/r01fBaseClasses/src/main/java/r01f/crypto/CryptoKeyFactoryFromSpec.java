package r01f.crypto;

import java.security.interfaces.RSAKey;
import java.security.spec.KeySpec;

import r01f.patterns.FactoryFrom;


public interface CryptoKeyFactoryFromSpec<K extends RSAKey>
		 extends FactoryFrom<KeySpec,K> {
	// just extends
}
