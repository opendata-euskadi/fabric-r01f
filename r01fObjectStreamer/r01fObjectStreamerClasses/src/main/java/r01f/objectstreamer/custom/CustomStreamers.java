package r01f.objectstreamer.custom;

import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.objectstreamer.custom.CanBeRepresentedAsStringCustomStreamers.CanBeRepresentedAsStringDeserializerBase;
import r01f.objectstreamer.custom.CanBeRepresentedAsStringCustomStreamers.CanBeRepresentedAsStringSerializerBase;
import r01f.objectstreamer.custom.CanBeRepresentedAsStringCustomStreamers.PolymorphicCanBeRepresentedAsStringDeserializerBase;
import r01f.objectstreamer.custom.CanBeRepresentedAsStringCustomStreamers.PolymorphicCanBeRepresentedAsStringSerializerBase;
import r01f.patterns.FactoryFromType;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.locale.Languages;

@Slf4j
public class CustomStreamers {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	// the serialized property name for the type (when the field being serialized is defined with an abstract / interface type)
	public final static String TYPE_ID_PROPERTY_NAME = "typeId";
	private final static String RANGE_TYPE_ID_PROPERTY_NAME = TYPE_ID_PROPERTY_NAME;
	private final static String RANGE_SPEC_PROPERTY_NAME = "spec";

/////////////////////////////////////////////////////////////////////////////////////////
//	UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	static String typeRootName(final MapperConfigBase<?,?> cfg,
							   final Class<?> type) {
		AnnotatedClass objTypeAnnotated = AnnotatedClassResolver.resolveWithoutSuperTypes(cfg,
																					  	  type);
		if (!objTypeAnnotated.hasAnnotation(MarshallType.class)) throw new AnnotationFormatError(String.format("Type %s does NOT have the @%s annotation",
																											   type,MarshallType.class.getSimpleName()));
		return objTypeAnnotated.getAnnotation(MarshallType.class).as();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class CanBeRepresentedAsStringSerializer<T extends CanBeRepresentedAsString>
				extends CanBeRepresentedAsStringSerializerBase<T,CanBeRepresentedAsStringSerializer<T>> {
		public CanBeRepresentedAsStringSerializer(final Class<T> oidType) {
			super(oidType);
		}
		public CanBeRepresentedAsStringSerializer(final Class<T> objType,
							 					  final BeanProperty property) {
			super(objType,
				  property);
		}
		@Override
		protected CanBeRepresentedAsStringSerializer<T> cloneFrom(final Class<T> objType,final BeanProperty property) {
			return new CanBeRepresentedAsStringSerializer<T>(objType,property);
		}
	}
	public static class CanBeRepresentedAsStringDeserializer<T extends CanBeRepresentedAsString>
		 		extends CanBeRepresentedAsStringDeserializerBase<T,CanBeRepresentedAsStringDeserializer<T>> {

		private static final long serialVersionUID = -5273319484446776784L;

		public CanBeRepresentedAsStringDeserializer(final Class<T> objType) {
			super(objType,new FactoryFromType<T>() {
									@Override
									public T create(final Class<? extends T> type,final Object... args) {
										return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
									}
						  });
		}
		public CanBeRepresentedAsStringDeserializer(final Class<T> objType,final FactoryFromType<T> objFactory,
							   						final BeanProperty property) {
			super(objType,objFactory,
				  property);
		}
		@Override
		protected CanBeRepresentedAsStringDeserializer<T> cloneFrom(final Class<T> objType,final FactoryFromType<T> objFactory,final BeanProperty property) {
			return new CanBeRepresentedAsStringDeserializer<T>(objType,objFactory,property);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PolymorphicCanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class PolymorphicCanBeRepresentedAsStringSerializer<T extends CanBeRepresentedAsString>
				extends PolymorphicCanBeRepresentedAsStringSerializerBase<T,PolymorphicCanBeRepresentedAsStringSerializer<T>> {
		public PolymorphicCanBeRepresentedAsStringSerializer(final Class<T> oidType,
															 final String valuePropName) {
			super(oidType,
				  valuePropName);
		}
		public PolymorphicCanBeRepresentedAsStringSerializer(final Class<T> objType,
							 					  			 final BeanProperty property,
							 					  			 final String valuePropName) {
			super(objType,
				  property,
				  valuePropName);
		}
		@Override
		protected PolymorphicCanBeRepresentedAsStringSerializer<T> cloneFrom(final Class<T> objType,final BeanProperty property) {
			return new PolymorphicCanBeRepresentedAsStringSerializer<T>(objType,property,
																		_valuePropName);
		}
	}
	public static class PolymorphicCanBeRepresentedAsStringDeserializer<T extends CanBeRepresentedAsString>
		 		extends PolymorphicCanBeRepresentedAsStringDeserializerBase<T,PolymorphicCanBeRepresentedAsStringDeserializer<T>> {

		private static final long serialVersionUID = -5273319484446776784L;

		public PolymorphicCanBeRepresentedAsStringDeserializer(final Class<T> objType,
															   final String valuePropName) {
			super(objType,new FactoryFromType<T>() {
									@Override
									public T create(final Class<? extends T> type,final Object... args) {
										return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
									}
						  },
				 valuePropName);
		}
		public PolymorphicCanBeRepresentedAsStringDeserializer(final Class<T> objType,final FactoryFromType<T> objFactory,
							   								   final BeanProperty property,
							   								   final String valuePropName) {
			super(objType,objFactory,
				  property,
				  valuePropName);
		}
		@Override
		protected PolymorphicCanBeRepresentedAsStringDeserializer<T> cloneFrom(final Class<T> objType,final FactoryFromType<T> objFactory,final BeanProperty property) {
			return new PolymorphicCanBeRepresentedAsStringDeserializer<T>(objType,objFactory,property,
																		  _valuePropName);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OID SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class OIDSerializer<O extends OID>
				extends PolymorphicCanBeRepresentedAsStringSerializerBase<O,OIDSerializer<O>> {
		public OIDSerializer(final Class<O> oidType) {
			super(oidType,
				  "value");
		}
		public OIDSerializer(final Class<O> oidType,
							 final BeanProperty property) {
			super(oidType,
				  property,
				  "value");
		}
		@Override
		protected OIDSerializer<O> cloneFrom(final Class<O> objType,final BeanProperty property) {
			return new OIDSerializer<O>(objType,property);
		}
	}
	public static class OIDDeserializer<O extends OID>
		 		extends PolymorphicCanBeRepresentedAsStringDeserializerBase<O,OIDDeserializer<O>> {

		private static final long serialVersionUID = -5273319484446776784L;

		public OIDDeserializer(final Class<O> oidType) {
			super(oidType,new FactoryFromType<O>() {
									@Override
									public O create(final Class<? extends O> type,final Object... args) {
										return ReflectionUtils.<O>createInstanceFromString(type,(String)args[0]);
									}
						  },
				  "value");
		}
		public OIDDeserializer(final Class<O> oidType,final FactoryFromType<O> objFactory,
							   final BeanProperty property) {
			super(oidType,objFactory,
				  property,
				  "value");
		}
		@Override
		protected OIDDeserializer<O> cloneFrom(final Class<O> objType,final FactoryFromType<O> objFactory,final BeanProperty property) {
			return new OIDDeserializer<O>(objType,objFactory,property);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	public static class RangeSerializer<T extends Comparable<T>>
				extends JsonSerializer<Range<T>>
	 		 implements ContextualSerializer {

		protected BeanProperty _property;			// property being deserialized

		public RangeSerializer() {
			// no-args constructor
		}
		public RangeSerializer(final BeanProperty property) {
			_property = property;
		}

		@Override
		public JsonSerializer<?> createContextual(final SerializerProvider prov,
												  final BeanProperty property) throws JsonMappingException {
			return _property == property ? this		// collections
										 : new RangeSerializer<T>(property);
		}
		@Override
		public void serialize(final Range<T> value,
							  final JsonGenerator gen,final SerializerProvider provider) throws IOException {
			// [0] - Guess the range data type
			Class<?> rangeDataType = Range.guessDataType(value);
			String rangeDataTypeId = rangeDataType.getSimpleName();
			String rangeSpec = value.asString();

			// [1] - Serialize
			if (_property != null) {
				// a) serialize the object being a field of a container object
				if (gen instanceof ToXmlGenerator) {
					// --- XML
					ToXmlGenerator xgen = (ToXmlGenerator)gen;
					MarshallField fmAnn = _property.getAnnotation(MarshallField.class);
					if (fmAnn != null
					 && fmAnn.whenXml() != null
					 && fmAnn.whenXml().attr()) {	// attribute
						// serialize as an XML attribute as [range_type_id]:[range_spec]
						xgen.setNextIsAttribute(true);
						//xgen.writeFieldName(fmAnn.as());	// ??? 2018/03 > this line was NOT previously present BUT it seems necessary
						//									// 2018/04 > commented!! if not fails... it seems it's NOT necessary
						xgen.writeString(String.format("%s:%s",
							   			 			   rangeDataTypeId,rangeSpec));
					} else {
						// serialize the object itself as an XML element <propName dataTypeId=[typeId]>[rangeSpec]</propName>
						_writeRangeTypedWrapped(rangeDataTypeId,rangeSpec,
										 		gen);
					}
				} else {
					// --- JSON
					gen.writeString(String.format("%s:%s",
						   			 			  rangeDataTypeId,rangeSpec));
//					// serialize the object itself [propName] = { typeId=[typeId],rangeSpec=[value] }
//					_writeRangeTypedWrapped(rangeDataTypeId,rangeSpec,
//									 		gen);
				}
			}
			else {
				// b) serialize the object itself as { [rootName] : { typeId=[typeId],rangeSpec=[value] } }
				_writeRangeTypedWrapped(rangeDataTypeId,rangeSpec,
								 		gen);
			}
		}
		private static void _writeRangeTypedWrapped(final String rangeDataTypeId,final String rangeSpec,
									  		 		final JsonGenerator gen) throws IOException {
			gen.writeStartObject();
			_writeRangeTyped(rangeDataTypeId,rangeSpec,
							 gen);
			gen.writeEndObject();
		}
		private static void _writeRangeTyped(final String rangeDataTypeId,final String rangeSpec,
									  		 final JsonGenerator gen) throws IOException {
			if ( !(gen instanceof ToXmlGenerator)) {
				gen.writeFieldName(RANGE_TYPE_ID_PROPERTY_NAME);
				gen.writeString(rangeDataTypeId);
				gen.writeFieldName(RANGE_SPEC_PROPERTY_NAME);
				gen.writeString(rangeSpec);
			}
			else if (gen instanceof ToXmlGenerator) {
				ToXmlGenerator xgen = (ToXmlGenerator)gen;
				// ... the dataTypeId attr
				xgen.setNextIsAttribute(true);
				xgen.writeFieldName(RANGE_TYPE_ID_PROPERTY_NAME);	//xgen.setNextName(QName.valueOf(RANGE_TYPE_ID_PROPERTY_NAME));
				xgen.writeString(rangeDataTypeId);
				// ... the range spec
				xgen.setNextIsAttribute(false);
				xgen.setNextIsUnwrapped(true);
				xgen.writeFieldName("fake_field_name");		// this field name is NEVER written to the XML output since NEXT IS UNWRAPPED was previously set
				xgen.writeString(rangeSpec);
			}
		}
	}
	private static final Pattern RANGE_XML_ATTR_PATTERN = Pattern.compile("(" + Date.class.getSimpleName() + "|" +
																				LocalDate.class.getSimpleName() + "|" +
																				LocalDateTime.class.getSimpleName() + "|" +
																				LocalTime.class.getSimpleName() + "|" +
																				Integer.class.getSimpleName() + "|" +
																				Long.class.getSimpleName() + "|" +
																				Short.class.getSimpleName() + "|" +
																				Double.class.getSimpleName() + "|" +
																				Float.class.getSimpleName() +
																			"):(.+)");
	public static class RangeDeserializer<T extends Comparable<T>>
		 		extends StdDeserializer<Range<T>>
			 implements ContextualDeserializer {	// needed to know which field is being deserialized

		private static final long serialVersionUID = 8141066318006052053L;

		// property being deserialized
		protected BeanProperty _property;

		public RangeDeserializer() {
			super(Range.class);
		}
		public RangeDeserializer(final BeanProperty property) {
			this();
			_property = property;
		}
		@Override
		public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,
													final BeanProperty property) throws JsonMappingException {
			return _property == property ? this		// collections
										 : new RangeDeserializer<T>(property);
		}
		@Override
		public Range<T> deserialize(final JsonParser parser,
							 		final DeserializationContext ctxt) throws IOException,
																	   		  JsonProcessingException {
			String rangeTypeId = null;				// the range type
			String rangeSpec = null;				// the range as string

			JsonNode node = parser.getCodec().readTree(parser); 	// reads all tree!!

			// [1] - Read the object string representation depending on the serialized format
			if (node.getNodeType() == JsonNodeType.STRING) {
				// a) the object was serialized being a field of a container object as [range_type_id]:[range_spec]
				String rangeSerialized = ((TextNode)node).asText();

				Matcher m = RANGE_XML_ATTR_PATTERN.matcher(rangeSerialized);
				if (m.find()) {
					rangeTypeId = m.group(1);
					rangeSpec = m.group(2);
				} else {
					ctxt.reportInputMismatch(this,"Bad range definition: %s. Range expression MUST match %s",
												  rangeSerialized,RANGE_XML_ATTR_PATTERN);
				}
			}
			else {
				// b) the object was serialized itself
				ObjectNode objNode = (ObjectNode)node;

				Map<String,String> propValues = Maps.newLinkedHashMapWithExpectedSize(2);
				for (Iterator<Map.Entry<String,JsonNode>> fIt = objNode.fields(); fIt.hasNext(); ) {
					Map.Entry<String,JsonNode> me = fIt.next();
					if (Strings.isNOTNullOrEmpty(me.getKey())) {
						propValues.put(me.getKey(),me.getValue().asText());
					} else {
						// ... when deserialized from xml as <propName typeId=[rangeTypeId]>[rangeSpec]</propName>
						//	   the property name is ""
						propValues.put(RANGE_SPEC_PROPERTY_NAME,me.getValue().asText());
					}
				}
				rangeTypeId = propValues.get(RANGE_TYPE_ID_PROPERTY_NAME);
				rangeSpec = propValues.get(RANGE_SPEC_PROPERTY_NAME);
			}

			// [2] - Get the oid type
			Range<T> outRange = _createRange(rangeSpec,rangeTypeId);
			return outRange;
		}
		@SuppressWarnings("unchecked")
		private Range<T> _createRange(final String rangeSpec,
									  final String rangeTypeId) {
			Class<?> rangeDataType = null;
			if (rangeTypeId.equals(Date.class.getSimpleName())) {
				rangeDataType = Date.class;
			} else if (rangeTypeId.equals(LocalDate.class.getSimpleName())) {
				rangeDataType = LocalDate.class;
			} else if (rangeTypeId.equals(LocalDateTime.class.getSimpleName())) {
				rangeDataType = LocalDateTime.class;
			} else if (rangeTypeId.equals(LocalTime.class.getSimpleName())) {
				rangeDataType = LocalTime.class;
			} else if (rangeTypeId.equals(Integer.class.getSimpleName())) {
				rangeDataType = Integer.class;
			} else if (rangeTypeId.equals(Long.class.getSimpleName())) {
				rangeDataType = Long.class;
			} else if (rangeTypeId.equals(Short.class.getSimpleName())) {
				rangeDataType = Short.class;
			} else if (rangeTypeId.equals(Double.class.getSimpleName())) {
				rangeDataType = Double.class;
			} else if (rangeTypeId.equals(Float.class.getSimpleName())) {
				rangeDataType = Float.class;
			}
			Range<T> outRange = Range.parse(rangeSpec,
								   			(Class<T>)rangeDataType);
			return outRange;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LANG-TEXTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class LanguageTextsSerializerBase<L extends LanguageTexts>
						  extends JsonSerializer<L>
					   implements ContextualSerializer {

		protected BeanProperty _property;		// property being serialized

		public LanguageTextsSerializerBase() {
			// LanguageTextsMapBacked
		}
		public LanguageTextsSerializerBase(final BeanProperty property) {
			_property = property;
		}

		protected abstract <S extends LanguageTextsSerializerBase<L>> S clone(final BeanProperty property);

		@Override
		public JsonSerializer<?> createContextual(final SerializerProvider prov,final BeanProperty property) throws JsonMappingException {
			return _property == property ? this
										 : this.clone(property);
		}
		@Override @SuppressWarnings("unchecked")
		public void serialize(final LanguageTexts langTexts,
							  final JsonGenerator gen,final SerializerProvider serializers) throws IOException {
			gen.writeStartObject();
			Map<Language,String> langTextsMap = (Map<Language,String>)langTexts;	// for now assume it's a LanguageTextsMapBacked
			for (Map.Entry<Language,String> me : langTextsMap.entrySet()) {
				Language lang = me.getKey();
				String value = me.getValue();
				if (gen instanceof ToXmlGenerator) {
					value = String.format("<![CDATA[%s]]>",value);
					gen.writeFieldName(lang.name());
					gen.writeRawValue(value);
				} else {
					gen.writeStringField(lang.name(),value);
				}
			}
			gen.writeEndObject();
		}
	}
	public static abstract class LanguageTextsDeserializerBase<L extends LanguageTexts>
		 				 extends StdDeserializer<L>
			 		  implements ContextualDeserializer {

		private static final long serialVersionUID = 4888982791994392289L;	// needed to know which field is being deserialized

		protected BeanProperty _property;		// property being deserialized

		public LanguageTextsDeserializerBase(final Class<L> langTextsType) {
			super(langTextsType);
		}
		public LanguageTextsDeserializerBase(final Class<L> langTextsType,
										 	 final BeanProperty property) {
			this(langTextsType);
			_property = property;
		}

		protected abstract <D extends LanguageTextsDeserializerBase<L>> D clone(final BeanProperty property);
		protected abstract L createLanguageTextsInstance(final Class<L> type);

		@Override
		public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,final BeanProperty property) throws JsonMappingException {
			return _property == property ? this
										 : this.clone(property);
		}
		@Override @SuppressWarnings("unchecked")
		public L deserialize(final JsonParser parser,final DeserializationContext ctxt) throws IOException,
																							   JsonProcessingException {
			L outLangTexts = this.createLanguageTextsInstance((Class<L>)this.handledType());

			JsonNode node = parser.getCodec().readTree(parser); 	// reads all tree!!
			for (Iterator<Map.Entry<String,JsonNode>> fIt = node.fields(); fIt.hasNext(); ) {
				Map.Entry<String,JsonNode> f = fIt.next();
				String langStr = f.getKey();
				String text = f.getValue().asText();
				if (Languages.canBe(langStr)) {
					outLangTexts.add(Languages.fromName(langStr),text);
				} else {
					log.error("NOT recognized language: {}",langStr);
				}
			}
			return outLangTexts;
		}
	}
	public static class LanguageTextsSerializer
				extends LanguageTextsSerializerBase<LanguageTexts> {
		public LanguageTextsSerializer() {
			super();
		}
		public LanguageTextsSerializer(final BeanProperty property) {
			super(property);
		}
		@Override @SuppressWarnings("unchecked")
		protected LanguageTextsSerializer clone(final BeanProperty property) {
			return new LanguageTextsSerializer(property);
		}
	}
	public static class LanguageTextsDeserializer
				extends LanguageTextsDeserializerBase<LanguageTexts> {
		private static final long serialVersionUID = -49725565214651477L;

		public LanguageTextsDeserializer() {
			super(LanguageTexts.class);
		}
		public LanguageTextsDeserializer(final BeanProperty property) {
			super(LanguageTexts.class,
				  property);
		}
		@Override @SuppressWarnings("unchecked")
		protected LanguageTextsDeserializer clone(final BeanProperty property) {
			return new LanguageTextsDeserializer(property);
		}
		@Override
		protected LanguageTexts createLanguageTextsInstance(final Class<LanguageTexts> type) {
			return new LanguageTextsMapBacked(); 	// for now asume that all LanguageTexts are Map backed
		}
	}
	public static class LanguageTextsMapBackedSerializer
				extends LanguageTextsSerializerBase<LanguageTextsMapBacked> {
		public LanguageTextsMapBackedSerializer() {
			super();
		}
		public LanguageTextsMapBackedSerializer(final BeanProperty property) {
			super(property);
		}
		@Override @SuppressWarnings("unchecked")
		protected LanguageTextsMapBackedSerializer clone(final BeanProperty property) {
			return new LanguageTextsMapBackedSerializer(property);
		}
	}
	public static class LanguageTextsMapBackedDeserializer
				extends LanguageTextsDeserializerBase<LanguageTextsMapBacked> {
		private static final long serialVersionUID = -49725565214651477L;

		public LanguageTextsMapBackedDeserializer() {
			super(LanguageTextsMapBacked.class);
		}
		public LanguageTextsMapBackedDeserializer(final BeanProperty property) {
			super(LanguageTextsMapBacked.class,
				  property);
		}
		@Override @SuppressWarnings("unchecked")
		protected LanguageTextsMapBackedDeserializer clone(final BeanProperty property) {
			return new LanguageTextsMapBackedDeserializer(property);
		}
		@Override
		protected LanguageTextsMapBacked createLanguageTextsInstance(final Class<LanguageTextsMapBacked> type) {
			return new LanguageTextsMapBacked(); 	// for now asume that all LanguageTexts are Map backed
		}

	}
	/**
	 * (not used...)
	 * Extends {@link AsPropertyTypeDeserializer} that uses a PROPERTY at the source stream
	 * to RESOLVE the object type and DESERIALIZE the object
	 * ... BUT in this case, NO PROPERTY is used, since the {@link LanguageTexts} concrete type
	 *	 guessed from the stream
	 * (see MarshallerTypeResolverBuilderDelegates)
	 */
	public static class LangTextsTypeResolverAndDeserializer
				extends AsPropertyTypeDeserializer {

		private static final long serialVersionUID = 454350390060455955L;

		// called from type resolver builder
		public LangTextsTypeResolverAndDeserializer(final JavaType bt,
							   				  		final TypeIdResolver typeIdResolver,	// gets the type from it's id (ie: MyOID = MyOID.class)
							   				  		final String typePropertyName,
							   				  		final boolean typeIdVisible,
							   				  		final JavaType defaultImpl) {
			super(bt,typeIdResolver,
				  typePropertyName,
				  typeIdVisible,
				  defaultImpl);
		}
		public LangTextsTypeResolverAndDeserializer(final AsPropertyTypeDeserializer src,
							   				  		final BeanProperty property,
							   				  		final TypeIdResolver typeIdResolver) {
			super(src,
				  property);
		}
		@Override
		public Object deserializeTypedFromObject(final JsonParser parser,
												 final DeserializationContext context) throws IOException {
			return new LanguageTextsDeserializer()
							.deserialize(parser,
										 context);
		}
	}
}
