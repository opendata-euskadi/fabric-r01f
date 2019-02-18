package r01f.io.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;
import org.apache.commons.io.output.StringBuilderWriter;

import com.google.common.io.CharStreams;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import r01f.resources.ResourcesLoader;
import r01f.types.Path;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringPersistenceUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  FILE LOADING
///////////////////////////////////////////////////////////////////////////////////////// 
    /**
     * Loads a file content as a string
     * @param loader a {@link ResourcesLoader} in charge of loading the file
     * @param filePath the file path
     * @return the file contents
     * @throws IOException 
     */
	public static String load(final ResourcesLoader loader,
							  final String filePath) throws IOException {
    	return StringPersistenceUtils.load(loader,
    									   Path.from(filePath));
    }
    /**
     * Loads a file content as a string
     * @param loader a {@link ResourcesLoader} in charge of loading the file
     * @param filePath the file path
     * @return the file contents
     * @throws IOException Si ocurre algun error al acceder al fichero
     */
	@SuppressWarnings("resource")
	public static String load(final ResourcesLoader loader,
							  final Path filePath) throws IOException {
    	@Cleanup InputStream is = loader.getInputStream(filePath,
    													true);	// true = do NOT use caches if they're in place
        String outStr = StringPersistenceUtils.load(is);
        return outStr;
    }
    /**
     * Loads a file content as a string
     * @param loader a {@link ResourcesLoader} in charge of loading the file
     * @param filePath the file path
     * @param encoding the file encoding
     * @return the file contents
     * @throws IOException 
     */
	public static String load(final ResourcesLoader loader,
							  final String filePath,final Charset encoding) throws IOException {
    	return StringPersistenceUtils.load(loader,
    									   Path.from(filePath),
    									   encoding);
    }
    /**
     * Loads a file content as a string
     * @param loader a {@link ResourcesLoader} in charge of loading the file
     * @param filePath the file path
     * @param encoding the file encoding
     * @return the file contents
     * @throws IOException 
     */
	@SuppressWarnings("resource")
	public static String load(final ResourcesLoader loader,
							  final Path filePath,
							  final Charset encoding) throws IOException {
    	@Cleanup InputStream is = loader.getInputStream(filePath,
    													true);	// true = do NOT use caches if they're in place
    	String outStr = StringPersistenceUtils.load(is,encoding);
    	return outStr;
    }  
/////////////////////////////////////////////////////////////////////////////////////////
//  FILE LOADING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads a file content
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String load(final Path filePath) throws IOException {
		return StringPersistenceUtils.load(new File(filePath.asAbsoluteString()));
	}
	/**
	 * Loads a file content
	 * @param filePath
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String load(final Path filePath,
							  final Charset encoding) throws IOException {
		return StringPersistenceUtils.load(new File(filePath.asAbsoluteString()),
										   encoding);
	}
    /**
     * Loads a file content as a String 
     * @param f file
     * @return the file content
     * @throws IOException
     */
    public static String load(final File f) throws IOException {
    	String outStr = StringPersistenceUtils.load(f,null);
    	return outStr;
    }    
    /**
     * Loads a file content as a String
     * @param f  the file
     * @param the file encoding
     * @return the file content
     * @throws IOException
     */
    @SuppressWarnings("resource")
	public static String load(final File f,
							  final Charset encoding) throws IOException {
    	Charset theEncoding = encoding != null ? encoding : Charset.defaultCharset();
    	@Cleanup FileInputStream fis = new FileInputStream(f);
    	@Cleanup BufferedInputStream bis = new BufferedInputStream(fis);
    	String outStr = StringPersistenceUtils.load(bis,
    												theEncoding);
    	return outStr;
    }  
/////////////////////////////////////////////////////////////////////////////////////////
// 	STREAM LOADING
/////////////////////////////////////////////////////////////////////////////////////////     
    /**
     * Loads a stream as a String
     * @param is the stream
     * @return the string
     * @throws IOException
     */
    public static String load(final InputStream is) throws IOException {
        if (is == null) return null;
        String outStr = StringPersistenceUtils.load(is,
        											null);		// default charset
        return outStr;
    }
    /**
     * Loads a stream as a String; if the stream has more than the given
	 * number of chars, it ignores the rest
     * @param is the stream
     * @param maxChars
     * @return the string
     * @throws IOException
     */
    public static String loadNotExceding(final InputStream is,final int maxChars) throws IOException {
        if (is == null) return null;
        String outStr = StringPersistenceUtils.loadNotExceding(is,
        													   maxChars,
        													   null);		// default charset
        return outStr;
    }
    /**
     * Loads a stream as a String
     * @param is the stream
     * @param encoding the encoding
     * @return the file content
     * @throws IOException
     */
	@SuppressWarnings("resource")
	public static String load(final InputStream is,
							  final Charset encoding) throws IOException {
    	Charset theEncoding = encoding != null ? encoding : Charset.defaultCharset();  
        @Cleanup Reader r = new InputStreamReader(is,
        										  theEncoding);    	
        String outStr = StringPersistenceUtils.load(r);
        return outStr;
    }
    /**
     * Loads a stream as a String; if the stream has more than the given
	 * number of chars, it ignores the rest
     * @param is the stream
     * @param maxChars
     * @param encoding
     * @return the string
     * @throws IOException
     */
    public static String loadNotExceding(final InputStream is,final int maxChars,
    									 final Charset encoding) throws IOException {
    	Charset theEncoding = encoding != null ? encoding : Charset.defaultCharset();  
        @Cleanup Reader r = new InputStreamReader(is,
        										  theEncoding);    	
        String outStr = StringPersistenceUtils.loadNotExceding(r,maxChars);
        return outStr;
    }
	/**
	 * Loads a stream as a String; if the stream has more than the given
	 * number of chars, it ignores the rest
	 * @param r
	 * @param maxChars
	 * @return
	 */
	public static String loadNotExceding(final Reader r,final int maxChars) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        long copied = IOUtils.copyLarge(r,sw,
        				  				0,						// initial offset
        				  				maxChars,					// max number of chars to be readed
        				  				new char[1024 * 4]);		// 4k buffer
        return sw.toString();
	}
    /**
     * Loads a stream as a String
     * @param r the stream
     * @return the file content
     * @throws IOException
     */
    public static String load(final Reader r) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        long copied = IOUtils.copyLarge(r,sw);		// IOUtils.copy() copies a maximum of Integer.MAX chars
        return sw.toString();
