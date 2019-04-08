package r01f.util;

import com.google.common.annotations.GwtIncompatible;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.patterns.Memoized;

@GwtIncompatible
public enum OSType 
 implements EnumExtended<OSType> {
	WINDOWS,
	MacOS,
	Linux,
	NIX,
	UNKNOWN;
	
	private static final EnumExtendedWrapper<OSType> WRAPPER = new EnumExtendedWrapper<OSType>(OSType.values());

	@Override
	public boolean isIn(final OSType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final OSType el) {
		return WRAPPER.is(this,el);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String OS_PROP = System.getProperty("os.name").toLowerCase();
	private static transient Memoized<OSType> OS = new Memoized<OSType>() {
															@Override
															protected OSType supply() {
																if (OS_PROP.indexOf("win") >= 0) return OSType.WINDOWS;
																if (OS_PROP.indexOf("mac") >= 0 || OS_PROP.indexOf("darwin") >= 0) return OSType.MacOS;
																if (OS_PROP.indexOf("nux") >= 0) return OSType.Linux;
																if (OS_PROP.indexOf("nix") >= 0 || OS_PROP.indexOf("aix") > 0 || OS_PROP.indexOf("sunos") >= 0) return OSType.NIX;
																throw new IllegalStateException("Unknown os.name property: " + OS_PROP + " cannot detect the OS");
															}
												  };
	public static OSType getOS() {
		return OS.get();
	}
}
