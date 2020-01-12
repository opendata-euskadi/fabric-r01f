package r01f.internal;

import java.nio.charset.Charset;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Charsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;


/**
 * Environment properties
 */
@Slf4j
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class R01F {
///////////////////////////////////////////////////////////////////////////////
// CONSTANTES
///////////////////////////////////////////////////////////////////////////////
	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";
	
	public static final Charset DEFAULT_CHARSET = Charsets.UTF_8; 
	
	public static final int CORE_GROUP = 0;
	
	@Deprecated
	public static final AppCode R01_APP_CODE = R01FAppCodes.R01_APP_CODE;
	
	@Deprecated
	public static final AppCode APP_CODE = R01_APP_CODE;
	
	@Deprecated
	public static final AppCode LEGACY_APP_CODE = R01FAppCodes.LEGACY_APP_CODE;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static void initSystemEnv() {
		log.warn("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		log.warn("Setting base system properties:");
		log.warn("networkaddress.cache.ttl=10");
		log.warn("file.encoding={}",DEFAULT_CHARSET.name());
		log.warn("mail.mime.charset={}",DEFAULT_CHARSET.name());
		log.warn("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		
		// set all encodings to UTF-8 (independent of the underlying OS) to provide a stable environment
		System.setProperty("file.encoding",DEFAULT_CHARSET.name());
		System.setProperty("mail.mime.charset",DEFAULT_CHARSET.name());
		
		// if DNS round robin based services are going to be used (like amazon S3 or a mail cluster),
		// don't forget to configure the DNS cache tiemout of Java (which is also infinite by default)
		java.security.Security.setProperty("networkaddress.cache.ttl","10"); 	// Only cache DNS lookups for 10 seconds
	}
}
