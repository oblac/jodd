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

	public static void writeConditionalComment(Appendable appendable, CharSequence value, boolean isStartingTag, boolean downlevelHidden, CharSequence additionalComment) throws IOException {
		if (isStartingTag) {
			if (downlevelHidden) {
				appendable.append("<!--[");
			} else {
				appendable.append("<![");
			}
			appendable.append(value);
			appendable.append("]>");

			if (additionalComment != null) {
				appendable.append(additionalComment);
			}
		} else {
			if (additionalComment != null) {
				appendable.append(additionalComment);
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
}
