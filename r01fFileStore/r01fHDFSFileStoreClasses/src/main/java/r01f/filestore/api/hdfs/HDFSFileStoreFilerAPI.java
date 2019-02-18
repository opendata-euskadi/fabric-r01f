package r01f.filestore.api.hdfs;

import java.io.IOException;
import java.util.Collection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class HDFSFileStoreFilerAPI
	 extends HDFSFileStoreAPIBase
  implements FileStoreFilerAPI {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Common checkings
	 */
	protected final FileStoreChecksDelegate _check;
	
	protected final HDFSFileStoreAPI _api;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HDFSFileStoreFilerAPI(final Configuration conf) throws IOException {
		super(conf);
		_api = new HDFSFileStoreAPI(_fs,this); // reuse the filesystem
		_check = new FileStoreChecksDelegate(_api, this);
		
	}
	HDFSFileStoreFilerAPI(final FileSystem fs,
						  final HDFSFileStoreAPI fileApi) throws IOException {
		super(fs);
		_api = fileApi;
		_check = new FileStoreChecksDelegate(fileApi,
					  					  	 this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FileProperties getFolderProperties(final r01f.types.Path path) throws IOException {
		// check
		_check.checkFolderExists(path);
		Path theHDFSFilePath = HDFSFileStoreAPIBase.r01fPathToHDFSPath(path);
		FileStatus file = _fs.getFileStatus(theHDFSFilePath);
		return HDFSFileProperties.from(file);
	}
	@Override
	public boolean existsFolder(r01f.types.Path path) throws IOException {
		// exists?
    	Path theHDFSFilePath = HDFSFileStoreAPIBase.r01fPathToHDFSPath(path);
		return _fs.exists(theHDFSFilePath) && _fs.isDirectory(theHDFSFilePath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean copyFolder(final r01f.types.Path srcPath,final r01f.types.Path dstPath,
    						  final FileFilter fileFilter,
    						  final boolean overwrite) throws IOException {
    	log.trace("Copying from {} to {}",
    			  srcPath,dstPath);    	

		// check
		_check.checkBeforeCopyFolder(srcPath,dstPath,
									 overwrite);
		
		boolean copyFolderStateOK = true;
		
		if (fileFilter != null) {
			
			// copy folder applying filter (recursive)
			FileProperties[] filteredFiles = this.listFolderContents(srcPath,fileFilter,
																	 false);	// not recursive
			for (int i=0; i<filteredFiles.length; i++) {
				FileProperties currentFileOrFolder = filteredFiles[i];

				r01f.types.Path effectiveDstPath = dstPath.joinedWith(currentFileOrFolder.getPath()
																						 .remainingPathFrom(srcPath));
				if (currentFileOrFolder.isFile()) {
					boolean copyFileStateOK = _api.copyFile(filteredFiles[i].getPath(),effectiveDstPath, 
															overwrite);
					if (!copyFileStateOK) copyFolderStateOK = false;
				} else {
					return this.copyFolder(currentFileOrFolder.getPath(),effectiveDstPath, 
										   fileFilter,
										   overwrite);
				}
			}
			
		} else {
			// copy folder without applying filter
	    	// If needed: The method FileUtils.stat2Path convert an array of FileStatus to an array of Path
	        copyFolderStateOK = FileUtil.copy(_fs,HDFSFileStoreAPIBase.r01fPathToHDFSPath(srcPath),
	    									  _fs,HDFSFileStoreAPIBase.r01fPathToHDFSPath(dstPath),
	    									  false, 		// delete source
	    									  overwrite,	// overwrite
	    									  _conf);
		}
		
        return copyFolderStateOK;
    }
	@Override
    public boolean moveFolder(final r01f.types.Path srcPath,final r01f.types.Path dstPath,
    						  final boolean overwrite) throws IOException {
    	log.trace("Moving folder from {} to {}",
    			  srcPath,dstPath);

		// check
		_check.checkBeforeMoveFolder(srcPath,dstPath,
									 overwrite);

		// copy
    	// If needed: The method FileUtils.stat2Path convert an array of FileStatus to an array of Path
        boolean copyFileStateOK = FileUtil.copy(_fs,HDFSFileStoreAPIBase.r01fPathToHDFSPath(srcPath),
        										_fs,HDFSFileStoreAPIBase.r01fPathToHDFSPath(dstPath),
        										true, 		// delete source
        										overwrite,	// overwrite
        										_conf);
        return copyFileStateOK;
    }
	@Override
	public boolean renameFolder(final r01f.types.Path existingPath,final FileNameAndExtension newName) throws IOException {
    	log.trace("Renaming folder from {} to {}",
    			  existingPath,newName);


		r01f.types.Path dstPath = r01f.types.Path.from(existingPath.withoutLastPathElement())
						  						 .joinedWith(newName);

		// check
		_check.checkBeforeMoveFolder(existingPath,dstPath,
									 false);		// DO NOT overwrite

		// rename
		return _fs.rename(HDFSFileStoreAPIBase.r01fPathToHDFSPath(existingPath),
				   		  HDFSFileStoreAPIBase.r01fPathToHDFSPath(dstPath));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean createFolder(final r01f.types.Path path) throws IOException {
		log.trace("Creating a folder at {}",
				   path);

		if (this.existsFolder(path)) return true;	// already created

		// check
		_check.checkBeforeCreateFolder(path);
		return _fs.mkdirs(HDFSFileStoreAPIBase.r01fPathToHDFSPath(path));
    }
	@Override
    public boolean deleteFolder(final r01f.types.Path path) throws IOException {
		log.trace("Deleting a folder at {}",
				  path);

		if (!this.existsFolder(path)) return true;	// does NOT exists

		// check
		_check.checkBeforeRemoveFolder(path);

		// delete
		boolean opState = _fs.delete(HDFSFileStoreAPIBase.r01fPathToHDFSPath(path),
									 true);		// recursive
		return opState;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public FileProperties[] listFolderContents(final r01f.types.Path folderPath,
    										   final FileFilter fileFilter,
    										   final boolean recursive) throws IOException {
    	log.debug("Listing folder {} contents",
    			  folderPath);

		// check
		_check.checkBeforeListFolderContents(folderPath);
		
		// filter
		PathFilter hdfsPathFilter = null;
		if (fileFilter != null) {
			hdfsPathFilter = new PathFilter() {
									@Override
									public boolean accept(final Path path) {
										r01f.types.Path r01fPath = HDFSFileStoreAPIBase.hdfsPathToR01FPath(path);
										return fileFilter.accept(r01fPath);				
									}
							};
		}
		// list
		FileProperties[] out = null;
        if (recursive) {
        	HDFSFolderRecursiveListing recursiveList = new HDFSFolderRecursiveListing(_fs,
        																			  fileFilter);
			Collection<FileProperties> allContents = recursiveList.recurseFolderContents(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath));
			out = CollectionUtils.hasData(allContents)
						? allContents.toArray(new FileProperties[allContents.size()])
						: null;
        } else {
        	FileStatus[] statusFiles = null;
        	if (hdfsPathFilter != null) {
        		statusFiles = _fs.listStatus(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath),
        									 hdfsPathFilter);
        	} else {
        		statusFiles = _fs.listStatus(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath));
        	}
			if (CollectionUtils.hasData(statusFiles)) {
				out = FluentIterable.from(statusFiles)
						  .transform(new Function<FileStatus,FileProperties>() {
											@Override
											public FileProperties apply(final FileStatus hdfsFile) {
												return HDFSFileProperties.fromOrNull(hdfsFile);
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
//	PROPERTIES (see FileStoreFileAPI)
/////////////////////////////////////////////////////////////////////////////////////////	
//	@Override
//	public FileProperties getFolderProperties(r01f.types.Path folderPath) throws IOException {
//		log.trace("Folder {} properties {}",
//				  folderPath);
//
//		if (!this.existsFolder(folderPath)) throw new IOException("Folder at " + folderPath + " DOES NOT exists!");
//		
//		FileStatus fs = _fs.getFileStatus(_pathToHDFSPath(folderPath));        
//        FileProperties outProps = HDFSFileProperties.from(fs);
//        if (!outProps.isFolder()) throw new IllegalArgumentException(folderPath + " is NOT a folder path; it's a " + 
//        															 (outProps.isFile() ? "file"
//        																	 		   : outProps.isSymLink() ? "symlink" : "unknown"));
//
//        return outProps;
//	}
}
