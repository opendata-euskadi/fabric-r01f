package r01f.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

/**
 * Streams utilities
 */
public class Streams {
/////////////////////////////////////////////////////////////////////////////////////////
//  InputStream
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets an {@link InputStream} as a byte array
	 * @param is
	 * @return the byte array from the {@link InputStream}
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] inputStreamBytes(final InputStream is) throws IOException {
		byte[] outBytes = IOUtils.toByteArray(is);
		return outBytes;
	}
    /**
     * Loads the wrapped {@link InputStream} as a String 
     * @param is 
     * @return 
     * @throws IOException
     */
	public static String inputStreamAsString(final InputStream is) throws IOException {
		return Streams.inputStreamAsString(is,Charset.defaultCharset());
	}
    /**
     * Loads the wrapped {@link InputStream} as a String
     * @param is
     * @param charset 
     * @return 
     * @throws IOException 
     */
	public static String inputStreamAsString(final InputStream is,final Charset charSet) throws IOException {
		Writer writer = null;
        if (is != null) {
            writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,charSet));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
            	try {
            		is.close();
            	} catch (IOException ioEx) {
            		/* ignore */
            	}
            }
        }         
        return writer != null ? writer.toString()
        					  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * (from org.apache.tomcat.util.http.fileupload.util.Streams)
	 * Copies the contents from the given {@link InputStream} to the given {@link OutputStream}
	 * Shortcut of 
	 * <pre class='brush:java'>
	 * 		copy(pInputStream, pOutputStream, new byte[8192]);
	 * </pre>
	 * @param pInputStream The input stream, which is being read. It is guaranteed, that java.io.InputStream.close() is called on the stream
	 * @param pOutputStream The output stream, to which data should be written. May be null, in which case the input streams contents are simply discarded
	 * @param pClose True guarantees, that java.io.OutputStream.close() is called on the stream. False indicates, that only java.io.OutputStream.flush() should be called finally
	 * @return Number of bytes, which have been copied.
	 */
	public static long copy(final InputStream pInputStream,
             				final OutputStream pOutputStream,
             				final boolean pClose) throws IOException {
		return Streams.copy(pInputStream,
						 	pOutputStream,
						 	pClose,
						 	2 * 1024);
		 
	}
	/**
	 * (from org.apache.tomcat.util.http.fileupload.util.Streams)
	 * Copies the contents from the given {@link InputStream} to the given {@link OutputStream}
	 * @param pInputStream The input stream, which is being read. It is guaranteed, that java.io.InputStream.close() is called on the stream
	 * @param pOutputStream The output stream, to which data should be written. May be null, in which case the input streams contents are simply discarded
	 * @param pClose True guarantees, that java.io.OutputStream.close() is called on the stream. False indicates, that only java.io.OutputStream.flush() should be called finally
	 * @return Number of bytes, which have been copied.
	 */
	public static long copy(final InputStream pIn,
							final OutputStream pOut,
							final boolean pClose,
							final int bufferSize) throws IOException {
		OutputStream out = pOut;
        InputStream in = pIn;
        byte[] pBuffer = new byte[bufferSize];
        try {
            long total = 0;
            for (;;) {
                int res = in.read(pBuffer);
                if (res == -1) {
                    break;
                }
                if (res > 0) {
                    total += res;
                    if (out != null) {
                        out.write(pBuffer, 0, res);
                    }
                }
            }
            if (out != null) {
                if (pClose) {
                    out.close();
                } else {
                    out.flush();
                }
                out = null;
            }
            in.close();
            in = null;
            return total;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioEx) {
                    /* Ignore me */
                }
            }
            if (pClose  &&  out != null) {
                try {
                    out.close();
                } catch (IOException ioEx) {
                    /* Ignore me */
                }
            }
        }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  READABLE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copies all {@link Readable} content to a {@link Writer}
	 * @param r
	 * @param w
	 * @throws IOException
	 */
	public static void copy(final Readable r,
							final Writer w) throws IOException {
		Streams.copy(r,w,
					 2*1024);
	}
	/**
	 * Copies all {@link Readable} content to a {@link Writer}
	 * @param r
	 * @param w
	 * @throws IOException
	 */
	public static void copy(final Readable r,
							final Writer w,
							final int buffSize) throws IOException {
		CharBuffer buff = CharBuffer.allocate(2 * 1024);
	    char[] chars = buff.array();
	
	    while (r.read(buff) != -1) { // Read from channel until EOF
	      // Put the char buffer into drain mode, and write its contents
	      // to the Writer, reading them from the backing array.
	      buff.flip();
	      w.write(chars,
	    		  buff.position(),buff.remaining());
	      buff.clear(); 		// Clear the character buffer
	    }
	}
		
}
