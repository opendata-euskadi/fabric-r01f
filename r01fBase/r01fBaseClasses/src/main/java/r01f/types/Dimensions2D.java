package r01f.types;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Object dimensions (ie: an image).
 *
 * Example of use:
 * BufferedImage image = null;
 * if (is != null) {
 *	image = ImageIO.read(is);
 * }
 * if(image != null) {
 *		int height = image.getHeight();
 *		int width = image.getWidth();
 *		return new Dimensions2D(width, height);
 * }
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="dimensions2D")
@Immutable
@Accessors(prefix="_")
public class Dimensions2D
  implements Serializable {
	private static final long serialVersionUID = -7580079139588296207L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * width
	 */
	@MarshallField(as="width",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _width;
	/**
	 * heigh
	 */
	@MarshallField(as="height",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _height;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Dimensions2D(@MarshallFrom("width") final int width,@MarshallFrom("height") final int height) {
		_width = width;
		_height = height;
	}
}
