// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.TagWriterUtil;

import java.io.IOException;

/**
 * CDATA node.
 */
public class CData extends Node {

	protected CData(LagartoDOMBuilder domBuilder, String cdata) {
		super(domBuilder, NodeType.CDATA, null);
		this.nodeValue = cdata;
	}

	@Override
	public CData clone() {
		return cloneTo(new CData(domBuilder, nodeValue));
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		TagWriterUtil.writeCData(appendable, nodeValue);
	}
}