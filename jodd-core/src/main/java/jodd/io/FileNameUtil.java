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

package jodd.io;

import jodd.system.SystemUtil;
import jodd.util.StringPool;

import java.io.File;

/**
 * General filename and filepath manipulation utilities.
 * <p>
 * When dealing with filenames you can hit problems when moving from a Windows
 * based development machine to a Unix based production machine.
 * This class aims to help avoid those problems.
 * <p>
 * <b>NOTE</b>: You may be able to avoid using this class entirely simply by
 * using JDK {@link java.io.File File} objects and the two argument constructor
 * {@link java.io.File#File(java.io.File, java.lang.String) File(File,String)}.
 * <p>
 * Most methods on this class are designed to work the same on both Unix and Windows.
 * Those that don't include 'System', 'Unix' or 'Windows' in their name.
 * <p>
 * Most methods recognise both separators (forward and back), and both
 * sets of prefixes. See the javadoc of each method for details.
 * <p>
 * This class defines six components within a filename
 * (example C:\dev\project\file.txt):
 * <ul>
 * <li>the prefix - C:\</li>
 * <li>the path - dev\project\</li>
 * <li>the full path - C:\dev\project\</li>
 * <li>the name - file.txt</li>
 * <li>the base name - file</li>
 * <li>the extension - txt</li>
 * </ul>
 * Note that this class works best if directory filenames end with a separator.
 * If you omit the last separator, it is impossible to determine if the filename
 * corresponds to a file or a directory. As a result, we have chosen to say
 * it corresponds to a file.
 * <p>
 * This class only supports Unix and Windows style names.
 * Prefixes are matched as follows:
 * <pre>{@code
 * Windows:
 * a\b\c.txt           --> ""          --> relative
 * \a\b\c.txt          --> "\"         --> current drive absolute
 * C:a\b\c.txt         --> "C:"        --> drive relative
 * C:\a\b\c.txt        --> "C:\"       --> absolute
 * \\server\a\b\c.txt  --> "\\server\" --> UNC
 *
 * Unix:
 * a/b/c.txt           --> ""          --> relative
 * /a/b/c.txt          --> "/"         --> absolute
 * ~/a/b/c.txt         --> "~/"        --> current user
 * ~                   --> "~/"        --> current user (slash added)
 * ~user/a/b/c.txt     --> "~user/"    --> named user
 * ~user               --> "~user/"    --> named user (slash added)
 * }</pre>
 * Both prefix styles are matched always, irrespective of the machine that you are
 * currently running on.
 */
public class FileNameUtil {

	/**
	 * The extension separator character.
	 */
	private static final char EXTENSION_SEPARATOR = '.';

	/**
	 * The Unix separator character.
	 */
	private static final char UNIX_SEPARATOR = '/';

	/**
	 * The Windows separator character.
	 */
	private static final char WINDOWS_SEPARATOR = '\\';

	/**
	 * The system separator character.
	 */
	private static final char SYSTEM_SEPARATOR = File.separatorChar;

	/**
	 * The separator character that is the opposite of the system separator.
	 */
	private static final char OTHER_SEPARATOR;
	static {
		if (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR) {
			OTHER_SEPARATOR = UNIX_SEPARATOR;
		} else {
			OTHER_SEPARATOR = WINDOWS_SEPARATOR;
		}
	}

	/**
	 * Checks if the character is a separator.
	 */
	private static boolean isSeparator(final char ch) {
		return (ch == UNIX_SEPARATOR) || (ch == WINDOWS_SEPARATOR);
	}

	// ---------------------------------------------------------------- normalization

	public static String normalize(final String filename) {
		return doNormalize(filename, SYSTEM_SEPARATOR, true);
	}

