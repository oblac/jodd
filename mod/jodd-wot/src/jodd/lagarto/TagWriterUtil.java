// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.io.IOException;

/**
 * Some common tag writer util methods.
 */
public class TagWriterUtil {

	public static void writeComment(Appendable appendable, CharSequence comment) throws IOException {
		appendable.append("<!--");
		appendable.append(comment);
		appendable.append("-->");
	}

	public static void writeConditionalComment(Appendable appendable, CharSequence value, boolean isStartingTag, boolean downlevelHidden) throws IOException {
		if (isStartingTag) {
			if (downlevelHidden) {
				appendable.append("<!--[");
			} else {
				appendable.append("<![");
			}
			appendable.append(value);
			appendable.append("]>");
		} else {
			appendable.append("<![");
			appendable.append(value);
			if (downlevelHidden) {
				appendable.append("]-->");
			} else {
				appendable.append("]>");
			}
		}
	}

	public static void writeCData(Appendable appendable, CharSequence value) throws IOException {
		appendable.append("<![CDATA[");
		appendable.append(value);
		appendable.append("]]>");
	}

	public static void writeDoctype(Appendable appendable, String name, String publicId, String baseUri) throws IOException {
		appendable.append("<!DOCTYPE ");
		if (name != null) {
			appendable.append(name);
		}
		if (publicId != null || baseUri != null) {
			if (publicId != null) {
				appendable.append(" PUBLIC \"").append(publicId).append('"');
			} else {
				appendable.append(" SYSTEM");
			}
			if (baseUri != null) {
				appendable.append(" \"").append(baseUri).append('"');
			}
		}
		appendable.append('>');
	}
}
