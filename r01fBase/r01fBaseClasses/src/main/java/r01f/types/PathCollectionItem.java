package r01f.types;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasID;
import r01f.facets.Tagged;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.facets.delegates.TaggeableDelegate;
import r01f.guids.OID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.tag.TagList;

@MarshallType(as="pathCollectionItem")
@Accessors(prefix="_")
public class PathCollectionItem<P extends IsPath> 
  implements HasPath<P>,
  			 HasID<PathCollectionItemID>,
  			 HasTaggeableFacet<String> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="path")
	@Getter @Setter	protected P _path;

	@MarshallField(as="id")
    @Getter @Setter	protected PathCollectionItemID _id;

	@MarshallField(as="tags",
				   whenXml=@MarshallFieldAsXml(collectionElementName="tag"))
	@Getter @Setter protected TagList<String> _tags;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public PathCollectionItem() {
		// default no-args constructor
	}
	public PathCollectionItem(final P path) {
		_id = PathCollectionItemID.forId(PathCollectionItemID.supplyId());
		_path = path;
	}
	public PathCollectionItem(final PathCollectionItemID id,final P path) {
		_id = id;
		_path = path;
	}
	public PathCollectionItem(final P path,
							  final TagList<String> tags) {
		this(path);
		_tags = tags;
	}
	public PathCollectionItem(final PathCollectionItemID id,final P path,
							  final TagList<String> tags) {
		this(id,path);
		_tags = tags;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Tagged<String> asTaggeable() {
		return new TaggeableDelegate<String,PathCollectionItem<P>>(this);
	}
	@Override
	public void unsafeSetId(final OID id) {
		_id = (PathCollectionItemID) id;
	}

}
