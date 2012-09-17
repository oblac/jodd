package jodd.bean.loader;

import jodd.bean.BeanLoaderManager;
import jodd.servlet.upload.MultipartRequest;
import jodd.servlet.upload.MultipartRequestWrapper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Registers additional bean loaders.
 */
public class ServletBeanLoaderManagerAddon {

	public static void registerDefaults() {
		BeanLoaderManager.register(HttpServletRequest.class, new RequestBeanLoader());
		BeanLoaderManager.register(HttpSession.class, new SessionBeanLoader());
		BeanLoaderManager.register(ServletContext.class, new ServletContextBeanLoader());
		BeanLoaderManager.register(MultipartRequest.class, new MultipartRequestBeanLoader());
		BeanLoaderManager.register(MultipartRequestWrapper.class, new MultipartRequestWrapperBeanLoader());
	}
}
