// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * Document type node.
 */
public class DocumentType extends Node {

	protected final String publicId;
	protected final String systemId;

	public DocumentType(Document ownerDocument, String value, String publicId, String systemId) {
		super(ownerDocument, NodeType.DOCUMENT_TYPE, null);
		this.nodeValue = value;
		this.publicId = publicId;
		this.systemId = systemId;
	}

	@Override
	public DocumentType clone() {
		return cloneTo(new DocumentType(
				ownerDocument, nodeValue, publicId, systemId));
	}

	public String getRootName() {
		return nodeValue;
	}

	public String getPublicIdentifier() {
		return publicId;
	}

	public String getSystemIdentifier() {
		return systemId;
	}

	@Override
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.documentType(this);
	}
}
