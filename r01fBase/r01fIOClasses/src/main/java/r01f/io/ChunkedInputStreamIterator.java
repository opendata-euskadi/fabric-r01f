package r01f.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Abstrae al cliente de la lógica de iterar sobre un array de bytes para trocearlo 
 * en paquetes
 * 
 * Esta clase es de utilidad por ejemplo cuando hay que enviar un churro de bytes (un array de bytes o un InputStream)
 * hay que enviarlo de forma "troceada" chunked a un servidor (ej: subir ficheros a iw)
 * Ejemplo de uso:
 * <pre class='brush:java'>
 *		R01MContentDataAPI contentDataAPI = R01MClientFactory.getContentDataAPI(_securityContext,contentOid);
 *		@Cleanup ChunkedStreamIterator chunkedIt = new ChunkedInputStreamIterator(is);	 
 *		while (bytesIt.hasNext()) {
 *			int offset = chunkedIt.offset();												// OJO!! leer siempre el offset ANTES 
 *			byte[] currChunk = chunkedIt.next();											// se lee un chunk de bytes
 *			contentDataAPI.uploadAttachmentFileChunk(documentOid,path,currChunk,offset);	// se pasa e chunk al servidor
 *		}
 * </pre>
 * También se puede utilizar de forma similar a un reader:
 * <pre class='brush:java'>
 *		R01MContentDataAPI contentDataAPI = R01MClientFactory.getContentDataAPI(_securityContext,contentOid);
 *		@Cleanup ChunkedStreamIterator chunkedReader = new ChunkedInputStreamIterator(is);	
 *		byte[] currChunk = null;
 *		do {
 *			int offset = chunkedReader.offset();					// OJO!! leer siempre el offset ANTES
 *			currChunk = chunkedReader.readChunk();					// se lee un chunk de bytes
 *			if (bytesReaded != null) contentDataAPI.uploadAttachmentFileChunk(documentOid,path,currChunk,offset);	// se pasa el chunk al servidor
 *		} while (bytesReaded != null);
 * </pre>
 */
public class ChunkedInputStreamIterator 
  implements ChunkedStreamIterator {
///////////////////////////////////////////////////////////////////////////////
// 	MIEMBROS
///////////////////////////////////////////////////////////////////////////////	
	private InputStream _is;
	private int _offset = 0;
	private int _blockSize = -1;	// tamaño de bloque
	
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTORES
///////////////////////////////////////////////////////////////////////////////	
	public ChunkedInputStreamIterator(final byte[] bytes) {
		this.setBytes(bytes);
		this.setBlockSize(bytes.length);
	}
	public ChunkedInputStreamIterator(final byte[] bytes,final int blockSize) {
		this.setBytes(bytes);
		this.setBlockSize(blockSize);	
	}
	public ChunkedInputStreamIterator(final InputStream is) {
		this.setInputStream(is);
		this.setBlockSize(100 * 1024);	// como no se sabe cuantos datos hay en el is, se crea un buffer con un tamaño por defecto
	}
	public ChunkedInputStreamIterator(final InputStream is,final int blockSize) {
		this.setInputStream(is);
		this.setBlockSize(blockSize);
	}
///////////////////////////////////////////////////////////////////////////////
// 	GET & SET
///////////////////////////////////////////////////////////////////////////////		
	public void setBytes(final byte[] bytes) {
		_is = new ByteArrayInputStream(bytes);
	}
	public void setInputStream(final InputStream is) {
		_is = is;
	}
	public void setBlockSize(final int size) {
		// Calcular dinámicamente el tamaño del bloque: 
		//		- si el tamaño del fichero es mayor de 1Mb, se sube por bloques
		if (_blockSize <= 0) {
			if (size > 999 * 1024) {
				_blockSize = 100*1024;		// tamaño de bloque 100 Kb
			} else {
				_blockSize = size;			// tamaño del bloque igual el fichero, subida unica
			}
		} else {
			_blockSize = 100 * 1024;
		}
	}
	@Override
	public int getOffset() {
		return _offset;
	}	
///////////////////////////////////////////////////////////////////////////////
// 	METODOS
///////////////////////////////////////////////////////////////////////////////	
	public byte[] readChunk() throws IOException {
		return this.readChunk(_blockSize);
	}
	public byte[] readChunk(final int chunkSize) throws IOException {
		byte[] buff = new byte[chunkSize];
		int readed = _is.read(buff);			
		byte[] outBytes = readed > 0 ? Arrays.copyOf(buff,readed) : null;
		_offset += readed;
		return outBytes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ Closeable
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void close() throws IOException {
		_is.close();
	}
///////////////////////////////////////////////////////////////////////////////
// 	INTERFAZ ITERATOR
///////////////////////////////////////////////////////////////////////////////	
	@Override
	public byte[] next() {
		byte[] outBytes = null;
		try {
			outBytes = this.readChunk();
		} catch (IOException ioEx) {
			ioEx.printStackTrace(System.out);
		}
		return outBytes;
	}	
	@Override
	public boolean hasNext() {
		try {
			return _is.available() > 0;
		} catch (IOException ioEx) {ioEx.printStackTrace(System.out);}
		return false;
	}
	@Override
	public void remove() {
		/* nothing to do */
	}
	public boolean isAvailable() {
		return this.hasNext();
	}
}
