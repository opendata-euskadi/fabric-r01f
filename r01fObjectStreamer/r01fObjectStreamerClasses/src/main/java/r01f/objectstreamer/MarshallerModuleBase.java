package r01f.objectstreamer;

import java.util.Collection;
import java.util.Set;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Sets;

import r01f.guids.OID;
import r01f.internal.R01FAppCodes;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.mime.MimeType;
import r01f.objectstreamer.custom.CustomStreamers.CanBeRepresentedAsStringDeserializer;
import r01f.objectstreamer.custom.CustomStreamers.CanBeRepresentedAsStringSerializer;
import r01f.objectstreamer.custom.CustomStreamers.LanguageTextsDeserializer;
import r01f.objectstreamer.custom.CustomStreamers.LanguageTextsMapBackedDeserializer;
import r01f.objectstreamer.custom.CustomStreamers.LanguageTextsMapBackedSerializer;
import r01f.objectstreamer.custom.CustomStreamers.LanguageTextsSerializer;
import r01f.objectstreamer.custom.CustomStreamers.OIDDeserializer;
import r01f.objectstreamer.custom.CustomStreamers.OIDSerializer;
import r01f.objectstreamer.custom.CustomStreamers.PolymorphicCanBeRepresentedAsStringDeserializer;
import r01f.objectstreamer.custom.CustomStreamers.PolymorphicCanBeRepresentedAsStringSerializer;
import r01f.objectstreamer.custom.CustomStreamers.RangeDeserializer;
import r01f.objectstreamer.custom.CustomStreamers.RangeSerializer;
import r01f.objectstreamer.util.TypeScan;
import r01f.types.AppVersion;
import r01f.types.Color;
import r01f.types.JavaPackage;
import r01f.types.Range;
import r01f.types.StringBase64;
import r01f.types.TimeLapse;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.DayOfWeek;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.SecondOfMinute;
import r01f.types.datetime.Time;
import r01f.types.datetime.Year;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

/**
 * see https://spin.atomicobject.com/2016/07/01/custom-serializer-jackson/
 */
