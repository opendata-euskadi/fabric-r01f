package r01f.file;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.CanBeRepresentedAsString;

/**
 * Models a file name like myFile.ext
 */
@Immutable
@ConvertToDirtyStateTrackable
@MarshallType(as="fileName")
@Accessors(prefix="_")
public class FileName
  implements CanBeRepresentedAsString,
  			 Serializable {
	
	private static final long serialVersionUID = -7901960255575168878L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final String _fileName;
	
	@MarshallIgnoredField
	private final transient FileNameAndExtensionParser _fileNameAndExtensionParser;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FileName(@MarshallFrom("fileName") final String fileName) {
		_fileName = fileName;
		_fileNameAndExtensionParser = new FileNameAndExtensionParserUsingRegEx();
	}
	public static FileName of(final String fileName) {
		return new FileName(fileName);
	}
	public static FileName valueOf(final String fileName) {
		return new FileName(fileName);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the file name part
	 */
	public String getName() {
		return _fileNameAndExtension.get()[0];
	}
	/**
	 * @return the file extension part
	 */
	public String getExtension() {
		return _fileNameAndExtension.get()[1];
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _fileName;
	}
	@Override
	public String toString() {
		return _fileName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( !(obj instanceof FileName) ) return false;
		
		FileName otherFileName = (FileName)obj;
		return _fileName != null ? _fileName.equals(otherFileName.getFileName())
								 : otherFileName.getFileName() != null ? false
										 							   : true;
	}
	@Override
	public int hashCode() {
		return _fileName != null ? _fileName.hashCode()
								 : super.hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private transient Memoized<String[]> _fileNameAndExtension = new Memoized<String[]>() {
																		@Override
																		public String[] supply() {
																			return _fileNameAndExtensionParser.parseFileNameAndExtension(_fileName);
																		}
													   			 };
}
