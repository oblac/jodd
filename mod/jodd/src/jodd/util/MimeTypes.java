// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.HashMap;

/**
 * Map file extensions to MIME types. Based on the Apache mime.types file.
 * http://www.iana.org/assignments/media-types/
 */
public class MimeTypes {

	public static final String MIME_APPLICATION_ANDREW_INSET 	= "application/andrew-inset";
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
	public static final String MIME_APPLICATION_RDF_XML			= "application/rdf+xml";
	public static final String MIME_APPLICATION_JAVA_ARCHIVE	= "application/java-archive";
	public static final String MIME_APPLICATION_RDF_SMIL		= "application/smil";
	public static final String MIME_APPLICATION_SRGS			= "application/srgs";
	public static final String MIME_APPLICATION_SRGS_XML		= "application/srgs+xml";
	public static final String MIME_APPLICATION_VND_MIF			= "application/vnd.mif";
	public static final String MIME_APPLICATION_VND_MSEXCEL		= "application/vnd.ms-excel";
	public static final String MIME_APPLICATION_VND_MSPOWERPOINT= "application/vnd.ms-powerpoint";
	public static final String MIME_APPLICATION_VND_RNREALMEDIA	= "application/vnd.rn-realmedia";
	public static final String MIME_APPLICATION_X_BCPIO 		= "application/x-bcpio";
	public static final String MIME_APPLICATION_X_CDLINK 		= "application/x-cdlink";
	public static final String MIME_APPLICATION_X_CHESS_PGN		= "application/x-chess-pgn";
	public static final String MIME_APPLICATION_X_CPIO			= "application/x-cpio";
	public static final String MIME_APPLICATION_X_CSH			= "application/x-csh";
	public static final String MIME_APPLICATION_X_DIRECTOR		= "application/x-director";
	public static final String MIME_APPLICATION_X_DVI			= "application/x-dvi";
	public static final String MIME_APPLICATION_X_FUTURESPLASH	= "application/x-futuresplash";
	public static final String MIME_APPLICATION_X_GTAR			= "application/x-gtar";
	public static final String MIME_APPLICATION_X_HDF			= "application/x-hdf";
	public static final String MIME_APPLICATION_X_JAVASCRIPT	= "application/x-javascript";
	public static final String MIME_APPLICATION_X_KOAN			= "application/x-koan";
	public static final String MIME_APPLICATION_X_LATEX			= "application/x-latex";
	public static final String MIME_APPLICATION_X_NETCDF		= "application/x-netcdf";
	public static final String MIME_APPLICATION_X_OGG			= "application/x-ogg";
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
	public static final String MIME_IMAGE_BMP					= "image/bmp";
	public static final String MIME_IMAGE_CGM					= "image/cgm";
	public static final String MIME_IMAGE_GIF					= "image/gif";
	public static final String MIME_IMAGE_IEF					= "image/ief";
	public static final String MIME_IMAGE_JPEG					= "image/jpeg";
	public static final String MIME_IMAGE_TIFF					= "image/tiff";
	public static final String MIME_IMAGE_PNG					= "image/png";
	public static final String MIME_IMAGE_SVG_XML				= "image/svg+xml";
	public static final String MIME_IMAGE_VND_DJVU				= "image/vnd.djvu";
	public static final String MIME_IMAGE_WAP_WBMP				= "image/vnd.wap.wbmp";
	public static final String MIME_IMAGE_X_CMU_RASTER			= "image/x-cmu-raster";
	public static final String MIME_IMAGE_X_ICON				= "image/x-icon";
	public static final String MIME_IMAGE_X_PORTABLE_ANYMAP		= "image/x-portable-anymap";
	public static final String MIME_IMAGE_X_PORTABLE_BITMAP		= "image/x-portable-bitmap";
	public static final String MIME_IMAGE_X_PORTABLE_GRAYMAP	= "image/x-portable-graymap";
	public static final String MIME_IMAGE_X_PORTABLE_PIXMAP		= "image/x-portable-pixmap";
	public static final String MIME_IMAGE_X_RGB					= "image/x-rgb";
	public static final String MIME_AUDIO_BASIC					= "audio/basic";
	public static final String MIME_AUDIO_MIDI					= "audio/midi";
	public static final String MIME_AUDIO_MPEG					= "audio/mpeg";
	public static final String MIME_AUDIO_X_AIFF				= "audio/x-aiff";
	public static final String MIME_AUDIO_X_MPEGURL				= "audio/x-mpegurl";
	public static final String MIME_AUDIO_X_PN_REALAUDIO		= "audio/x-pn-realaudio";
	public static final String MIME_AUDIO_X_WAV 				= "audio/x-wav";
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
	public static final String MIME_TEXT_SGML					= "text/sgml";
	public static final String MIME_TEXT_TAB_SEPARATED_VALUES	= "text/tab-separated-values";
	public static final String MIME_TEXT_VND_WAP_XML			= "text/vnd.wap.wml";
	public static final String MIME_TEXT_VND_WAP_WMLSCRIPT		= "text/vnd.wap.wmlscript";
	public static final String MIME_TEXT_X_SETEXT				= "text/x-setext";
	public static final String MIME_TEXT_X_COMPONENT			= "text/x-component";
	public static final String MIME_VIDEO_QUICKTIME				= "video/quicktime";
	public static final String MIME_VIDEO_MPEG					= "video/mpeg";
	public static final String MIME_VIDEO_VND_MPEGURL			= "video/vnd.mpegurl";
	public static final String MIME_VIDEO_X_MSVIDEO				= "video/x-msvideo";
	public static final String MIME_VIDEO_X_MS_WMV				= "video/x-ms-wmv";
	public static final String MIME_VIDEO_X_SGI_MOVIE			= "video/x-sgi-movie";
	public static final String MIME_X_CONFERENCE_X_COOLTALK		= "x-conference/x-cooltalk";

