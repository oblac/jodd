// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.htmlstapler;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.NetUtil;
import jodd.io.ZipUtil;
import jodd.io.findfile.FindFile;
import jodd.util.Base32;
import jodd.util.CharUtil;
import jodd.util.DigestEngine;
import jodd.util.RandomString;
import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

	private static final Logger log = LoggerFactory.getLogger(HtmlStaplerBundlesManager.class);

	protected int bundleCount;		// counter for new bundles

	protected Map<String, String> actionBundles; 			// action -> bundleId/digest
	protected Map<String, String> mirrors;					// temp id -> bundleId

	protected final String webRoot;
	protected final String contextPath;
	protected final Strategy strategy;

	// parameters
	protected String localFilesEncoding = "UTF-8";
	protected String bundleFolder;
	protected String staplerPath = "jodd-bundle";
	protected String localAddressAndPort = "http://localhost:8080";
	protected boolean downloadLocal;
	protected boolean sortResources;
	protected boolean notFoundExceptionEnabled = true;
	protected int randomDigestChars = 0;

	private static String uniqueDigestKey;

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
	public HtmlStaplerBundlesManager(final String contextPath, final String webRoot, final Strategy strategy) {
		this.contextPath = contextPath;
		this.webRoot = webRoot;
		this.strategy = strategy;
		this.bundleFolder = SystemUtil.info().getTempDir();

		if (strategy == Strategy.ACTION_MANAGED) {
			actionBundles = new HashMap<>();
			mirrors = new HashMap<>();
		}
	}

	/**
	 * Starts bundle usage by creating new {@link BundleAction}.
	 */
	public BundleAction start(final String servletPath, final String bundleName) {
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
	public void setSortResources(final boolean sortResources) {
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
	public void setBundleFolder(final String bundleFolder) {
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
	public void setStaplerPath(final String staplerPath) {
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
	public void setLocalFilesEncoding(final String localFilesEncoding) {
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
	public void setLocalAddressAndPort(final String localAddressAndPort) {
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
	public void setDownloadLocal(final boolean downloadLocal) {
		this.downloadLocal = downloadLocal;
	}

	/**
	 * Returns <code>true</code> if exception will be thrown when
	 * resource is not found.
	 */
	public boolean isNotFoundExceptionEnabled() {
		return notFoundExceptionEnabled;
	}

	/**
	 * Sets if exception should be thrown when some resource is not found.
	 * If not enabled, the error will be logged as a warning.
	 */
	public void setNotFoundExceptionEnabled(final boolean notFoundExceptionEnabled) {
		this.notFoundExceptionEnabled = notFoundExceptionEnabled;
	}

	/**
	 * Returns the number of random digest chars.
	 */
	public int getRandomDigestChars() {
		return randomDigestChars;
	}

	/**
	 * Sets the number of random characters that will be appended to the
	 * {@link #createDigest(String) digest}. When it is set to 0, nothing
	 * will be appended to the digest. Otherwise, a random string will be
	 * generated (containing only letters and digits) and appended to the
	 * digest.
	 * <p>
	 * Random digest chars is a <b>unique</b> key per one VM!
	 * This key is initialized only once.
	 * This is useful to automatically expire any cache that browsers may have in
	 * JS and CSS files, so that changes in those files will be downloaded by the
	 * browser.
	 */
	public void setRandomDigestChars(final int randomDigestChars) {
		this.randomDigestChars = randomDigestChars;

		if (randomDigestChars == 0) {
			uniqueDigestKey = null;
		}
		else {
			uniqueDigestKey = new RandomString().randomAlphaNumeric(randomDigestChars);
		}
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Creates bundle file in bundleFolder/staplerPath. Only file object
	 * is created, not the file content.
	 */
	protected File createBundleFile(final String bundleId) {
		final File folder = new File(bundleFolder, staplerPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return new File(folder, bundleId);
	}

	/**
	 * Lookups for bundle file.
	 */
	public File lookupBundleFile(String bundleId) {
		if ((mirrors != null) && (!mirrors.isEmpty())) {
			final String realBundleId = mirrors.remove(bundleId);

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
	public File lookupGzipBundleFile(final File file) throws IOException {
		final String path = file.getPath() + ZipUtil.GZIP_EXT;
		final File gzipFile = new File(path);

		if (!gzipFile.exists()) {
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
	public String lookupBundleId(final String actionPath) {
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
	public synchronized String registerBundle(final String contextPath, final String actionPath, final String tempBundleId, final String bundleContentType, final List<String> sources) {

		if (tempBundleId == null || sources.isEmpty()) {
			if (strategy == Strategy.ACTION_MANAGED) {
				// page does not include any resource source file
				actionBundles.put(actionPath, StringPool.EMPTY);
			}
			return null;
		}

		// create unique digest from the collected sources
		final String[] sourcesArray = sources.toArray(new String[0]);
		for (int i = 0, sourcesArrayLength = sourcesArray.length; i < sourcesArrayLength; i++) {
			sourcesArray[i] = sourcesArray[i].trim().toLowerCase();
		}
		if (sortResources) {
			Arrays.sort(sourcesArray);
		}

		final StringBand sb = new StringBand(sourcesArray.length);
		for (final String src : sourcesArray) {
			sb.append(src);
		}
		final String sourcesString = sb.toString();

		String bundleId = createDigest(sourcesString);
		bundleId += '.' + bundleContentType;

		// bundle appears for the first time, create the bundle
		if (strategy == Strategy.ACTION_MANAGED) {
			actionBundles.put(actionPath, bundleId);
			mirrors.put(tempBundleId, bundleId);
		}
		try {
			createBundle(contextPath, actionPath, bundleId, sources);
		} catch (final IOException ioex) {
			throw new HtmlStaplerException("Can't create bundle", ioex);
		}
		return bundleId;
	}

	/**
	 * Creates digest i.e. bundle id from given string.
	 * Returned digest must be filename safe, for all platforms.
	 */
	protected String createDigest(final String source) {
		final DigestEngine digestEngine = DigestEngine.sha256();

		final byte[] bytes = digestEngine.digest(CharUtil.toSimpleByteArray(source));

		String digest = Base32.encode(bytes);

		if (uniqueDigestKey != null) {
			digest += uniqueDigestKey;
		}

		return digest;
	}

	/**
	 * Creates bundle file by loading resource files content. If bundle file already
	 * exist it will not be recreated!
	 */
	protected void createBundle(final String contextPath, final String actionPath, final String bundleId, final List<String>sources) throws IOException {
		final File bundleFile = createBundleFile(bundleId);
		if (bundleFile.exists()) {
			return;
		}

		final StringBand sb = new StringBand(sources.size() * 2);
		for (String src : sources) {
			if (sb.length() != 0) {
				sb.append(StringPool.NEWLINE);
			}
			String content;
			if (isExternalResource(src)) {
				content = downloadString(src);
			} else {
				if (!downloadLocal) {
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
					final int qmndx = localFile.indexOf('?');
					if (qmndx != -1) {
						localFile = localFile.substring(0, qmndx);
					}

					try {
						content = FileUtil.readString(localFile);
					} catch (final IOException ioex) {
						if (notFoundExceptionEnabled) {
							throw ioex;
						}
						if (log.isWarnEnabled()) {
							log.warn(ioex.getMessage());
						}
						content = null;
					}
				} else {
					// download local resource
					String localUrl = localAddressAndPort;

					if (src.startsWith(StringPool.SLASH)) {
						localUrl += contextPath + src;
					} else {
						localUrl += contextPath + FileNameUtil.getPath(actionPath) + '/' + src;
					}

					content = downloadString(localUrl);
				}

				if (content != null) {
					if (isCssResource(src)) {
						content = fixCssRelativeUrls(content, src);
					}
				}
			}

			if (content != null) {
				content = onResourceContent(content);
				sb.append(content);
			}
		}

		FileUtil.writeString(bundleFile, sb.toString());

		if (log.isInfoEnabled()) {
			log.info("Bundle created: " + bundleId);
		}
	}

	private String downloadString(final String localUrl) throws IOException {
		String content;
		try {
			content = NetUtil.downloadString(localUrl, Charset.forName(localFilesEncoding));
		} catch (final IOException ioex) {
			if (notFoundExceptionEnabled) {
				throw ioex;
			}
			if (log.isWarnEnabled()) {
				log.warn("Download failed: " + localUrl + "; " + ioex.getMessage());
			}
			content = null;
		}
		return content;
	}

	/**
	 * Returns <code>true</code> if resource link has to be downloaded.
	 * By default, if resource link starts with "http://" or with "https://"
	 * it will be considered as external resource.
	 */
	protected boolean isExternalResource(final String link) {
		return link.startsWith("http://") || (link.startsWith("https://"));
	}

	/**
	 * Invoked before resource content is stored in the bundle.
	 * May be us used for additional resource processing, such as
	 * compressing, cleaning etc. By default it just returns unmodified
	 * content.
	 */
	protected String onResourceContent(final String content) {
		return content;
	}

	// ---------------------------------------------------------------- url rewriting

	/**
	 * Resolves real action path for given one.
	 * When some URLs are dynamically created, many different links points
	 * to the same page. Use this to prevent memory leaking.
	 */
	protected String resolveRealActionPath(final String actionPath) {
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

		final FindFile ff = new FindFile();
		ff.includeDirs(false);
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
	protected boolean isCssResource(final String src) {
		return src.endsWith(".css");
	}

	private static final Pattern CSS_URL_PATTERN = Pattern.compile("url\\s*\\(\\s*([^\\)\\s]*)\\s*\\)", Pattern.CASE_INSENSITIVE);

	/**
	 * Returns the content with all relative URLs fixed.
	 */
	protected String fixCssRelativeUrls(final String content, final String src) {
		final String path = FileNameUtil.getPath(src);

		final Matcher matcher = CSS_URL_PATTERN.matcher(content);

		final StringBuilder sb = new StringBuilder(content.length());

		int start = 0;

		while (matcher.find()) {
			sb.append(content, start, matcher.start());

			final String matchedUrl = StringUtil.removeChars(matcher.group(1), "'\"");

			final String url;
			if (matchedUrl.startsWith("https://") || matchedUrl.startsWith("http://") || matchedUrl.startsWith("data:")) {
				url = "url('" + matchedUrl + "')";
			}
			else {
				url = fixRelativeUrl(matchedUrl, path);
			}

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
	protected String fixRelativeUrl(final String url, final String offsetPath) {
		final StringBuilder res = new StringBuilder();
		res.append("url('");

		if (!url.startsWith(StringPool.SLASH)) {
			res
				.append("../")
				.append(offsetPath);
		}

		res.append(url).append("')");

		return res.toString();
	}

}
