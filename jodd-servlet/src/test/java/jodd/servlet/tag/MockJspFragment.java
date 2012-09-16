// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.io.Writer;

public class MockJspFragment extends JspFragment {

	public int count;
	public InvokeCallback invokeCallback;

	@Override
	public JspContext getJspContext() {
		return null;
	}

	@Override
	public void invoke(Writer writer) throws JspException, IOException {
		count++;

		if (invokeCallback != null) {
			invokeCallback.onInvoke(count);
		}
	}


	public interface InvokeCallback {
		void onInvoke(int count);
	}

}