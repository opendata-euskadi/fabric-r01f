package r01f.filestore.api.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileFilters;
import r01f.util.types.collections.CollectionUtils;

/**
 * Recursive file listing under a specified directory.
 */
@Slf4j
final class LocalFolderRecursiveListing2 {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Recursively walk a directory tree and return a List of all
	 * files found, the list is sorted using File.compareTo().
	 * @param aStartingDir is a valid directory, which can be read.
	 * @param filter the filter, <code>null</code> if filter is not applied.
	 * @throws FileNotFoundException
	 */
	public Collection<FileProperties> recurseFolderContents(final File aStartingDir,
	                                 	   			 		final FileFilter filter) throws IOException {
		_validateDirectory(aStartingDir);
		
		Collection<FileProperties> result = _recurse(aStartingDir,
										   			 filter);
		return result;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private Collection<FileProperties> _recurse(final File aStartingDir,
	                                  			final FileFilter filter) throws IOException {
		Collection<FileProperties> outFiles = Lists.newArrayList();
		
		java.io.FileFilter ioFileFilter = FileFilters.ioFileFilterFor(filter);
		File[] filesAndDirsArray = aStartingDir.listFiles(ioFileFilter);
		
		if (CollectionUtils.hasData(filesAndDirsArray)) {
			Collection<File> filesAndDirs = Arrays.asList(filesAndDirsArray);
			
			for (File file : filesAndDirs) {
				FileProperties props = _filePropertiesOf(file);
				if (props == null) continue;
				
				outFiles.add(props); // always add, even if directory
				
				if (props.isFolder()) {
					// subfolder: recursive call!
					Collection<FileProperties> deeperList = _recurse(file,
													 	   			 filter);
					if (CollectionUtils.hasData(deeperList)) outFiles.addAll(deeperList);
				}
			}
		}
		return outFiles;
	}
	private FileProperties _filePropertiesOf(final File file) {
		FileProperties outProps = null;
		try {
			outProps = LocalFileProperties.from(file);
		} catch(IOException ioEx) {
			log.error("Error creating a {} from {}: {}",
					  LocalFileProperties.class,file.getPath(),
					  ioEx.getMessage(),ioEx);
		}
		return outProps;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 * @param aDirectory the dir.
	 * @throws IOException
	 */
	private void _validateDirectory(final File aDirectory) throws IOException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}
}