	private static HashMap<String, String> mimeTypeMapping;

	static {
		mimeTypeMapping = new HashMap<String, String>(200) {
			private void put1(String key, String value) {
				if (put(key, value) != null) {
					throw new IllegalArgumentException("Duplicated extension: " + key);
				}
			}
			{
			put1("xul", MIME_APPLICATION_VND_MOZZILLA_XUL_XML);
			put1("json", MIME_APPLICATION_JSON);
			put1("ice", MIME_X_CONFERENCE_X_COOLTALK);
			put1("movie", MIME_VIDEO_X_SGI_MOVIE);
			put1("avi", MIME_VIDEO_X_MSVIDEO);
			put1("wmv", MIME_VIDEO_X_MS_WMV);
			put1("m4u", MIME_VIDEO_VND_MPEGURL);
			put1("mxu", MIME_VIDEO_VND_MPEGURL);
			put1("htc", MIME_TEXT_X_COMPONENT);
			put1("etx", MIME_TEXT_X_SETEXT);
			put1("wmls", MIME_TEXT_VND_WAP_WMLSCRIPT);
			put1("wml", MIME_TEXT_VND_WAP_XML);
			put1("tsv", MIME_TEXT_TAB_SEPARATED_VALUES);
			put1("sgm", MIME_TEXT_SGML);
			put1("sgml", MIME_TEXT_SGML);
			put1("css", MIME_TEXT_CSS);
			put1("ifb", MIME_TEXT_CALENDAR);
			put1("ics", MIME_TEXT_CALENDAR);
			put1("wrl", MIME_MODEL_VRLM);
			put1("vrlm", MIME_MODEL_VRLM);
			put1("silo", MIME_MODEL_MESH);
			put1("mesh", MIME_MODEL_MESH);
			put1("msh", MIME_MODEL_MESH);
			put1("iges", MIME_MODEL_IGES);
			put1("igs", MIME_MODEL_IGES);
			put1("rgb", MIME_IMAGE_X_RGB);
			put1("ppm", MIME_IMAGE_X_PORTABLE_PIXMAP);
			put1("pgm", MIME_IMAGE_X_PORTABLE_GRAYMAP);
			put1("pbm", MIME_IMAGE_X_PORTABLE_BITMAP);
			put1("pnm", MIME_IMAGE_X_PORTABLE_ANYMAP);
			put1("ico", MIME_IMAGE_X_ICON);
			put1("ras", MIME_IMAGE_X_CMU_RASTER);
			put1("wbmp", MIME_IMAGE_WAP_WBMP);
			put1("djv", MIME_IMAGE_VND_DJVU);
			put1("djvu", MIME_IMAGE_VND_DJVU);
			put1("svg", MIME_IMAGE_SVG_XML);
			put1("ief", MIME_IMAGE_IEF);
			put1("cgm", MIME_IMAGE_CGM);
			put1("bmp", MIME_IMAGE_BMP);
			put1("xyz", MIME_CHEMICAL_X_XYZ);
			put1("pdb", MIME_CHEMICAL_X_PDB);
			put1("ra", MIME_AUDIO_X_PN_REALAUDIO);
			put1("ram", MIME_AUDIO_X_PN_REALAUDIO);
			put1("m3u", MIME_AUDIO_X_MPEGURL);
			put1("aifc", MIME_AUDIO_X_AIFF);
			put1("aif", MIME_AUDIO_X_AIFF);
			put1("aiff", MIME_AUDIO_X_AIFF);
			put1("mp3", MIME_AUDIO_MPEG);
			put1("mp2", MIME_AUDIO_MPEG);
			put1("mp1", MIME_AUDIO_MPEG);
			put1("mpga", MIME_AUDIO_MPEG);
			put1("kar", MIME_AUDIO_MIDI);
			put1("mid", MIME_AUDIO_MIDI);
			put1("midi", MIME_AUDIO_MIDI);
			put1("dtd", MIME_APPLICATION_XML_DTD);
			put1("xsl", MIME_APPLICATION_XML);
			put1("xml", MIME_APPLICATION_XML);
			put1("xslt", MIME_APPLICATION_XSLT_XML);
			put1("xht", MIME_APPLICATION_XHTML_XML);
			put1("xhtml", MIME_APPLICATION_XHTML_XML);
			put1("src", MIME_APPLICATION_X_WAIS_SOURCE);
			put1("ustar", MIME_APPLICATION_X_USTAR);
			put1("ms", MIME_APPLICATION_X_TROFF_MS);
			put1("me", MIME_APPLICATION_X_TROFF_ME);
			put1("man", MIME_APPLICATION_X_TROFF_MAN);
			put1("roff", MIME_APPLICATION_X_TROFF);
			put1("tr", MIME_APPLICATION_X_TROFF);
			put1("t", MIME_APPLICATION_X_TROFF);
			put1("texi", MIME_APPLICATION_X_TEXINFO);
			put1("texinfo", MIME_APPLICATION_X_TEXINFO);
			put1("tex", MIME_APPLICATION_X_TEX);
			put1("tcl", MIME_APPLICATION_X_TCL);
			put1("sv4crc", MIME_APPLICATION_X_SV4CRC);
			put1("sv4cpio", MIME_APPLICATION_X_SV4CPIO);
			put1("sit", MIME_APPLICATION_X_STUFFIT);
			put1("swf", MIME_APPLICATION_X_SHOCKWAVE_FLASH);
			put1("shar", MIME_APPLICATION_X_SHAR);
			put1("sh", MIME_APPLICATION_X_SH);
			put1("cdf", MIME_APPLICATION_X_NETCDF);
			put1("nc", MIME_APPLICATION_X_NETCDF);
			put1("latex", MIME_APPLICATION_X_LATEX);
			put1("skm", MIME_APPLICATION_X_KOAN);
			put1("skt", MIME_APPLICATION_X_KOAN);
			put1("skd", MIME_APPLICATION_X_KOAN);
			put1("skp", MIME_APPLICATION_X_KOAN);
			put1("js", MIME_APPLICATION_X_JAVASCRIPT);
			put1("hdf", MIME_APPLICATION_X_HDF);
			put1("gtar", MIME_APPLICATION_X_GTAR);
			put1("spl", MIME_APPLICATION_X_FUTURESPLASH);
			put1("dvi", MIME_APPLICATION_X_DVI);
			put1("dxr", MIME_APPLICATION_X_DIRECTOR);
			put1("dir", MIME_APPLICATION_X_DIRECTOR);
			put1("dcr", MIME_APPLICATION_X_DIRECTOR);
			put1("csh", MIME_APPLICATION_X_CSH);
			put1("cpio", MIME_APPLICATION_X_CPIO);
			put1("pgn", MIME_APPLICATION_X_CHESS_PGN);
			put1("vcd", MIME_APPLICATION_X_CDLINK);
			put1("bcpio", MIME_APPLICATION_X_BCPIO);
			put1("rm", MIME_APPLICATION_VND_RNREALMEDIA);
			put1("ppt", MIME_APPLICATION_VND_MSPOWERPOINT);
			put1("mif", MIME_APPLICATION_VND_MIF);
			put1("grxml", MIME_APPLICATION_SRGS_XML);
			put1("gram", MIME_APPLICATION_SRGS);
			put1("smil", MIME_APPLICATION_RDF_SMIL);
			put1("smi", MIME_APPLICATION_RDF_SMIL);
			put1("rdf", MIME_APPLICATION_RDF_XML);
			put1("ogg", MIME_APPLICATION_X_OGG);
			put1("oda", MIME_APPLICATION_ODA);
			put1("dmg", MIME_APPLICATION_OCTET_STREAM);
			put1("lzh", MIME_APPLICATION_OCTET_STREAM);
			put1("so", MIME_APPLICATION_OCTET_STREAM);
			put1("lha", MIME_APPLICATION_OCTET_STREAM);
			put1("dms", MIME_APPLICATION_OCTET_STREAM);
			put1("bin", MIME_APPLICATION_OCTET_STREAM);
			put1("mathml", MIME_APPLICATION_MATHML_XML);
			put1("cpt", MIME_APPLICATION_MAC_COMPACTPRO);
			put1("hqx", MIME_APPLICATION_MAC_BINHEX40);
			put1("jnlp", MIME_APPLICATION_JNLP);
			put1("ez", MIME_APPLICATION_ANDREW_INSET);
			put1("txt", MIME_TEXT_PLAIN);
			put1("ini", MIME_TEXT_PLAIN);
			put1("c", MIME_TEXT_PLAIN);
			put1("h", MIME_TEXT_PLAIN);
			put1("cpp", MIME_TEXT_PLAIN);
			put1("cxx", MIME_TEXT_PLAIN);
			put1("cc", MIME_TEXT_PLAIN);
			put1("chh", MIME_TEXT_PLAIN);
			put1("java", MIME_TEXT_PLAIN);
			put1("csv", MIME_TEXT_PLAIN);
			put1("bat", MIME_TEXT_PLAIN);
			put1("cmd", MIME_TEXT_PLAIN);
			put1("asc", MIME_TEXT_PLAIN);
			put1("rtf", MIME_TEXT_RTF);
			put1("rtx", MIME_TEXT_RICHTEXT);
			put1("html", MIME_TEXT_HTML);
			put1("htm", MIME_TEXT_HTML);
			put1("zip", MIME_APPLICATION_ZIP);
			put1("rar", MIME_APPLICATION_X_RAR_COMPRESSED);
			put1("gzip", MIME_APPLICATION_X_GZIP);
			put1("gz", MIME_APPLICATION_X_GZIP);
			put1("tgz", MIME_APPLICATION_TGZ);
			put1("tar", MIME_APPLICATION_X_TAR);
			put1("gif", MIME_IMAGE_GIF);
			put1("jpeg", MIME_IMAGE_JPEG);
			put1("jpg", MIME_IMAGE_JPEG);
			put1("jpe", MIME_IMAGE_JPEG);
			put1("tiff", MIME_IMAGE_TIFF);
			put1("tif", MIME_IMAGE_TIFF);
			put1("png", MIME_IMAGE_PNG);
			put1("au", MIME_AUDIO_BASIC);
			put1("snd", MIME_AUDIO_BASIC);
			put1("wav", MIME_AUDIO_X_WAV);
			put1("mov", MIME_VIDEO_QUICKTIME);
			put1("qt", MIME_VIDEO_QUICKTIME);
			put1("mpeg", MIME_VIDEO_MPEG);
			put1("mpg", MIME_VIDEO_MPEG);
			put1("mpe", MIME_VIDEO_MPEG);
			put1("abs", MIME_VIDEO_MPEG);
			put1("doc", MIME_APPLICATION_MSWORD);
			put1("xls", MIME_APPLICATION_VND_MSEXCEL);
			put1("eps", MIME_APPLICATION_POSTSCRIPT);
			put1("ai", MIME_APPLICATION_POSTSCRIPT);
			put1("ps", MIME_APPLICATION_POSTSCRIPT);
			put1("pdf", MIME_APPLICATION_PDF);
			put1("exe", MIME_APPLICATION_OCTET_STREAM);
			put1("dll", MIME_APPLICATION_OCTET_STREAM);
			put1("class", MIME_APPLICATION_OCTET_STREAM);
			put1("jar", MIME_APPLICATION_JAVA_ARCHIVE);
		}};
	}

	public static void main(String[] args) {
		System.out.println(mimeTypeMapping.size());
	}

	/**
	 * Registers MIME type for provided extension. Existing extension type will be overriden.
	 */
	public static void registerMimeType(String ext, String mimeType) {
		mimeTypeMapping.put(ext, mimeType);
	}

	/**
	 * Returns the corresponding MIME type to the given extension.
	 * If no MIME type was found it returns 'application/octet-stream' type.
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
		return mimeTypeMapping.get(ext.toLowerCase());
	}
}
