package r01f.mime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypesFactory;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.patterns.Memoized;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.types.Path;

@GwtIncompatible
@Slf4j
public class MimeTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	static Memoized<org.apache.tika.mime.MimeTypes> MIME_TYPES = new Memoized<org.apache.tika.mime.MimeTypes>() {
																		@Override
																		protected org.apache.tika.mime.MimeTypes supply() {
																				final ResourcesLoader resLoader = ResourcesLoaderBuilder.createDefaultResourcesLoader();
																				try {
																					return MimeTypesFactory.create(resLoader.getInputStream(Path.from("org/apache/tika/mime/tika-mimetypes.xml")),		// tika's core (located at tika-core.jar)
																												   resLoader.getInputStream(Path.from("org/apache/tika/mime/custom-mimetypes.xml")),	// tika's extension (located at R01F)
																												   resLoader.getInputStream(Path.from("r01fb/mime/rdf-mimetypes.xml")));				// tika's extension (located at R01F)
																				} catch (final Throwable th) {
																					log.error("Error loading TIKA mime-types: {}",th.getMessage(),th);
																					throw new InternalError(th.getMessage());
																				}
																		}
																 };
	public static MimeType TEXT_PLAIN = MimeTypes.forName("text/plain");
	public static MimeType APPLICATION_XML = MimeTypes.forName("application/xml");
	public static MimeType APPLICATION_JSON = MimeTypes.forName("application/json");
	public static MimeType OCTECT_STREAM = MimeTypes.forName("application/octet-stream");
	public static MimeType XHTML = MimeTypes.forName("application/xhtml+xml");
	public static MimeType HTML = MimeTypes.forName("text/html");
	public static MimeType JAVASCRIPT = MimeTypes.forName("application/javascript");
	public static MimeType STYLESHEET = MimeTypes.forName("text/css");
	
	public static MimeType FORM_URL_ENCODED = MimeTypes.forName("application/x-www-form-urlencoded");

	static org.apache.tika.mime.MimeType CSS_MIME = MimeTypes.mimeTypeFor("text/css");
	static org.apache.tika.mime.MimeType LESS_MIME = MimeTypes.mimeTypeFor("text/x-less");
	static org.apache.tika.mime.MimeType JS_MIME = MimeTypes.mimeTypeFor("application/javascript");
	static org.apache.tika.mime.MimeType HTML_MIME = MimeTypes.mimeTypeFor("text/html");
	static org.apache.tika.mime.MimeType XHTML_MIME = MimeTypes.mimeTypeFor("application/xhtml+xml");

	static org.apache.tika.mime.MimeType mimeTypeFor(final String mediaTypeName) {
		org.apache.tika.mime.MimeType outMimeType = null;
		try {
			outMimeType = MIME_TYPES.get().forName(mediaTypeName);
		} catch (final MimeTypeException mimeEx) {
			mimeEx.printStackTrace(System.out);
		}
		return outMimeType;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  MimeType Groups
/////////////////////////////////////////////////////////////////////////////////////////
	public static class MimeTypeGroupDef {
		private final Collection<org.apache.tika.mime.MimeType> _mimeTypes;
		private final Map<org.apache.tika.mime.MimeType,Collection<String>> _extensions;

		public MimeTypeGroupDef(final String... mimeTypes) {
			_mimeTypes = FluentIterable.from(Arrays.asList(mimeTypes))
									   .transform(new Function<String,org.apache.tika.mime.MimeType>() {
														@Override
														public org.apache.tika.mime.MimeType apply(final String mimeTypeName) {
															try {
																return MIME_TYPES.get().forName(mimeTypeName);
															} catch (final MimeTypeException mimeEx) {
																mimeEx.printStackTrace();
															}
															return null;
														}
												   })
									   .toList();
			_extensions = new HashMap<org.apache.tika.mime.MimeType,Collection<String>>();
			for (final org.apache.tika.mime.MimeType mimeType : _mimeTypes) {
				final Collection<String> extensions = mimeType.getExtensions();
				if (extensions != null && extensions.size() > 0) _extensions.put(mimeType,extensions);
			}
		}
		public boolean contains(final org.apache.tika.mime.MimeType mimeType) {
			return _mimeTypes.contains(mimeType);
		}
		public org.apache.tika.mime.MimeType mimeTypeForFileExtension(final String ext) {
			org.apache.tika.mime.MimeType outMime = null;
			if (_extensions != null && _extensions.size() > 0) {
				for (final Map.Entry<org.apache.tika.mime.MimeType,Collection<String>> me : _extensions.entrySet()) {
					if (me.getValue().contains(ext)) {
						outMime = me.getKey();
						break;
					}
				}
			}
			return outMime;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
//--Text/plain
	static final MimeTypeGroupDef TEXT_PLAIN_GRP = new MimeTypeGroupDef("text/plain");
//--Binary
	static final MimeTypeGroupDef BINARY_GRP = new MimeTypeGroupDef("application/octet-stream",
																	"application/x-deb");		// debian install package

	static final MimeTypeGroupDef COMPRESSED_GRP = new MimeTypeGroupDef("application/zip",
													  			        "application/gzip");
													  			      //"x-rar-compressed",
																      //"x-tar"
	static final MimeTypeGroupDef FONT_GRP = new MimeTypeGroupDef("application/x-font-ttf",
													  			  "application/font-woff");
//--Document
	static final MimeTypeGroupDef DOCUMENT_GRP = new MimeTypeGroupDef("application/pdf",

																      "application/vnd.ms-excel","application/msexcel",
												  				      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",			// excel 2007+
												  				      "application/vnd.oasis.opendocument.spreadsheet",

												  				      "application/vnd.ms-powerpoint","application/mspowerpoint",
												  				      "application/vnd.openxmlformats-officedocument.presentationml.presentation",	// powerpoint 2007+
												  				      "application/vnd.oasis.opendocument.presentation",

												  				      "application/vnd.ms-word","application/msword","application/msword2","application/msword5",
												  				      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",		// word 2007+
												  				      "application/vnd.oasis.opendocument.text",

												  				      "application/vnd.ms-visio","application/vnd.visio",
												  				      "application/vnd.oasis.opendocument.graphics",

												  				      "application/vnd.ms-outlook",

												  				      "application/postscript");
//--HTML
	static final MimeTypeGroupDef WEB_GRP = new MimeTypeGroupDef("text/html","application/xhtml+xml",
													 				    "text/css",
													 				    "application/javascript",
													 				    "application/x-www-form-urlencoded");
//--MultiPart
	static final MimeTypeGroupDef MULTI_PART_GRP = new MimeTypeGroupDef("multipart/form-data",
																		"multipart/mixed",
																		"multipart/alternative",
																		"multipart/related",
																		"multipart/signed",
																		"multipart/encrypted");
//--Image
	static final MimeTypeGroupDef IMAGE_GRP = new MimeTypeGroupDef("image/gif",
													   		       "image/jpeg",
													   		       "image/pjpeg",
													   		       "image/bmp",
													   		       "image/png",
													   		       "image/svg+xml",
													   		       "image/tiff",
													   		       "image/webp");
//--Audio
	static final MimeTypeGroupDef AUDIO_GRP = new MimeTypeGroupDef("audio/basic",
													   		       "audio/L24",
													   		       "audio/mp3",
													   		       "audio/mp4",
													   		       "audio/mpeg",
													   		       "audio/ogg",
													   		       "audio/vorbis",
													   		       "audio/vnd.rn-realaudio",
													   		       "audio/vnd.wave",
													   		       "audio/webm",
													   		       "audio/x-aac");
//--Video
	static final MimeTypeGroupDef VIDEO_GRP = new MimeTypeGroupDef("video/mpeg",
													   		       "video/mp4",
													   		       "video/ogg",
													   		       "video/quicktime",
													   		       "video/webm",
													   		       "video/x-matroska",
													   		       "video/x-ms-wmv",
													   		       "video/x-flv");
//--Flash
//	public static final MimeTypeGroupDef FLASH = new MimeTypeGroupDef("x-shockwave-flash");

//--Model3D
	static final MimeTypeGroupDef MODEL3D_GRP = new MimeTypeGroupDef("model/example",
														 			 "model/iges",
														 			 "model/mesh",
														 			 "model/vrml",
														 			 "model/x3d+binary",
														 			 "model/x3d+vrml",
														 			 "model/x3d+xml");
//--OpenData
	static final MimeTypeGroupDef DATA_GRP = new MimeTypeGroupDef("text/xml","application/xml",
															      "application/json",
															      "text/csv",
															      "text/tab-separated-values",
															      "text/vcard",
															      "application/rdf+xml",
															      "application/x-binary-rdf",
															      "application/rdf+json",
															      "application/ld+json",
															      "application/trig",
															      "application/trix",
															      "text/turtle",
															      "text/rdf+n3",
															      "text/x-nquads",
															      "application/sparql-results+xml",
															      "application/sparql-results+json",
															      "application/x-binary-rdf-results-table",
															      "application/rss+xml","application/atom+xml",
															      "application/soap+xml");

	static final MimeTypeGroupDef MAP_GRP = new MimeTypeGroupDef("application/vnd.google-earth.kml+xml",
																 "application/vnd.google-earth.kmz");
///////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////
	public static MimeTypeWrapper wrap(final MimeType mimeType) {
		final org.apache.tika.mime.MimeType tikaMimeType = MimeTypes.mimeTypeFor(mimeType.getName());
		return new MimeTypeWrapper(tikaMimeType);
	}
	@RequiredArgsConstructor
	public static class MimeTypeWrapper {
		private final org.apache.tika.mime.MimeType _mimeType;

		public boolean isTextPlain() {
			return MimeTypes.TEXT_PLAIN_GRP.contains(_mimeType);
		}
		public boolean isBinary() {
			return MimeTypes.BINARY_GRP.contains(_mimeType);
		}
		public boolean isCompressed() {
			return MimeTypes.COMPRESSED_GRP.contains(_mimeType);
		}
		public boolean isFont() {
			return MimeTypes.FONT_GRP.contains(_mimeType);
		}
		public boolean isDocument() {
			return MimeTypes.DOCUMENT_GRP.contains(_mimeType);
		}
		public boolean isWeb() {
			return MimeTypes.WEB_GRP.contains(_mimeType);
		}
		public boolean isHtml() {
			return _mimeType.equals(MimeTypes.HTML_MIME) || _mimeType.equals(MimeTypes.XHTML_MIME);
		}
		public boolean isStyleSheet() {
			return _mimeType.equals(MimeTypes.CSS_MIME) || _mimeType.equals(MimeTypes.LESS_MIME);
		}
		public boolean isJavaScript() {
			return _mimeType.equals(MimeTypes.JS_MIME);
		}
		public boolean isMultiPart() {
			return MimeTypes.MULTI_PART_GRP.contains(_mimeType);
		}
		public boolean isImage() {
			return MimeTypes.IMAGE_GRP.contains(_mimeType);
		}
		public boolean isAudio() {
			return MimeTypes.AUDIO_GRP.contains(_mimeType);
		}
		public boolean isVideo() {
			return MimeTypes.VIDEO_GRP.contains(_mimeType);
		}
		public boolean is3DModel() {
			return MimeTypes.MODEL3D_GRP.contains(_mimeType);
		}
		public boolean isMap() {
			return MimeTypes.MAP_GRP.contains(_mimeType);
		}
		public boolean isData() {
			return MimeTypes.DATA_GRP.contains(_mimeType);
		}
		public String getExtension() {
			return _mimeType.getExtension();
		}
		public Collection<String> getExtensions() {
			return _mimeType.getExtensions();
		}
		public String getDescription() {
			return _mimeType.getDescription();
		}
		public String getUniformTypeIdentifier() {
			return _mimeType.getUniformTypeIdentifier();
		}
		public String getAcronym() {
			return _mimeType.getAcronym();
		}
		public MediaType getMediaType() {
			return _mimeType.getType();
		}
		public org.apache.tika.mime.MimeType getType() {
			return _mimeType;
		}
		public boolean hasMagic() {
			return _mimeType.hasMagic();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE
/////////////////////////////////////////////////////////////////////////////////////////
	public static org.apache.tika.mime.MimeType tikaMimeTypeFor(final String mediaTypeName) {
		final org.apache.tika.mime.MimeType mimeType = MimeTypes.mimeTypeFor(mediaTypeName);
		return mimeType;
	}
	public static MimeType fromString(final String mimeType) {
		return MimeTypes.forName(mimeType);
	}
	public static MimeType valueOf(final String mimeType) {
		return MimeTypes.forName(mimeType);
	}
	/**
	 * Detects the {@link MimeType} from the bytes stream
	 * @see http://tika.apache.org/1.4/detection.html
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static MimeType from(final InputStream is) throws IOException {
		return MimeTypes.from(is,
					 		  null,null);
	}
	/**
	 * Detects the {@link MimeType} from the bytes stream
	 * @see http://tika.apache.org/1.4/detection.html
	 * @param is
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static MimeType from(final InputStream is,
								final String fileName) throws IOException {
		return MimeTypes.from(is,
							  fileName,null);
	}
	/**
	 * Detects the {@link MimeType} from the bytes stream
	 * @see http://tika.apache.org/1.4/detection.html
	 * @param is
	 * @param fileName
	 * @param contentType
	 * @return
	 * @throws IOException
	 */
	public static MimeType from(final InputStream is,
								final String fileName,final String contentType) throws IOException {
		final Metadata md = new Metadata();
		if (fileName != null) md.add(TikaMetadataKeys.RESOURCE_NAME_KEY,fileName);
		if (contentType != null) md.add(HttpHeaders.CONTENT_TYPE,contentType);

		final Tika tika = new Tika();
		final String mimeTypeStr = tika.detect(is,fileName);

		return MimeTypes.forName(mimeTypeStr);
	}
	public static MimeType forFileExtension(final String fileExtension) {
		final String theExt = fileExtension.startsWith(".") ? fileExtension : ("." + fileExtension);	// ensure the file extension starts with a dot

		org.apache.tika.mime.MimeType mimeType = null;
							  mimeType = MimeTypes.TEXT_PLAIN_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.BINARY_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.COMPRESSED_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.FONT_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.DOCUMENT_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.WEB_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.IMAGE_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.AUDIO_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.VIDEO_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.MODEL3D_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.DATA_GRP.mimeTypeForFileExtension(theExt);
		if (mimeType == null) mimeType = MimeTypes.MAP_GRP.mimeTypeForFileExtension(theExt);

		return mimeType != null ? new MimeType(mimeType.getName())
								: null;
	}

	public static MimeType forFileSimpleName(final String fileSimpleName) {
		try {
			final Metadata m = new Metadata();
			m.add(Metadata.RESOURCE_NAME_KEY, fileSimpleName);
			final MimeType mimeType = new  MimeType(MIME_TYPES.get().detect(null, m).toString());
			if (mimeType != null) return mimeType;
		} catch (final IOException e) {
			log.error("Error loading TIKA mime-types from extension: {}. Continue finding as default",e.getMessage());
		}
		return MimeTypes.forFileExtension(FileNameAndExtension.of(fileSimpleName).getExtension());
	}
	/**
	 * Returns the
	 * @param mediaTypeName
	 * @return
	 */
	public static MimeType forName(final String mediaTypeName) {
		final org.apache.tika.mime.MimeType mimeType = MimeTypes.tikaMimeTypeFor(mediaTypeName);
		return new MimeType(mimeType.getName());
	}
}
