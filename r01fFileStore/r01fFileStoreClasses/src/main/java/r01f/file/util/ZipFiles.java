package r01f.file.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.filestore.api.local.LocalFileStoreFilerAPI;
import r01f.io.Streams;
import r01f.types.Path;

/**
 * ZIP with a folder contents
 * <pre class='brush:java'>
 * 		// using the local file-store
 * 		ZipFiles zips = new ZipFiles();
 * 		zips.zip(path,
 * 				 os);		// the output stream where the zip will be written
 * </pre>
 */
@Slf4j
public class ZipFiles {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreAPI _fsApi;
	private final FileStoreFilerAPI _fsFilerApi;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public ZipFiles() throws IOException {
		this(new LocalFileStoreAPI(),
			 new LocalFileStoreFilerAPI());
	}
	@SuppressWarnings("unused")
	public ZipFiles(final FileStoreAPI fsApi,final FileStoreFilerAPI fsFilerApi) throws IOException {
		_fsApi = fsApi;
		_fsFilerApi = fsFilerApi;
	}
	public static ZipFiles using(final FileStoreAPI fsApi,final FileStoreFilerAPI fsFilerApi) throws IOException {
		return new ZipFiles(fsApi,fsFilerApi);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ZIP 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Zips a folder contents 
	 * @param folder
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public void zip(final Path folder,
					final OutputStream os) throws IOException {
		this.walk(folder,
				  -1,			// depth limit = -1 (no limit)
				  null,			// no file filter
				  os);	
	}
	/**
	 * Zips a folder contents 
	 * @param folder
	 * @param fileFilter
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public void zip(final Path folder,
					final FileFilter fileFilter,
					final OutputStream os) throws IOException {
		this.walk(folder,
				  -1,			// depth limit = -1 (no limit)
				  fileFilter,
				  os);	
	}
	/**
	 * Zips a folder contents 
	 * @param folder
	 * @param depthLimit
	 * @param fileFilter
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public void zip(final Path folder,
					final int depthLimit,
					final FileFilter fileFilter,
					final OutputStream os) throws IOException {
		this.walk(folder,
				  depthLimit,
				  fileFilter,
				  os);	
	}
    protected final void walk(final Path startFolderPath,
    						  final int deepthLimit,
    						  final FileFilter fileFilter,
    						  final OutputStream os) throws IOException {
        if (startFolderPath == null) throw new NullPointerException("Start folder is null");
        FileProperties starFolderProps = _fsFilerApi.getFolderProperties(startFolderPath);
        if (!starFolderProps.isFolder()) throw new IllegalArgumentException("Path to zip " + startFolderPath + " is NOT a folder!");
        
        // Create the ZipOutputStream
        ZipOutputStream zipOS = null;
        try {
        	// create the zip os
        	zipOS = new ZipOutputStream(os);
	        // walk
	        _walk(startFolderPath,
	        	  starFolderProps,0,
	        	  deepthLimit,
	        	  fileFilter,
	        	  zipOS);
        } finally {
        	if (zipOS != null) {
        		try {
        			zipOS.flush();
        			zipOS.close();
        		} catch (IOException ioEx) {
        			log.error("Error closing ZIP output stream: {}",
        					  ioEx.getMessage(),ioEx);
        		}
        	}
        }
    }
    private void _walk(final Path startFolderPath,
    				   final FileProperties folderProps,final int depth,
    				   final int depthLimit, 
    				   final FileFilter fileFilter,
    				   final ZipOutputStream zipOS) throws IOException {
        int childDepth = depth + 1;
        if (depthLimit < 0 || childDepth <= depthLimit) {
            FileProperties[] folderChildren = _fsFilerApi.listFolderContents(folderProps.getPath(),
																  		   fileFilter,
																  		   false);	// not recursive 
            // give an opportunity to filter folder contents
            folderChildren = _filterFolderContents(folderProps,depth,
            									   folderChildren);
            for (FileProperties childProps : folderChildren) {
                if (childProps.isFolder()) {	
                	// is folder >> BEWARE!! recursion
                    _walk(startFolderPath,
                    	  childProps,childDepth,
                    	  depthLimit,
                    	  fileFilter,
                    	  zipOS);		
                } 
                else {						
                	// ... it's a file >> create a zip file entry
			    	Path zipEntryPath = childProps.getPath()
			    								  .remainingPathFrom(startFolderPath);
			        ZipEntry zipEntry = new ZipEntry(zipEntryPath.asRelativeString());
			        zipOS.putNextEntry(zipEntry);
			        
			        // add the file to the entry
			        InputStream fileIS = _fsApi.readFromFile(childProps.getPath());	        
					Streams.copy(fileIS,
								 zipOS,
								 false);	// do not close (a zipOS flush & close is needed)
					zipOS.flush();
			        zipOS.closeEntry();	        
			        fileIS.close();
                }
            }
        }
    }
    /**
     * Overridable method invoked with the contents of each folder.
     * @param folderProps  the current folder being processed
     * @param depth  the current folder level (starting folder = 0)
     * @param files the files (possibly filtered) in the folder
     * @return the filtered list of files
     * @throws IOException if an I/O Error occurs
     */
    protected static FileProperties[] _filterFolderContents(final FileProperties folderProps,final int depth,
    										  	     		final FileProperties[] files) throws IOException {
        return files;	// returns the files unchanged
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	UNZIP                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * unzips a file to a given path
	 * @param zIS
	 * @param dstPath
	 * @return
	 * @throws IOException
	 */
	public void unzip(final InputStream zIS,
					  final Path dstPath) throws IOException {
		// check that the destination is NOT a file
		if (_fsApi.existsFile(dstPath))	throw new IllegalArgumentException("Cannot extract ZIP to " + dstPath + " since it's an existent FILE!!");
		
		ZipInputStream zipIS = new ZipInputStream(zIS);
		ZipEntry zipEntry = zipIS.getNextEntry();
		while (zipEntry != null) {
			// BEWARE! a file entry can come before the directory entry where the file is located
			// i.e.:
			//   /foo/foo.txt
			//   /foo/
			if (zipEntry.isDirectory()) {
				// next entry
				zipEntry = zipIS.getNextEntry();
				continue;
			}
			
			// it's a file...
			
			// a) get the file destination path
			Path filePathInsideZip = Path.from(zipEntry.getName());
			Path fileDstPath = dstPath.joinedWith(filePathInsideZip);
			
			// b) Extract the file
			NonCloseableInputStreamWrapper nonCloseableZipIS = new NonCloseableInputStreamWrapper(zipIS);
			_fsApi.writeToFile(nonCloseableZipIS,
							   fileDstPath,
							   true);		// overwrite
			nonCloseableZipIS.close();		
			
			// next entry
			zipEntry = zipIS.getNextEntry();
		}
		zipIS.close();
	}  
}
