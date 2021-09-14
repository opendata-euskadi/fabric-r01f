package r01f.filestore.api.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import r01f.file.FileProperties;
import r01f.file.util.FolderWalker;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileFilters;
import r01f.types.Path;

/**
 * Recursive file listing under a specified directory.
 */
final class LocalFolderRecursiveListing 
	extends FolderWalker<FileProperties> {	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LocalFolderRecursiveListing(final FileFilter filter) {
		super(filter,
			  -1);		// no depth limit
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Recursively walk a directory tree and return a List of all
	 * files found, the list is sorted using File.compareTo().
	 * @param startingFolder is a valid directory, which can be read.
	 * @throws FileNotFoundException
	 */
	public Collection<FileProperties> recurseFolderContents(final File startingFolder) throws IOException {
		_validateDirectory(startingFolder);
		
		Collection<FileProperties> results = Lists.newArrayList();
		this.walk(Path.from(startingFolder),
				  results);
		return results;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected FileProperties getFolderProperties(final Path folderPath) throws IOException {
		File f = new File(folderPath.asAbsoluteString());
		if (f.exists()
		 && f.isDirectory()) {
			return LocalFileProperties.from(f);
		} 
		throw new IllegalArgumentException(folderPath + " is NOT a folder!");
	}
	@Override
	protected Collection<FileProperties> listFolderContents(final Path folderPath,
															final FileFilter filter) throws IOException {
		File f = new File(folderPath.asAbsoluteString());
		File[] files = f.listFiles(FileFilters.ioFileFilterFor(filter));
		return files != null ? FluentIterable.from(files)
											 .transform(new Function<File,FileProperties>() {
																@Override
																public FileProperties apply(final File file) {
																	return LocalFileProperties.fromOrNull(file);
																}
											 			})
											 .filter(Predicates.notNull())
											 .toList()
							 : Lists.<FileProperties>newArrayList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    protected void _handleFolderStart(final FileProperties folderProps,final int depth,
    							      final Collection<FileProperties> results) throws IOException {
		results.add(folderProps);
    }
	@Override
    protected void _handleFile(final FileProperties fileProps,final int depth,
    						   final Collection<FileProperties> results) throws IOException {
    	results.add(fileProps);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 * @param aDirectory the dir.
	 * @throws IOException
	 */
	private static void _validateDirectory(final File aDirectory) throws IOException {
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
