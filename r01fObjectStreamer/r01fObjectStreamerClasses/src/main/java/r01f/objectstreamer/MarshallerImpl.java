package r01f.objectstreamer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.objectstreamer.annotations.MarshallFormat;
import r01f.patterns.Memoized;

@RequiredArgsConstructor
public class MarshallerImpl
  implements Marshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Set<AppCode> _appCodes;
	private final Set<? extends MarshallerModule> _jacksonModules;
	private final Charset _defaultCharset;

	private final Memoized<MarshallerMapperForXml> _marshallerXmlMapper = new Memoized<MarshallerMapperForXml>() {
																				@Override
																				protected MarshallerMapperForXml supply() {
																					return new MarshallerMapperForXml(_appCodes,
																													  _jacksonModules);
																				}
																	   };
	private final Memoized<MarshallerMapperForJson> _marshallerJsonMapper = new Memoized<MarshallerMapperForJson>() {
																				@Override
																				protected MarshallerMapperForJson supply() {
																					return new MarshallerMapperForJson(_appCodes,
																													   _jacksonModules);
																				}
																	   };
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerImpl findTypesToMarshallAt(final AppCode... appCodes) {
		return this.findTypesToMarshallAt(Lists.newArrayList(appCodes));
	}
	public MarshallerImpl findTypesToMarshallAt(final Collection<AppCode> appCodes) {
		_appCodes.addAll(appCodes);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	READ
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public MarshallerReadFromStream forReading() {
		return new MarshallerReadFromStream() {
						@Override
						public <T> T from(final InputStream is,final MarshallFormat format,
										  final Class<T> type) {
							if (format == MarshallFormat.XML) {
								return this.fromXml(is,
										  			type);
							} else if (format == MarshallFormat.JSON) {
								return this.fromJson(is,
													 type);
							} else {
								throw new IllegalArgumentException(format + " is NOT a supported marshall format!");
							}
						}
						@Override
						public <T> T from(final InputStream is,final MarshallFormat format,
										  final TypeToken<T> typeToken) {
							if (format == MarshallFormat.XML) {
								return this.fromXml(is,
										  			typeToken);
							} else if (format == MarshallFormat.JSON) {
								return this.fromJson(is,
													 typeToken);
							} else {
								throw new IllegalArgumentException(format + " is NOT a supported marshall format!");
							}
						}
						@Override
						public <T> T from(final String xml,final Charset charset,final MarshallFormat format,
										  final Class<T> type) {
							return this.from(new ByteArrayInputStream(xml.getBytes(charset)),format,
										 	 type);
						}
						@Override
						public <T> T from(final String xml,final MarshallFormat format,
										  final Class<T> type) {
							return this.from(xml,_defaultCharset,format,
											 type);
						}
						@Override
						public <T> T from(final String xml,final Charset charset,final MarshallFormat format,
										  final TypeToken<T> typeToken) {
							return this.from(new ByteArrayInputStream(xml.getBytes(charset)),format,
										 	 typeToken);
						}
						@Override
						public <T> T from(final String xml,final MarshallFormat format,
										  final TypeToken<T> typeToken) {
							return this.from(xml,_defaultCharset,format,
											 typeToken);
						}
			
						// ================================================================================
						@Override
						public <T> T fromXml(final InputStream is,
											 final Class<T> type) {
							MarshallerMapperForXml xmlMapper = _marshallerXmlMapper.get();
							return this.<T>_readFromXml(xmlMapper,
														is,
														xmlMapper.constructType(type));
						}
						@Override
						public <T> T fromXml(final InputStream is,
											 final TypeToken<T> typeToken) {
							MarshallerMapperForXml xmlMapper = _marshallerXmlMapper.get();
							return this.<T>_readFromXml(xmlMapper,
														is,
														xmlMapper.constructType(typeToken.getType()));
						}
						@Override
						public <T> T fromXml(final String xml,final Charset charset,
											 final Class<T> type) {
							return this.fromXml(new ByteArrayInputStream(xml.getBytes(charset)),
										 		type);
						}
						@Override
						public <T> T fromXml(final String xml,
											 final Class<T> type) {
							return this.fromXml(xml,_defaultCharset,
												type);
						}
						@Override
						public <T> T fromXml(final String xml,final Charset charset,
											 final TypeToken<T> typeToken) {
							return this.fromXml(new ByteArrayInputStream(xml.getBytes(charset)),
										 		typeToken);
						}
						@Override
						public <T> T fromXml(final String xml,
											 final TypeToken<T> typeToken) {
							return this.fromXml(xml,_defaultCharset,
												typeToken);
						}
						
						// ================================================================================
						@Override
						public <T> T fromJson(final InputStream is,
											  final Class<T> type) {
							MarshallerMapperForJson jsonMapper = _marshallerJsonMapper.get();
							return this.<T>_readFromJson(jsonMapper,
												 		 is,
												 		 jsonMapper.constructType(type));
						}
						@Override
						public <T> T fromJson(final InputStream is,
											  final TypeToken<T> typeToken) {
							MarshallerMapperForJson jsonMapper = _marshallerJsonMapper.get();
							return this.<T>_readFromJson(jsonMapper,
												 		 is,
												 		 jsonMapper.constructType(typeToken.getType()));
						}
						@Override
						public <T> T fromJson(final String json,final Charset charset,
											  final Class<T> type) {
							return this.fromJson(new ByteArrayInputStream(json.getBytes()),
												 type);
						}
						@Override
						public <T> T fromJson(final String json,
											  final Class<T> type) {
							return this.fromJson(json,_defaultCharset,
												 type);
						}
						@Override
						public <T> T fromJson(final String json,final Charset charset,
											  final TypeToken<T> typeToken) {
							return this.fromJson(new ByteArrayInputStream(json.getBytes(charset)),
												 typeToken);
						}
						@Override
						public <T> T fromJson(final String json,
											  final TypeToken<T> typeToken) {
							return this.fromJson(json,_defaultCharset,
												 typeToken);
						}
						private <T> T _readFromXml(final MarshallerMapperForXml xmlMapper,
												   final InputStream is,
												   final JavaType type) {
							try {
								T outObj = xmlMapper.<T>readValue(is,
															   	  type);
								return outObj;
							} catch (Throwable th) {
								th.printStackTrace();
								throw new MarshallerException(th);
							}
						}
						private <T> T _readFromJson(final MarshallerMapperForJson jsonMapper,
													final InputStream is,
												  	final JavaType type) {
							// BEWARE: JSON specification states, that only valid encodings are UTF-8, UTF-16 and UTF-32.
							//		   No other encodings (like Latin-1) can be used
							//		   ... so data is ALWAYS readed as UTF by Jackson... if data is NOT in UTF-8 format,
							//			   jackson tries to detect the encoding using the first bytes of the stream
							try {
								T outObj = jsonMapper.<T>readValue(is,
																   type);
								return outObj;
							} catch (Throwable th) {
								throw new MarshallerException(th);
							}
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WRITE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public MarshallerWriteToStream forWriting() {
		return new MarshallerWriteToStream() {
						@Override
						public <T> void to(final MarshallFormat format,
										   final T obj,
										   final OutputStream os) {
							if (format == MarshallFormat.XML) {
								this.toXml(obj,os);
							} else if (format == MarshallFormat.JSON) {
								this.toJson(obj,os);
							} else {
								throw new IllegalArgumentException(format + " is NOT a supported marshall format!"); 
							}
						}
						@Override
						public <T> String to(final MarshallFormat format,
											 final T obj,final Charset charset) {
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							this.to(format,
									obj,os);
							return new String(os.toByteArray(),charset);
						}
						@Override
						public <T> String to(final MarshallFormat format,
											 final T obj) {
							return this.to(format,
										   obj,_defaultCharset);
						}
						
						// ================================================================================
						@Override
						public <T> void toXml(final T obj,
											  final OutputStream os) {
							// BEWARE: JSON specification states, that only valid encodings are UTF-8, UTF-16 and UTF-32.
							//		   No other encodings (like Latin-1) can be used
							//		   ... so data is ALWAYS written as UTF by Jackson
							MarshallerMapperForXml xmlMapper = _marshallerXmlMapper.get();
							try {
								xmlMapper.writeValue(os,
													 obj);
							} catch (Throwable th) {
								throw new MarshallerException(th);
							}
						}
						@Override
						public <T> String toXml(final T obj,final Charset charset) {
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							this.toXml(obj,os);
							return new String(os.toByteArray(),charset);
						}
						@Override
						public <T> String toXml(final T obj) {
							return this.toXml(obj,_defaultCharset);
						}
						
						// ================================================================================
						@Override
						public <T> void toJson(final T obj,
											   final OutputStream os) {
							// BEWARE: JSON specification states, that only valid encodings are UTF-8, UTF-16 and UTF-32.
							//		   No other encodings (like Latin-1) can be used
							MarshallerMapperForJson jsonMapper = _marshallerJsonMapper.get();
							try {
								jsonMapper.writeValue(os,
													  obj);
							} catch (Throwable th) {
								throw new MarshallerException(th);
							}
						}
						@Override
						public <T> String toJson(final T obj,final Charset charset) {
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							this.toJson(obj,os);
							return new String(os.toByteArray(),charset);
						}
						@Override
						public <T> String toJson(final T obj) {
							return this.toJson(obj,_defaultCharset);
						}
			   };
	}

}
