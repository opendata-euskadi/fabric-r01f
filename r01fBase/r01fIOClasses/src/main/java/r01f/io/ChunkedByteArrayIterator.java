package r01f.io;

import java.io.IOException;
import java.util.Arrays;

/**
 * Abstrae al cliente de la lógica de iterar sobre un array de bytes para trocearlo 
 * en paquetes
 * 
 * Esta clase es de utilidad por ejemplo cuando un churro de bytes hay que enviarlo en paquetes (chunks) 
 * por ejemplo a otro servidor (ej: subir ficheros a iw)
 * De esta forma, el cliente se abstrae de la complejidad de implementar el troceado del churro de entrada
 * Ejemplo de uso:
 * <pre class='brush:java'>
 *		R01MContentDataAPI contentDataAPI = R01MClientFactory.getContentDataAPI(_securityContext,contentOid);
 *		ChunkedStreamIterator bytesIt = new ChunkedByteArrayIterator(bytes);	 
 *		while (bytesIt.hasNext()) {
 *			int offset = bytesIt.getOffset();
 *			byte[] bytes = bytesIt.next();
 *			contentDataAPI.uploadAttachmentFileChunk(documentOid,path,bytes,offset);
 *		}
 * </pre>
 * También se puede utilizar de forma similar a un reader:
 * <pre class='brush:java'>
 *		R01MContentDataAPI contentDataAPI = R01MClientFactory.getContentDataAPI(_securityContext,contentOid);
 *		@Cleanup ChunkedByteArrayIterator chunkedReader = new ChunkedByteArrayIterator(bytes);	
 *		byte[] currChunk = null;
 *		do {
 *			int offset = chunkedIS.offset();						// OJO!! leer siempre el offset ANTES
 *			currChunk = chunkedIS.readChunk();						// se lee un chunk de bytes
 *			if (bytesReaded != null) contentDataAPI.uploadAttachmentFileChunk(documentOid,path,currChunk,offset);	// se pasa el chunk al servidor
 *		} while (bytesReaded != null);
 * </pre>
 */
public class ChunkedByteArrayIterator 
  implements ChunkedStreamIterator {
///////////////////////////////////////////////////////////////////////////////
// 	STATUS
///////////////////////////////////////////////////////////////////////////////	
	private byte[] _bytes;
	private int _initOffset = 0;
	private int _endOffset = 0;
	private int _blockSize = 10*1024;	// tamaño de bloque
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	public ChunkedByteArrayIterator() {			
	}
	public ChunkedByteArrayIterator(final byte[] bytes) {
		this.setBytes(bytes);			
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET / SET
/////////////////////////////////////////////////////////////////////////////////////////
	public void setBytes(byte[] bytes) {
		_bytes = bytes;
		_computeBlockSize();
	}
	@Override
	public int getOffset() {
		return _endOffset;
	}
	private void _computeBlockSize() {
		// Calcular dinámicamente el tamaño del bloque: 
		//		- si el tamaño del fichero es mayor de 1Mb, se sube por bloques
		if (_bytes.length > 999 * 1024) {
			_blockSize = 100*1024;		// tamaño de bloque 100 Kb
		} else {
			_blockSize = _bytes.length;	// tamaño del bloque igual el fichero, subida unica
		}			
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////
	public byte[] readChunk() {
		byte[] outBytes = _read();
		return outBytes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ Closeable
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void close() throws IOException {
		// no hace nada ya que no hay nada que cerrar
	}		
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ Iterator
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public byte[] next() {
		byte[] outBytes = _read();
		return outBytes;
	}		
	@Override
	public boolean hasNext() {
		return _endOffset < _bytes.length;
	}
	@Override
	public void remove() {
		/* nothing to do */
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////
	private byte[] _read() {				
		_initOffset = _endOffset;	// mover el puntero de inicio 
		byte[] outBytes = null;
		if (this.hasNext()) {
			outBytes = Arrays.copyOfRange(_bytes,						// array de bytes de donde leer
										  _initOffset,					// posicion de inicio de lectura
										  _initOffset + _blockSize); 	// posicion de fin de lectura
			// Actualizacion del desplazamiento
			_endOffset += _blockSize;
			// Hay que tener cuidado para la siguiente lectura si hay menos datos
			if (_endOffset + _blockSize > _bytes.length) {
				_blockSize = _bytes.length - _endOffset;	
			}
		}
		return outBytes;
	}
}
