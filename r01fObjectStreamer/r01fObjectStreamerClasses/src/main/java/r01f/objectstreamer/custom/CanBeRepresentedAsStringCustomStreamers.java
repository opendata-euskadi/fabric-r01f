package r01f.objectstreamer.custom;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.guids.OID;
import r01f.patterns.FactoryFromType;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.collections.CollectionUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class CanBeRepresentedAsStringCustomStreamers {
/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class CanBeRepresentedAsStringSerializerBase<T extends CanBeRepresentedAsString,
																		SELF_TYPE extends CanBeRepresentedAsStringSerializerBase<T,SELF_TYPE>>
						 extends JsonSerializer<T>
 			 		  implements ContextualSerializer {

		protected final Class<T> _objType;		// type being serialized
		protected BeanProperty _property;		// property being deserialized

		public CanBeRepresentedAsStringSerializerBase(final Class<T> objType) {
			super();
			_objType = objType;
		}
		public CanBeRepresentedAsStringSerializerBase(final Class<T> objType,
													  final BeanProperty property) {
			super();
			_objType = objType;
			_property = property;
		}
		/**
		 * Creates a new serializer
		 * @param objType
		 * @param property
		 * @return
		 */
		protected abstract SELF_TYPE cloneFrom(final Class<T> objType,final BeanProperty property);

		@Override
		public JsonSerializer<?> createContextual(final SerializerProvider prov,
												  final BeanProperty property) throws JsonMappingException {
			return _property == property ? this		// collections
										 : this.cloneFrom(_objType,
												 		  property);
		}
		@Override
		public void serialize(final T value,
							  final JsonGenerator gen,final SerializerProvider provider) throws IOException,
																								 JsonProcessingException {
			if (_property != null) {
				// a) serialize the object being a field of a container object
				gen.writeString(value.asString());
			}
			else {
				// b) serialize the object itself as { [rootName] : [value] }
				if (gen instanceof ToXmlGenerator) {
					// if serializing as xml DO NOT include the root name since it's already included
					gen.writeString(value.asString());
				} else {
					String typeRootName = CustomStreamers.typeRootName(provider.getConfig(),
																	   _objType);
					gen.writeStartObject();
					gen.writeFieldName(typeRootName);
					gen.writeString(value.asString());
					gen.writeEndObject();
				}
			}
		}
	}
	public static abstract class CanBeRepresentedAsStringDeserializerBase<T extends CanBeRepresentedAsString,
																		  SELF_TYPE extends CanBeRepresentedAsStringDeserializerBase<T,SELF_TYPE>>
						 extends StdDeserializer<T>
					  implements ContextualDeserializer {	// needed to know which field is being deserialized

		private static final long serialVersionUID = 8141066318006052053L;

		// object factory
		protected final FactoryFromType<T> _objFactory;

		// property being deserialized
		protected BeanProperty _property;

		public CanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
														final FactoryFromType<T> objFactory) {
			super(objType);
			_objFactory = objFactory;
		}
		public CanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
														final FactoryFromType<T> objFactory,
														final BeanProperty property) {
			this(objType,objFactory);
			_property = property;
		}
		public CanBeRepresentedAsStringDeserializerBase(final Class<T> objType) {
			super(objType);
			_objFactory = new FactoryFromType<T>() {
									@Override
									public T create(final Class<? extends T> type,final Object... args) {
										Preconditions.checkArgument(CollectionUtils.hasData(args) && args.length == 1 && args[0] instanceof String,
																	"No string representation was handed to create the object!");
										return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
									}
						  };
		}
		public CanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
														final BeanProperty property) {
			this(objType);
			_property = property;
		}
		/**
		 * Creates a new serializer
		 * @param objType
		 * @param objFactory
		 * @param property
		 * @return
		 */
		protected abstract SELF_TYPE cloneFrom(final Class<T> objType,final FactoryFromType<T> objFactory,final BeanProperty property);

		@Override @SuppressWarnings("unchecked")
		public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,
													final BeanProperty property) throws JsonMappingException {
			return _property == property ? this		// collections
										 : this.cloneFrom((Class<T>)_valueClass,
												 		  _objFactory,
												 		  property);
		}
		@Override @SuppressWarnings("unchecked")
		public T deserialize(final JsonParser parser,
							 final DeserializationContext ctxt) throws IOException,
																	   JsonProcessingException {
			String objAsString = null;				// the obj as string

			JsonNode node = parser.getCodec().readTree(parser); 	// reads all tree!!

			// [1] - Read the object string representation depending on the serialized format
			if (node.getNodeType() == JsonNodeType.STRING) {
				// a) the object was serialized being a field of a container object as [value]
				objAsString = ((TextNode)node).asText();
			}
			else {
				// b) the object was serialized itself as as { [rootName] : [value] }
				ObjectNode objNode = (ObjectNode)node;

				Map<String,String> propValues = Maps.newLinkedHashMapWithExpectedSize(2);
				for (Iterator<Map.Entry<String,JsonNode>> fIt = objNode.fields(); fIt.hasNext(); ) {
					Map.Entry<String,JsonNode> me = fIt.next();
					propValues.put(me.getKey(),me.getValue().asText());
				}
				if (parser instanceof FromXmlParser) {
					// if serialized as xml, the node only contains the value without property name:  <rootName>value</rootName>
					objAsString = propValues.get("");	// no property
				} else {
					String typeRootName = CustomStreamers.typeRootName(ctxt.getConfig(),
																	   _valueClass);
					objAsString = propValues.get(typeRootName);
				}
			}

			// [2] - Get the oid type
			T outObj = _objFactory.create((Class<? extends T>)_valueClass,objAsString); 	// ReflectionUtils.createInstanceFromString(objType,objAsString);
			return outObj;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	POLYMORPHIC CanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class PolymorphicCanBeRepresentedAsStringSerializerBase<T extends CanBeRepresentedAsString,
																				   SELF_TYPE extends PolymorphicCanBeRepresentedAsStringSerializerBase<T,SELF_TYPE>>
						 extends CanBeRepresentedAsStringSerializerBase<T,SELF_TYPE> {

		// the serialized property name for the string representation of the object
		protected final String _valuePropName;

		public PolymorphicCanBeRepresentedAsStringSerializerBase(final Class<T> objType,
							 						  		 	 final String valuePropName) {
			super(objType);
			_valuePropName = valuePropName;
		}
		public PolymorphicCanBeRepresentedAsStringSerializerBase(final Class<T> objType,
															 	 final BeanProperty property,
															 	 final String valuePropName) {
			super(objType,
				  property);
			_valuePropName = valuePropName;
		}
		@Override
		public void serializeWithType(final T value,
									  final JsonGenerator gen,final SerializerProvider serializers,final TypeSerializer typeSer) throws IOException {
			// [1] - guess the type id
			WritableTypeId typeId = typeSer.typeId(value,JsonToken.START_OBJECT);

			// [2] - type prefix (ass attribute)
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsAttribute(true);	// ... the dataTypeId attr
			typeSer.writeTypePrefix(gen,
									typeId);
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsAttribute(false);	// ... the object itself is NOT an attribute

			// [3] - The object


			// object
			// ... usually just call this type's serialize method
			// this.serialize(value,
			// 				  jgen,provider); // call customized serialize method
			gen.writeFieldName("value");
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsUnwrapped(true);
			gen.writeString(value.asString());
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsUnwrapped(false);

			// type suffix
			typeId.wrapperWritten = !gen.canWriteTypeId();
			typeSer.writeTypeSuffix(gen,
									typeId);
		}
	}
	public static abstract class PolymorphicCanBeRepresentedAsStringDeserializerBase<T extends CanBeRepresentedAsString,
																					 SELF_TYPE extends PolymorphicCanBeRepresentedAsStringDeserializerBase<T,SELF_TYPE>>
		 				 extends CanBeRepresentedAsStringDeserializerBase<T,SELF_TYPE> {

		private static final long serialVersionUID = 8141066318006052053L;

		// the serialized property name for the string representation of the object
		protected final String _valuePropName;

		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,final FactoryFromType<T> objFactory,
							   								   	   final String valuePropName) {
			super(objType,objFactory);
			_valuePropName = valuePropName;
		}
		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,final FactoryFromType<T> objFactory,
															   	   final BeanProperty property,
															   	   final String valuePropName) {
			this(objType,objFactory,
				 valuePropName);
			_property = property;
		}
		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
							   								   	   final String valuePropName) {
			this(objType,new FactoryFromType<T>() {
									@Override
									public T create(final Class<? extends T> type,final Object... args) {
										return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
									}
						  },
				 valuePropName);
		}
		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
															   	   final BeanProperty property,
															   	   final String valuePropName) {
			this(objType,new FactoryFromType<T>() {
									@Override
									public T create(final Class<? extends T> type,final Object... args) {
										return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
									}
						  },
				 property,
				 valuePropName);
		}
		@Override @SuppressWarnings("unchecked")
		public T deserialize(final JsonParser parser,
							 final DeserializationContext ctxt) throws IOException,
																	   JsonProcessingException {
			String objAsStringVal = null;			// the obj as string
			Class<? extends T> objType = null;		// the obj type

			JsonNode node = parser.getCodec().readTree(parser);

			// [1] - Read the object string representation depending on the serialized format
			if (node.getNodeType() == JsonNodeType.STRING) {
				// a) the object was serialized being a field of a container object as [value]
				objAsStringVal = ((TextNode)node).asText();
				if (_property != null) {
					if (_property.getType().isContainerType()
					 || _property.getType().isArrayType()) {
						objType = (Class<? extends T>)_property.getType().getContentType().getRawClass();
					} else {
						objType = (Class<? extends T>)_property.getType().getRawClass();
					}
				} else if (_valueClass != null
						&& ReflectionUtils.isInstanciable(_valueClass)) {
					objType = (Class<? extends T>)_valueClass;
				} else {
					ctxt.reportInputMismatch(this,"Cannot get the oid type for value=%s",objAsStringVal);
				}
			}
			else {
				ObjectNode objNode = (ObjectNode)node;

				Map<String,String> propValues = Maps.newLinkedHashMapWithExpectedSize(2);
				for (Iterator<Map.Entry<String,JsonNode>> fIt = objNode.fields(); fIt.hasNext(); ) {
					Map.Entry<String,JsonNode> me = fIt.next();
					propValues.put(me.getKey(),me.getValue().asText());
				}
				// b) abstract obj impl serialized as { typeId=[type],value={value] }
				if (propValues.size() == 2) {	// beware!! the typeId property MUST be visible!!
					objAsStringVal = propValues.get(_valuePropName);
					// TODO find a better way to check the format (now 1) try propValues.get({propName}) if NOT found try propValues.get({propName})
					// if serialized as xml, the node only contains the value without property name:  <rootName typeId="{typeId}">value</rootName>
					if (objAsStringVal == null) objAsStringVal = propValues.get("");

					String typeId = propValues.get(CustomStreamers.TYPE_ID_PROPERTY_NAME);	// objNode.findValue(CustomStreamers.TYPE_ID_PROPERTY_NAME).asText();
					objType = _objTypeFromId(ctxt.getAnnotationIntrospector(),
										  	 					   ctxt.getConfig(),
										  	 					   typeId);
				}
				// c) the object was serialized itself as { [type's root name] : [value] }
				else {
					if (parser instanceof FromXmlParser) {
						// if serialized as xml, the node only contains the value without property name:  <rootName>value</rootName>
						objAsStringVal = propValues.get("");	// no property
					} else {
						String typeRootName = CustomStreamers.typeRootName(ctxt.getConfig(),
																		   _valueClass);
						objAsStringVal = propValues.get(typeRootName);
					}
					objType = (Class<? extends T>)_valueClass;
				}
			}

			// [2] - Get the oid type
			if (objType == null) ctxt.reportInputMismatch(this,"Could NOT guess object type!");

			T outObj = _objFactory.create(objType,objAsStringVal); 	// ReflectionUtils.createInstanceFromString(objType,objAsString);
			return outObj;
		}
		@SuppressWarnings("unchecked")
		private Class<? extends T> _objTypeFromId(final AnnotationIntrospector annotationIntrospector,
												  final DeserializationConfig cfg,
												  final String typeId) {
			Class<? extends T> outType = null;
			AnnotatedClass annotatedClass = AnnotatedClassResolver.resolveWithoutSuperTypes(cfg,
																							OID.class);
			List<NamedType> subtypes = annotationIntrospector.findSubtypes(annotatedClass);
			for (NamedType type: subtypes) {
				if (type.getName().equals(typeId)) {
					outType = (Class<? extends T>)type.getType();
				}
			}
			return outType;
		}
	}
}
