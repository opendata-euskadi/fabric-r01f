package r01f.types.url.web;

import java.util.Collection;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.UrlCollection;

/**
 * A collection of links
 */
@MarshallType(as="weblinks")
@ConvertToDirtyStateTrackable
public class WebLinkCollection
	 extends UrlCollection<WebLink> {

	private static final long serialVersionUID = -5210515759912563841L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkCollection() {
		// default no-args constructor
	}
	public WebLinkCollection(final WebLink... links) {
		super(links);
	}
	public WebLinkCollection(final Collection<WebLink> links) {
		super(links);
	}
	public WebLinkCollection(final WebLink other) {
		if (other != null) this.add(other);
	}

}
