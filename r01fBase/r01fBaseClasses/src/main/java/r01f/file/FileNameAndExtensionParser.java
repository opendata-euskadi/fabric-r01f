package r01f.file;

public interface FileNameAndExtensionParser {
	/**
	 * Splits a {@link String} with the file name and it's extension into a
	 * {@link String} array where the first entry is the file name (without extension)
	 * and the second entry is the extension (if the file has extension)
	 * @param fileNameWithExtension
	 * @return
	 */
	public String[] parseFileNameAndExtension(final String fileName); 
}