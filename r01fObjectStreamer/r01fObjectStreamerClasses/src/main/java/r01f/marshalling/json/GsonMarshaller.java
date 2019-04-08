package r01f.marshalling.json;

import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.Strings;

/**
 * Simple objects JSON marshaller
 * 
 * Usage:
 * <pre class='brush:java'>
 * 		jsonMarshaller.forType(MyType.class)
 * 					  .jsonToObject(jsonStr);
 * </pre>
 * It it's a generic type a TypeToken must be used:
 * <pre class='brush:java'>
 * 		jsonMarshaller.forType(new TypeToken<List<MyType>>() {})
 * 					  .jsonToObject(jsonStr);
 * </pre>
 * Sometimes the type is NOT available; only the type name is
 * (ie, if the name of the type is given as a string to a REST endpoint=
 * Ex: r01f.test.MyType or java.util.List<r01f.test.MyType>
 * In such situation, the marshaller can be used as::
 * <pre class='brush:java'>
 * 		jsonMarshaller.forType("java.util.List<r01f.test.MyType>")
 * 					  .jsonToObject(jsonStr);
 * </pre>
 * 
 * [Marshaller CREATIONs]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
 * ----------------------------------------------------------------------------
 * [OPTION 1]: Use GUICE to inject the marshallerInyectar el Marshaller utilizando GUICE
 * 		The type where the marshaller should be injectd MUST have a annotated field as:
 * 		<pre class='brush:java'>
 * 		public class MyMarshallerService {
 * 			@Inject @JsonMarshaller private JSonMarshaller _jsonMarshaller;
 * 			...
 * 		}
 * 		</pre>
 * 		or using the constructor;
 * 		<pre class='brush:java'>
 * 		public class MyMarshallerService {
 * 			private JSonMarshaller _jsonMarshaller;
 * 			@Inject
 * 			public MyMarshallerService(@JSonMarshaller private JSonMarshaller marshaller) {
 * 				_jsonMarshaller = marshaller;
 * 			}
 * 			...
 * 		}
 *		</pre>
 *
 * [OPTION 2]: Use the GUICE injector directly (not recommended)
 * 		<pre class='brush:java'>
 * 			JSonMarshaller marshaller = Guice.createInjector(new JSonMarshallerGuiceModule())
 *										 	 .getInstance(JSonMarshaller.class)
 *		</pre>
 *
 * [OPTION 3]: NOT using Guice (BEWARE! a new marshallers will be created each time it's created like this)
 * 		<pre class='brush:java'>
 * 			JSonMarshaller jsonMarshaller = JSonMarshaller.create();
 * 		</pre>
 */
@Accessors(prefix="_")
@NoArgsConstructor
@Slf4j
public class GsonMarshaller {
///////////////////////////////////////////////////////////////////////////////
// 	INJECT
///////////////////////////////////////////////////////////////////////////////
	private Gson _gson;

///////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	@Inject
	public GsonMarshaller(final Gson gson) {
		_gson = gson;
	}
	@Inject
	public GsonMarshaller(final Provider<Gson> gsonProvider) {
		_gson = gsonProvider.get();
	}
///////////////////////////////////////////////////////////////////////////////
//  FLUENT
///////////////////////////////////////////////////////////////////////////////
	public JSonMarshallerFromTypeAsString forType(final String type) {
		return new JSonMarshallerFromTypeAsString(type);
	}
	public JSonMarshallerFromTypeAsClass forType(final Class<?> type) {
		return new JSonMarshallerFromTypeAsClass(type);
	}
	public JSonMarshallerFromTypeAsTypeToken forType(final TypeToken<?> typeToken) {
		return new JSonMarshallerFromTypeAsTypeToken(typeToken);
	}
///////////////////////////////////////////////////////////////////////////////
// 	Marshaller using a type name as a string
///////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class JSonMarshallerFromTypeAsString {
		private final String _type;
		
		@SuppressWarnings("unchecked")
		public <T> T jsonToObject(final String json) {
			return _type != null ? (T)_gson.fromJson(json,_objectType(_type))
								 : null;
		}
		public <T> String objectToJson(final T value) {
			return _type != null ? _gson.toJson(value,_objectType(_type))
								 : _gson.toJson(value);
		}
		/**
		 * Gets the Type from it-s String rep
		 * @param type the type in String format
		 * @return a {@link Type}
		 */
		private Type _objectType(final String type) {
			if (Strings.isNullOrEmpty(type)) throw new IllegalArgumentException("The type cannot be null in order to get its Type");
			Type objType = null;
			try {
				objType = ReflectionUtils.getObjectType(type);
			} catch(ReflectionException refEx) {
				log.error("The type {} contains a class which could NOT be found in the classPath!",type,refEx);
			}
			return objType;
		}
	}
///////////////////////////////////////////////////////////////////////////////
// 	Marshaller using a type
///////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class JSonMarshallerFromTypeAsClass {
		private final Class<?> _type;
		
		@SuppressWarnings("unchecked")
		public <T> T jsonToObject(final String json) {
			return _type != null ? (T)_gson.fromJson(json,_type)
								 : null;
		}
		public <T> String objectToJson(final T value) {
			return _type != null ? _gson.toJson(value,_type)
								 : _gson.toJson(value);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// Marshaller using a TypeRef
///////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class JSonMarshallerFromTypeAsTypeToken {
		private final TypeToken<?> _type;
		
		@SuppressWarnings("unchecked")
		public <T> T jsonToObject(final String json) {
			return _type != null ? (T)_gson.fromJson(json,_type.getType())
								 : null;
		}
		public <T> String objectToJson(final T value) {
			return _type != null ? _gson.toJson(value,_type.getType())
								 : _gson.toJson(value);
		}
	}

}
