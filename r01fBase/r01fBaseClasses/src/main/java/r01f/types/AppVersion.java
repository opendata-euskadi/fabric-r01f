package r01f.types;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@Immutable
@ConvertToDirtyStateTrackable
@MarshallType(as="version")
@Accessors(prefix="_")
@NoArgsConstructor
@AllArgsConstructor
public class AppVersion 
  implements CanBeRepresentedAsString,
  			 Serializable {

	private static final long serialVersionUID = 3935180518462516439L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private int _major;
	@Getter @Setter private int _minor;
	@Getter @Setter private int _patch;
	@Getter @Setter private String _alias;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@GwtIncompatible
	public static AppVersion fromString(final String appVersionStr) {
		return AppVersion.valueOf(appVersionStr);
	}
	@GwtIncompatible
	public static AppVersion from(final String appVersionStr) {
		return AppVersion.valueOf(appVersionStr);
	}
	@GwtIncompatible
	public static AppVersion valueOf(final String appVersionStr) {
		return new DefaultAppVersionParser()
						.parse(appVersionStr);
	}
	@GwtIncompatible
	public static boolean canBe(final String appVersionStr) {
		return new DefaultAppVersionParser()
						.canBe(appVersionStr);
	}
	public AppVersion(final int major,final int minor,final int path) {
		this(major,minor,path,
			 null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PARSING
/////////////////////////////////////////////////////////////////////////////////////////	
	public interface AppVersionParser {
		public boolean canBe(final String appVersionStr);
		public AppVersion parse(final String appVersionStr);		
	}
	@GwtIncompatible	// not gwt-compatible BUT can be emulated (see [r01fbGWTClasses])
	public static class DefaultAppVersionParser 
			 implements AppVersionParser {
		private static final Pattern APP_VERSION_PATTERN = Pattern.compile("([0-9]+).([0-9]+).([0-9]+)(-[0-9a-zA-Z_])?");
		
		@Override
		public boolean canBe(final String appVersionStr) {
			if (Strings.isNullOrEmpty(appVersionStr)) return false;
			return APP_VERSION_PATTERN.matcher(appVersionStr).find();
		}
		@Override
		public AppVersion parse(final String appVersionStr) {
			AppVersion outAppVersion = null;
			if (Strings.isNullOrEmpty(appVersionStr)) _notValidAppVersionString(appVersionStr);
			Matcher m = APP_VERSION_PATTERN.matcher(appVersionStr);
			if (m.find()) {
				int major = Integer.parseInt(m.group(1));
				int minor = Integer.parseInt(m.group(2));
				int patch = Integer.parseInt(m.group(3));
				String alias = m.groupCount() == 4 ? m.group(4) : null;
				outAppVersion = new AppVersion(major,minor,patch,
											   alias);
			} 
			if (outAppVersion == null) _notValidAppVersionString(appVersionStr);
			return outAppVersion;
		}
		private static void _notValidAppVersionString(final String appVersionStr) {
			throw new IllegalArgumentException(appVersionStr + " is NOT a valid version strign (does NOT match " + APP_VERSION_PATTERN);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasSameMajorVersion(final AppVersion other) {
		return _major == other.getMajor();
	}
	public boolean hasGreaterMajorVersion(final AppVersion other) {
		return _major > other.getMajor();
	}
	public boolean hasSameMinorVersion(final AppVersion other) {
		return _minor == other.getMinor();
	}
	public boolean hasGreaterMinorVersion(final AppVersion other) {
		return _minor > other.getMinor();
	}
	public boolean hasSamePatchLevel(final AppVersion other) {
		return _patch == other.getPatch();
	}
	public boolean hasGreaterPathLevel(final AppVersion other) {
		return _patch > other.getPatch();
	}
	public boolean hasSameAlias(final AppVersion other) {
		return _alias != null ? _alias.equals(other.getAlias())
							  : other.getAlias() != null ? false
									  					 : true;	// both null
	}
	public boolean isSameAs(final AppVersion other) {
		return this.hasSameMajorVersion(other)
			&& this.hasSameMinorVersion(other)
			&& this.hasSamePatchLevel(other);
	}
	public boolean isGreaterThan(final AppVersion other) {
		if (this.hasGreaterMajorVersion(other)) return true;
		if (this.hasSameMajorVersion(other) 
		 && this.hasGreaterMinorVersion(other)) return true;
		if (this.hasSameMajorVersion(other)
		 && this.hasSameMinorVersion(other)
		 && this.hasGreaterPathLevel(other)) return true;
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return Strings.customized("{}.{}.{}{}",
								  _major,_minor,_patch,
								  Strings.isNOTNullOrEmpty(_alias) ? "-" + _alias : "");
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (!(other instanceof AppVersion)) return false;
		
		AppVersion otherVer = (AppVersion)other;
		return this.getMajor() == otherVer.getMajor()
			&& this.getMinor() == otherVer.getMinor()
			&& this.getPatch() == otherVer.getPatch()
			&& Objects.equal(this.getAlias(),otherVer.getAlias());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_major,_minor,_patch,
								_alias);
	}
	
}
