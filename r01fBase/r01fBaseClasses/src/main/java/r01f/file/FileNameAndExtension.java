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
@MarshallType(as="file")
@Accessors(prefix="_")
public class FileNameAndExtension
  implements CanBeRepresentedAsString,
  			 Serializable {
	
	private static final long serialVersionUID = -7901960255575168878L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final String _nameWithExtension;
	
	@MarshallIgnoredField
	private final transient FileNameAndExtensionParser _fileNameAndExtensionParser;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final transient Memoized<String[]> _fileNameAndExtension = new Memoized<String[]>() {
																						@Override
																						public String[] supply() {
																							return _fileNameAndExtensionParser.parseFileNameAndExtension(_nameWithExtension);
																						}
																			   };
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FileNameAndExtension(@MarshallFrom("nameWithExtension") final String fileNameWithExtension) {
		_nameWithExtension = fileNameWithExtension;
		_fileNameAndExtensionParser = new FileNameAndExtensionParserNotUsingRegEx();
	}
	public static FileNameAndExtension of(final String fileName) {
		return new FileNameAndExtension(fileName);
	}
	public static FileNameAndExtension valueOf(final String fileName) {
		return new FileNameAndExtension(fileName);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the file name part
	 */
	public String getNameWithoutExtension() {
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
		return _nameWithExtension;
	}
	@Override
	public String toString() {
		return _nameWithExtension;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( !(obj instanceof FileNameAndExtension) ) return false;
		
		FileNameAndExtension otherFileNameAndExt = (FileNameAndExtension)obj;
		return _nameWithExtension != null ? _nameWithExtension.equals(otherFileNameAndExt.getNameWithExtension())
										  : otherFileNameAndExt.getNameWithExtension() != null ? false
										 							   						   : true;
	}
	@Override
	public int hashCode() {
		return _nameWithExtension != null ? _nameWithExtension.hashCode()
								 		  : super.hashCode();
	}
}