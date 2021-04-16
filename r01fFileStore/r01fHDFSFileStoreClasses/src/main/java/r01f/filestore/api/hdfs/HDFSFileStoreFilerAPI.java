package r01f.filestore.api.hdfs;

import java.io.IOException;
import java.util.Collection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
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
import r01f.types.IsPath;
import r01f.types.TimeLapse;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class HDFSFileStoreFilerAPI
	 extends HDFSFileStoreAPIBase
  implements FileStoreFilerAPI {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final HDFSFileStoreAPI _api;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HDFSFileStoreFilerAPI(final Configuration conf) {
		this(conf,
			 null);		// no credentials refresh period
	}
	public HDFSFileStoreFilerAPI(final Configuration conf,
								 final TimeLapse credentialsRefreshPeriod) {
		super(new HDFSFileSystemProvider(conf,
										 credentialsRefreshPeriod));
		_api = new HDFSFileStoreAPI(this.getHDFSFileSystemProvider(),this); // reuse the filesystem provider
		_check = new FileStoreChecksDelegate(_api,this);
		
	}
	HDFSFileStoreFilerAPI(final HDFSFileSystemProvider fsProvider,
						  final HDFSFileStoreAPI fileApi) {
		super(fsProvider);
		_api = fileApi;
		_check = new FileStoreChecksDelegate(fileApi,
					  					  	 this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public FileProperties getFolderProperties(final r01f.types.Path path) throws IOException {
		FileProperties outProps = null;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				// check
				_check.checkFolderExists(path);
				Path theHDFSFilePath = HDFSFileStoreAPIBase.r01fPathToHDFSPath(path);
				FileStatus file = this.getHDFSFileSystem()
									  .getFileStatus(theHDFSFilePath);
				outProps = HDFSFileProperties.from(file);
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		
		return outProps;
	}
	@Override @SuppressWarnings("resource")
	public boolean existsFolder(final r01f.types.Path path) throws IOException {
		boolean outExists = false;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				// exists?
		    	Path theHDFSFilePath = HDFSFileStoreAPIBase.r01fPathToHDFSPath(path);
				outExists = this.getHDFSFileSystem().exists(theHDFSFilePath) 
						 && this.getHDFSFileSystem().isDirectory(theHDFSFilePath);
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return outExists;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
    public boolean copyFolder(final r01f.types.Path srcPath,final r01f.types.Path dstPath,
    						  final FileFilter fileFilter,
    						  final boolean overwrite) throws IOException {
		boolean copyFolderStateOK = false;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
		    	log.trace("Copying from {} to {}",
		    			  srcPath,dstPath);    	
		
				// check
				_check.checkBeforeCopyFolder(srcPath,dstPath,
											 overwrite);
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
							copyFolderStateOK = copyFileStateOK;
						} else {
							copyFolderStateOK = this.copyFolder(currentFileOrFolder.getPath(),effectiveDstPath, 
												   				fileFilter,
												   				overwrite);
						}
					}
				} else {
					// copy folder without applying filter
			    	// If needed: The method FileUtils.stat2Path convert an array of FileStatus to an array of Path
			        copyFolderStateOK = FileUtil.copy(this.getHDFSFileSystem(),HDFSFileStoreAPIBase.r01fPathToHDFSPath(srcPath),
			    									  this.getHDFSFileSystem(),HDFSFileStoreAPIBase.r01fPathToHDFSPath(dstPath),
			    									  false, 		// delete source
			    									  overwrite,	// overwrite
			    									  this.getHDFSConfiguration());
				}
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return copyFolderStateOK;
    }
	@Override @SuppressWarnings("resource")
    public boolean moveFolder(final r01f.types.Path srcPath,final r01f.types.Path dstPath,
    						  final boolean overwrite) throws IOException {
		boolean copyFileStateOK = false;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
		    	log.trace("Moving folder from {} to {}",
		    			  srcPath,dstPath);
		
				// check
				_check.checkBeforeMoveFolder(srcPath,dstPath,
											 overwrite);
		
				// copy
		    	// If needed: The method FileUtils.stat2Path convert an array of FileStatus to an array of Path
		        copyFileStateOK = FileUtil.copy(this.getHDFSFileSystem(),HDFSFileStoreAPIBase.r01fPathToHDFSPath(srcPath),
		        								this.getHDFSFileSystem(),HDFSFileStoreAPIBase.r01fPathToHDFSPath(dstPath),
		        								true, 		// delete source
		        								overwrite,	// overwrite
		        								this.getHDFSConfiguration());
				// ------------------------------------------------------------
		        retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
        return copyFileStateOK;
    }
	@Override @SuppressWarnings("resource")
	public boolean renameFolder(final r01f.types.Path existingPath,final FileNameAndExtension newName) throws IOException {
		boolean outRenamed = false;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
		    	log.trace("Renaming folder from {} to {}",
		    			  existingPath,newName);
		
				@SuppressWarnings("cast")
				r01f.types.Path dstPath = r01f.types.Path.from((IsPath)existingPath.withoutLastPathElement())
								  						 .joinedWith(newName);
		
				// check
				_check.checkBeforeMoveFolder(existingPath,dstPath,
											 false);		// DO NOT overwrite
		
				// rename
				outRenamed = this.getHDFSFileSystem()
								 .rename(HDFSFileStoreAPIBase.r01fPathToHDFSPath(existingPath),
						   		  		 HDFSFileStoreAPIBase.r01fPathToHDFSPath(dstPath));
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return outRenamed;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
    public boolean createFolder(final r01f.types.Path path) throws IOException {
		boolean outCreated = false;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				log.trace("Creating a folder at {}",
						   path);
		
				if (this.existsFolder(path)) return true;	// already created
		
				// check
				_check.checkBeforeCreateFolder(path);
				outCreated = this.getHDFSFileSystem()
								 .mkdirs(HDFSFileStoreAPIBase.r01fPathToHDFSPath(path));
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return outCreated;
    }
	@Override @SuppressWarnings("resource")
    public boolean deleteFolder(final r01f.types.Path path) throws IOException {
		boolean opState = false;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				log.trace("Deleting a folder at {}",
						  path);
		
				if (!this.existsFolder(path)) return true;	// does NOT exists
		
				// check
				_check.checkBeforeRemoveFolder(path);
		
				// delete
				opState = this.getHDFSFileSystem()
							  .delete(HDFSFileStoreAPIBase.r01fPathToHDFSPath(path),
									  true);		// recursive
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return opState;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
    public FileProperties[] listFolderContents(final r01f.types.Path folderPath,
    										   final FileFilter fileFilter,
    										   final boolean recursive) throws IOException {
		FileProperties[] out = null;
		
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
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
		        if (recursive) {
		        	HDFSFolderRecursiveListing recursiveList = new HDFSFolderRecursiveListing(this.getHDFSFileSystem(),
		        																			  fileFilter);
					Collection<FileProperties> allContents = recursiveList.recurseFolderContents(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath));
					out = CollectionUtils.hasData(allContents)
								? allContents.toArray(new FileProperties[allContents.size()])
								: null;
		        } else {
		        	FileStatus[] statusFiles = null;
		        	if (hdfsPathFilter != null) {
		        		statusFiles = this.getHDFSFileSystem()
		        						  .listStatus(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath),
		        									  hdfsPathFilter);
		        	} else {
		        		statusFiles = this.getHDFSFileSystem()
		        						  .listStatus(HDFSFileStoreAPIBase.r01fPathToHDFSPath(folderPath));
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
				// ------------------------------------------------------------
		        retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		
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
