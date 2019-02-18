package r01f.io;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Interfaz que han de implementar las clases que iteran sobre un churro de bytes que se va a tratar de forma chunked
 * (particionada), bien sea un array de bytes (ByteArrayChunkedIterator) o sobre un InputStream (InputStreamChunkedIterator)
 * 
 * Esta clase es de utilidad por ejemplo cuando un churro de bytes hay que enviarlo en paquetes (chunks) 
 * por ejemplo a otro servidor (ej: subir ficheros a iw)
 * De esta forma, el cliente se abstrae de la complejidad de implementar el troceado del churro de entrada
 * Ejemplo de uso:
 * <pre>
 *		R01MContentDataAPI contentDataAPI = R01MClientFactory.getContentDataAPI(_securityContext,contentOid);
 *		ChunkedIterator bytesIt = new InputStreamChunkedIterator(is);	// cambiar por new ByteArrayChunkedIterator(fileBytes) si en lugar de un InputStream se pasa un array de bytes 
 *		while (bytesIt.hasNext()) {
 *			int offset = bytesIt.getOffset();
 *			byte[] bytes = bytesIt.next();
 *			contentDataAPI.uploadAttachmentFileChunk(documentOid,path,bytes,offset);
 *		}
 * </pre>
 */
public interface ChunkedStreamIterator 
         extends Iterator<byte[]>,
   		         Closeable {
        	 
		public int getOffset();	
}
