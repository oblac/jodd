// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.io.Writer;

public class EmptyJspFragment extends JspFragment {

	int count;

	@Override
	public JspContext getJspContext() {
		return null;
	}

	@Override
	public void invoke(Writer writer) throws JspException, IOException {
		count++;
	}

	public int getCount() {
		return count;
	}
}
