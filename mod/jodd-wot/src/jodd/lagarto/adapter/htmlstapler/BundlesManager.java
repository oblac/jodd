// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.NetUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.servlet.ServletUtil;
import jodd.util.Base32;
import jodd.util.CharUtil;
import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.SystemUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTML resources bundles manager.
 */
public class BundlesManager {

	private static final String ATTRIBUTE_NAME = BundlesManager.class.getName();

	protected int bundleCount;		// for new bundles

	protected Map<String, String> actionBundles = new HashMap<String, String>();	// action -> bundleId/digest
	protected Map<String, String> mirrors = new HashMap<String, String>();			// temp id -> bundleId

	protected String localFilesEncoding = StringPool.UTF_8;
	protected String webRoot;
	protected String bundleFolder;
	protected String staplerServletPath = "/jodd-bundle";
	protected String bundleFilenamePrefix = "jodd-bundle-";
	protected String localAddressAndPort = "http://localhost:8080";
	protected boolean downloadLocal;
	protected final String contextPath;

	// ---------------------------------------------------------------- init

	/**
	 * Returns bundles manager.
	 */
	public static BundlesManager getBundlesManager(HttpServletRequest servletRequest) {
		return getBundlesManager(servletRequest.getSession().getServletContext());
	}

	/**
	 * Returns bundles manager from servlet context.
	 */
	public static BundlesManager getBundlesManager(ServletContext servletContext) {
		return (BundlesManager) servletContext.getAttribute(ATTRIBUTE_NAME);
	}

	/**
	 * Creates new instance, initialize it and stores it in servlet context.
	 */
	public BundlesManager(ServletContext servletContext) {
		servletContext.setAttribute(ATTRIBUTE_NAME, this);
		this.webRoot = servletContext.getRealPath(StringPool.EMPTY);
		this.bundleFolder = SystemUtil.getTempDir();
		this.contextPath = ServletUtil.getContextPath(servletContext);
	}

	/**
	 * Starts bundle usage by creating new {@link BundleAction}.
	 */
	public BundleAction start(HttpServletRequest servletRequest, String bundleName) {
		return new BundleAction(this, servletRequest, bundleName);
	}

	// ---------------------------------------------------------------- access

	/**
	 * Returns current web root.
	 */
	public String getWebRoot() {
		return webRoot;
	}

	/**
	 * Sets web root, i.e. real path to the exploded files.
	 * Web root is used to load local javascript files.
	 */
	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	/**
	 * Returns bundles folder. By default, it is a system temp folder.
	 */
	public String getBundleFolder() {
		return bundleFolder;
	}

	/**
	 * Sets bundle folder.
	 */
	public void setBundleFolder(String bundleFolder) {
		this.bundleFolder = bundleFolder;
	}

	/**
	 * Returns {@link HtmlStaplerServlet} servlet path.
	 * Must be the same as in web.xml.
	 */
	public String getStaplerServletPath() {
		return staplerServletPath;
	}

	/**
	 * Sets registered path for {@link HtmlStaplerServlet} as registered in web.xml.
	 */
	public void setStaplerServletPath(String staplerServletPath) {
		this.staplerServletPath = staplerServletPath;
	}

	/**
	 * Returns prefix of all bundle file names.
	 */
	public String getBundleFilenamePrefix() {
		return bundleFilenamePrefix;
	}

	/**
	 * Sets the prefix of bundle file names stored on disk.
	 */
	public void setBundleFilenamePrefix(String bundleFilenamePrefix) {
		this.bundleFilenamePrefix = bundleFilenamePrefix;
	}

	/**
	 * Returns local files encoding. By default its UTF8.
	 */
	public String getLocalFilesEncoding() {
		return localFilesEncoding;
	}

	/**
	 * Sets local files encoding.
	 */
	public void setLocalFilesEncoding(String localFilesEncoding) {
		this.localFilesEncoding = localFilesEncoding;
	}

	/**
	 * Returns local address and port for downloading
	 * local javascripts.
	 */
	public String getLocalAddressAndPort() {
		return localAddressAndPort;
	}

	/**
	 * Specifies local address and port for downloading
	 * local javascripts. By default its "http://localhost:8080".
	 */
	public void setLocalAddressAndPort(String localAddressAndPort) {
		this.localAddressAndPort = localAddressAndPort;
	}

	/**
	 * Returns <code>true</code> if local javascript files are downloaded
	 * and not loaded from file system.
	 */
	public boolean isDownloadLocal() {
		return downloadLocal;
	}

