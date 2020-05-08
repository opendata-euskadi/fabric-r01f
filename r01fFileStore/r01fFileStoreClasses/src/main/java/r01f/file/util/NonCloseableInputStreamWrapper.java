package r01f.file.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import lombok.RequiredArgsConstructor;
import r01f.filestore.api.FileStoreAPI;

/**
 * An {@link InputStream} wrapper that just do NOT closes
 * Used at {@link ZipFile} while unzipping files
 * 		Sometimes the underlying {@link FileStoreAPI} CLOSES the InputStream after writing a file
 * 		... but while unzipping the {@link InputStream} MUST remain open while the unzip operation
 * 			is in progress
 */
@RequiredArgsConstructor
class NonCloseableInputStreamWrapper 
	 extends InputStream {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final InputStream _wrappedIS;

/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void close() throws IOException {
		// just do not close
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int read() throws IOException {
		return _wrappedIS.read();
	}
	@Override
	public int read(final byte[] b) throws IOException {
		return _wrappedIS.read(b);
	}
	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		return _wrappedIS.read(b, off, len);
	}
	@Override
	public long skip(final long n) throws IOException {
		return _wrappedIS.skip(n);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int available() throws IOException {
		return _wrappedIS.available();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MARK
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public synchronized void mark(final int readlimit) {
		_wrappedIS.mark(readlimit);
	}
	@Override
	public synchronized void reset() throws IOException {
		_wrappedIS.reset();
	}
	@Override
	public boolean markSupported() {
		return _wrappedIS.markSupported();
	}
}
