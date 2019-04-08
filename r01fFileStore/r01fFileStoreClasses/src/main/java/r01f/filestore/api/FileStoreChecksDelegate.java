package r01f.filestore.api;

import java.io.IOException;

import com.google.common.base.Preconditions;

import lombok.RequiredArgsConstructor;
import r01f.file.FileID;
import r01f.types.Path;

@RequiredArgsConstructor
public class FileStoreChecksDelegate {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreAPI _fileStoreAPI;
	private final FileStoreFilerAPI _fileStoreFilerAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//  FILE
/////////////////////////////////////////////////////////////////////////////////////////
	public void checkBeforeCopyFile(final FileID srcFile,final FileID dstFile,
									final boolean overwrite) throws IOException {
        Preconditions.checkArgument(srcFile != null && dstFile != null,
        							"The source and destination files cannot be null");
		_checkBeforeCopyOrMoveFile(srcFile,dstFile,
								   overwrite);
	}
	public void checkBeforeMoveFile(final FileID srcFile,final FileID dstFile,
									final boolean overwrite) throws IOException {
        Preconditions.checkArgument(srcFile != null && dstFile != null,
        							"The source and destination files cannot be null");
		_checkBeforeCopyOrMoveFile(srcFile,dstFile,
								   overwrite);
	}
	private void _checkBeforeCopyOrMoveFile(final FileID srcFile,final FileID dstFile,
											final boolean overwrite) throws IOException {
        Preconditions.checkArgument(srcFile != null && dstFile != null,
        							"The source and destination files cannot be null");
		if (!_fileStoreAPI.existsFile(srcFile)) throw new IOException("The source file " + srcFile + " does NOT exists!");
		if (!overwrite && _fileStoreAPI.existsFile(dstFile)) throw new IOException("The destination file " + dstFile + " already exists!");
		if (_fileStoreFilerAPI.existsFolder(Path.from(dstFile))) throw new IOException("The destination file " + dstFile + " already exists as a folder!"); 
	}
	public void checkBeforeWriteToFile(final FileID dstFileId,
									   final boolean overwrite) throws IOException {
        Preconditions.checkArgument(dstFileId != null,
        							"The destination file cannot be null");
		if (!overwrite && _fileStoreAPI.existsFile(dstFileId)) throw new IOException("The file " + dstFileId + " cannot be written: already exists!");
		if (_fileStoreFilerAPI.existsFolder(Path.from(dstFileId))) throw new IOException("The file " + dstFileId + " cannot be written: already exists as a folder!");
	}
	public void checkBeforeAppendToFile(final FileID dstFileId) throws IOException {
        Preconditions.checkArgument(dstFileId != null,
        							"The destination file cannot be null");
		if (_fileStoreFilerAPI.existsFolder(Path.from(dstFileId))) throw new IOException("The file " + dstFileId + " cannot be written: already exists as a folder!");
	}
	public void checkBeforeReadingFromFile(final FileID fileId) throws IOException {
        Preconditions.checkArgument(fileId != null,
        							"The source file cannot be null");
		if (!_fileStoreAPI.existsFile(fileId)) throw new IOException("Cannot read from the file " + fileId + ": it does NOT exists (or maybe it's a folder)!");
		if (_fileStoreFilerAPI.existsFolder(Path.from(fileId))) throw new IOException("Cannot read from the file " + fileId + ": it's a folder!");
	}
	public void checkBeforeDeleteFile(final FileID fileId) throws IOException {
        Preconditions.checkArgument(fileId != null,
        							"The file cannot be null");
		if (!_fileStoreAPI.existsFile(fileId)) throw new IOException("File " + fileId + " does NOT exists (or maybe it's a folder)!");
		if (_fileStoreFilerAPI.existsFolder(Path.from(fileId))) throw new IOException("Cannot delete file " + fileId + ": it's a folder!");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FOLDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public void checkBeforeCopyFolder(final Path srcPath,final Path dstPath,
									  final boolean overwrite) throws IOException {
		_checkBeforeCopyOrMoveFolder(srcPath,dstPath,
									 overwrite);
	}
	public void checkBeforeMoveFolder(final Path srcPath,final Path dstPath,
									  final boolean overwrite) throws IOException {
		_checkBeforeCopyOrMoveFolder(srcPath,dstPath,
									 overwrite);
	}
	private void _checkBeforeCopyOrMoveFolder(final Path srcPath,final Path dstPath,
											  final boolean overwrite) throws IOException {
        Preconditions.checkArgument(srcPath != null && dstPath != null,
        							"The source & destination paths cannot be null");
		if (!_fileStoreFilerAPI.existsFolder(srcPath)) throw new IOException("The source folder at " + srcPath + " does NOT exists (or maybe it's a regular file)");
		if (_fileStoreAPI.existsFile(srcPath)) throw new IOException("The source folder at " + srcPath + " is NOT a folder; it's a file!");		
		if (!overwrite && _fileStoreFilerAPI.existsFolder(dstPath)) throw new IOException("The destination folder at " + dstPath + " already exists!");
		if (_fileStoreAPI.existsFile(dstPath)) throw new IOException("The destination folder at " + dstPath + " already exists as a file!"); 
	}
	/**
	 * Checks before creating a folder
	 * @param path
	 * @throws IOException
	 */
	public void checkBeforeCreateFolder(final Path path) throws IOException {
        Preconditions.checkArgument(path != null,
        							"The path cannot be null");
		if (_fileStoreAPI.existsFile(path)) throw new IOException("Cannot create folder at " + path + " it's already a file!");
	}
	public void checkBeforeRemoveFolder(final Path path) throws IOException {
        Preconditions.checkArgument(path != null,
        							"The path cannot be null");
		if (_fileStoreAPI.existsFile(path)) throw new IOException("Cannot remove folder at " + path + " it's a file!!");
	}
	public void checkBeforeListFolderContents(final Path path) throws IOException {
        Preconditions.checkArgument(path != null,
        							"The path cannot be null");
		if (_fileStoreAPI.existsFile(path)) throw new IOException("Cannot list folder contents at " + path + " it's a file!");
		if (!_fileStoreFilerAPI.existsFolder(path)) throw new IOException("Cannot list folder contents at " + path + " the folder does NOT exists");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void checkFileExists(final Path path) throws IOException {
        Preconditions.checkArgument(path != null,
        							"The path cannot be null");
		if (_fileStoreFilerAPI.existsFolder(path)) throw new IOException(path + " is a folder NOT a file");
		if (!_fileStoreAPI.existsFile(path)) throw new IOException(path + " file does NOT a folder!");		
	}
	public void checkFolderExists(final Path path) throws IOException {
        Preconditions.checkArgument(path != null,
        							"The path cannot be null");
		if (_fileStoreAPI.existsFile(path)) throw new IOException(path + " is a file, NOT a folder!");
		if (!_fileStoreFilerAPI.existsFolder(path)) throw new IOException(path + " folder does NOT exists");		
	}	
    public void checkFileId(final FileID... fileIds) throws IOException {
    	for (FileID fileId : fileIds) {
    		if (fileId == null) throw new IllegalArgumentException("The fileId MUST NOT be null!");
    		if (!(fileId instanceof Path)) throw new IllegalArgumentException("the fileId have to be a Path (now it's " + fileId.getClass() + ")");
    	}
    }
}

