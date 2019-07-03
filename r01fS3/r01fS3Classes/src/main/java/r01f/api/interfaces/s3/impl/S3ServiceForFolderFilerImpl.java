package r01f.api.interfaces.s3.impl;

import static r01f.s3.filer.model.FolderPath.DELIMITER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.s3.S3ServiceForFolderFiler;
import r01f.file.FileNameAndExtension;
import r01f.httpclient.HttpResponseCode;
import r01f.s3.S3BucketName;
import r01f.s3.S3ObjectKey;
import r01f.s3.S3ObjectSummaryItem;
import r01f.s3.filer.model.FileFilter;
import r01f.s3.filer.model.FolderPath;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class S3ServiceForFolderFilerImpl
	 extends S3ServiceBaseImpl
  implements S3ServiceForFolderFiler {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public S3ServiceForFolderFilerImpl(final AmazonS3 s3Client)  {
		super(s3Client);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks:
	 * 	 > Checks the folder has a logical existence within a hierarchy as part of the key of a file.
	 *   > Checks the folder has a physical existence with certain characteristics, the key has the common delimiter, it has 0 bytes.
	 */
	@Override
	public boolean existsFolder(final S3BucketName bucketName,
								final Path path,
								final boolean  physicallyExistenceCheck)  {
		log.warn("\nCheck if folder '{}'  exist into bucket '{}' ", path, bucketName );
	    //	Checks the folder has a logical existence within a hierarchy as part of the key of a file.
		boolean folderExistsLogically = CollectionUtils.hasData(listFolderContents(bucketName, path, null, false));
		if  (! physicallyExistenceCheck  ) {//|| (physicallyExistenceCheck && !folderExistsLogically ) ) {
			return folderExistsLogically;
		}
		 //	Checks the folder has a physical existence with certain characteristics, the key has the common delimiter, it has 0 bytes.
		FolderPath folderPath = FolderPath.forPath(path);
		try {
		    ObjectMetadata objectMetadata =
						_s3Client.getObjectMetadata(bucketName.asString(), folderPath.asString());
		    if (! objectMetadata.getContentType().equals(Mimetypes.MIMETYPE_OCTET_STREAM)) {
		    	log.warn("..content type of supposed folder '{}' is not octect stream", path.asString() );
		    	return false;
		    }
		    if (objectMetadata.getContentLength() > 0) {
		    	log.warn("..content length of supposed folder '{}' has more than 0 bytes...", path.asString() );
		    	return false;
		    }
		    if (! folderPath.asString().contains(FolderPath.DELIMITER)) {
		    	log.warn("...Not delimiter found '{}'",folderPath.asString());
		    	return false;
		    }
		} catch (final AmazonS3Exception s3Exception) {
			if ( HttpResponseCode.of(s3Exception.getStatusCode()) == HttpResponseCode.NOT_FOUND ) {
				log.warn(" Not found folder {}", path.asString());
				return false;
			} else {
				log.error(" Error {}",s3Exception.getErrorMessage());
				throw s3Exception;
			}
		}
		return true;
	}
	@Override
	public boolean copyFolder(final S3BucketName bucket,
							  final Path srcPath,final Path dstPath,
							  final FileFilter fileFilter,
							  final boolean overwrite) {
		throw new UnsupportedOperationException(">No implemented yet");
	}

	@Override
	public boolean moveFolder(final S3BucketName bucket,
							  final Path srcPath,final Path dstPath,
							  final boolean overwrite)  {
		throw new UnsupportedOperationException(">No implemented yet");
	}

	@Override
	public boolean renameFolder(final S3BucketName bucket,
								final Path existingPath,final FileNameAndExtension newName) {
		throw new UnsupportedOperationException(">No implemented yet");
	}

	@Override
	public boolean createFolder(final S3BucketName bucket,
								final Path path)  {
		Collection<FolderPath> fp = FolderPath.getAllFoldersForPath(path);
		for (FolderPath folder : fp ) {
			if ( ! existsFolder(bucket, Path.valueOf(folder.asString()), true)) {
				 log.warn(" '{}' folder does not exist, so will be created",folder);
				_s3Client.putObject(_buildObjectRequestForEmptyFolder(bucket, folder));
			}
		}
		return true;
	}

	@Override
	public boolean deleteFolder(final S3BucketName bucket,
								final Path path) {
		throw new UnsupportedOperationException(">No implemented yet");
	}



	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket, final Path folderPath, boolean recursive) {
		return listFolderContents(bucket,folderPath,null,recursive);
	}
	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket,  final Path folderPath,
			                                                  boolean recursive, boolean excludeFolderTypes) {
		return listFolderContents(bucket,folderPath,null,recursive,excludeFolderTypes);
	}



	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket,
			                                                  final Path folderPath,final FileFilter fileFilter,
			                                                  final boolean recursive,  final boolean excludeFolderTypes){
		FolderPath prefix = FolderPath.forPath(folderPath);
		ListObjectsRequest req = _buildListObjectsRequest(bucket,prefix);
		ObjectListing listing = _s3Client.listObjects(req);
		List<S3ObjectSummaryItem> results = Lists.newArrayList();
		// First, Filter folder results and its children if requested (recursive)
		Collection<S3ObjectSummaryItem> folderResults = _listFolderContentsOfTypeFolder(listing,bucket,prefix);
		if (CollectionUtils.hasData(folderResults)){
			if (!excludeFolderTypes) {
				results.addAll(folderResults);
			}
			if (recursive) {
				for (S3ObjectSummaryItem folder : folderResults ) {
					results.addAll(listFolderContents(bucket,  Path.valueOf(folder.getKey().asString()), null, recursive,excludeFolderTypes));
				}
			}
		}
		// Filter file results.
		Collection<S3ObjectSummaryItem> fileResults = _listFolderContentsOfTypeFile(listing,bucket,prefix);
		if (CollectionUtils.hasData(fileResults)){
			results.addAll(fileResults);
		}
		return results;
	}

	@Override
	public Collection<S3ObjectSummaryItem> listFolderContents(final S3BucketName bucket,
			                                                  final Path folderPath,final FileFilter fileFilter,
			                                                  final boolean recursive){
		return  listFolderContents(bucket,folderPath,fileFilter,recursive,false);
	}

	@Override
	public Collection<S3ObjectSummaryItem> listBucketContents(final S3BucketName bucket,
															  final FileFilter fileFilter,
			                                                  final boolean recursive) {
		return this.listFolderContents(bucket,
									   Path.valueOf("/"),
									   fileFilter,
									   recursive);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds a PutObjectRequest for a empty folder.
	 * @param bucket
	 * @param path
	 * @return
	 */
	private PutObjectRequest _buildObjectRequestForEmptyFolder(final S3BucketName bucket,
															  final FolderPath path)  {
	    InputStream stream = new ByteArrayInputStream(new byte[0]);
	    ObjectMetadata metadata = new ObjectMetadata();
	    metadata.setContentLength(0);
		return new PutObjectRequest(bucket.asString(),
									path.asString(), stream, new ObjectMetadata());

	}
	/**
	 * This builds a List Object Reques for a given folderpath.
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	private ListObjectsRequest _buildListObjectsRequest(final S3BucketName bucket,
													   final FolderPath folderPath) {
		ListObjectsRequest req =
				(folderPath.asString()
							.equalsIgnoreCase(DELIMITER))
									// Root CASE, that means..bucket level.
									? new ListObjectsRequest()
												.withBucketName(bucket.asString())
												.withDelimiter(DELIMITER)
									// not root
									: new ListObjectsRequest()
							                      .withBucketName(bucket.asString())
							                      .withPrefix(folderPath.asString())
							                      .withDelimiter(DELIMITER);
	   return req;

	}
	/**
	 * From a ObjectListing result, gets the folder objects.
	 * @param listing
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	private Collection<S3ObjectSummaryItem> _listFolderContentsOfTypeFolder(final ObjectListing listing ,
																		   final S3BucketName bucket,
                                                                           final FolderPath folderPath) {
		// First, Filter folder results and its children if requested (recursive)
		List<S3ObjectSummaryItem> folderResults = null;
		if (CollectionUtils.hasData(listing.getCommonPrefixes())){
			folderResults = FluentIterable.from(listing.getCommonPrefixes())
									.transform(new Function<String,S3ObjectSummaryItem>() {
														@Override
														public S3ObjectSummaryItem apply(final String input) {
															 S3ObjectSummaryItem folderItem = new S3ObjectSummaryItem();
														     folderItem.setBucketName(bucket);
														     folderItem.setKey(S3ObjectKey.forId(input));
														     folderItem.setIsFolder(true);
														     return folderItem;
														}
												})
									.toList();
		}
		return folderResults;
	}
	/**
	 * From a ObjectListing result, gets the file objects.
	 * @param listing
	 * @param bucket
	 * @param folderPath
	 * @return
	 */
	private Collection<S3ObjectSummaryItem> _listFolderContentsOfTypeFile(final ObjectListing listing ,
																		 final S3BucketName bucket,
                                                                         final FolderPath folderPath){
		Collection<S3ObjectSummaryItem> fileResults = null;
		if (CollectionUtils.hasData(listing.getObjectSummaries())){
			List<S3ObjectSummary> summary = listing.getObjectSummaries();

            // Remove root folder (prefix) , returned as object.
			// First , find the root element.
			Collection<S3ObjectSummary> toRemove = Collections2.filter(summary,
																	   new Predicate<S3ObjectSummary>() {
																						@Override
																						public boolean apply(final S3ObjectSummary input) {

																							return input.getKey().equalsIgnoreCase(folderPath.asString());
																						}
																		});
			// ..and then, remove from summary ( "toRemove" is a list with just one element)
			summary.removeAll(toRemove);

			fileResults = FluentIterable.from(summary)
								.transform(new Function<S3ObjectSummary,S3ObjectSummaryItem>(){
													@Override
													public S3ObjectSummaryItem apply(final S3ObjectSummary input) {
														 S3ObjectSummaryItem folderItem = new S3ObjectSummaryItem();
													     folderItem.setBucketName(bucket);
													     folderItem.setKey(S3ObjectKey.forId(input.getKey()));
													     folderItem.setIsFolder(false);
													     return folderItem;
													}
											})
								.toList();
		}
		return fileResults;
	}

}
