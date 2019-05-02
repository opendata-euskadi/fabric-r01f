package r01f.api.interfaces.s3;

import java.io.IOException;
import java.util.Collection;

import r01f.file.FileNameAndExtension;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectSummaryItem;
import r01f.s3.filer.model.FileFilter;
import r01f.types.Path;

public interface S3ServiceForFolderFiler {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks if a folder exists into bucket
	 * @param bucket
	 * @param path
	 * @param physicallyExistenceCheck :
	 *          - a folder can exist as a 0-bytes object into a bucker or
	 *          - just and only implicitly forming part of the name of an object foo/myfolder
	 * @return
	 */
	public boolean existsFolder(final S3BucketName bucket, final Path path, final boolean physicallyExistenceCheck);
    /**
     * Copies a folder
     * @param srcPath
     * @param dstPath
     * @param fileFilter
     * @return
     * @throws IOException
     */
    public boolean copyFolder(final S3BucketName bucket,
    		                  final Path srcPath,final Path dstPath,
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
    public boolean moveFolder(final S3BucketName bucket,
    		                  final Path srcPath,final Path dstPath,
    						  final boolean overwrite) ;
    /**
     * Renames a folder
     * @param existingPath
     * @param newName
     * @return
     * @throws IOException
     */
    public boolean renameFolder(final S3BucketName bucket,
    							final Path existingPath,
    					  		final FileNameAndExtension newName) ;
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a dir
     * @param path
     * @return
     * @throws IOException
     */
    public boolean createFolder(final S3BucketName bucket,
    		                    final Path path);
    /**
	 * Deletes a directory no matter if it's not empty
     * @param path
     * @return
     * @throws IOException
     */
    public boolean deleteFolder(final  S3BucketName bucket, final Path path) ;
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
    public Collection<S3ObjectSummaryItem> listFolderContents(final  S3BucketName bucket,
    											   			  final Path folderPath,
    										                  final FileFilter fileFilter,
    										                  final boolean recursive) ;
    /**
     * Lists the bucket contents .
     * @param filter the filter, <code>null</code> if filter is not applied.
     * @param recursive if <code>true</code> explore all subdir files.
     * @return files and dirs
     * @throws IOException
     */
    public Collection<S3ObjectSummaryItem> listBucketContents(final  S3BucketName bucket,
    										                  final FileFilter fileFilter,
    										                  final boolean recursive) ;

      /**
     * Lists a dir's contents, 1st level or complete subdirectories if recursive mode to true, but excluding folder types.
     * @param filter the filter, <code>null</code> if filter is not applied.
     * @param recursive if <code>true</code> explore all subdir files.
     * @return files and dirs
     * @throws IOException
     */
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket, final Path folderPath, final FileFilter fileFilter,
													          final boolean recursive, final boolean excludeFolderTypes);

	 /**
     * Lists a dir's contents, 1st level or complete subdirectories if recursive mode to true, but excluding folder types.
     * @param filter the filter, <code>null</code> if filter is not applied.
     * @param recursive if <code>true</code> explore all subdir files.
     * @return files and dirs
     * @throws IOException
     */
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket, final Path folderPath, boolean recursive);

	 /**
     * Lists a dir's contents, 1st level or complete subdirectories if recursive mode to true, but excluding folder types.
     * @param filter the filter, <code>null</code> if filter is not applied.
     * @param recursive if <code>true</code> explore all subdir files.
     * @return files and dirs
     * @throws IOException
     */
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket, final  Path folderPath, final  boolean recursive,
															  final boolean excludeFolderTypes);
}
