// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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

	public static void writeDirective(Appendable appendable, CharSequence value) throws IOException {
		appendable.append("<!");
		appendable.append(value);
		appendable.append(">");
	}
}
