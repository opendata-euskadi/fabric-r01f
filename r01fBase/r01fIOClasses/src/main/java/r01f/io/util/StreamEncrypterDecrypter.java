package r01f.io.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.ByteStreams;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(prefix="_")
public class StreamEncrypterDecrypter {
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTANT ( this are paired, if one is used....)
/////////////////////////////////////////////////////////////////////////////////////////
	public static String DEFAULT_ALGORITHM = "AES/ECB/PKCS5Padding";
	public static String DEFAULT_KEY_SPEC = "PBKDF2WithHmacSHA1";
	private static final byte[] DEFAULT_SALT = { (byte) 0x16, (byte) 0x33, (byte) 0x11,
                                                 (byte) 0x12, (byte) 0xee, (byte) 0x33, (byte) 0x12, (byte) 0x12, };
/////////////////////////////////////////////////////////////////////////////////////////
// FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private final SecretKey _secretKey;
    private final Cipher _cipher;
/////////////////////////////////////////////////////////////////////////////////////////
// OTHER CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
    public StreamEncrypterDecrypter(final char[] key){
		try {
			/*SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(DEFAULT_SALT);*/
			KeySpec spec = new PBEKeySpec(key, DEFAULT_SALT, 65536, 256); // AES-256
			SecretKeyFactory f= SecretKeyFactory.getInstance(DEFAULT_KEY_SPEC);
			byte[] keyEncoded = f.generateSecret(spec).getEncoded();
			_secretKey = new SecretKeySpec(keyEncoded, "AES");
			_cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
		} catch (final Throwable thrs) {
			 throw new RuntimeException(thrs);
		}
    }
/////////////////////////////////////////////////////////////////////////////////////////
// METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    public  InputStream encrypt (final InputStream stream) {
        try {
          _cipher.init(Cipher.ENCRYPT_MODE, _secretKey);
          byte[] bytes = _cipher.doFinal(ByteStreams.toByteArray(stream));
          return new ByteArrayInputStream(bytes);
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
  	}

     public  InputStream decrypt (final InputStream stream) {
        try {
          _cipher.init(Cipher.DECRYPT_MODE, _secretKey);
          byte[] bytes = _cipher.doFinal(ByteStreams.toByteArray(stream));
          return new ByteArrayInputStream(bytes);
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
  	}
}