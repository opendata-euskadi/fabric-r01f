package r01f.filestore.api.local;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileFilters;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class LocalFileStoreFilerAPI
	 extends LocalFileStoreAPIBase
  implements FileStoreFilerAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LocalFileStoreFilerAPI() throws IOException {
		_check = new FileStoreChecksDelegate(new LocalFileStoreAPI(this),
											 this);
	}
	LocalFileStoreFilerAPI(final LocalFileStoreAPI fileStoreApi) throws IOException {
		_check = new FileStoreChecksDelegate(fileStoreApi,
											 this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHECK
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FileProperties getFolderProperties(final Path path) throws IOException {
		// check
		_check.checkFolderExists(path);
		File f = new File(path.asAbsoluteString());
		return LocalFileProperties.from(f);
	}
	@Override
	public boolean existsFolder(final Path path) throws IOException {
		File f = new File(path.asAbsoluteString());
		return f.exists() && f.isDirectory();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean copyFolder(final Path srcPath,final Path dstPath,
    						  final FileFilter fileFilter,
    						  final boolean overwrite) throws IOException {

		// check
		_check.checkBeforeCopyFolder(srcPath,dstPath,
									 overwrite);

		// copy folder
		File src = new File(srcPath.asAbsoluteString());
		File dst = new File(dstPath.asAbsoluteString());
		if (fileFilter != null) {
			FileUtils.copyDirectory(src,dst,
									new java.io.FileFilter() {
											@Override
											public boolean accept(final File file) {
												return fileFilter.accept(Path.from(file));
											}
									},
									false);		// preserve file dates
		} else {
			FileUtils.copyDirectory(src,dst,false);
		}
		
		return true;
    }
	@Override
    public boolean moveFolder(final Path srcPath,final Path dstPath,
    						  final boolean overwrite) throws IOException {
		// check
		_check.checkBeforeMoveFolder(srcPath,dstPath,
									 overwrite);

		// move folder
		File src = new File(srcPath.asAbsoluteString());
		File dst = new File(dstPath.asAbsoluteString());

		Files.move(src,dst);
		return true;

    }
	@Override
	public boolean renameFolder(final Path existingPath,final FileNameAndExtension newName) throws IOException {
		Path dstPath = Path.from(existingPath.getPathElementsExceptLast())
						   .joinedWith(newName);
		return this.moveFolder(existingPath,dstPath,
							   false);		// DO NOT overwrite
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean createFolder(final Path path) throws IOException {
		if (this.existsFolder(path)) return true;	// already created

		// check
		_check.checkBeforeCreateFolder(path);

		// create the folder
		File f = new File(path.asAbsoluteString());
		Files.createParentDirs(f);

		return f.mkdir();
    }
	@Override
    public boolean deleteFolder(final Path path) throws IOException {
		if (!this.existsFolder(path)) return true;	// does NOT exists

		// check
		_check.checkBeforeRemoveFolder(path);

		// delete
		File file = new File(path.asAbsoluteString());
		if (!file.exists()) {
			log.warn("Could NOT delete folder at {} since it does NOT exists",path.asAbsoluteString());
			return false;
		}
		if (!file.isDirectory()) {
			log.warn("Could NOT delete file at {} since it's NOT a folder",path.asAbsoluteString());
			return false;
		}
		return FileUtils.deleteQuietly(file);		// maybe? FileUtils.deleteDirectory(folder)
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public FileProperties[] listFolderContents(final Path folderPath,
    										   final FileFilter fileFilter,
    										   final boolean recursive) throws IOException {		
		// check
		_check.checkBeforeListFolderContents(folderPath);
		
		// list
		File folder = new File(folderPath.asAbsoluteString());
		
		FileProperties[] out = null;
		if (recursive) {
			LocalFolderRecursiveListing recursiveList = new LocalFolderRecursiveListing(fileFilter);
			Collection<FileProperties> allContents = recursiveList.recurseFolderContents(folder);
			out = CollectionUtils.hasData(allContents)
						? allContents.toArray(new FileProperties[allContents.size()])
						: null;
		} else {
			java.io.FileFilter ioFileFilter = FileFilters.ioFileFilterFor(fileFilter);
			File[] files = folder.listFiles(ioFileFilter);
			if (CollectionUtils.hasData(files)) {
				out = FluentIterable.from(files)
						  .transform(new Function<File,FileProperties>() {
											@Override
											public FileProperties apply(final File f) {
												return LocalFileProperties.fromOrNull(f);
											}
						  			 })
						  .filter(Predicates.notNull())
						  .toArray(FileProperties.class);
			} else {
				out = new FileProperties[] { /* empty */ };
			}
		}
		return out;
    }

/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTIES see FileStoreFileAPI
/////////////////////////////////////////////////////////////////////////////////////////
//	@Override
//	public FileProperties getFolderProperties(final Path folderPath) throws IOException {
//		if (folderPath == null) throw new IllegalArgumentException("the FileID MUST NOT be null and it have to be a Path");
//
//		File dir = new File(folderPath.asAbsoluteString());
//		if (!dir.exists()) throw new IOException("Folder at " + folderPath + " DOES NOT exists!");
//		
//
//		FileProperties outProps = LocalFileProperties.from(dir);
//        if (!outProps.isFolder()) throw new IllegalArgumentException(folderPath + " is NOT a folder path; it's a " + 
//        															 (outProps.isFile() ? "file"
//        																	 		   : outProps.isSymLink() ? "symlink" : "unknown"));
//        return outProps;
//	}
}