abstract class MarshallerModuleBase
	   extends SimpleModule {

	private static final long serialVersionUID = 803481859958735692L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The java packages that contains objects to be marshalled
	 * (it's used to scan for subtypes of abstract types -see TypeScan.java-)
	 */
	protected final Set<JavaPackage> _javaPackages;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked","rawtypes" })
	public MarshallerModuleBase(final String name,final Version version,
								final Set<JavaPackage> javaPackages) {
		super(name,version);

		// java packages that contains objects to be marshalled
		// ....ensure that r01f is listed
		Set<JavaPackage> theJavaPackages = CollectionUtils.hasData(javaPackages) ? javaPackages
																  	 			 : Sets.<JavaPackage>newLinkedHashSet();
		if (!theJavaPackages.contains(JavaPackage.of(R01FAppCodes.R01_APP_CODE))) theJavaPackages.add(JavaPackage.of(R01FAppCodes.R01_APP_CODE));
		_javaPackages = theJavaPackages;

		// naming strategy
		// BEWARE!!  problems with lombok's noargs constructors: https://github.com/FasterXML/jackson-databind/issues/1197
		//			 need to configure the mapper with: mapper.configure(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES,false);
		this.setNamingStrategy(new MarshallerJacksonPropertyNamingStrategy());

		// SERIALIZERS & DESERIALIZERS ================================================================

		// Add the OID instances serializer & deserializer
		// (note that when introspecting annotations the @JsonSubTypes and @JsonTypeResolver info is injected)
		Collection<Class<? extends OID>> oidImplementingTypes = TypeScan.findSubTypesOfInJavaPackages(OID.class,
																									  _javaPackages);
		if (CollectionUtils.hasData(oidImplementingTypes)) {
			for (Class<? extends OID> oidImplementingType : oidImplementingTypes) {
				this.addSerializer(oidImplementingType,new OIDSerializer(oidImplementingType));
				this.addDeserializer(oidImplementingType,new OIDDeserializer(oidImplementingType));
			}
		}

		// Add the LanguageTexts serializer
		this.addSerializer(LanguageTexts.class,new LanguageTextsSerializer());
		this.addDeserializer(LanguageTexts.class,new LanguageTextsDeserializer());

		this.addSerializer(LanguageTextsMapBacked.class,new LanguageTextsMapBackedSerializer());
		this.addDeserializer(LanguageTextsMapBacked.class,new LanguageTextsMapBackedDeserializer());

		// Add the range serializer & deserializer
		this.addSerializer(Range.class,new RangeSerializer());
		this.addDeserializer(Range.class,new RangeDeserializer());

		// Add the Url serializer & deserializer
		this.addSerializer(Url.class,new CanBeRepresentedAsStringSerializer(Url.class));
		this.addDeserializer(Url.class,new CanBeRepresentedAsStringDeserializer(Url.class));
		
		// Add the StringBase64 serializer & deserializer
		this.addSerializer(StringBase64.class,new CanBeRepresentedAsStringSerializer(StringBase64.class));
		this.addDeserializer(StringBase64.class,new CanBeRepresentedAsStringDeserializer(StringBase64.class));		

		// Add the MimeType serializer & deserializer
		this.addSerializer(MimeType.class,new PolymorphicCanBeRepresentedAsStringSerializer(MimeType.class,"mimeType"));
		this.addDeserializer(MimeType.class,new PolymorphicCanBeRepresentedAsStringDeserializer(MimeType.class,"mimeType"));

		// Add date/time serializer & deserializer
		this.addSerializer(Year.class,new CanBeRepresentedAsStringSerializer(Year.class));
		this.addDeserializer(Year.class,new CanBeRepresentedAsStringDeserializer(Year.class));

		this.addSerializer(MonthOfYear.class,new CanBeRepresentedAsStringSerializer(MonthOfYear.class));
		this.addDeserializer(MonthOfYear.class,new CanBeRepresentedAsStringDeserializer(MonthOfYear.class));

		this.addSerializer(DayOfMonth.class,new CanBeRepresentedAsStringSerializer(DayOfMonth.class));
		this.addDeserializer(DayOfMonth.class,new CanBeRepresentedAsStringDeserializer(DayOfMonth.class));

		this.addSerializer(DayOfWeek.class,new CanBeRepresentedAsStringSerializer(DayOfWeek.class));
		this.addDeserializer(DayOfWeek.class,new CanBeRepresentedAsStringDeserializer(DayOfWeek.class));

		this.addSerializer(Time.class,new CanBeRepresentedAsStringSerializer(Time.class));
		this.addDeserializer(Time.class,new CanBeRepresentedAsStringDeserializer(Time.class));

		this.addSerializer(HourOfDay.class,new CanBeRepresentedAsStringSerializer(HourOfDay.class));
		this.addDeserializer(HourOfDay.class,new CanBeRepresentedAsStringDeserializer(HourOfDay.class));

		this.addSerializer(MinuteOfHour.class,new CanBeRepresentedAsStringSerializer(MinuteOfHour.class));
		this.addDeserializer(MinuteOfHour.class,new CanBeRepresentedAsStringDeserializer(MinuteOfHour.class));

		this.addSerializer(SecondOfMinute.class,new CanBeRepresentedAsStringSerializer(SecondOfMinute.class));
		this.addDeserializer(SecondOfMinute.class,new CanBeRepresentedAsStringDeserializer(SecondOfMinute.class));

		// Add TimeLapse serializer & desarializer
		this.addSerializer(TimeLapse.class,new CanBeRepresentedAsStringSerializer(TimeLapse.class));
		this.addDeserializer(TimeLapse.class,new CanBeRepresentedAsStringDeserializer(TimeLapse.class));

		// Add color serializer & deserializer
		this.addSerializer(Color.class,new CanBeRepresentedAsStringSerializer(Color.class));
		this.addDeserializer(Color.class,new CanBeRepresentedAsStringDeserializer(Color.class));

		// Add AppVerson serializer & desarializer
		this.addSerializer(AppVersion.class,new CanBeRepresentedAsStringSerializer(AppVersion.class));
		this.addDeserializer(AppVersion.class,new CanBeRepresentedAsStringDeserializer(AppVersion.class));

		// Add Phone serializer & desarializer
		this.addSerializer(Phone.class,new CanBeRepresentedAsStringSerializer(Phone.class));
		this.addDeserializer(Phone.class,new CanBeRepresentedAsStringDeserializer(Phone.class));
		

		// Add Mail serializer & desarializer
		this.addSerializer(EMail.class,new CanBeRepresentedAsStringSerializer(EMail.class));
		this.addDeserializer(EMail.class,new CanBeRepresentedAsStringDeserializer(EMail.class));

		// Add XY serializer & desarializer
//		this.addSerializer(XY.class,new CanBeRepresentedAsStringSerializer(XY.class));
//		this.addDeserializer(XY.class,new CanBeRepresentedAsStringDeserializer(XY.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A custom Jackson {@link PropertyNamingStrategy} that just removes the underscore (_) used to
	 * name the private fields
	 * see http://www.javaroots.com/2013/03/how-to-use-naming-in-jackson.html
	 */
	public class MarshallerJacksonPropertyNamingStrategy
		 extends PropertyNamingStrategy {

		private static final long serialVersionUID = 7510871826090649773L;

		@Override
		public String nameForField(final MapperConfig<?> config,
								   final AnnotatedField field,
								   final String defaultName) {
			return defaultName.startsWith("_") ? defaultName.substring(1)
											   : super.nameForField(config, field, defaultName);
		}
	}
}
