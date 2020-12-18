package r01f.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.Path;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CryptoKeys {
/////////////////////////////////////////////////////////////////////////////////////////
//	LOAD FROM CLASSPATH
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K extends RSAKey> K loadRSAKeyFromClasspath(final Path keyClasspahtLocation,
															   final CryptoKeySpecFactory keySpecFactory,
															   final CryptoKeyFactoryFromSpec<K> keyFactoryFromSpec) {
		KeySpec keySpec = CryptoKeys.loadRSAKeySpecFromClasspath(keyClasspahtLocation,
																 keySpecFactory);
		K key = keyFactoryFromSpec.from(keySpec);	// private key: (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(keySpec)
													// public key:   (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec)
		return key;
	}
	public static KeySpec loadRSAKeySpecFromClasspath(final Path keyClasspahtLocation,
											   		  final CryptoKeySpecFactory keySpecFactory) {
		try {
			// TODO use ResourceLoader
			InputStream inputStream = CryptoKeys.class.getClassLoader()
													  .getResourceAsStream(keyClasspahtLocation.asRelativeString());		// BEWARE! relative

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			byte[] byteArray = buffer.toByteArray();

			KeySpec keySpec = keySpecFactory.from(byteArray);		// private key: PKCS8EncodedKeySpec
																	// public key:  X509EncodedKeySpec
			return keySpec;
		} catch (IOException ioEx) {
			log.error("Error while trying to load the key: {}",
					  ioEx.getMessage(),ioEx);
		}
		return null;	// !!
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE KEY: PKCS8EncodedKeySpec
/////////////////////////////////////////////////////////////////////////////////////////
	public static CryptoKeySpecFactory PRIVATE_KEY_SPEC_FACTORY = new CryptoKeySpecFactory() {
																		@Override
																		public KeySpec from(final byte[] byteArray) {
																			return new PKCS8EncodedKeySpec(byteArray);
																		}
														    	  };
	public static CryptoKeyFactoryFromSpec<RSAPrivateKey> PRIVATE_KEY_FROM_SPEC_FACTORY = new CryptoKeyFactoryFromSpec<RSAPrivateKey>() {
																										@Override
																										public RSAPrivateKey from(final KeySpec keySpec) {
																											try {
																												return (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(keySpec);
																											} catch (InvalidKeySpecException ikExc) {
																												log.error("Error on creating PRIVATE KEY: {}",
																														  ikExc.getMessage(),ikExc);
																											} catch (NoSuchAlgorithmException nsaExc) {
																												log.error("Error on creating PRIVATE KEY: {}",
																														  nsaExc.getMessage(),nsaExc);
																											}
																											return null;
																										}
																						   };
/////////////////////////////////////////////////////////////////////////////////////////
//	PUBLIC KEY: X509EncodedKeySpec
/////////////////////////////////////////////////////////////////////////////////////////
	public static CryptoKeySpecFactory PUBLIC_KEY_SPEC_FACTORY = new CryptoKeySpecFactory() {
																		@Override
																		public KeySpec from(final byte[] byteArray) {
																			return new X509EncodedKeySpec(byteArray);
																		}
														   		 };
	public static CryptoKeyFactoryFromSpec<RSAPublicKey> PUBLIC_KEY_FROM_SEPC_FACTORY = new CryptoKeyFactoryFromSpec<RSAPublicKey>() {
																										@Override
																										public RSAPublicKey from(final KeySpec keySpec) {
																											try {
																												return (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(keySpec);
																											} catch (InvalidKeySpecException ikExc) {
																												log.error("Error on creating PRIVATE KEY: {}",
																														  ikExc.getMessage(),ikExc);
																											} catch (NoSuchAlgorithmException nsaExc) {
																												log.error("Error on creating PRIVATE KEY: {}",
																														  nsaExc.getMessage(),nsaExc);
																											}
																											return null;
																										}
																						 };

}
