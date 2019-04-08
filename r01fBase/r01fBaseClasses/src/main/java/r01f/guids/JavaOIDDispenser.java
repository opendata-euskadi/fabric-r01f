package r01f.guids;

import java.util.UUID;

public class JavaOIDDispenser {
	/**
	 * Generates a GUID
	 * @return 
	 */
	public static String generateGUID() {
		UUID uuid = UUID.randomUUID();	
        return uuid.toString();
	}
}
