package r01f.client.api.delegates.s3;

import java.util.Collection;

import com.amazonaws.services.s3.AmazonS3;

import r01f.api.interfaces.s3.S3ServiceForFolderFiler;
import r01f.api.interfaces.s3.impl.S3ServiceForFolderFilerImpl;
import r01f.file.FileNameAndExtension;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectSummaryItem;
import r01f.s3.filer.model.FileFilter;
import r01f.types.Path;

public class S3ClientAPIDelegateForFolderFiler
  implements S3ServiceForFolderFiler {
///////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	protected final S3ServiceForFolderFilerImpl _serviceForFolderFilerImpl;

///////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////////////////
	public S3ClientAPIDelegateForFolderFiler(final AmazonS3 s3Client){
		_serviceForFolderFilerImpl = new  S3ServiceForFolderFilerImpl(s3Client);
	}
///////////////////////////////////////////////////////////////////////////////////////////
//METHODS
///////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsFolder(final S3BucketName bucket,
								final Path path,
								final boolean physicallyExistenceCheck) {
		return _serviceForFolderFilerImpl.existsFolder(bucket,
													   path,
													   physicallyExistenceCheck);
	}
	@Override
	public boolean copyFolder(final S3BucketName bucket,
							  final Path srcPath,final Path dstPath,
							  final FileFilter fileFilter,
							  final boolean overwrite){
		return _serviceForFolderFilerImpl.copyFolder(bucket,
													 srcPath,dstPath,
													 fileFilter,
													 overwrite);
	}
	@Override
	public boolean moveFolder(final S3BucketName bucket,
							  final Path srcPath,final Path dstPath,
							  final boolean overwrite){
		return _serviceForFolderFilerImpl.moveFolder(bucket,
													 srcPath,dstPath,
													 overwrite);
	}
	@Override
	public boolean renameFolder(final S3BucketName bucket,
								final Path existingPath,
								final FileNameAndExtension newName){
		return _serviceForFolderFilerImpl.renameFolder(bucket,
													   existingPath,newName);
	}
	@Override
	public boolean createFolder(final S3BucketName bucket,
								final Path path) {
		return _serviceForFolderFilerImpl.createFolder(bucket,
													   path);
	}
	@Override
	public boolean deleteFolder(final S3BucketName bucket,
								final Path path) {
		return _serviceForFolderFilerImpl.deleteFolder(bucket,
													   path);
	}
	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket,final  Path folderPath,
															  final FileFilter fileFilter,
															  final boolean recursive)  {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,
															 folderPath,
															 fileFilter,
															 recursive);
	}
	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket,final  Path folderPath,
															  final boolean recursive)  {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,
															 folderPath,
															 null,
															 recursive);
	}

	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket, final Path folderPath,
															  final FileFilter fileFilter, final  boolean recursive, final boolean excludeFolderTypes) {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,
															 folderPath,
															 fileFilter,
															 recursive, excludeFolderTypes);
	}

	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket, final Path folderPath,
															  final  boolean recursive, final boolean excludeFolderTypes) {
		return _serviceForFolderFilerImpl.listFolderContents(bucket,
															 folderPath,
															 null,
															 recursive, excludeFolderTypes);
	}

	@Override
	public Collection<S3ObjectSummaryItem> listBucketContents(final S3BucketName bucket,
															  final FileFilter fileFilter,
															  final boolean recursive) {
		return _serviceForFolderFilerImpl.listBucketContents(bucket,
															 fileFilter,
															 recursive);
	}

}
