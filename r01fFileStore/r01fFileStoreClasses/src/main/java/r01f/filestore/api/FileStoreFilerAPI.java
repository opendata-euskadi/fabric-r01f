package r01f.filestore.api;

import java.io.IOException;

import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.types.Path;

public interface FileStoreFilerAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a folder properties
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public FileProperties getFolderProperties(final Path path) throws IOException;
	/**
	 * Checks if a folder exists
	 * @param path
	 * @return
	 */
	public boolean existsFolder(final Path path) throws IOException;
	/**
	 * Copies a folder
	 * @param srcPath
	 * @param dstPath
	 * @param fileFilter
	 * @return
	 * @throws IOException
	 */
	public boolean copyFolder(final Path srcPath,final Path dstPath,
							  final FileFilter fileFilter,
							  final boolean overwrite) throws IOException;
	/**
	 * Moves a folder
	 * @param srcPath
	 * @param dstPath
	 * @param fileFilter
	 * @param overwrite
	 * @return
	 * @throws IOException
	 */
	public boolean moveFolder(final Path srcPath,final Path dstPath,
							  final boolean overwrite) throws IOException; 
	/**
	 * Renames a folder
	 * @param existingPath
	 * @param newName
	 * @return
	 * @throws IOException
	 */
	public boolean renameFolder(final Path existingPath,
						  		final FileNameAndExtension newName) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a dir
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean createFolder(final Path path) throws IOException;
	/**
	 * Deletes a directory no matter if it's not empty
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean deleteFolder(final Path path) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  LIST
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Lists a dir's contents, 1st level or complete subdirectories if recursive mode to true.
	 * @param folderPath
	 * @param filter the filter, <code>null</code> if filter is not applied.
	 * @param recursive if <code>true</code> explore all subdir files.
	 * @return files and dirs
	 * @throws IOException
	 */
	public FileProperties[] listFolderContents(final Path folderPath,
											   final FileFilter fileFilter,
											   final boolean recursive) throws IOException;
}
