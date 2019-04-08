package r01f.file.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FilePropertiesBase;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreAPIWrapper;
import r01f.types.Path;


@Slf4j
public abstract class Files {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
     * Returns the file size with it's unit
     * <ul>
     * 		<li>If the file size is less than 1024 bytes: 'x b.'</li>
     * 		<li>If the file size is between 1024 bytes and 1048576 bytes :'x Kb.'</li>
     * 		<li>If the file size is between 1048576 bytes and 1073741824 bytes : 'x Mb.'</li>
     * 		<li>If the file size is greater than 1073741824 bytes: 'x Gb.'</li>
     * </ul>
     * @param fileBytes file size in bytes
     * @return the formatted file size 
     */
    public static String formatFileSize(final long fileBytes) {
    	return FilePropertiesBase.formatSize(fileBytes);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the file name part
	 */
	public static String getName(final String fileName) {
		return Files.fileNameAndExtension(fileName)[0];
	}
	/**
	 * @return the file extension part
	 */
	public static String getExtension(final String fileName) {
		return Files.fileNameAndExtension(fileName)[1];
	}
	/**
	 * Splits a {@link String} with the file name and it's extension into a
	 * {@link String} array where the first entry is the file name (without extension)
	 * and the second entry is the extension (if the file has extension)
	 * @param fileName
	 * @return
	 */
	public static String[] fileNameAndExtension(final String fileName) {
		return FileNameAndExtension.of(fileName)
								   .getFileNameAndExtension().get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns true if the file is a symbolic link
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean isSymLink(final File file) throws IOException {
    	File canon;
    	if (file.getParent() == null) {
    		canon = file;
    	} else {
    		File canonDir = file.getParentFile().getCanonicalFile();
    		canon = new File(canonDir,
    						 file.getName());
    	}
    	return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }
    /**
     * Returns the symbolic link target if the file is a symbolic link; false otherwise
     * @param file
     * @return
     * @throws IOException
     */
    public static Path symLinkTarget(final File file) throws IOException {
    	if (!Files.isSymLink(file)) return null;
    	File canon;
    	if (file.getParent() == null) {
    		canon = file;
    	} else {
    		File canonDir = file.getParentFile().getCanonicalFile();
    		canon = new File(canonDir,
    						 file.getName());
    	}
    	return Path.from(canon.getCanonicalFile());
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns an utility type that wraps a {@link FileStoreAPI}
     * @param api
     * @return
     */
    public static FileStoreAPIWrapper wrap(final FileStoreAPI api) {
    	return new FileStoreAPIWrapper(api);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  ZIP FILE EXTRACT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified
	 * by destDirectory (will be created if does not exists)
	 * @param zipFileIS
	 * @param destFolder
	 * @return a collection containing the paths of the extracted files
	 * @throws IOException
	 */
	public static Collection<Path> unzip(final InputStream zipFileIS,
							 			 final Path destFolder) throws IOException {
		Collection<Path> outPaths = Lists.newLinkedList();
		
		ZipInputStream zipIn = new ZipInputStream(zipFileIS);
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			Path extractedFilePath = destFolder.joinedWith(entry.getName());
			File extractedFile = new File(extractedFilePath.asAbsoluteString());
			
			// ensure the parent folder exists... otherwise a FileNotFoundException will be raised
			FileUtils.forceMkdir(extractedFile.getParentFile());
			// extract the file
			_extractFile(zipIn,
						 extractedFilePath);
			outPaths.add(extractedFilePath);
			
			// close the zip entry and go for the next
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
		return outPaths;			
	}
	/**
	 * Extracts a zip entry (file entry)
	 * @param zipIn
	 * @param extractedFilePath
	 * @throws IOException
	 */
	private static void _extractFile(final ZipInputStream zipIn,
									 final Path extractedFilePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(extractedFilePath.asAbsoluteString()));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Deletes the folder that contains the given file
	 * @param filePath
	 */
	public static void deleteFolderContainingFile(final Path filePath) {
		File containerFolder = null;
		try {
			File file = new File(filePath.asAbsoluteString()); 
			if (file.exists()) {
				containerFolder = file.getParentFile();
				log.info("Deleting folder at {}",containerFolder.getAbsolutePath());
				FileUtils.deleteQuietly(containerFolder);		// maybe? FileUtils.deleteDirectory(folder)
			}
		} catch(Throwable th) {
			log.error("Error trying to delete the folder with path={}",(containerFolder != null ? containerFolder.getAbsolutePath() : filePath.asAbsoluteString()));
		}
	}
}
