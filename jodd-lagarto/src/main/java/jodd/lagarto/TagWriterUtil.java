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
