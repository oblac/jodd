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

package jodd.http;

import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.URLCoder;
import jodd.util.URLDecoder;

import java.util.Map;

/**
 * Few HTTP utilities.
 */
public class HttpUtil {

	// ---------------------------------------------------------------- query

	/**
	 * Builds a query string from given query map.
	 */
	public static String buildQuery(HttpMultiMap<?> queryMap, String encoding) {
		if (queryMap.isEmpty()) {
			return StringPool.EMPTY;
		}

		int queryMapSize = queryMap.size();

		StringBand query = new StringBand(queryMapSize * 4);

		int count = 0;
		for (Map.Entry<String, ?> entry : queryMap) {
			String key = entry.getKey();
			key = URLCoder.encodeQueryParam(key, encoding);

			Object value = entry.getValue();

			if (value == null) {
				if (count != 0) {
					query.append('&');
				}

				query.append(key);
				count++;
			} else {
				if (count != 0) {
					query.append('&');
				}

				query.append(key);
				count++;
				query.append('=');

				String valueString = URLCoder.encodeQueryParam(value.toString(), encoding);
				query.append(valueString);
			}
		}

		return query.toString();
	}

	/**
	 * Parses query from give query string. Values are optionally decoded.
	 */
	public static HttpMultiMap<String> parseQuery(String query, boolean decode) {

		HttpMultiMap<String> queryMap = HttpMultiMap.newCaseInsensitiveMap();

		int ndx, ndx2 = 0;
		while (true) {
			ndx = query.indexOf('=', ndx2);
			if (ndx == -1) {
				if (ndx2 < query.length()) {
					queryMap.add(query.substring(ndx2), null);
				}
				break;
			}
			String name = query.substring(ndx2, ndx);
			if (decode) {
				name = URLDecoder.decodeQuery(name);
			}

			ndx2 = ndx + 1;

			ndx = query.indexOf('&', ndx2);

			if (ndx == -1) {
				ndx = query.length();
			}

			String value = query.substring(ndx2, ndx);

			if (decode) {
				value = URLDecoder.decodeQuery(value);
			}

			queryMap.add(name, value);

			ndx2 = ndx + 1;
		}

		return queryMap;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Makes nice header names.
	 */
	public static String prepareHeaderParameterName(String headerName) {

		// special cases

		if (headerName.equals("etag")) {
			return HttpBase.HEADER_ETAG;
		}

		if (headerName.equals("www-authenticate")) {
			return "WWW-Authenticate";
		}

		char[] name = headerName.toCharArray();

		boolean capitalize = true;

		for (int i = 0; i < name.length; i++) {
			char c = name[i];

			if (c == '-') {
				capitalize = true;
				continue;
			}

			if (capitalize) {
				name[i] = Character.toUpperCase(c);
				capitalize = false;
			} else {
				name[i] = Character.toLowerCase(c);
			}
		}

		return new String(name);
	}

	// ---------------------------------------------------------------- content type

	/**
	 * Extracts media-type from value of "Content Type" header.
	 */
	public static String extractMediaType(String contentType) {
		int index = contentType.indexOf(';');

		if (index == -1) {
			return contentType;
		}

		return contentType.substring(0, index);
	}

	/**
	 * @see #extractHeaderParameter(String, String, char)
	 */
	public static String extractContentTypeCharset(String contentType) {
		return extractHeaderParameter(contentType, "charset", ';');
	}

	// ---------------------------------------------------------------- keep-alive

	/**
	 * Extract keep-alive timeout.
	 */
	public static String extractKeepAliveTimeout(String keepAlive) {
		return extractHeaderParameter(keepAlive, "timeout", ',');
	}

	public static String extractKeepAliveMax(String keepAlive) {
		return extractHeaderParameter(keepAlive, "max", ',');
	}

	// ---------------------------------------------------------------- header

	/**
	 * Extracts header parameter. Returns <code>null</code>
	 * if parameter not found.
	 */
	public static String extractHeaderParameter(String header, String parameter, char separator) {
		int index = 0;

		while (true) {
			index = header.indexOf(separator, index);

			if (index == -1) {
				return null;
			}

			index++;

			// skip whitespaces
			while (header.charAt(index) == ' ') {
				index++;
			}

			int eqNdx = header.indexOf('=', index);

			if (eqNdx == -1) {
				return null;
			}

			String paramName = header.substring(index, eqNdx);

			eqNdx++;

			if (!paramName.equalsIgnoreCase(parameter)) {
				index = eqNdx;
				continue;
			}

			int endIndex = header.indexOf(';', eqNdx);

			if (endIndex == -1) {
				return header.substring(eqNdx);
			} else {
				return header.substring(eqNdx, endIndex);
			}
		}
	}

}