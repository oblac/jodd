// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.TagWriterUtil;

import java.io.IOException;

/**
 * Document type node.
 */
public class DocumentType extends Node {

	protected final String publicId;
	protected final String baseUri;

	public DocumentType(String value, String publicId, String baseUri) {
		super(NodeType.DOCUMENT_TYPE, null);
		this.nodeValue = value;
		this.publicId = publicId;
		this.baseUri = baseUri;
	}

	public String getRootName() {
		return nodeValue;
	}

	public String getPublicId() {
		return publicId;
	}

	public String getBaseUri() {
		return baseUri;
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		TagWriterUtil.writeDoctype(appendable, nodeValue, publicId, baseUri);
	}
}
