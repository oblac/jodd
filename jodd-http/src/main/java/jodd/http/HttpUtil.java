// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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
	public static String buildQuery(HttpValuesMap<Object> queryMap, String encoding) {
		int queryMapSize = queryMap.size();

		if (queryMapSize == 0) {
			return StringPool.EMPTY;
		}

		StringBand query = new StringBand(queryMapSize * 4);

		int count = 0;
		for (Map.Entry<String, Object[]> entry : queryMap.entrySet()) {
			String key = entry.getKey();
			Object[] values = entry.getValue();

			key = URLCoder.encodeQueryParam(key, encoding);

			if (values == null) {
				if (count != 0) {
					query.append('&');
				}

				query.append(key);
				count++;
			} else {
				for (Object value : values) {
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
		}

		return query.toString();
	}

	/**
	 * Parses query from give query string. Values are optionally decoded.
	 */
	public static HttpValuesMap<Object> parseQuery(String query, boolean decode) {

		HttpValuesMap<Object> queryMap = HttpValuesMap.ofObjects();

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