	/**
	 * Normalizes a path, removing double and single dot path steps.
	 * <p>
	 * This method normalizes a path to a standard format.
	 * The input may contain separators in either Unix or Windows format.
	 * The output will contain separators in the format of the system.
	 * <p>
	 * A trailing slash will be retained.
	 * A double slash will be merged to a single slash (but UNC names are handled).
	 * A single dot path segment will be removed.
	 * A double dot will cause that path segment and the one before to be removed.
	 * If the double dot has no parent path segment to work with, <code>null</code>
	 * is returned.
	 * <p>
	 * The output will be the same on both Unix and Windows except
	 * for the separator character.
	 * <pre>{@code
	 * /foo//               -->   /foo/
	 * /foo/./              -->   /foo/
	 * /foo/../bar          -->   /bar
	 * /foo/../bar/         -->   /bar/
	 * /foo/../bar/../baz   -->   /baz
     * //foo//./bar         -->   /foo/bar
	 * /../                 -->   null
	 * ../foo               -->   null
	 * foo/bar/..           -->   foo/
	 * foo/../../bar        -->   null
	 * foo/../bar           -->   bar
	 * //server/foo/../bar  -->   //server/bar
	 * //server/../bar      -->   null
	 * C:\foo\..\bar        -->   C:\bar
	 * C:\..\bar            -->   null
	 * ~/foo/../bar/        -->   ~/bar/
	 * ~/../bar             -->   null
	 * }</pre>
	 * (Note the file separator returned will be correct for Windows/Unix)
	 *
	 * @param filename  the filename to normalize, null returns null
	 * @return the normalized filename, or null if invalid
	 */
	public static String normalize(final String filename, final boolean unixSeparator) {
		char separator = (unixSeparator ? UNIX_SEPARATOR : WINDOWS_SEPARATOR);
		return doNormalize(filename, separator, true);
	}

	public static String normalizeNoEndSeparator(final String filename) {
		return doNormalize(filename, SYSTEM_SEPARATOR, false);
	}

	/**
	 * Normalizes a path, removing double and single dot path steps,
	 * and removing any final directory separator.
	 * <p>
	 * This method normalizes a path to a standard format.
	 * The input may contain separators in either Unix or Windows format.
	 * The output will contain separators in the format of the system.
	 * <p>
	 * A trailing slash will be removed.
	 * A double slash will be merged to a single slash (but UNC names are handled).
	 * A single dot path segment will be removed.
	 * A double dot will cause that path segment and the one before to be removed.
	 * If the double dot has no parent path segment to work with, <code>null</code>
	 * is returned.
	 * <p>
	 * The output will be the same on both Unix and Windows except
	 * for the separator character.
	 * <pre>{@code
	 * /foo//               -->   /foo
	 * /foo/./              -->   /foo
	 * /foo/../bar          -->   /bar
	 * /foo/../bar/         -->   /bar
	 * /foo/../bar/../baz   -->   /baz
	 * /foo//./bar          -->   /foo/bar
	 * /../                 -->   null
	 * ../foo               -->   null
	 * foo/bar/..           -->   foo
	 * foo/../../bar        -->   null
	 * foo/../bar           -->   bar
	 * //server/foo/../bar  -->   //server/bar
	 * //server/../bar      -->   null
	 * C:\foo\..\bar        -->   C:\bar
	 * C:\..\bar            -->   null
	 * ~/foo/../bar/        -->   ~/bar
	 * ~/../bar             -->   null
	 * }</pre>
	 * (Note the file separator returned will be correct for Windows/Unix)
	 *
	 * @param filename  the filename to normalize, null returns null
	 * @return the normalized filename, or null if invalid
	 */
	public static String normalizeNoEndSeparator(final String filename, final boolean unixSeparator) {
		char separator = (unixSeparator ? UNIX_SEPARATOR : WINDOWS_SEPARATOR);
		return doNormalize(filename, separator, false);
	}

