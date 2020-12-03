package r01f.filestore.api.hdfs;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.file.FileAction;
import r01f.file.FilePermission;
import r01f.file.FileProperties;
import r01f.file.FilePropertiesBase;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.UserGroupCode;

@MarshallType(as="hdfsFileProperties")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
@Slf4j
public class HDFSFileProperties 
	 extends FilePropertiesBase {
	
	private static final long serialVersionUID = -5407160921534936414L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public static FileProperties from(final FileStatus hdfsStatus) throws IOException {		
		// transform into FileProperties
		FileProperties outProperties = new HDFSFileProperties();
		outProperties.setPath(r01f.types.Path.from(Path.getPathWithoutSchemeAndAuthority(hdfsStatus.getPath())));
		if (hdfsStatus.isSymlink()) outProperties.setSymLink(r01f.types.Path.from(Path.getPathWithoutSchemeAndAuthority(hdfsStatus.getSymlink())));
		outProperties.setFolder(hdfsStatus.isDirectory());
		outProperties.setSize(hdfsStatus.getLen());
		outProperties.setModificationTimeStamp(hdfsStatus.getModificationTime());
		outProperties.setAccessTimeStamp(hdfsStatus.getAccessTime());
		outProperties.setGroup(UserGroupCode.forId(hdfsStatus.getGroup()));
		outProperties.setOwner(LoginID.forId(hdfsStatus.getOwner()));
		outProperties.setPermission(new FilePermission(FileAction.fromSymbol(hdfsStatus.getPermission().getUserAction().SYMBOL),
													   FileAction.fromSymbol(hdfsStatus.getPermission().getGroupAction().SYMBOL),
													   FileAction.fromSymbol(hdfsStatus.getPermission().getOtherAction().SYMBOL),
													   hdfsStatus.getPermission().getStickyBit()));		
		return outProperties;
    }
    public static FileProperties fromOrNull(final FileStatus hdfsStatus) {
		FileProperties outProps = null;
		try {
			outProps = HDFSFileProperties.from(hdfsStatus);
		} catch (IOException ioEx) {
			log.error("Error creating a {} from {}: {}",
					  HDFSFileProperties.class,hdfsStatus.getPath(),
					  ioEx.getMessage(),ioEx);
		}
		return outProps;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public long getCreateTimeStamp() {
		log.debug("HDFS does NOT stores the creation time stamp");
		return 0;
	}
	@Override
	public void setCreateTimeStamp(final long ts) {
		log.debug("HDFS does NOT stores the creation time stamp");
	}
}
