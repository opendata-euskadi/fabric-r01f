package r01f.types;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.tag.TagList;

@MarshallType(as="pathCollectionItem")
public class PathCollectionItem 
	 extends IsPathCollectionItem<Path> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public PathCollectionItem() {
		// default no-args constructor
	}
	public PathCollectionItem(final Path path) {
		super(path);
	}
	public PathCollectionItem(final PathCollectionItemID id,final Path path) {
		super(id,path);
	}
	public PathCollectionItem(final Path path,
							  final TagList<String> tags) {
		super(path,tags);
	}
	public PathCollectionItem(final PathCollectionItemID id,final Path path,
							  final TagList<String> tags) {
		super(id,path,tags);
	}
}
