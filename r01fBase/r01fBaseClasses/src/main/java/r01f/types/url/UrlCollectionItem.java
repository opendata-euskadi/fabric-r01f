package r01f.types.url;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasID;
import r01f.facets.HasLanguage;
import r01f.facets.Tagged;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.facets.delegates.TaggeableDelegate;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.tag.TagList;

@MarshallType(as="urlCollectionItem")
@Accessors(prefix="_")
public class UrlCollectionItem 
  implements HasUrl,
  			 HasID<UrlCollectionItemID>,
  			 HasLanguage,
  			 HasTaggeableFacet<String> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="url")
	@Getter @Setter	protected Url _url;

	@MarshallField(as="id")
    @Getter @Setter	protected UrlCollectionItemID _id;

	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Language _language;
	
	@MarshallField(as="tags",
				   whenXml=@MarshallFieldAsXml(collectionElementName="tag"))
	@Getter @Setter protected TagList<String> _tags;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlCollectionItem() {
		// default no-args constructor
	}
	public UrlCollectionItem(final Url url) {	
		this(UrlCollectionItemID.forId(UrlCollectionItemID.supplyId()),url);
	}
	public UrlCollectionItem(final UrlCollectionItemID id,final Url url) {
		this(id,url,
			 (Language)null);
	}
	public UrlCollectionItem(final UrlCollectionItemID id,final Url url,
							 final Language lang) {
		this(id,url,
			 lang,
			 null);
	}
	public UrlCollectionItem(final UrlCollectionItemID id,final Url url,
							 final Language lang,
							 final TagList<String> tags) {
		_id = id;
		_url = url;
		_language = lang;
		_tags = tags;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Tagged<String> asTaggeable() {
		return new TaggeableDelegate<String,UrlCollectionItem>(this);
	}
	@Override
	public void unsafeSetId(final OID id) {
		_id = (UrlCollectionItemID) id;
	}

}