	/**
	 * Sets if local javascript files should be downloaded or loaded from file system.
	 */
	public void setDownloadLocal(boolean downloadLocal) {
		this.downloadLocal = downloadLocal;
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Creates bundle file.
	 */
	protected File createBundleFile(String bundleId) {
		return new File(bundleFolder, bundleFilenamePrefix + bundleId);
	}

	/**
	 * Lookups for bundle file.
	 */
	public File lookupBundleFile(String bundleId) {
		if (mirrors.isEmpty() == false) {
			String realBundleId = mirrors.remove(bundleId);

			if (realBundleId != null) {
				bundleId = realBundleId;
			}
		}
		return createBundleFile(bundleId);
	}

	/**
	 * Lookups for a bundle id for a given action.
	 * Returns <code>null</code> if action still has no bundle.
	 * Returns an empty string if action has an empty bundle.
	 */
	public String lookupBundleId(String actionPath) {
		return actionBundles.get(actionPath);
	}

	// ---------------------------------------------------------------- register

	/**
	 * Registers new, temporary bundle id for given action path.
	 * This id is used on first bundle usage, later it will be replaces with
	 * real bundle id.
	 */
	public String registerNewBundleId() {
		bundleCount++;
		return String.valueOf(bundleCount);
	}

	/**
	 * Registers new bundle that consist of provided list of source paths.
	 */
	public synchronized void registerBundle(String actionPath, String bundleId, List<String> sources) {

		if (bundleId == null) {
			// page does not include any javascript source file
			actionBundles.put(actionPath, StringPool.EMPTY);
			return;
		}

		// create unique digest from the collected sources
		String[] sourcesArray = sources.toArray(new String[sources.size()]);
		for (int i = 0, sourcesArrayLength = sourcesArray.length; i < sourcesArrayLength; i++) {
			sourcesArray[i] = sourcesArray[i].trim().toLowerCase();
		}
		Arrays.sort(sourcesArray);

		StringBand sb = new StringBand(sourcesArray.length);
		for (String src : sourcesArray) {
			sb.append(src);
		}
		String sourcesString = sb.toString();
		String digest = createDigest(sourcesString);

		// bundle appears for the first time, create the bundle
		actionBundles.put(actionPath, digest);
		mirrors.put(bundleId, digest);
		try {
			createBundle(actionPath, digest, sources);
		} catch (IOException ioex) {
			throw new HtmlStaplerException("Can't create bundle from sources.", ioex);
		}
	}

	/**
	 * Creates digest i.e. bundle id from given string.
	 * Returned digest must be filename safe, for all platforms.
	 */
	protected String createDigest(String source) {
		MessageDigest shaDigester;
		try {
			shaDigester = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException nsaex) {
			throw new HtmlStaplerException(nsaex);
		}

		byte[] bytes = shaDigester.digest(CharUtil.toSimpleByteArray(source));
		return Base32.encode(bytes);
	}

	/**
	 * Creates bundle file by loading javascript files content. If bundle file already
	 * exist it will not be recreated!
	 */
	protected void createBundle(String actionPath, String bundleId, List<String>sources) throws IOException {
		File bundleFile = createBundleFile(bundleId);
		if (bundleFile.exists()) {
			return;
		}

		StringBand sb = new StringBand(sources.size() * 2);
		for (String src : sources) {
			if (sb.length() != 0) {
				sb.append(StringPool.NEWLINE);
			}
			String content;
			if (src.startsWith("http://") || (src.startsWith("https://"))) {
				content = NetUtil.downloadString(src, localFilesEncoding);
			} else {
				if (downloadLocal == false) {
					// load local javascript from file system
					String localFile = webRoot;
					if (src.startsWith(StringPool.SLASH)) {
						localFile += src;
					} else {
						localFile += FileNameUtil.getPath(actionPath) + '/' + src;
					}
					content = FileUtil.readString(localFile);
				} else {
					// download local javascript
					String localUrl = localAddressAndPort;
					if (src.startsWith(StringPool.SLASH)) {
						localUrl += contextPath + src;
					} else {
						localUrl += contextPath + FileNameUtil.getPath(actionPath) + '/' + src;
					}
					content = NetUtil.downloadString(localUrl, localFilesEncoding);
				}
			}

			content = onJavascriptContent(content);
			sb.append(content);
		}

		FileUtil.writeString(bundleFile, sb.toString());
	}

	/**
	 * Invoked before javascript content is stored in the bundle.
	 * May be us used for additional javascript processing, such as
	 * compressing, cleaning etc. By default it just returns unmodified
	 * content.
	 */
	protected String onJavascriptContent(String content) {
		return content;
	}

	// ---------------------------------------------------------------- reset

	/**
	 * Clears all settings and removes all created bundle files from file system.
	 */
	public synchronized void reset() {
		actionBundles.clear();
		FindFile ff = new WildcardFindFile("*/" + bundleFilenamePrefix + StringPool.STAR);
		ff.setIncludeDirs(false);
		ff.searchPath(bundleFolder);

		File f;
		while ((f = ff.nextFile()) != null) {
			f.delete();
		}
	}

}
