// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.htmlstapler;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.NetUtil;
import jodd.io.ZipUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.log.Log;
import jodd.util.Base32;
import jodd.util.CharUtil;
import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML resources bundles manager.
 */
public class HtmlStaplerBundlesManager {

	private static final Log log = Log.getLogger(HtmlStaplerBundlesManager.class);

	protected int bundleCount;		// counter for new bundles

	protected Map<String, String> actionBundles; 			// action -> bundleId/digest
	protected Map<String, String> mirrors;					// temp id -> bundleId

	protected final String webRoot;
	protected final String contextPath;
	protected final Strategy strategy;

	// parameters
	protected String localFilesEncoding = StringPool.UTF_8;
	protected String bundleFolder;
	protected String staplerPath = "jodd-bundle";
	protected String localAddressAndPort = "http://localhost:8080";
	protected boolean downloadLocal;
	protected boolean sortResources;

	// ---------------------------------------------------------------- strategy

	public enum Strategy {

		/**
		 * For each action manager stores the bundle id.
		 * This gives top performances, as the links are collected only
		 * once per page and bundle id (digest) is calculated only once
		 * as well, only first time page is accessed. However, if there
		 * are dynamic REST-alike links, there might be a large or infinite
		 * number of action links in the application. This can be handled
		 * in the code by overriding method <code>resolveRealActionPath()</code>.
		 */
		ACTION_MANAGED,

		/**
		 * Pragmatic strategy that collects all links and builds  bundle id
		 * (digest) every time when page is processed. This gives slightly
		 * slower performances, but there is no additional memory consumption.
		 */
		RESOURCES_ONLY
	}

	// ---------------------------------------------------------------- init

	/**
	 * Creates new instance and initialize it.
	 */
	public HtmlStaplerBundlesManager(String contextPath, String webRoot, Strategy strategy) {
		this.contextPath = contextPath;
		this.webRoot = webRoot;
		this.strategy = strategy;

		setBundleFolder(SystemUtil.getTempDir());

		if (strategy == Strategy.ACTION_MANAGED) {
			actionBundles = new HashMap<String, String>();
			mirrors = new HashMap<String, String>();
		}
	}

	/**
	 * Starts bundle usage by creating new {@link BundleAction}.
	 */
	public BundleAction start(String servletPath, String bundleName) {
		return new BundleAction(this, servletPath, bundleName);
	}

	// ---------------------------------------------------------------- params

	/**
	 * Returns current {@link Strategy strategy}.
	 */
	public Strategy getStrategy() {
		return strategy;
	}

	/**
	 * Returns <code>true</code> if resources are sorted before
	 * bundle id (a digest) is created. When sorting is enabled,
	 * two pages will share the same bundle even if they list
	 * resources in different order.
	 */
	public boolean isSortResources() {
		return sortResources;
	}

	/**
	 * Sets the resources sorting before bundle id (i.e. a digest)
	 * is created.
	 */
	public void setSortResources(boolean sortResources) {
		this.sortResources = sortResources;
	}

