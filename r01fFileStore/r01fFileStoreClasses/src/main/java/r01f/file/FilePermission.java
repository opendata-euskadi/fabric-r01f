package r01f.file;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="filePermission")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class FilePermission
  implements Debuggable,
  			 Serializable {
	
	private static final long serialVersionUID = 3871233841722811189L;
/////////////////////////////////////////////////////////////////////////////////////////
//	POSIX permission style  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="user",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FileAction _userAction = null;
	
	@MarshallField(as="group",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FileAction _groupAction = null;
	
	@MarshallField(as="other",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FileAction _otherAction = null;
	
	@MarshallField(as="sticky",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _stickyBit = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern UNIX_PERMISSION_PATTERN = Pattern.compile("([rwx-]{3})([rwx-]{3})([rwx-]{3})");
	/**
	 * Creates a {@link FilePermission} object from a unix-like permission string
	 * as rw-rw-rwx
	 * @param str
	 * @return
	 */
	public static FilePermission createFromUNIXPermissionString(final String str) {
		Matcher m = UNIX_PERMISSION_PATTERN.matcher(str);
		if (m.find()) {
			FileAction userAction = FileAction.fromUnix(m.group(1));
			FileAction groupAction = FileAction.fromUnix(m.group(2));
			FileAction otherAction = FileAction.fromUnix(m.group(3));
			return new FilePermission(userAction,
									  groupAction,
									  otherAction,
									  false);
		}
		return null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (this == other) return true;
		if ( !(other instanceof FilePermission) ) return false;
		
		FilePermission otherPerm = (FilePermission)other;
		return this.getUserAction() == otherPerm.getUserAction()
			&& this.getGroupAction() == otherPerm.getGroupAction()
			&& this.getOtherAction() == otherPerm.getOtherAction()
			&& this.isStickyBit() == otherPerm.isStickyBit();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("user: {} / group: {} / other: {} / sticky: {}",
								  _userAction,_groupAction,_otherAction,
								  _stickyBit);
	}
}