	/**
	 * Internal method to perform the normalization.
	 *
	 * @param filename file name
	 * @param separator separator character to use
	 * @param keepSeparator <code>true</code> to keep the final separator
	 * @return normalized filename
	 */
	private static String doNormalize(final String filename, final char separator, final boolean keepSeparator) {
		if (filename == null) {
			return null;
		}
		int size = filename.length();
		if (size == 0) {
			return filename;
		}
		int prefix = getPrefixLength(filename);
		if (prefix < 0) {
			return null;
		}

		char[] array = new char[size + 2];  // +1 for possible extra slash, +2 for arraycopy
		filename.getChars(0, filename.length(), array, 0);

		// fix separators throughout
		char otherSeparator = (separator == SYSTEM_SEPARATOR ? OTHER_SEPARATOR : SYSTEM_SEPARATOR);
		for (int i = 0; i < array.length; i++) {
			if (array[i] == otherSeparator) {
				array[i] = separator;
			}
		}

		// add extra separator on the end to simplify code below
		boolean lastIsDirectory = true;
		if (array[size - 1] != separator) {
            array[size++] = separator;
			lastIsDirectory = false;
		}

		// adjoining slashes
		for (int i = prefix + 1; i < size; i++) {
			if (array[i] == separator && array[i - 1] == separator) {
				System.arraycopy(array, i, array, i - 1, size - i);
				size--;
				i--;
			}
		}

		// dot slash
		for (int i = prefix + 1; i < size; i++) {
			if (array[i] == separator && array[i - 1] == '.' &&
					(i == prefix + 1 || array[i - 2] == separator)) {
				if (i == size - 1) {
					lastIsDirectory = true;
				}
				System.arraycopy(array, i + 1, array, i - 1, size - i);
				size -= 2;
				i--;
			}
		}

		// double dot slash
		outer:
		for (int i = prefix + 2; i < size; i++) {
			if (array[i] == separator && array[i - 1] == '.' && array[i - 2] == '.' &&
					(i == prefix + 2 || array[i - 3] == separator)) {
				if (i == prefix + 2) {
					return null;
				}
				if (i == size - 1) {
					lastIsDirectory = true;
				}
				int j;
				for (j = i - 4 ; j >= prefix; j--) {
					if (array[j] == separator) {
						// remove b/../ from a/b/../c
						System.arraycopy(array, i + 1, array, j + 1, size - i);
						size -= (i - j);
						i = j + 1;
						continue outer;
					}
				}
				// remove a/../ from a/../c
				System.arraycopy(array, i + 1, array, prefix, size - i);
				size -= (i + 1 - prefix);
				i = prefix + 1;
			}
		}

		if (size <= 0) {  // should never be less than 0
			return StringPool.EMPTY;
		}
		if (size <= prefix) {  // should never be less than prefix
			return new String(array, 0, size);
		}
		if (lastIsDirectory && keepSeparator) {
			return new String(array, 0, size);  // keep trailing separator
		}
		return new String(array, 0, size - 1);  // lose trailing separator
	}

	//-----------------------------------------------------------------------
	/**
	 * Concatenates a filename to a base path using normal command line style rules.
	 * <p>
	 * The effect is equivalent to resultant directory after changing
	 * directory to the first argument, followed by changing directory to
	 * the second argument.
	 * <p>
	 * The first argument is the base path, the second is the path to concatenate.
	 * The returned path is always normalized via {@link #normalize(String)},
	 * thus <code>..</code> is handled.
	 * <p>
	 * If <code>pathToAdd</code> is absolute (has an absolute prefix), then
	 * it will be normalized and returned.
	 * Otherwise, the paths will be joined, normalized and returned.
	 * <p>
	 * The output will be the same on both Unix and Windows except
	 * for the separator character.
	 * <pre>{@code
	 * /foo/ + bar          -->   /foo/bar
	 * /foo + bar           -->   /foo/bar
	 * /foo + /bar          -->   /bar
	 * /foo + C:/bar        -->   C:/bar
	 * /foo + C:bar         -->   C:bar (*)
	 * /foo/a/ + ../bar     -->   foo/bar
	 * /foo/ + ../../bar    -->   null
	 * /foo/ + /bar         -->   /bar
	 * /foo/.. + /bar       -->   /bar
	 * /foo + bar/c.txt     -->   /foo/bar/c.txt
	 * /foo/c.txt + bar     -->   /foo/c.txt/bar (!)
	 * }</pre>
	 * (*) Note that the Windows relative drive prefix is unreliable when
	 * used with this method.
	 * (!) Note that the first parameter must be a path. If it ends with a name, then
	 * the name will be built into the concatenated path. If this might be a problem,
	 * use {@link #getFullPath(String)} on the base path argument.
	 *
	 * @param basePath  the base path to attach to, always treated as a path
	 * @param fullFilenameToAdd  the filename (or path) to attach to the base
	 * @return the concatenated path, or null if invalid
	 */
	public static String concat(final String basePath, final String fullFilenameToAdd) {
		return doConcat(basePath, fullFilenameToAdd, SYSTEM_SEPARATOR);
	}
	public static String concat(final String basePath, final String fullFilenameToAdd, final boolean unixSeparator) {
		char separator = (unixSeparator ? UNIX_SEPARATOR : WINDOWS_SEPARATOR);
		return doConcat(basePath, fullFilenameToAdd, separator);
	}
	public static String doConcat(final String basePath, final String fullFilenameToAdd, final char separator) {
		int prefix = getPrefixLength(fullFilenameToAdd);
		if (prefix < 0) {
			return null;
		}
		if (prefix > 0) {
			return doNormalize(fullFilenameToAdd, separator, true);
		}
		if (basePath == null) {
			return null;
		}
		int len = basePath.length();
		if (len == 0) {
			return doNormalize(fullFilenameToAdd, separator, true);
		}
		char ch = basePath.charAt(len - 1);
		if (isSeparator(ch)) {
			return doNormalize(basePath + fullFilenameToAdd, separator, true);
		} else {
			return doNormalize(basePath + '/' + fullFilenameToAdd, separator, true);
		}
	}

