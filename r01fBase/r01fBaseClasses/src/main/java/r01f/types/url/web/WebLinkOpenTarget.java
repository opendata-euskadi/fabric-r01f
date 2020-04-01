package r01f.types.url.web;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * The window open target: https://www.w3schools.com/tags/att_a_target.asp
 * Pre-defined targets:
 * <pre class='brush:java'>		
 * 		WebLinkOpenTarget tgt = WebLinkOpenTarget.SELF;
 * </pre>
 * For a frame target:
 * <pre class='brush:java'>	
 * 		WebLinkOpenTarget frmTgt = WebLinkOpenTarget.frameNamed("my-frame");
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="openTaget")
@Accessors(prefix="_")
public class WebLinkOpenTarget 
     extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 5416033585280880913L;
	
	public WebLinkOpenTarget(final String oid) {
		super(oid);
	}
	public static WebLinkOpenTarget forId(final String id) {
		return new WebLinkOpenTarget(id);
	}
	public static WebLinkOpenTarget valueOf(final String id) {
		return WebLinkOpenTarget.forId(id);
	}
	public static WebLinkOpenTarget forIdOrNull(final String id) {
		if (id == null) return null;
		return new WebLinkOpenTarget(id);
	}
	public static WebLinkOpenTarget frameNamed(final String frameName) {
		return new WebLinkOpenTarget(frameName);
	}
	public static WebLinkOpenTarget BLANK = WebLinkOpenTarget.forId("_blank"); 
	public static WebLinkOpenTarget SELF = WebLinkOpenTarget.forId("_self");
	public static WebLinkOpenTarget TOP = WebLinkOpenTarget.forId("_top");
	public static WebLinkOpenTarget PARENT = WebLinkOpenTarget.forId("_parent");
}
