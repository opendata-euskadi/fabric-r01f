package r01f.objectstreamer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import com.google.common.reflect.TypeToken;

import r01f.objectstreamer.annotations.MarshallFormat;

public interface Marshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//	MAIN METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	public MarshallerReadFromStream forReading();
	public MarshallerWriteToStream forWriting();

/////////////////////////////////////////////////////////////////////////////////////////
//	READ 
/////////////////////////////////////////////////////////////////////////////////////////	
	public interface MarshallerReadFromStream {
		public <T> T from(final InputStream is,final MarshallFormat format,
						  final Class<T> type);
		
		public <T> T from(final InputStream is,final MarshallFormat format,
						  final TypeToken<T> typeToken);
		
		public <T> T from(final String xml,final Charset charset,final MarshallFormat format,
						  final Class<T> type);
		
		public <T> T from(final String xml,final Charset charset,final MarshallFormat format,
						  final TypeToken<T> typeToken);
		
		public <T> T from(final String xml,final MarshallFormat format,
						  final Class<T> type);
		
		public <T> T from(final String xml,final MarshallFormat format,
						  final TypeToken<T> typeToken);
		
		// ================================================================================
		public <T> T fromXml(final InputStream is,
							 final Class<T> type);
		
		public <T> T fromXml(final InputStream is,
							 final TypeToken<T> typeToken);
		
		public <T> T fromXml(final String xml,final Charset charset,
							 final Class<T> type);
		
		public <T> T fromXml(final String xml,
							  final Class<T> type);
		
		public <T> T fromXml(final String xml,final Charset charset,
							 final TypeToken<T> typeToken);
		
		public <T> T fromXml(final String xml,
							 final TypeToken<T> typeToken);
	
		// ================================================================================
		public <T> T fromJson(final InputStream is,
							  final Class<T> type);
		
		public <T> T fromJson(final InputStream is,
							  final TypeToken<T> typeToken);
		
		public <T> T fromJson(final String json,final Charset charset,
							  final Class<T> type);
		
		public <T> T fromJson(final String json,
							  final Class<T> type);		
		
		public <T> T fromJson(final String json,final Charset charset,
							  final TypeToken<T> typeToken);
		
		public <T> T fromJson(final String json,
							  final TypeToken<T> typeToken);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WRITE	
/////////////////////////////////////////////////////////////////////////////////////////	
	public interface MarshallerWriteToStream {
		public <T> void to(final MarshallFormat format,
						   final T obj,
						   final OutputStream os);
		
		public <T> String to(final MarshallFormat format,
							 final T obj,final Charset charset);
		
		public <T> String to(final MarshallFormat format,
							 final T obj);
		// ================================================================================
		public <T> void toXml(final T obj,
							  final OutputStream os);
		public <T> void toXml(final T obj,
							  final Writer w);
		
		public <T> String toXml(final T obj,final Charset charset);
		
		public <T> String toXml(final T obj);
		// ================================================================================		
		public <T> void toJson(final T obj,
							   final OutputStream os);
		public <T> void toJson(final T obj,
							   final Writer w);
		
		public <T> String toJson(final T obj,final Charset charset);
		
		public <T> String toJson(final T obj);
	}
}
