package r01f.types.html;

import static r01f.types.html.MediaQuery.MediaQueryOrientationValue.LANDSCAPE;
import static r01f.types.html.MediaQuery.MediaQueryOrientationValue.PORTAIT;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * MediaQueries creation utility (ver http://nmsdvid.com/snippets/#)
 * Usage
 * <pre class='brush:java'>
 * 		// Builds two OR-combined media queries
 * 		Collection<MediaQuery> IPHONE4 = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
 *											 								.pixelRatioMinForWebKit(1.5F),
 *										 				    MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
 *													 						.pixelRatioMin(1.5F));
 *		// Get the media-query text to be included at a @media directive or at a link's media attribute
 *		String mediaStr = MediaQuery.toMediaQueryString(IPHONE4);
 * </pre>
 * 
 * To ease the usage some commonly used media queries are defined as templates 
 * (see http://nmsdvid.com/snippets/#)
 * <pre class='brush:java'>
 * 		Collection<MediaQuery> mediaQuery = MediaQuery.IPHONE4;
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="mediaQuery")
@Accessors(prefix="_")
public class MediaQuery {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="device",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter(AccessLevel.PRIVATE) private MediaQueryDevice _device;
	
	@MarshallField(as="values",
				   whenXml=@MarshallFieldAsXml(collectionElementName="value"))
	@Getter @Setter(AccessLevel.PRIVATE) private Collection<MediaQueryValue<?>> _values;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public MediaQuery() {
		// default no-args constructor
	}
	public MediaQuery(final MediaQueryDevice device,final Collection<MediaQueryValue<?>> values) {
		_device = device;
		_values = values;
	}
	public MediaQuery(final MediaQuery other) {
		_device = other.getDevice();
		_values = CollectionUtils.hasData(other.getValues()) ? FluentIterable.from(other.getValues())
																			 .transform(new Function<MediaQueryValue<?>,MediaQueryValue<?>>() {
																								@Override
																								public MediaQueryValue<?> apply(final MediaQueryValue<?> mqv) {
																									return _cloneMediaQueryValue(mqv.getName(),mqv.getValue());
																								}
																			 			})
																			 .toList()
															 : null;
	}
	private static <T> MediaQueryValue<T> _cloneMediaQueryValue(final String name,final T value) {
		return new MediaQueryValue<T>(name,value);		
	}
	public static MediaQuery createForDevice(final MediaQueryDevice device) {
		MediaQuery outMediaQuery = new MediaQuery();
		outMediaQuery.setDevice(device);
		return outMediaQuery;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public MediaQuery widthPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("width",pixels + "px"));
		return this;
	}
	public MediaQuery widthMinPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("min-width",pixels + "px"),
				  "width");			// exclude if present
		return this;
	}
	public MediaQuery widthMaxPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("max-width",pixels + "px"),
				  "width");			// exclude if present
		return this;
	}
	public MediaQuery heightPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("height",pixels + "px"));
		return this;
	}
	public MediaQuery heightMinPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("min-height",pixels + "px"),
				  "height");			// exclude if present
		return this;
	}
	public MediaQuery heightMaxPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("max-height",pixels + "px"),
				  "height");			// exclude if present
		return this;
	}
	public MediaQuery deviceWidthPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("device-width",pixels + "px"));
		return this;
	}
	public MediaQuery deviceWidthMinPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("min-device-width",pixels + "px"),
				  "device-width");			// exclude if present
		return this;
	}
	public MediaQuery deviceWidthMaxPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("max-device-width",pixels + "px"),
				  "device-width");			// exclude if present
		return this;
	}
	public MediaQuery deviceHeightPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("device-height",pixels + "px"));	
		return this;
	}
	public MediaQuery deviceHeightMinPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("min-device-height",pixels + "px"),
				  "device-height");			// exclude if present
		return this;
	}
	public MediaQuery deviceHeightMaxPixels(final int pixels) {
		_putValue(new MediaQueryValue<String>("max-device-height",pixels + "px"),
				  "device-height");			// exclude if present
		return this;
	}
	public MediaQuery orientation(final MediaQueryOrientationValue orientation) {
		_putValue(new MediaQueryValue<String>("orientation",orientation.name().toLowerCase()));
		return this;
	}
	public MediaQuery aspectRatio(final String ratio) {
		_putValue(new MediaQueryValue<String>("aspect-ratio",ratio));	
		return this;
	}
	public MediaQuery aspectMinRatio(final String ratio) {
		_putValue(new MediaQueryValue<String>("min-aspect-ratio",ratio),
				  "aspect-ratio");			// exclude if present
		return this;
	}
	public MediaQuery aspectMaxRatio(final String ratio) {
		_putValue(new MediaQueryValue<String>("max-aspect-ratio",ratio),
				  "aspect-ratio");			// exclude if present
		return this;
	}
	public MediaQuery deviceAspectRatio(final String ratio) {
		_putValue(new MediaQueryValue<String>("device-aspect-ratio",ratio));	
		return this;
	}
	public MediaQuery deviceAspectMinRatio(final String ratio) {
		_putValue(new MediaQueryValue<String>("min-device-aspect-ratio",ratio),
				  "device-aspect-ratio");			// exclude if present
		return this;
	}
	public MediaQuery deviceAspectMaxRatio(final String ratio) {
		_putValue(new MediaQueryValue<String>("max-device-aspect-ratio",ratio),
				  "device-aspect-ratio");			// exclude if present
		return this;
	}
	public MediaQuery bitsPerColor(final int bits) {
		_putValue(new MediaQueryValue<Integer>("color",bits));
		return this;
	}
	public MediaQuery bitsPerColorMin(final int bits) {
		_putValue(new MediaQueryValue<Integer>("min-color",bits),
				  "color");			// exclude if present
		return this;
	}
	public MediaQuery bitsPerColorMax(final int bits) {
		_putValue(new MediaQueryValue<Integer>("max-color",bits),
				  "color");			// exclude if present
		return this;
	}
	public MediaQuery numberOfColors(final int colors) {
		_putValue(new MediaQueryValue<Integer>("color-index",colors));
		return this;
	}
	public MediaQuery numberOfColorsMin(final int colors) {
		_putValue(new MediaQueryValue<Integer>("min-color-index",colors),
				  "color-index");			// exclude if present
		return this;
	}
	public MediaQuery numberOfColorsMax(final int colors) {
		_putValue(new MediaQueryValue<Integer>("max-color-index",colors),
				  "colorindex");			// exclude if present
		return this;
	}
	public MediaQuery monochromeBitsPerPixel(final int bits) {
		_putValue(new MediaQueryValue<Integer>("monochrome",bits));	
		return this;
	}
	public MediaQuery monochromeBitsPerPixelMin(final int bits) {
		_putValue(new MediaQueryValue<Integer>("min-monochrome",bits),
				  "monochrome");			// exclude if present
		return this;
	}
	public MediaQuery monochromeBitsPerPixelMax(final int bits) {
		_putValue(new MediaQueryValue<Integer>("max-monochrome",bits),
				  "monochrome");			// exclude if present
		return this;
	}
	public MediaQuery resolution(final int dots,final MediaQueryResolutionUnit unit) {
		_putValue(new MediaQueryValue<String>("resolution",dots + unit.name().toLowerCase()));
		return this;
	}
	public MediaQuery resolutionMin(final int dots,final MediaQueryResolutionUnit unit) {
		_putValue(new MediaQueryValue<String>("min-resolution",dots + unit.name().toLowerCase()),
				  "resolution");			// exclude if present
		return this;
	}
	public MediaQuery resolutionMax(final int dots,final MediaQueryResolutionUnit unit) {
		_putValue(new MediaQueryValue<String>("max-resolution",dots + unit.name().toLowerCase()),
				  "resolution");			// exclude if present
		return this;
	}
	public MediaQuery pixelRatio(final float pixelRatio) {
		_putValue(new MediaQueryValue<Float>("device-pixel-ratio",pixelRatio));
		return this;
	}
	public MediaQuery pixelRatioMin(final float pixelRatio) {
		_putValue(new MediaQueryValue<Float>("min-device-pixel-ratio",pixelRatio),
				  "device-pixel-ratio");			// exclude if present
		return this;
	}
	public MediaQuery pixelRatioMax(final float pixelRatio) {
		_putValue(new MediaQueryValue<Float>("max-device-pixel-ratio",pixelRatio),
				  "device-pixel-ratio");			// exclude if present
		return this;
	}
	public MediaQuery pixelRatioForWebKit(final float pixelRatio) {
		_putValue(new MediaQueryValue<Float>("-webkit-device-pixel-ratio",pixelRatio));
		return this;
	}
	public MediaQuery pixelRatioMinForWebKit(final float pixelRatio) {
		_putValue(new MediaQueryValue<Float>("-webkit-min-device-pixel-ratio",pixelRatio),
				  "-webkit-device-pixel-ratio");			// exclude if present
		return this;
	}
	public MediaQuery pixelRatioMaxForWebKit(final float pixelRatio) {
		_putValue(new MediaQueryValue<Float>("-webkit-max-device-pixel-ratio",pixelRatio),
				  "-webkit-device-pixel-ratio");			// exclude if present
		return this;
	}
	public MediaQuery scanMethod(final MediaQueryScanMethod method) {
		_putValue(new MediaQueryValue<String>("scan",method.name().toLowerCase()));	
		return this;
	}
	public MediaQuery grid() {
		_putValue(new MediaQueryValue<String>("grid","1"),
				  "bitmap");	
		return this;
	}
	public MediaQuery bitmap() {
		_putValue(new MediaQueryValue<String>("bitmap","0"),
				  "grid");	
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private <T> void _putValue(final MediaQueryValue<T> value,
							   final String... valuesToExcludeIfPresent) {
		if (_values == null) _values = Lists.newArrayList();
		if (CollectionUtils.hasData(valuesToExcludeIfPresent)) {
			for (String valToExclude : valuesToExcludeIfPresent) {
				Collection<MediaQueryValue<?>> valsToExclude = Lists.newArrayList();
				for (MediaQueryValue<?> val : _values) {
					if (val.getName().equals(valToExclude)) {
						valsToExclude.add(val);
						continue;
					}
				}
				if (CollectionUtils.hasData(valsToExclude)) _values.removeAll(valsToExclude);
			}
		}
		_values.add(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static String toMediaQueryString(final Collection<MediaQuery> orCombinedMediaQueries) {
		StringBuilder outMediaQuery = new StringBuilder(orCombinedMediaQueries.size() * 100);
		for (Iterator<MediaQuery> mqIt = orCombinedMediaQueries.iterator(); mqIt.hasNext(); ) {
			MediaQuery mq = mqIt.next();
			outMediaQuery.append(_mediaQueryToString(mq));
			if (mqIt.hasNext()) outMediaQuery.append(", ");
		}
		return outMediaQuery.toString();
	}
	private static String _mediaQueryToString(final MediaQuery mediaQuery) {
		StringBuilder valuesToString = null;
		if (mediaQuery.getValues() != null) {
			valuesToString = new StringBuilder(mediaQuery.getValues().size() * 30);
			for (Iterator<MediaQueryValue<?>> valIt = mediaQuery.getValues().iterator(); valIt.hasNext(); ) {
				MediaQueryValue<?> val = valIt.next();
				valuesToString.append(val.asString());
				if (valIt.hasNext()) valuesToString.append(" AND ");
			}
		}
		return Strings.customized("{} AND {}",
					  		 	  mediaQuery.getDevice().name().toLowerCase(),
					  		 	  valuesToString);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TEMPLATES (see http://nmsdvid.com/snippets/)
/////////////////////////////////////////////////////////////////////////////////////////
	public static Collection<MediaQuery> MONITOR_320PX = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 									.widthMaxPixels(320));
	public static Collection<MediaQuery> MONITOR_640PX = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 									.widthMaxPixels(640));
	public static Collection<MediaQuery> MONITOR_800PX = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 									.widthMaxPixels(800));
	public static Collection<MediaQuery> MONITOR_1024PX = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 									.widthMaxPixels(1024));
	public static Collection<MediaQuery> MONITOR_1028PX = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
																						.widthMaxPixels(1028));
	public static Collection<MediaQuery> BLACKBERRY_TORCH = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthMaxPixels(480));
	public static Collection<MediaQuery> IPHONE_LANDSCAPE = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthMaxPixels(480));
	public static Collection<MediaQuery> IPHONE_PORTRAIT = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthMaxPixels(320));
	public static Collection<MediaQuery> IPHONE3G = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthMaxPixels(320)
													 										.pixelRatioForWebKit(1));
	public static Collection<MediaQuery> IPHONE4 = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 								.pixelRatioMinForWebKit(1.5F),
													 				  MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 								.pixelRatioMin(1.5F));
	public static Collection<MediaQuery> IPHONE5 = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceHeightPixels(568)
													 										.pixelRatioForWebKit(2));
	public static Collection<MediaQuery> IPAD_LANDSCAPE = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthMaxPixels(1024)
													 										.orientation(LANDSCAPE));
	public static Collection<MediaQuery> IPAD_PORTRAIT = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthMaxPixels(768)
													 										.orientation(PORTAIT));
	public static Collection<MediaQuery> IPAD2_LANDSCAPE = IPAD_LANDSCAPE;
	public static Collection<MediaQuery> IPAD2_PORTRAIT = IPAD_PORTRAIT;
	public static Collection<MediaQuery> IPAD3_LANDSCAPE = IPAD_LANDSCAPE;
	public static Collection<MediaQuery> IPAD3_PORTRAIT = IPAD_PORTRAIT;
	public static Collection<MediaQuery> IPAD4 = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthPixels(768)
													 										.deviceHeightPixels(1024)
													 										.pixelRatioForWebKit(2));
	public static Collection<MediaQuery> IPAD_MINI = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthPixels(768)
													 										.deviceHeightPixels(1024)
													 										.pixelRatioForWebKit(1));
	public static Collection<MediaQuery> HTC_EVO = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 										.deviceWidthMaxPixels(480));
	public static Collection<MediaQuery> HD2 = HTC_EVO;
	public static Collection<MediaQuery> HTC_THUNDERBOLT = HTC_EVO;
	public static Collection<MediaQuery> SAMSUNG_GALAXY_S2 = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 											.deviceWidthPixels(320)
													 											.deviceHeightPixels(533)
													 											.pixelRatioForWebKit(1.5F));
	public static Collection<MediaQuery> SAMSUNG_GALAXY_TAB10_1_LANDSCAPE = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
															 												.deviceWidthMaxPixels(1028)
															 												.orientation(LANDSCAPE));
	public static Collection<MediaQuery> SAMSUNG_GALAXY_TAB10_1_PORTRAIT = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
															 												.deviceWidthMaxPixels(800)
															 												.orientation(PORTAIT));
	public static Collection<MediaQuery> SAMSUNG_GALAXY_S3 = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
													 											.pixelRatioForWebKit(2F));
	public static Collection<MediaQuery> GOOGLE_NEXUS7 = Lists.newArrayList(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
																							.deviceWidthPixels(600)
																							.deviceHeightPixels(905)
																							.pixelRatioMinForWebKit(1.331F)
																							.pixelRatioMaxForWebKit(1.332F));
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static enum MediaQueryDevice {
		ALL,
		AURAL,
		BRAILLE,
		HANDHELD,
		PROJECTION,
		PRINT,
		SCREEN,
		TTY,
		TV;
	}
	public static enum MediaQueryOrientationValue {
		PORTAIT,
		LANDSCAPE;
	}
	public static enum MediaQueryResolutionUnit {
		DPI,	// dots per inch 
		DPCM, 	// dots per cm
		DPPX;	// dots per pixel
	}
	public static enum MediaQueryScanMethod {
		PROGRESSIVE,
		INTERLACE;
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	private static class MediaQueryValue<T>
	   implements CanBeRepresentedAsString {
		
		@Getter @Setter private String _name;
		@Getter @Setter private T _value;
			
		@Override
		public String asString() {
			return Strings.customized("({}:{})",
							     	  _name,_value);
		}
		@Override
		public boolean equals(final Object other) {
			if (other == this) return true;
			boolean outEq = false;
			if (other instanceof MediaQueryValue) {
				MediaQueryValue<?> mq = (MediaQueryValue<?>)other;
				if (mq.getName().equals(_name)
				 && mq.getValue().equals(_value)) {
					outEq = true;
				}
			}
			return outEq;
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(_name,_value);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/*
	public boolean hasErrors() {
		return !CollectionUtils.hasData(this.errors());
	}
	public Collection<String> errors() {
		Collection<String> outErrors = null;
		if (CollectionUtils.hasData(_values)) {
			for (String key : _values.keySet()) {
				// If min-xxx or max-xxx value is present, the inverse should be also present
				if (key.startsWith("min-") || key.startsWith("max-")) {
					Matcher m = MIN_MAX_PATTERN.matcher(key);
					if (m.find()) {
						String minOrMax = m.group(1);
						String inverseOfMinOrMax = minOrMax.equals("min") ? "max" : "min";
						String valueName = m.group(2);
						String keyToSearch = Strings.of("{}-{}")
												    .customizeWith(inverseOfMinOrMax,valueName)
												    .asString();
						if (!_values.containsKey(keyToSearch)) {
							if (outErrors == null) outErrors = new ArrayList<String>();
							outErrors.add(Strings.of("{} value was present but not the inverse {}-{}")
												 .customizeWith(key,inverseOfMinOrMax,valueName)
												 .asString());
						}
					}
				}
			}
		}
		return outErrors;
	}
	private static final transient Pattern MIN_MAX_PATTERN = Pattern.compile("(min|max)-(.+)");
	*/

}
