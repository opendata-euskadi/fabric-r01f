package r01f.s3.model.metadata;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import r01f.s3.S3ObjectMetadataItemId;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Transforms a s3 metadata model <> into a model object metadata
 */
public class ObjectMetaDataTransformer {
	/**
	 * Returns a ObjectMetaData from a S3 Metadata
	 * @param s3Metadata
	 * @return
	 */
	public static ObjectMetaData fromS3ObjectMetaData( final com.amazonaws.services.s3.model.ObjectMetadata s3Metadata) {
		ObjectMetaData metaData = new ObjectMetaData();

		if ( CollectionUtils.hasData(s3Metadata.getRawMetadata().keySet())){
			metaData.putAll(FluentIterable.from(s3Metadata.getRawMetadata().keySet())
									.transform(new Function <String,ObjectMetaDataItem>() {
													@Override
													public ObjectMetaDataItem apply(final String id) {
														ObjectMetaDataItem item = new ObjectMetaDataItem();
														item.setId(S3ObjectMetadataItemId.forId(id));
														item.setValue(Strings.customized("{}", s3Metadata.getRawMetadata().get(id)));
														return item;
													}
											  })
									.toList());

		}
		if ( CollectionUtils.hasData(s3Metadata.getUserMetadata().keySet())){
			metaData.putAll(FluentIterable.from(s3Metadata.getUserMetadata().keySet())
									.transform(new Function <String,ObjectMetaDataItem>(){
														@Override
														public ObjectMetaDataItem apply(final String id) {
															ObjectMetaDataItem item = new ObjectMetaDataItem();
															item.setId(S3ObjectMetadataItemId.forId(id));
															item.setValue(Strings.customized("{}", s3Metadata.getUserMetadata().get(id)));
															item.setUserDefinedCustomMetadata(true);
															return item;
														}
												})
									.toList());

		}
		return metaData;
	}
	/**
	 * Converts metadata to s3 object metadata.
	 * @param metadata
	 * @return
	 */
	public static com.amazonaws.services.s3.model.ObjectMetadata toS3ObjectMetaData(final ObjectMetaData metadata) {
		com.amazonaws.services.s3.model.ObjectMetadata s3Metadata
						= new com.amazonaws.services.s3.model.ObjectMetadata();
		//No way to use Guava Fluents here...

		for (ObjectMetaDataItem item : metadata.systemDefined() ){
			s3Metadata.setHeader(item.getId().asString(),
								 item.getValue());
		}
		for (ObjectMetaDataItem item : metadata.userDefined() ){
			s3Metadata.addUserMetadata(item.getId().asString(),
									   item.getValue());
		}
		return s3Metadata;
	}
}
