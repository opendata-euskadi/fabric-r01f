package r01f.s3.model.metadata;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.s3.S3ObjectMetadataItemId;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@NoArgsConstructor
@Accessors(prefix="_")
public class ObjectMetaData
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter Collection<ObjectMetaDataItem> _items;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void put (final ObjectMetaDataItem item) {
		if ( _items == null  ) {
			_items = Lists.newArrayList();
		}
	   _items.add(item);
	}
	public void putAll(final Collection<ObjectMetaDataItem> items) {
		if (_items == null) {
			_items = Lists.newArrayList();
		}
		_items.addAll(items);
	}
	public ObjectMetaDataItem get(final S3ObjectMetadataItemId id) {
	  return FluentIterable.from(_items)
			    .firstMatch(new Predicate<ObjectMetaDataItem>() {
			      @Override
			      public boolean apply(final ObjectMetaDataItem element) {
			        return element.getId().asString().equalsIgnoreCase(id.asString());
			      }
			    }).orNull();

	}
	public Collection<ObjectMetaDataItem> userDefined(){
	    Iterable<ObjectMetaDataItem> filtered =  Iterables.filter(_items,
	    														  new Predicate<ObjectMetaDataItem>() {
																			@Override
																			public boolean apply(final ObjectMetaDataItem input) {
																				return input.isUserDefinedCustomMetadata();
																  }});
	    return Lists.newArrayList(filtered);
	}
	public Collection<ObjectMetaDataItem> systemDefined(){
	    Iterable<ObjectMetaDataItem> filtered =  Iterables.filter(_items,
	    														  new Predicate<ObjectMetaDataItem>() {
																			@Override
																			public boolean apply(final ObjectMetaDataItem input) {
																				return !input.isUserDefinedCustomMetadata();
																  }});
	    return Lists.newArrayList(filtered);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n metadata items  :{");
	    for (ObjectMetaDataItem i : _items ) {
	    	outDbgInfo.append(Strings.customized("\n  {} : {} ,  {} ",
	    			                             i.getId(),i.getValue(),
	    			                             i.isUserDefinedCustomMetadata()? "user defined" : "system" ));
	    }
		outDbgInfo.append(" }")	;
	    return outDbgInfo.toString();
	}


}
