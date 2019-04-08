package r01f.file;

import com.google.common.base.Preconditions;

import r01f.util.types.Strings;

public class FileNameAndExtensionParserNotUsingRegEx
  implements FileNameAndExtensionParser {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String[] parseFileNameAndExtension(final String fileNameWithExtension) {
		String[] outFileNameAndExtension = new String[2];
		if (Strings.isNOTNullOrEmpty(fileNameWithExtension)) {
			String name = _getFileNameWithoutExtension(fileNameWithExtension);
			String ext = _getFileExtension(fileNameWithExtension);
			outFileNameAndExtension = new String[] { name,ext };
		}
		return outFileNameAndExtension;
	}
	// See com.google.common.io.Files.getNameWithoutExtension(fileNameWithExtension);
	// cannot use guava's Files since it's GWT incompatible
	private static String _getFileNameWithoutExtension(final String fileName) {
	    Preconditions.checkNotNull(fileName);
	    int index = fileName.lastIndexOf("/");
	    String theFileName = index >= 0 ? fileName.substring(index + 1)
	    							    : fileName;
	    int dotIndex = theFileName.lastIndexOf('.');
	    return (dotIndex == -1) ? theFileName : theFileName.substring(0,dotIndex);
	}
	// see com.google.common.io.Files.getFileExtension
	// cannot use guava's Files since it's GWT incompatible
	public static String _getFileExtension(final String fileName) {
	    Preconditions.checkNotNull(fileName);
	    int index = fileName.lastIndexOf("/");
	    String theFileName = index >= 0 ? fileName.substring(index + 1)
	    							    : fileName;
	    int dotIndex = theFileName.lastIndexOf('.');
	    return (dotIndex == -1) ? "" : theFileName.substring(dotIndex + 1);
	}
}