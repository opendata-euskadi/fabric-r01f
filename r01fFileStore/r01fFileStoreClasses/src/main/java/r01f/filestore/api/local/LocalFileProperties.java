package r01f.filestore.api.local;

import java.io.File;
import java.io.IOException;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.file.FileProperties;
import r01f.file.FilePropertiesBase;
import r01f.file.util.Files;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Path;

@MarshallType(as="localFileProperties")
@ConvertToDirtyStateTrackable
@Slf4j
@Accessors(prefix="_")
public class LocalFileProperties 
	 extends FilePropertiesBase {
	
	private static final long serialVersionUID = -5407160921534936414L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileProperties from(final File file) throws IOException {
		// transform into FileProperties
		FileProperties outProperties = new LocalFileProperties();
		outProperties.setPath(Path.from(file));
		outProperties.setSymLink(Files.symLinkTarget(file));
		outProperties.setFolder(file.isDirectory());
		if (!file.isDirectory()) outProperties.setSize(file.length());
		outProperties.setModificationTimeStamp(file.lastModified());
		outProperties.setAccessTimeStamp(file.lastModified());
		outProperties.setGroup(null);	// not possible until java 7
		outProperties.setOwner(null);	// not possible until java 7
		outProperties.setPermission(null);	// not possible until java 7
		return outProperties;
	}
	public static FileProperties fromOrNull(final File file) {
		FileProperties outProps = null;
		try {
			outProps = LocalFileProperties.from(file);
		} catch (IOException ioEx) {
			log.error("Error creating a {} from {}: {}",
					  LocalFileProperties.class,file.getPath(),
					  ioEx.getMessage(),ioEx);
		}
		return outProps;
	}
}
