package r01f.internal;

import java.io.File;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.Environment;
import r01f.patterns.Memoized;
import r01f.types.Path;
import r01f.util.OSType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Environment variable that sets the R01_HOME location
 * In local-development environments this location is:
 * 		windows: R01_HOME = d:/develop/   or   c:/develop/
 * 		  linux: R01_HOME = /develop
 * The R01_HOME var is set as a JVM environment var:
 * 		-DR01_HOME=/develop
 *
 *  If the property is not set, a default value is used:
 *
 *
 *
 * This location is used in many places:
 * 		- Logs location (see r01f/r01fLogbackGlobal.xml)
 * 		- ...
 */
@Slf4j
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class R01HomeLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Path HOME_PATH = R01HomeLocation._guessDefaultHomeLocation();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guess the {@link Environment} value from a system wide property
	 * @return
	 */
	public static Path guessHomeLocationFromSystemEnvProp() {
		String homeLocProp = System.getProperty("R01_HOME");
		return Strings.isNOTNullOrEmpty(homeLocProp) ? Path.from(homeLocProp)
							   				     	 : DEFAULT_HOME_PATH.get();
	}
	private static Memoized<Path> DEFAULT_HOME_PATH = new Memoized<Path>() {
																@Override
																public Path supply() {
																	return _guessDefaultHomeLocation();
																}

													  };
	private static Path _guessDefaultHomeLocation() {
		Path outPath = null;
		OSType os = OSType.getOS();
		if (os.is(OSType.WINDOWS)) {
			// find the development home
			File[] roots = File.listRoots();
			if (CollectionUtils.hasData(roots)) {
				for (File root : roots) {
					Path devPath = Path.from(root)
									   .joinedWith("develop");
					File home = new File(devPath.asAbsoluteString());
					if (home.exists()) {
						outPath = Path.from(home);
						break;
					}
				}
			}
		}
		if (outPath == null) outPath = Path.from("/develop");
		log.info("R01_HOME jvm envrionment var was NOT set: the default value for {} is {}",
				 os,outPath);
		return outPath;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
