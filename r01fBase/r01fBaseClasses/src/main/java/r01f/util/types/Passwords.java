package r01f.util.types;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.google.common.io.BaseEncoding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.PasswordHash;

/**
 * Hash passwords for storage, and test passwords against password tokens.
 * Instances of this class can be used concurrently by multiple threads.
 * @see <a href="http://stackoverflow.com/a/2861125/3474">StackOverflow</a>
 */
public final class Passwords {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Each token produced by this class uses this identifier as a prefix.
	 */
	public static final String ID = "31";
	/**
	 * The minimum recommended cost, used by default
	 */
	public static final int DEFAULT_COST = 16;

	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final int SIZE = 128;
	private static final Pattern LAYOUT = Pattern.compile("^\\$" + ID + "\\$" + 	// ${ID}${cost}$BASE64{hash}
														  "(\\d\\d?)" + "\\$" +
														  "(.{43})$");	
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	private final SecureRandom _random;
	private final int _cost;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS                                                                           
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Create a password manager with a default cost (the exponential computational cost of hashing a password, 0 to 30)
	 */
	public Passwords() {
		this(DEFAULT_COST);
	}
	/**
	 * Create a password manager with a specified cost
	 * @param cost the exponential computational cost of hashing a password, 0 to 30
	 */
	public Passwords(final int cost) {
		_iterations(cost); /* Validate cost */
		_cost = cost;
		_random = new SecureRandom();
	}
	/**
	 * Create a password manager with a default cost (the exponential computational cost of hashing a password, 0 to 30)
	 */
	public static Passwords createWithDefaultCost() {
		return new Passwords();
	}
	/**
	 * Create a password manager with a specified cost
	 * @param cost the exponential computational cost of hashing a password, 0 to 30
	 */
	public static Passwords createWithCost(final int cost) {
		return new Passwords(cost);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Hash a password for storage.
	 * @param password
	 * @return a secure authentication token to be stored for later authentication
	 */
	public PasswordHash hash(final Password password) {
		byte[] salt = new byte[SIZE / 8];
		_random.nextBytes(salt);
		byte[] dk = _pbkdf2(password.toCharArray(), 
							salt,
							1 << _cost);
		byte[] hash = new byte[salt.length + dk.length];
		System.arraycopy(salt,0,hash,0, salt.length);
		System.arraycopy(dk,0,hash,salt.length,dk.length);
		
		// Return ${ID}${cost}$BASE64{hash}
//		Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding(); // JDK 1.8
//		String token = '$' + ID + '$' + _cost + '$' +
//					   enc.encodeToString(hash);		
		String token = '$' + ID + '$' + _cost + '$' + 
					   BaseEncoding.base64Url().encode(hash)
					   						   .substring(0,43); // substring to remove padding characters, Base64 do it automatically with function withoutPadding()
		return new PasswordHash(token);
	}
	/**
	 * Authenticate with a password and a stored password token.
	 * @param password the received password
	 * @param token contains the password hash encoded like ${ID}${cost}$BASE64{hash}
	 * @return true if the password and token match
	 */
	public boolean authenticate(final Password password,
								final PasswordHash token) {		// token is encoded like ${ID}${cost}$BASE64{hash}
		// decode the token
		PasswordHashToken tokenDecoded = _decodeToken(token);
		int iterations = _iterations(tokenDecoded.getCost());
		byte[] hash = tokenDecoded.getHash();
		
		// check
		byte[] salt = Arrays.copyOfRange(hash,0,SIZE / 8);
		byte[] check = _pbkdf2(password.toCharArray(), 
							   salt,
							   iterations);
		int zero = 0;
		for (int idx = 0; idx < check.length; ++idx) {
			zero |= hash[salt.length + idx] ^ check[idx];
		}
		return zero == 0;
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	private class PasswordHashToken {
		@Getter private final int _cost;
		@Getter private final byte[] _hash;
	}
	private PasswordHashToken _decodeToken(final PasswordHash token) {	// token is encoded like ${ID}${cost}$BASE64{hash}
		// Get the hash from the stored token
		Matcher m = LAYOUT.matcher(token.asString());
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid token format");
		}
		int cost = Integer.parseInt(m.group(1));
		
		String base64hash = m.group(2);
//		byte[] hash = Base64.getUrlDecoder().decode(m.group(2)); // JDK 1.8
		byte[] hash = BaseEncoding.base64Url()
								  .decode(base64hash);
		return new PasswordHashToken(cost,
									hash);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	private static byte[] _pbkdf2(final char[] password,
								  final byte[] salt,
								  final int iterations) {
		KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
			return f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
		} catch (InvalidKeySpecException ex) {
			throw new IllegalStateException("Invalid SecretKeyFactory", ex);
		}
	}
	private static int _iterations(final int cost) {
		if ((cost < 0) || (cost > 30)) {
			throw new IllegalArgumentException("cost: " + cost);
		}
		return 1 << cost;
	}
}