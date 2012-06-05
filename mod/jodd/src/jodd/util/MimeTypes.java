// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Map file extensions to MIME types. Based on the Apache mime.types file.
 * http://www.iana.org/assignments/media-types/
 */
public class MimeTypes {

	public static final String MIME_APPLICATION_ANDREW_INSET 	= "application/andrew-inset";
	public static final String MIME_APPLICATION_ATOM_XML 		= "application/atom+xml";
	public static final String MIME_APPLICATION_JSON 			= "application/json";
	public static final String MIME_APPLICATION_ZIP 			= "application/zip";
	public static final String MIME_APPLICATION_X_GZIP 			= "application/x-gzip";
	public static final String MIME_APPLICATION_TGZ 			= "application/tgz";
	public static final String MIME_APPLICATION_MSWORD 			= "application/msword";
	public static final String MIME_APPLICATION_POSTSCRIPT 		= "application/postscript";
	public static final String MIME_APPLICATION_PDF 			= "application/pdf";
	public static final String MIME_APPLICATION_JNLP 			= "application/jnlp";
	public static final String MIME_APPLICATION_MAC_BINHEX40	= "application/mac-binhex40";
	public static final String MIME_APPLICATION_MAC_COMPACTPRO	= "application/mac-compactpro";
	public static final String MIME_APPLICATION_MATHML_XML		= "application/mathml+xml";
	public static final String MIME_APPLICATION_OCTET_STREAM	= "application/octet-stream";
	public static final String MIME_APPLICATION_ODA				= "application/oda";
	public static final String MIME_APPLICATION_OGG				= "application/ogg";
	public static final String MIME_APPLICATION_FONT_WOFF		= "application/font-woff";
	public static final String MIME_APPLICATION_RDF_XML			= "application/rdf+xml";
	public static final String MIME_APPLICATION_RSS_XML			= "application/rss+xml";
	public static final String MIME_APPLICATION_JAVASCRIPT		= "application/javascript";
	public static final String MIME_APPLICATION_JAVA_ARCHIVE	= "application/java-archive";
	public static final String MIME_APPLICATION_RDF_SMIL		= "application/smil";
	public static final String MIME_APPLICATION_SRGS			= "application/srgs";
	public static final String MIME_APPLICATION_SOAP_XML		= "application/soap+xml";
	public static final String MIME_APPLICATION_SRGS_XML		= "application/srgs+xml";
	public static final String MIME_APPLICATION_VND_MIF			= "application/vnd.mif";
	public static final String MIME_APPLICATION_VND_MSEXCEL		= "application/vnd.ms-excel";
	public static final String MIME_APPLICATION_VND_MSPOWERPOINT= "application/vnd.ms-powerpoint";
	public static final String MIME_APPLICATION_VND_RNREALMEDIA	= "application/vnd.rn-realmedia";
	public static final String MIME_APPLICATION_VND_RNREALAUDIO	= "application/vnd.rn-rn-realaudio";
	public static final String MIME_APPLICATION_VND_GOOGLEEARTH	= "application/vnd.google-earth.kml+xml";
	public static final String MIME_APPLICATION_X_BCPIO 		= "application/x-bcpio";
	public static final String MIME_APPLICATION_X_CDLINK 		= "application/x-cdlink";
	public static final String MIME_APPLICATION_X_CPIO			= "application/x-cpio";
	public static final String MIME_APPLICATION_X_CSH			= "application/x-csh";
	public static final String MIME_APPLICATION_X_DIRECTOR		= "application/x-director";
	public static final String MIME_APPLICATION_X_DVI			= "application/x-dvi";
	public static final String MIME_APPLICATION_X_GTAR			= "application/x-gtar";
	public static final String MIME_APPLICATION_X_HDF			= "application/x-hdf";
	public static final String MIME_APPLICATION_X_KOAN			= "application/x-koan";
	public static final String MIME_APPLICATION_X_LATEX			= "application/x-latex";
	public static final String MIME_APPLICATION_X_NETCDF		= "application/x-netcdf";
	public static final String MIME_APPLICATION_X_SH			= "application/x-sh";
	public static final String MIME_APPLICATION_X_SHAR			= "application/x-shar";
	public static final String MIME_APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
	public static final String MIME_APPLICATION_X_STUFFIT 		= "application/x-stuffit";
	public static final String MIME_APPLICATION_X_SV4CPIO 		= "application/x-sv4cpio";
	public static final String MIME_APPLICATION_X_SV4CRC 		= "application/x-sv4crc";
	public static final String MIME_APPLICATION_X_TAR 			= "application/x-tar";
	public static final String MIME_APPLICATION_X_RAR_COMPRESSED= "application/x-rar-compressed";
	public static final String MIME_APPLICATION_X_TCL 			= "application/x-tcl";
	public static final String MIME_APPLICATION_X_TEX 			= "application/x-tex";
	public static final String MIME_APPLICATION_X_TEXINFO		= "application/x-texinfo";
	public static final String MIME_APPLICATION_X_TROFF			= "application/x-troff";
	public static final String MIME_APPLICATION_X_TROFF_MAN		= "application/x-troff-man";
	public static final String MIME_APPLICATION_X_TROFF_ME		= "application/x-troff-me";
	public static final String MIME_APPLICATION_X_TROFF_MS		= "application/x-troff-ms";
	public static final String MIME_APPLICATION_X_USTAR			= "application/x-ustar";
	public static final String MIME_APPLICATION_X_WAIS_SOURCE	= "application/x-wais-source";
	public static final String MIME_APPLICATION_VND_MOZZILLA_XUL_XML = "application/vnd.mozilla.xul+xml";
	public static final String MIME_APPLICATION_XHTML_XML 		= "application/xhtml+xml";
	public static final String MIME_APPLICATION_XSLT_XML 		= "application/xslt+xml";
	public static final String MIME_APPLICATION_XML		 		= "application/xml";
	public static final String MIME_APPLICATION_XML_DTD 		= "application/xml-dtd";
	public static final String MIME_APPLICATION_XOP_XML 		= "application/xop+xml";
	public static final String MIME_IMAGE_BMP					= "image/bmp";
	public static final String MIME_IMAGE_CGM					= "image/cgm";
	public static final String MIME_IMAGE_GIF					= "image/gif";
	public static final String MIME_IMAGE_IEF					= "image/ief";
	public static final String MIME_IMAGE_JPEG					= "image/jpeg";
	public static final String MIME_IMAGE_PJPEG					= "image/pjpeg";
	public static final String MIME_IMAGE_TIFF					= "image/tiff";
	public static final String MIME_IMAGE_PNG					= "image/png";
	public static final String MIME_IMAGE_SVG_XML				= "image/svg+xml";
	public static final String MIME_IMAGE_VND_DJVU				= "image/vnd.djvu";
	public static final String MIME_IMAGE_WAP_WBMP				= "image/vnd.wap.wbmp";
	public static final String MIME_IMAGE_X_CMU_RASTER			= "image/x-cmu-raster";
	public static final String MIME_IMAGE_VND_MS_ICON			= "image/vnd.microsoft.icon";
	public static final String MIME_IMAGE_X_PORTABLE_ANYMAP		= "image/x-portable-anymap";
	public static final String MIME_IMAGE_X_PORTABLE_BITMAP		= "image/x-portable-bitmap";
	public static final String MIME_IMAGE_X_PORTABLE_GRAYMAP	= "image/x-portable-graymap";
	public static final String MIME_IMAGE_X_PORTABLE_PIXMAP		= "image/x-portable-pixmap";
	public static final String MIME_IMAGE_X_RGB					= "image/x-rgb";
	public static final String MIME_AUDIO_BASIC					= "audio/basic";
	public static final String MIME_AUDIO_MIDI					= "audio/midi";
	public static final String MIME_AUDIO_MP4					= "audio/mp4";
	public static final String MIME_AUDIO_MPEG					= "audio/mpeg";
	public static final String MIME_AUDIO_OGG					= "audio/ogg";
	public static final String MIME_AUDIO_VORBIS				= "audio/vorbis";
	public static final String MIME_AUDIO_X_AIFF				= "audio/x-aiff";
	public static final String MIME_AUDIO_X_MPEGURL				= "audio/x-mpegurl";
	public static final String MIME_AUDIO_X_PN_REALAUDIO		= "audio/x-pn-realaudio";
	public static final String MIME_AUDIO_VND_WAVE 				= "audio/vnd.wave";
	public static final String MIME_AUDIO_X_MS_WMA 				= "audio/x-ms-wma";
	public static final String MIME_AUDIO_X_MATROSKA			= "audio/x-matroska";
	public static final String MIME_CHEMICAL_X_PDB				= "chemical/x-pdb";
	public static final String MIME_CHEMICAL_X_XYZ				= "chemical/x-xyz";
	public static final String MIME_MODEL_IGES					= "model/iges";
	public static final String MIME_MODEL_MESH					= "model/mesh";
	public static final String MIME_MODEL_VRLM					= "model/vrml";
	public static final String MIME_TEXT_PLAIN 					= "text/plain";
	public static final String MIME_TEXT_RICHTEXT				= "text/richtext";
	public static final String MIME_TEXT_RTF					= "text/rtf";
	public static final String MIME_TEXT_HTML					= "text/html";
	public static final String MIME_TEXT_CALENDAR				= "text/calendar";
	public static final String MIME_TEXT_CSS					= "text/css";
	public static final String MIME_TEXT_CSV					= "text/csv";
	public static final String MIME_TEXT_VCARD					= "text/vcard";
	public static final String MIME_TEXT_SGML					= "text/sgml";
	public static final String MIME_TEXT_TAB_SEPARATED_VALUES	= "text/tab-separated-values";
	public static final String MIME_TEXT_VND_WAP_XML			= "text/vnd.wap.wml";
	public static final String MIME_TEXT_VND_WAP_WMLSCRIPT		= "text/vnd.wap.wmlscript";
	public static final String MIME_TEXT_X_SETEXT				= "text/x-setext";
	public static final String MIME_TEXT_X_COMPONENT			= "text/x-component";
	public static final String MIME_VIDEO_QUICKTIME				= "video/quicktime";
	public static final String MIME_VIDEO_MPEG					= "video/mpeg";
	public static final String MIME_VIDEO_MP4					= "video/mp4";
	public static final String MIME_VIDEO_OGG					= "video/ogg";
	public static final String MIME_VIDEO_VND_MPEGURL			= "video/vnd.mpegurl";
	public static final String MIME_VIDEO_X_FLV					= "video/x-flv";
	public static final String MIME_VIDEO_X_MSVIDEO				= "video/x-msvideo";
	public static final String MIME_VIDEO_X_MS_WMV				= "video/x-ms-wmv";
	public static final String MIME_VIDEO_X_SGI_MOVIE			= "video/x-sgi-movie";
	public static final String MIME_VIDEO_X_MATROSKA			= "video/x-matroska";