	// ---------------------------------------------------------------- separator conversion

	/**
	 * Converts all separators to the Unix separator of forward slash.
	 *
	 * @param path  the path to be changed, null ignored
	 * @return the updated path
	 */
	public static String separatorsToUnix(final String path) {
		if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1) {
			return path;
		}
		return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
	}

	/**
	 * Converts all separators to the Windows separator of backslash.
	 *
	 * @param path  the path to be changed, null ignored
	 * @return the updated path
	 */
	public static String separatorsToWindows(final String path) {
		if (path == null || path.indexOf(UNIX_SEPARATOR) == -1) {
			return path;
		}
		return path.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
	}

	/**
	 * Converts all separators to the system separator.
	 *
	 * @param path  the path to be changed, null ignored
	 * @return the updated path
	 */
	public static String separatorsToSystem(final String path) {
		if (path == null) {
			return null;
		}
		if (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR) {
			return separatorsToWindows(path);
		} else {
			return separatorsToUnix(path);
		}
	}

	// ---------------------------------------------------------------- prefix
	/**
	 * Returns the length of the filename prefix, such as <code>C:/</code> or <code>~/</code>.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * <p>
	 * The prefix length includes the first slash in the full filename
	 * if applicable. Thus, it is possible that the length returned is greater
	 * than the length of the input string.
	 * <pre>{@code
	 * Windows:
	 * a\b\c.txt           --> ""          --> relative
	 * \a\b\c.txt          --> "\"         --> current drive absolute
	 * C:a\b\c.txt         --> "C:"        --> drive relative
	 * C:\a\b\c.txt        --> "C:\"       --> absolute
	 * \\server\a\b\c.txt  --> "\\server\" --> UNC
	 *
	 * Unix:
	 * a/b/c.txt           --> ""          --> relative
	 * /a/b/c.txt          --> "/"         --> absolute
	 * ~/a/b/c.txt         --> "~/"        --> current user
	 * ~                   --> "~/"        --> current user (slash added)
	 * ~user/a/b/c.txt     --> "~user/"    --> named user
	 * ~user               --> "~user/"    --> named user (slash added)
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * ie. both Unix and Windows prefixes are matched regardless.
	 *
	 * @param filename  the filename to find the prefix in, null returns -1
	 * @return the length of the prefix, -1 if invalid or null
	 */
	public static int getPrefixLength(final String filename) {
		if (filename == null) {
			return -1;
		}
		int len = filename.length();
		if (len == 0) {
			return 0;
		}
		char ch0 = filename.charAt(0);
		if (ch0 == ':') {
			return -1;
		}
		if (len == 1) {
			if (ch0 == '~') {
				return 2;  // return a length greater than the input
			}
			return (isSeparator(ch0) ? 1 : 0);
		} else {
			if (ch0 == '~') {
				int posUnix = filename.indexOf(UNIX_SEPARATOR, 1);
				int posWin = filename.indexOf(WINDOWS_SEPARATOR, 1);
				if (posUnix == -1 && posWin == -1) {
					return len + 1;  // return a length greater than the input
				}
				posUnix = (posUnix == -1 ? posWin : posUnix);
				posWin = (posWin == -1 ? posUnix : posWin);
				return Math.min(posUnix, posWin) + 1;
			}
			char ch1 = filename.charAt(1);
			if (ch1 == ':') {
				ch0 = Character.toUpperCase(ch0);
				if (ch0 >= 'A' && ch0 <= 'Z') {
					if (len == 2 || !isSeparator(filename.charAt(2))) {
						return 2;
					}
					return 3;
				}
				return -1;

			} else if (isSeparator(ch0) && isSeparator(ch1)) {
				int posUnix = filename.indexOf(UNIX_SEPARATOR, 2);
				int posWin = filename.indexOf(WINDOWS_SEPARATOR, 2);
				if ((posUnix == -1 && posWin == -1) || posUnix == 2 || posWin == 2) {
					return -1;
				}
				posUnix = (posUnix == -1 ? posWin : posUnix);
				posWin = (posWin == -1 ? posUnix : posWin);
				return Math.min(posUnix, posWin) + 1;
			} else {
				return (isSeparator(ch0) ? 1 : 0);
			}
		}
	}

	/**
	 * Returns the index of the last directory separator character.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The position of the last forward or backslash is returned.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there is no such character
	 */
	public static int indexOfLastSeparator(final String filename) {
		if (filename == null) {
			return -1;
		}
		int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
		int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	/**
	 * Returns the index of the last extension separator character, which is a dot.
	 * <p>
	 * This method also checks that there is no directory separator after the last dot.
	 * To do this it uses {@link #indexOfLastSeparator(String)} which will
	 * handle a file in either Unix or Windows format.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there
	 * is no such character
	 */
	public static int indexOfExtension(final String filename) {
		if (filename == null) {
			return -1;
		}
		int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
		int lastSeparator = indexOfLastSeparator(filename);
		return (lastSeparator > extensionPos ? -1 : extensionPos);
	}

	/**
	 * Returns <code>true</code> if file has extension.
	 */
	public static boolean hasExtension(final String filename) {
		return indexOfExtension(filename) != -1;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Gets the prefix from a full filename, such as <code>C:/</code>
	 * or <code>~/</code>.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The prefix includes the first slash in the full filename where applicable.
	 * <pre>{@code
	 * Windows:
	 * a\b\c.txt           --> ""          --> relative
	 * \a\b\c.txt          --> "\"         --> current drive absolute
	 * C:a\b\c.txt         --> "C:"        --> drive relative
	 * C:\a\b\c.txt        --> "C:\"       --> absolute
	 * \\server\a\b\c.txt  --> "\\server\" --> UNC
	 *
	 * Unix:
	 * a/b/c.txt           --> ""          --> relative
	 * /a/b/c.txt          --> "/"         --> absolute
	 * ~/a/b/c.txt         --> "~/"        --> current user
	 * ~                   --> "~/"        --> current user (slash added)
	 * ~user/a/b/c.txt     --> "~user/"    --> named user
	 * ~user               --> "~user/"    --> named user (slash added)
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * ie. both Unix and Windows prefixes are matched regardless.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the prefix of the file, null if invalid
	 */
	public static String getPrefix(final String filename) {
		if (filename == null) {
			return null;
		}
		int len = getPrefixLength(filename);
		if (len < 0) {
			return null;
		}
		if (len > filename.length()) {
			return filename + UNIX_SEPARATOR;  // we know this only happens for unix
		}
		return filename.substring(0, len);
	}

	/**
	 * Gets the path from a full filename, which excludes the prefix.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before and
	 * including the last forward or backslash.
	 * <pre>{@code
	 * C:\a\b\c.txt --> a\b\
	 * ~/a/b/c.txt  --> a/b/
	 * a.txt        --> ""
	 * a/b/c        --> a/b/
	 * a/b/c/       --> a/b/c/
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * <p>
	 * This method drops the prefix from the result.
	 * See {@link #getFullPath(String)} for the method that retains the prefix.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getPath(final String filename) {
		return doGetPath(filename, 1);
	}

	/**
	 * Gets the path from a full filename, which excludes the prefix, and
	 * also excluding the final directory separator.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before the
	 * last forward or backslash.
	 * <pre>{@code
	 * C:\a\b\c.txt --> a\b
	 * ~/a/b/c.txt  --> a/b
	 * a.txt        --> ""
	 * a/b/c        --> a/b
	 * a/b/c/       --> a/b/c
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * <p>
	 * This method drops the prefix from the result.
	 * See {@link #getFullPathNoEndSeparator(String)} for the method that retains the prefix.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getPathNoEndSeparator(final String filename) {
		return doGetPath(filename, 0);
	}

	/**
	 * Does the work of getting the path.
	 *
	 * @param filename  the filename
	 * @param separatorAdd  0 to omit the end separator, 1 to return it
	 * @return the path
	 */
	private static String doGetPath(final String filename, final int separatorAdd) {
		if (filename == null) {
			return null;
		}
		int prefix = getPrefixLength(filename);
		if (prefix < 0) {
			return null;
		}
		int index = indexOfLastSeparator(filename);
        int endIndex = index + separatorAdd;
        if (prefix >= filename.length() || index < 0 || prefix >= endIndex) {
			return StringPool.EMPTY;
		}
        return filename.substring(prefix, endIndex);
	}

	/**
	 * Gets the full path from a full filename, which is the prefix + path.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before and
	 * including the last forward or backslash.
	 * <pre>{@code
	 * C:\a\b\c.txt --> C:\a\b\
	 * ~/a/b/c.txt  --> ~/a/b/
	 * a.txt        --> ""
	 * a/b/c        --> a/b/
	 * a/b/c/       --> a/b/c/
	 * C:           --> C:
	 * C:\          --> C:\
	 * ~            --> ~/
	 * ~/           --> ~/
	 * ~user        --> ~user/
	 * ~user/       --> ~user/
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getFullPath(final String filename) {
		return doGetFullPath(filename, true);
	}

	/**
	 * Gets the full path from a full filename, which is the prefix + path,
	 * and also excluding the final directory separator.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before the
	 * last forward or backslash.
	 * <pre>{@code
	 * C:\a\b\c.txt --> C:\a\b
	 * ~/a/b/c.txt  --> ~/a/b
	 * a.txt        --> ""
	 * a/b/c        --> a/b
	 * a/b/c/       --> a/b/c
	 * C:           --> C:
	 * C:\          --> C:\
	 * ~            --> ~
	 * ~/           --> ~
	 * ~user        --> ~user
	 * ~user/       --> ~user
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getFullPathNoEndSeparator(final String filename) {
		return doGetFullPath(filename, false);
	}

	/**
	 * Does the work of getting the path.
	 *
	 * @param filename  the filename
	 * @param includeSeparator  true to include the end separator
	 * @return the path
	 */
	private static String doGetFullPath(final String filename, final boolean includeSeparator) {
		if (filename == null) {
			return null;
		}
		int prefix = getPrefixLength(filename);
		if (prefix < 0) {
			return null;
		}
		if (prefix >= filename.length()) {
			if (includeSeparator) {
				return getPrefix(filename);  // add end slash if necessary
			} else {
				return filename;
			}
		}
		int index = indexOfLastSeparator(filename);
		if (index < 0) {
			return filename.substring(0, prefix);
		}
		int end = index + (includeSeparator ?  1 : 0);
        if (end == 0) {
            end++;
        }
		return filename.substring(0, end);
	}

	/**
	 * Gets the name minus the path from a full filename.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The text after the last forward or backslash is returned.
	 * <pre>{@code
	 * a/b/c.txt --> c.txt
	 * a.txt     --> a.txt
	 * a/b/c     --> c
	 * a/b/c/    --> ""
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the name of the file without the path, or an empty string if none exists
	 */
	public static String getName(final String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfLastSeparator(filename);
		return filename.substring(index + 1);
	}

	/**
	 * Gets the base name, minus the full path and extension, from a full filename.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The text after the last forward or backslash and before the last dot is returned.
	 * <pre>{@code
	 * a/b/c.txt --> c
	 * a.txt     --> a
	 * a/b/c     --> c
	 * a/b/c/    --> ""
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the name of the file without the path, or an empty string if none exists
	 */
	public static String getBaseName(final String filename) {
		return removeExtension(getName(filename));
	}

	/**
	 * Gets the extension of a filename.
	 * <p>
	 * This method returns the textual part of the filename after the last dot.
	 * There must be no directory separator after the dot.
	 * <pre>{@code
	 * foo.txt      --> "txt"
	 * a/b/c.jpg    --> "jpg"
	 * a/b.txt/c    --> ""
	 * a/b/c        --> ""
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename the filename to retrieve the extension of.
	 * @return the extension of the file or an empty string if none exists.
	 */
	public static String getExtension(final String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return StringPool.EMPTY;
		} else {
			return filename.substring(index + 1);
		}
	}

	//----------------------------------------------------------------------- remove

	/**
	 * Removes the extension from a filename.
	 * <p>
	 * This method returns the textual part of the filename before the last dot.
	 * There must be no directory separator after the dot.
	 * <pre>{@code
	 * foo.txt    --> foo
	 * a\b\c.jpg  --> a\b\c
	 * a\b\c      --> a\b\c
	 * a.b\c      --> a.b\c
	 * }</pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the filename minus the extension
	 */
	public static String removeExtension(final String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return filename;
		} else {
			return filename.substring(0, index);
		}
	}

	// ---------------------------------------------------------------- equals

	/**
	 * Checks whether two filenames are equal exactly.
	 */
	public static boolean equals(final String filename1, final String filename2) {
		return equals(filename1, filename2, false);
	}

	/**
	 * Checks whether two filenames are equal using the case rules of the system.
	 */
	public static boolean equalsOnSystem(final String filename1, final String filename2) {
		return equals(filename1, filename2, true);
	}

	/**
	 * Checks whether two filenames are equal optionally using the case rules of the system.
	 * <p>
	 *
	 * @param filename1  the first filename to query, may be null
	 * @param filename2  the second filename to query, may be null
	 * @param system  whether to use the system (windows or unix)
	 * @return true if the filenames are equal, null equals null
	 */
	private static boolean equals(final String filename1, final String filename2, final boolean system) {
		//noinspection StringEquality
		if (filename1 == filename2) {
			return true;
		}
		if (filename1 == null || filename2 == null) {
			return false;
		}
		if (system && (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR)) {
			return filename1.equalsIgnoreCase(filename2);
		} else {
			return filename1.equals(filename2);
		}
	}

	// ---------------------------------------------------------------- split

	/**
	 * Splits filename into a array of four Strings containing prefix, path, basename and extension.
	 * Path will contain ending separator.
	 */
	public static String[] split(final String filename) {
		String prefix = getPrefix(filename);
		if (prefix == null) {
			prefix = StringPool.EMPTY;
		}
		int lastSeparatorIndex = indexOfLastSeparator(filename);
		int lastExtensionIndex = indexOfExtension(filename);

		String path;
		String baseName;
		String extension;

		if (lastSeparatorIndex == -1) {
			path = StringPool.EMPTY;
			if (lastExtensionIndex == -1) {
				baseName = filename.substring(prefix.length());
				extension = StringPool.EMPTY;
			} else {
				baseName = filename.substring(prefix.length(), lastExtensionIndex);
				extension = filename.substring(lastExtensionIndex + 1);
			}
		} else {
			path = filename.substring(prefix.length(), lastSeparatorIndex + 1);
			if (lastExtensionIndex == -1) {
				baseName = filename.substring(prefix.length() + path.length());
				extension = StringPool.EMPTY;
			} else {
				baseName = filename.substring(prefix.length() + path.length(), lastExtensionIndex);
				extension = filename.substring(lastExtensionIndex + 1);
			}
		}
		return new String[] {prefix, path, baseName, extension};
	}

	// ---------------------------------------------------------------- home

	/**
	 * Resolve <code>~</code> in the path.
	 */
	public static String resolveHome(final String path) {
		if (path.length() == 1) {
			if (path.charAt(0) == '~') {
				return SystemUtil.info().getHomeDir();
			}
			return path;
		}
		if (path.length() >= 2) {
			if ((path.charAt(0) == '~') && (path.charAt(1) == File.separatorChar)) {
				return SystemUtil.info().getHomeDir() + path.substring(1);
			}
		}
		return path;
	}

	/**
	 * Calculates relative path of target path on base path.
	 */
	public static String relativePath(final String targetPath, final String basePath) {
		return new File(basePath).toPath().relativize(new File(targetPath).toPath()).toString();
	}

}