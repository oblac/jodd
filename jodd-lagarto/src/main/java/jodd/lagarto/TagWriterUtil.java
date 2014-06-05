// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

	public static void writeConditionalComment(Appendable appendable, CharSequence value, boolean isStartingTag, boolean downlevelHidden, boolean isHiddenEndTag) throws IOException {
		if (isStartingTag) {
			if (downlevelHidden) {
				appendable.append("<!--[");
			} else {
				appendable.append("<![");
			}

			appendable.append(value);

			appendable.append("]>");
		} else {
			if (isHiddenEndTag) {
				appendable.append("<!--");
			}

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

	public static void writeDoctype(
			Appendable appendable,
			CharSequence name,
			CharSequence publicIdentifier,
			CharSequence systemIdentifier) throws IOException {

		appendable.append("<!DOCTYPE ");
		if (name != null) {
			appendable.append(name);
		}

		if (publicIdentifier != null) {
			appendable.append(" PUBLIC");
		} else if (systemIdentifier != null) {
			appendable.append(" SYSTEM");
		}

		if (publicIdentifier != null) {
			appendable.append(" \"").append(publicIdentifier).append('"');
		}
		if (systemIdentifier != null) {
			appendable.append(" \"").append(systemIdentifier).append('"');
		}

		appendable.append('>');
	}

	public static void writeXml(Appendable appendable, CharSequence version, CharSequence encoding, CharSequence standalone) throws IOException {
		appendable.append("<?xml");

		if (version != null) {
			appendable.append(" version=\"").append(version).append("\"");
		}
		if (encoding != null) {
			appendable.append(" encoding=\"").append(encoding).append("\"");
		}
		if (standalone != null) {
			appendable.append(" standalone=\"").append(standalone).append("\"");
		}
		appendable.append("?>");
	}
}
