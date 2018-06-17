// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.net;

import jodd.io.StreamUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.Wildcard;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Map file extensions to MIME types. Based on the most recent Apache mime.types file.
 * Duplicated extensions (wmz, sub) are manually resolved.
 * <p>
 * See also:
 * http://www.iana.org/assignments/media-types/
 * http://www.webmaster-toolkit.com/mime-types.shtml
 */
public class MimeTypes {

	public static final String MIME_APPLICATION_ATOM_XML 		= "application/atom+xml";
	public static final String MIME_APPLICATION_JAVASCRIPT		= "application/javascript";
	public static final String MIME_APPLICATION_JSON 			= "application/json";
	public static final String MIME_APPLICATION_OCTET_STREAM	= "application/octet-stream";
	public static final String MIME_APPLICATION_XML 			= "application/xml";
	public static final String MIME_TEXT_CSS					= "text/css";
	public static final String MIME_TEXT_PLAIN 					= "text/plain";
	public static final String MIME_TEXT_HTML					= "text/html";

	private static final HashMap<String, String> MIME_TYPE_MAP;	// extension -> mime-type map

	static {
		final Properties mimes = new Properties();

		final InputStream is = MimeTypes.class.getResourceAsStream(MimeTypes.class.getSimpleName() + ".properties");
		if (is == null) {
			throw new IllegalStateException("Mime types file missing");
		}

		try {
			mimes.load(is);
		}
		catch (IOException ioex) {
			throw new IllegalStateException("Can't load properties", ioex);
		} finally {
			StreamUtil.close(is);
		}

		MIME_TYPE_MAP = new HashMap<>(mimes.size() * 2);

		final Enumeration keys = mimes.propertyNames();
		while (keys.hasMoreElements()) {
			String mimeType = (String) keys.nextElement();
			final String extensions = mimes.getProperty(mimeType);

			if (mimeType.startsWith("/")) {
				mimeType = "application" + mimeType;
			} else if (mimeType.startsWith("a/")) {
				mimeType = "audio" + mimeType.substring(1);
			} else if (mimeType.startsWith("i/")) {
				mimeType = "image" + mimeType.substring(1);
			} else if (mimeType.startsWith("t/")) {
				mimeType = "text" + mimeType.substring(1);
			} else if (mimeType.startsWith("v/")) {
				mimeType = "video" + mimeType.substring(1);
			}

			final String[] allExtensions = StringUtil.splitc(extensions, ' ');

			for (final String extension : allExtensions) {
				if (MIME_TYPE_MAP.put(extension, mimeType) != null) {
					throw new IllegalArgumentException("Duplicated extension: " + extension);
				}
			}
		}
	}

	/**
	 * Registers MIME type for provided extension. Existing extension type will be overridden.
	 */
	public static void registerMimeType(final String ext, final String mimeType) {
		MIME_TYPE_MAP.put(ext, mimeType);
	}

	/**
	 * Returns the corresponding MIME type to the given extension.
	 * If no MIME type was found it returns <code>application/octet-stream</code> type.
	 */
	public static String getMimeType(final String ext) {
		String mimeType = lookupMimeType(ext);
		if (mimeType == null) {
			mimeType = MIME_APPLICATION_OCTET_STREAM;
		}
		return mimeType;
	}

	/**
	 * Simply returns MIME type or <code>null</code> if no type is found.
	 */
	public static String lookupMimeType(final String ext) {
		return MIME_TYPE_MAP.get(ext.toLowerCase());
	}

	/**
	 * Finds all extensions that belong to given mime type(s).
	 * If wildcard mode is on, provided mime type is wildcard pattern.
	 * @param mimeType list of mime types, separated by comma
	 * @param useWildcard if set, mime types are wildcard patterns
	 */
	public static String[] findExtensionsByMimeTypes(String mimeType, final boolean useWildcard) {
		final ArrayList<String> extensions = new ArrayList<>();

		mimeType = mimeType.toLowerCase();
		final String[] mimeTypes = StringUtil.splitc(mimeType, ", ");

		for (final Map.Entry<String, String> entry : MIME_TYPE_MAP.entrySet()) {
			final String entryExtension = entry.getKey();
			final String entryMimeType = entry.getValue().toLowerCase();

			final int matchResult = useWildcard ?
					Wildcard.matchOne(entryMimeType, mimeTypes) :
					StringUtil.equalsOne(entryMimeType, mimeTypes);

			if (matchResult != -1) {
				extensions.add(entryExtension);
			}
		}

		if (extensions.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		}

		return extensions.toArray(new String[0]);
	}

	/**
	 * Returns {@code true} if given value is one of the registered MIME extensions.
	 */
	public static boolean isRegisteredExtension(final String extension) {
		return MIME_TYPE_MAP.containsKey(extension);
	}
}