	/**
	 * Returns current web root.
	 */
	public String getWebRoot() {
		return webRoot;
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
	 * Returns stapler path. It is both the file system folder
	 * name and the web folder name.
	 */
	public String getStaplerPath() {
		return staplerPath;
	}

	/**
	 * Sets stapler path.
	 */
	public void setStaplerPath(String staplerPath) {
		this.staplerPath = staplerPath;
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
	 * local resources.
	 */
	public String getLocalAddressAndPort() {
		return localAddressAndPort;
	}

	/**
	 * Specifies local address and port for downloading
	 * local resources. By default its "http://localhost:8080".
	 */
	public void setLocalAddressAndPort(String localAddressAndPort) {
		this.localAddressAndPort = localAddressAndPort;
	}

	/**
	 * Returns <code>true</code> if local resource files are downloaded
	 * and not loaded from file system.
	 */
	public boolean isDownloadLocal() {
		return downloadLocal;
	}

	/**
	 * Sets if local resource files should be downloaded or loaded from file system.
	 */
	public void setDownloadLocal(boolean downloadLocal) {
		this.downloadLocal = downloadLocal;
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Creates bundle file in bundleFolder/staplerPath. Only file object
	 * is created, not the file content.
	 */
	protected File createBundleFile(String bundleId) {
		File folder = new File(bundleFolder, staplerPath);
		if (folder.exists() == false) {
			folder.mkdirs();
		}
		return new File(folder, bundleId);
	}

	/**
	 * Lookups for bundle file.
	 */
	public File lookupBundleFile(String bundleId) {
		if ((mirrors != null) && (mirrors.isEmpty() == false)) {
			String realBundleId = mirrors.remove(bundleId);

			if (realBundleId != null) {
				bundleId = realBundleId;
			}
		}
		return createBundleFile(bundleId);
	}

	/**
	 * Locates gzipped version of bundle file. If gzip file
	 * does not exist, it will be created.
	 */
	public File lookupGzipBundleFile(File file) throws IOException {
		String path = file.getPath() + ZipUtil.GZIP_EXT;
		File gzipFile = new File(path);

		if (gzipFile.exists() == false) {
			if (log.isDebugEnabled()) {
				log.debug("gzip bundle to " + path);
			}
			ZipUtil.gzip(file);
		}

		return gzipFile;
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
	 * Returns the real bundle id, as provided one is just a temporary bundle id.
	 */
	public synchronized String registerBundle(String contextPath, String actionPath, String tempBundleId, String bundleContentType, List<String> sources) {

		if (tempBundleId == null || sources.isEmpty()) {
			if (strategy == Strategy.ACTION_MANAGED) {
				// page does not include any resource source file
				actionBundles.put(actionPath, StringPool.EMPTY);
			}
			return null;
		}

		// create unique digest from the collected sources
		String[] sourcesArray = sources.toArray(new String[sources.size()]);
		for (int i = 0, sourcesArrayLength = sourcesArray.length; i < sourcesArrayLength; i++) {
			sourcesArray[i] = sourcesArray[i].trim().toLowerCase();
		}
		if (sortResources) {
			Arrays.sort(sourcesArray);
		}

		StringBand sb = new StringBand(sourcesArray.length);
		for (String src : sourcesArray) {
			sb.append(src);
		}
		String sourcesString = sb.toString();

		String bundleId = createDigest(sourcesString);
		bundleId += '.' + bundleContentType;

		// bundle appears for the first time, create the bundle
		if (strategy == Strategy.ACTION_MANAGED) {
			actionBundles.put(actionPath, bundleId);
			mirrors.put(tempBundleId, bundleId);
		}
		try {
			createBundle(contextPath, actionPath, bundleId, sources);
		} catch (IOException ioex) {
			throw new HtmlStaplerException("Can't create bundle.", ioex);
		}
		return bundleId;
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
	 * Creates bundle file by loading resource files content. If bundle file already
	 * exist it will not be recreated!
	 */
	protected void createBundle(String contextPath, String actionPath, String bundleId, List<String>sources) throws IOException {
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
			if (isExternalResource(src)) {
				content = NetUtil.downloadString(src, localFilesEncoding);
			} else {
				if (downloadLocal == false) {
					// load local resource from file system
					String localFile = webRoot;

					if (src.startsWith(contextPath + '/')) {
						src = src.substring(contextPath.length());
					}

					if (src.startsWith(StringPool.SLASH)) {
						// absolute path
						localFile += src;
					} else {
						// relative path
						localFile += '/' + FileNameUtil.getPathNoEndSeparator(actionPath) + '/' + src;
					}

					// trim link parameters, if any
					int qmndx = localFile.indexOf('?');
					if (qmndx != -1) {
						localFile = localFile.substring(0, qmndx);
					}

					content = FileUtil.readString(localFile);
				} else {
					// download local resource
					String localUrl = localAddressAndPort;
					if (src.startsWith(StringPool.SLASH)) {
						localUrl += contextPath + src;
					} else {
						localUrl += contextPath + FileNameUtil.getPath(actionPath) + '/' + src;
					}
					content = NetUtil.downloadString(localUrl, localFilesEncoding);
				}

				if (isCssResource(src)) {
					content = fixCssRelativeUrls(content, src);
				}
			}

			content = onResourceContent(content);
			sb.append(content);
		}

		FileUtil.writeString(bundleFile, sb.toString());

		if (log.isInfoEnabled()) {
			log.info("Bundle created: " + bundleId);
		}
	}

	/**
	 * Returns <code>true</code> if resource link has to be downloaded.
	 * By default, if resource link starts with "http://" or with "https://"
	 * it will be considered as external resource.
	 */
	protected boolean isExternalResource(String link) {
		return link.startsWith("http://") || (link.startsWith("https://"));
	}

	/**
	 * Invoked before resource content is stored in the bundle.
	 * May be us used for additional resource processing, such as
	 * compressing, cleaning etc. By default it just returns unmodified
	 * content.
	 */
	protected String onResourceContent(String content) {
		return content;
	}

	// ---------------------------------------------------------------- url rewriting

	/**
	 * Resolves real action path for given one.
	 * When some URLs are dynamically created, many different links points
	 * to the same page. Use this to prevent memory leaking.
	 */
	protected String resolveRealActionPath(String actionPath) {
		return actionPath;
	}

	// ---------------------------------------------------------------- reset

	/**
	 * Clears all settings and removes all created bundle files from file system.
	 */
	public synchronized void reset() {
		if (strategy == Strategy.ACTION_MANAGED) {
			actionBundles.clear();
			mirrors.clear();
		}

		FindFile ff = new WildcardFindFile("*");
		ff.setIncludeDirs(false);
		ff.searchPath(new File(bundleFolder, staplerPath));

		File f;
		int count = 0;
		while ((f = ff.nextFile()) != null) {
			f.delete();
			count++;
		}
		if (log.isInfoEnabled()) {
			log.info("reset: " + count + " bundle files deleted.");
		}
	}

	// ---------------------------------------------------------------- css related

	/**
	 * Returns <code>true</code> if resource is CSS, so the
	 * CSS urls can be fixed.
	 */
	protected boolean isCssResource(String src) {
		return src.endsWith(".css");
	}

	private static final Pattern CSS_URL_PATTERN = Pattern.compile("url\\s*\\(\\s*([^\\)\\s]*)\\s*\\)", Pattern.CASE_INSENSITIVE);

	/**
	 * Returns the content with all relative URLs fixed.
	 */
	protected String fixCssRelativeUrls(String content, String src) {

		String path = FileNameUtil.getPath(src);

		Matcher matcher = CSS_URL_PATTERN.matcher(content);

		StringBuilder sb = new StringBuilder(content.length());

		int start = 0;

		while (matcher.find()) {
			sb.append(content.substring(start, matcher.start()));

			String url = fixRelativeUrl(matcher.group(1), path);

			sb.append(url);

			start = matcher.end();
		}

		sb.append(content.substring(start));

		return sb.toString();
	}

	/**
	 * For a given URL (optionally quoted), produces CSS URL
	 * where relative paths are fixed and prefixed with offsetPath.
	 */
	protected String fixRelativeUrl(String url, String offsetPath) {

		url = StringUtil.removeChars(url, "'\"");   // remove quotes

		StringBuilder res = new StringBuilder();
		res.append("url('");

		if (url.startsWith(StringPool.SLASH) == false) {
			res.append("../");
			res.append(offsetPath);
		}

		res.append(url);
		res.append("')");

		return res.toString();
	}

}