package r01f.types;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFormat;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;

/**
 * Contains an object in a serialized format json / xml ...
 * Usage:
 * Given a data type like:
 * <pre class='brush:java'>
 *			@MarshallType(as="data")
 * 			@Accessors(prefix="_")
 *			@NoArgsConstructor @AllArgsConstructor
 *			public static class TestData {
 *				@MarshallField(as="oid",
 *							   whenXml=@MarshallFieldAsXml(attr=true))
 *				@Getter @Setter private String _oid;
 *				
 *				@MarshallField(as="otherData",escape=true)
 *				@Getter @Setter private String _text;
 *
 *
 *				@Override
 *				public boolean equals(final Object other) {
 *					Data otherData = (Data)other;
 *					return otherData.getOid().equals(_oid)
 *						&& otherData.getText().equals(_text);
 *				}
 *				@Override
 *				public int hashCode() {
 *					return Objects.hashCode(_oid,_text);
 *				}
 *			}
 * </pre>
 * 
 * It can be serialized as:
 * <pre class='brush:java'>
 * 		// [1] - Create a data obj
 * 		TestData data = new TestData("oid","This > is some text");
 * 		
 * 		// [2] - Wrap into a serialized data objects
 *		SerializedData<Data> serData = SerializedData.from(data)
 *																 .marshalledUsing(m)
 *																 .toXml();
 * </pre>
 * This object can be used like:
 * <pre class='brush:java'>
 * 		// data as xml
 * 		String xml = serData.getSerializedData();
 * 
 * 		// data as object
 * 		// [1] - Build a marshaller
 * 		//		 (or get it injected)
 * 		Marshaller m =  MarshallerBuilder.findTypesToMarshallAt(AppCode.API.code())
 *									     .build();
 *		// [2] - Serialize
 * 		Data obj = serData.toModelObjUsing(m);
 * 
 * 		Assert.asserEquals(data,obj);O
 * </pre>
 * @param <T>
 */
@MarshallType(as="serializedData")
@Accessors(prefix="_")
public class SerializedData<T> 
  implements Serializable,
  			 CanBeRepresentedAsString {

	private static final long serialVersionUID = -6088268044735624023L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="format",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MarshallFormat _format;
	
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true)) 
	@Getter @Setter private Class<T> _type;
	
	@MarshallField(as="data",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _serializedData;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public SerializedData() {
		super();
	}
	public SerializedData(final MarshallFormat format,
							  final Class<T> type,
							  final String dataString) {
		_format = format;
		_type = type;
		_serializedData = dataString;
	}
	public static <T> SerializedConfigDataBuilderMarshallStep<T> from(final T configObj) {
		return new SerializedConfigDataBuilderMarshallStep<T>(configObj);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class SerializedConfigDataBuilderMarshallStep<T> {
		private final T _obj;
		
		public SerializedConfigDataBuilderFormatStep<T> marshalledUsing(final Marshaller m) {
			return new SerializedConfigDataBuilderFormatStep<T>(_obj,
																	m);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class SerializedConfigDataBuilderFormatStep<T> {
		private final T _obj;
		private final Marshaller _marshaller;
		
		@SuppressWarnings("unchecked")
		public SerializedData<T> toXml() {
			String xml = _marshaller.forWriting().toXml(_obj);
			return new SerializedData<T>(MarshallFormat.XML,
												   (Class<T>)_obj.getClass(),
												   xml);
		}
		@SuppressWarnings("unchecked")
		public SerializedData<T> toJson() {
			String json = _marshaller.forWriting().toJson(_obj);
			return new SerializedData<T>(MarshallFormat.JSON,
												   (Class<T>)_obj.getClass(),
												   json);			
		}
		@SuppressWarnings("unchecked")
		public SerializedData<T> to(final MarshallFormat format) {
			String configAsString = _marshaller.forWriting().to(format,_obj);
			return new SerializedData<T>(format,
												   (Class<T>)_obj.getClass(),
												   configAsString);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STRING
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _serializedData;
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public T toModelObjUsing(final Marshaller m) {
		return m.forReading()
				.from(_serializedData,_format,
					  _type);
	}
}
