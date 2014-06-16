// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * Similar to {@link jodd.lagarto.TagVisitor}, this is
 * a visitor for DOM tree.
 */
public interface NodeVisitor {

	void cdata(CData cdata);

	void comment(Comment comment);

	void document(Document document);

	void documentType(DocumentType documentType);

	void element(Element element);

	void text(Text text);

	void xmlDeclaration(XmlDeclaration xmlDeclaration);
}