	private static final HashMap<String, String> MIME_TYPE_MAP;

	static {
		Properties mimes = new Properties();

		InputStream is = MimeTypes.class.getResourceAsStream(MimeTypes.class.getSimpleName() + ".properties");
		if (is == null) {
			throw new IllegalStateException("Mime types file missing");
		}

		try {
			mimes.load(is);
		}
		catch (IOException ioex) {
			throw new IllegalStateException(ioex.getMessage());
		} finally {
			StreamUtil.close(is);
		}

		MIME_TYPE_MAP = new HashMap<String, String>(mimes.size());

		Enumeration keys = mimes.propertyNames();
		while (keys.hasMoreElements()) {
			String fileExtension = (String) keys.nextElement();
			String mimeNameField = mimes.getProperty(fileExtension);

			String mimeType;
			try {
				Field field = MimeTypes.class.getField(mimeNameField);
				field.setAccessible(true);
				mimeType = (String) field.get(null);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Invalid field: " + mimeNameField, ex);
			}

			if (MIME_TYPE_MAP.put(fileExtension, mimeType) != null) {
				throw new IllegalArgumentException("Duplicated extension: " + fileExtension);
			}
		}
	}

	/**
	 * Registers MIME type for provided extension. Existing extension type will be overridden.
	 */
	public static void registerMimeType(String ext, String mimeType) {
		MIME_TYPE_MAP.put(ext, mimeType);
	}

	/**
	 * Returns the corresponding MIME type to the given extension.
	 * If no MIME type was found it returns <code>application/octet-stream</code> type.
	 */
	public static String getMimeType(String ext) {
		String mimeType = lookupMimeType(ext);
		if (mimeType == null) {
			mimeType = MIME_APPLICATION_OCTET_STREAM;
		}
		return mimeType;
	}

	/**
	 * Simply returns MIME type or <code>null</code> if no type is found.
	 */
	public static String lookupMimeType(String ext) {
		return MIME_TYPE_MAP.get(ext.toLowerCase());
	}
}