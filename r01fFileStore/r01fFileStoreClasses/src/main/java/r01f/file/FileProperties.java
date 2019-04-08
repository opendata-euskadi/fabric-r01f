package r01f.file;

import java.io.Serializable;
import java.util.Date;

import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.UserGroupCode;
import r01f.types.Path;

public interface FileProperties 
		 extends Debuggable,
		 		 Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////	
	public Path getPath();
	public void setPath(final Path path);
/////////////////////////////////////////////////////////////////////////////////////////
//  DIR
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isFile();
	public boolean isFolder();
	public void setFolder(final boolean folder);
/////////////////////////////////////////////////////////////////////////////////////////
// 	SYMLINK
/////////////////////////////////////////////////////////////////////////////////////////
	public Path getSymLink();
	public void setSymLink(final Path path);
	public boolean isSymLink();
/////////////////////////////////////////////////////////////////////////////////////////
//  LENGTH
/////////////////////////////////////////////////////////////////////////////////////////
	public long getSize();
	public void setSize(final long size);
	public String getSizeFormatted();
	
	public long getModificationTimeStamp();
	public void setModificationTimeStamp(final long ts);
	public Date getModificationDate();
	
	public long getCreateTimeStamp();
	public void setCreateTimeStamp(final long ts);
	public Date getCreateDate();
	
	public long getAccessTimeStamp();
	public void setAccessTimeStamp(final long ts);
	public Date getAccessDate();
	
	public UserCode getOwner();
	public void setOwner(final UserCode userCode);
	
	public UserGroupCode getGroup();
	public void setGroup(final UserGroupCode group);
	
	public FilePermission getPermission();
	public void setPermission(final FilePermission perm);
}