//        StringBuilder outString = new StringBuilder();
//        if (r != null) {
//            char[] buf = new char[2 * 1024]; // Buffer 2K
//            int charsReaded = -1;
//            do {
//                charsReaded = r.read(buf);
//                if (charsReaded != -1) outString.append(new String(buf,0,charsReaded));
//            } while (charsReaded != -1);
//            return outString.toString();
//        }
//        return null;
    }    
    /**
     * Loads a stream as a String
     * @param r reader
     * @param encoding stream encoding
     * @return the file content
     * @throws IOException
     */
    public static String load(final Reader r,
    						  final Charset encoding) throws IOException {
    	Charset theEncoding = encoding != null ? encoding 
    										   : Charset.defaultCharset();    	
        String srcString = StringPersistenceUtils.load(r);
        ByteBuffer bb = theEncoding.encode(CharBuffer.wrap(srcString)); 
        return new String(bb.array());
    }
	/**
	 * Loads a stream as a String; it the stream has more than the given
	 * number of chars, it ignores the rest
	 * @param r
	 * @param maxChars
	 * @return
	 */
	public static String loadNotExceding(final Reader r,final int maxChars,
										 final Charset encoding) throws IOException {
    	Charset theEncoding = encoding != null ? encoding 
    										   : Charset.defaultCharset();    	
        String srcString = StringPersistenceUtils.loadNotExceding(r,maxChars);
        ByteBuffer bb = theEncoding.encode(CharBuffer.wrap(srcString)); 
        return new String(bb.array());
	}
    /**
     * Loads a stream as a String
     * @param r reader
     * @return the file content
     * @throws IOException
     */
    public static String load(final Readable r) throws IOException {
    	return CharStreams.toString(r);
    }
    /**
     * Loads a stream as a String
     * @param r reader
     * @param encoding stream encoding
     * @return the file content
     * @throws IOException
     */
    public static String load(final Readable r,
    						  final Charset encoding) throws IOException {
    	Charset theEncoding = encoding != null ? encoding 
    										   : Charset.defaultCharset();  
        String srcString = StringPersistenceUtils.load(r);
        ByteBuffer bb = theEncoding.encode(CharBuffer.wrap(srcString)); 
        return new String(bb.array());
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FILE SAVE
/////////////////////////////////////////////////////////////////////////////////////////
    public static interface StringLineProducer {
    	public String nextLine();
    }
    /**
     * Stores the lines produced by a line producer
     * @param lineProducer
     * @param file
     * @throws IOException
     */
    public static void save(final StringLineProducer lineProducer,
    						final File file) throws IOException {
    	String currLine = lineProducer.nextLine();
    	if (currLine == null) return;
    	
    	FileWriter w = new FileWriter(file);
    	do {
    		w.write(currLine);
    		if (!currLine.endsWith("\n")) w.write("\n");
    		currLine = lineProducer.nextLine();
    	} while(currLine != null);
    	
    	w.flush();
    	w.close();
    }
    /**
     * Stores an inputstream into a file using the default charset
     * @param is
     * @param f
     * @throws IOException
     */
    public static void save(final InputStream is,final File f) throws IOException {
    	InputStreamReader reader = new InputStreamReader(is,Charset.defaultCharset());
        StringPersistenceUtils.save(reader,f);
    }
    /**
     * Stores an inputstream into a file
     * @param is
     * @param charset
     * @param f
     * @throws IOException
     */
    public static void save(final InputStream is,final Charset charset,
    						final File f) throws IOException {
    	InputStreamReader reader = new InputStreamReader(is,charset);
        StringPersistenceUtils.save(reader,f);
    }
    /**
     * Stores a String into a file
     * @param f the file
     * @param theString the string
     * @throws IOException
     */
    public static void save(final Reader reader,final File f) throws IOException {
        BufferedReader bufReader = new BufferedReader(reader);
        BufferedWriter bufWriter = new BufferedWriter(new FileWriter(f, false));
        String line = bufReader.readLine();
        while (line != null) {
            bufWriter.write(line + "\r\n");
            line = bufReader.readLine();
        }
        reader.close();
        bufWriter.flush();
        bufWriter.close();
    } 
    /**
     * Stores a String into a file
     * @param f the file
     * @param theString the string
     * @throws IOException
     */
    public static void save(final CharSequence theString,final File f) throws IOException {
    	StringPersistenceUtils.save(new CharSequenceReader(theString),f);
    } 
    /**
     * Saves a String into a file
     * @param filePath the file path
     * @param theString the String
     * @throws IOException
     */
    public static void save(final CharSequence theString,
    						final Path filePath) throws IOException {
        StringPersistenceUtils.save(theString,new File(filePath.asString()));
    }   
    /**
     * Saves a String into a file
     * @param filePath the file path
     * @param theString the String
     * @throws IOException 
     */
    public static void save(final CharSequence theString,
    						final String filePath) throws IOException {
        StringPersistenceUtils.save(theString,new File(filePath));
    }      
}
