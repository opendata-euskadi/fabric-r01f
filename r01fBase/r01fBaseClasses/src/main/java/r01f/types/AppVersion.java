package r01f.types;

import java.util.Comparator;
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
import r01f.guids.OID;
import r01f.guids.VersionID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Immutable
@ConvertToDirtyStateTrackable
@MarshallType(as="version")
@Accessors(prefix="_")
@NoArgsConstructor
@AllArgsConstructor
public class AppVersion 
  implements CanBeRepresentedAsString,
  			 VersionID {

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
	public AppVersion(final String appVersionStr) {
		if (Strings.isNullOrEmpty(appVersionStr)) throw new IllegalArgumentException(appVersionStr + " is NOT a valid version strign (does NOT match " + APP_VERSION_PATTERN);
		Matcher m = APP_VERSION_PATTERN.matcher(appVersionStr);
		if (m.find()) {
			_major = Integer.parseInt(m.group(1));
			_minor = Integer.parseInt(m.group(2));
			_patch = Integer.parseInt(m.group(3));
			_alias = m.groupCount() == 4 ? m.group(4) : null;
		} else {
			throw new IllegalArgumentException(appVersionStr + " is NOT a valid version strign (does NOT match " + APP_VERSION_PATTERN);
		}
	}
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
	private static final transient Pattern APP_VERSION_PATTERN = Pattern.compile("([0-9]+).([0-9]+).([0-9]+)(-[0-9a-zA-Z_])?");
	
	public interface AppVersionParser {
		public boolean canBe(final String appVersionStr);
		public AppVersion parse(final String appVersionStr);		
	}
	@GwtIncompatible	// not gwt-compatible BUT can be emulated (see [r01fbGWTClasses])
	public static class DefaultAppVersionParser 
			 implements AppVersionParser {
		
		@Override
		public boolean canBe(final String appVersionStr) {
			if (Strings.isNullOrEmpty(appVersionStr)) return false;
			return APP_VERSION_PATTERN.matcher(appVersionStr).find();
		}
		@Override
		public AppVersion parse(final String appVersionStr) {
			AppVersion outAppVersion = new AppVersion(appVersionStr);
			return outAppVersion;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasSameMajorVersion(final AppVersion other) {
		if (other == null) return false;
		
		return _major == other.getMajor();
	}
	public boolean hasGreaterMajorVersion(final AppVersion other) {
		return _major > other.getMajor();
	}
	public boolean hasSameMinorVersion(final AppVersion other) {
		if (other == null) return false;
		
		return _minor == other.getMinor();
	}
	public boolean hasGreaterMinorVersion(final AppVersion other) {
		if (other == null) return true;
		
		return _minor > other.getMinor();
	}
	public boolean hasSamePatchLevel(final AppVersion other) {
		if (other == null) return false;
		
		return _patch == other.getPatch();
	}
	public boolean hasGreaterPathLevel(final AppVersion other) {
		if (other == null) return true;
		
		return _patch > other.getPatch();
	}
	public boolean hasSameAlias(final AppVersion other) {
		if (other == null) return false;
		
		return _alias != null ? _alias.equals(other.getAlias())
							  : other.getAlias() != null ? false
									  					 : true;	// both null
	}
	public boolean isSameAs(final AppVersion other) {
		if (other == null) return false;
		
		return this.hasSameMajorVersion(other)
			&& this.hasSameMinorVersion(other)
			&& this.hasSamePatchLevel(other);
	}
	public boolean isGreaterThan(final AppVersion other) {
		if (other == null) return true;
		
		if (this.hasGreaterMajorVersion(other)) return true;
		if (this.hasSameMajorVersion(other) 
		 && this.hasGreaterMinorVersion(other)) return true;
		if (this.hasSameMajorVersion(other)
		 && this.hasSameMinorVersion(other)
		 && this.hasGreaterPathLevel(other)) return true;
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPARATOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static transient Comparator<AppVersion> COMPARATOR = _createCompartor();
	
	protected static <V extends AppVersion> Comparator<V> _createCompartor() {
		return new Comparator<V>() {
					@Override
					public int compare(final V v1,final V v2) {
						if (v1.isSameAs(v2)) return 0;
						if (v1.isGreaterThan(v2)) return 1;
						return -1;
					}
				};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TO STRING
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
/////////////////////////////////////////////////////////////////////////////////////////
//	VersionID
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public <O extends OID> boolean is(final O other) {
		return this.equals(other);
	}
	@Override
	public <O extends OID> boolean isNOT(final O other) {
		return !this.is(other);
	}
	@Override @SuppressWarnings("unchecked") 
	public <O extends OID> boolean isContainedIn(final O... oids) {
		if (CollectionUtils.isNullOrEmpty(oids)) return false;
		boolean isContained = false;
		for (O oid : oids) {
			if (this.is(oid)) {
				isContained = true;
				break;
			}
		}
		return isContained;
	}
	@Override @SuppressWarnings("unchecked") 
	public <O extends OID> boolean isNOTContainedIn(final O... oids) {
		return !this.isContainedIn(oids);
	}
	@Override
	public <O extends OID> boolean isContainedIn(final Iterable<O> oids) {
		if (oids == null) return false;
		boolean isContained = false;
		for (O oid : oids) {
			if (this.is(oid)) {
				isContained = true;
				break;
			}
		}
		return isContained;
	}
	@Override
	public <O extends OID> boolean isNOTContainedIn(final Iterable<O> oids) {
		return !this.isContainedIn(oids);
	}
	@Override
	public <O extends OID> boolean isIgnoringCase(final O other) {
		return this.is(other);
	}
	@Override
	public boolean isValid() {
		return AppVersion.canBe(this.asString());
	}
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O cast() {
		return (O)this;
	}
	@Override
	public int compareTo(final OID o) {
		if (!(o instanceof AppVersion)) return -1;
		AppVersion ov = (AppVersion)o;
		return COMPARATOR.compare(this,ov);
	}
}
