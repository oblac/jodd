// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.io.IOException;

/**
 * Document type node.
 */
public class DocumentType extends Node {

	protected final String publicId;
	protected final String baseUri;

	protected DocumentType(LagartoDOMBuilder domBuilder, String value, String publicId, String baseUri) {
		super(domBuilder, NodeType.DOCUMENT_TYPE, null);
		this.nodeValue = value;
		this.publicId = publicId;
		this.baseUri = baseUri;
	}

	@Override
	public DocumentType clone() {
		return cloneTo(new DocumentType(domBuilder, nodeValue, publicId, baseUri));
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
		domBuilder.getRenderer().renderDocumentType(this, appendable);
	}
}
