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

package jodd.mail;

import jodd.util.StringPool;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse, clean up, and extract email addresses per RFC2822 syntax.
 * It can be trusted to only provide authenticated results.
 * This class has been successfully used on many billion real-world addresses, live in
 * production environments, but it's not perfect yet, since the standard is quite complex.
 * <p>
 * Note: Unlike <code>InternetAddress</code>, this class will preserve any RFC-2047-encoding of international
 * characters.
 */
public class RFC2822AddressParser {
	private boolean ALLOW_DOMAIN_LITERALS = false;
	private boolean ALLOW_QUOTED_IDENTIFIERS = true;
	private boolean ALLOW_DOT_IN_ATEXT = false;
	private boolean EXTRACT_CFWS_PERSONAL_NAMES = true;
	private boolean ALLOW_SQUARE_BRACKETS_IN_ATEXT = false;
	private boolean ALLOW_PARENS_IN_LOCALPART = true;

	/**
	 * Strict parser.
	 */
	public static final RFC2822AddressParser STRICT =
		new RFC2822AddressParser()
			.allowDomainLiterals(true)
			.allowQuotedIdentifiers(true)
			.allowDotInAtext(false)
			.extractCfwsPersonalName(true)
			.allowSquareBracketsInAtext(false)
			.allowParentheseInLocalpart(true);

	/**
	 * Loose parser.
	 */
	public static final RFC2822AddressParser LOOSE = new RFC2822AddressParser();

	/**
	 * Changes the behavior of the domain parsing. If {@code true}, the parser will
	 * allow 2822 domains, which include single-level domains (e.g. bob@localhost) as well
	 * as domain literals, e.g.:
	 *
	 * <ul>
	 * <li><code>someone@[192.168.1.100]</code> or</li>
	 * <li><code>john.doe@[23:33:A2:22:16:1F]</code> or</li>
	 * <li><code>me@[my computer]</code></li>
	 * </ul>
	 *
	 * The RFC says these are valid email addresses, but many don't like
	 * allowing them. If you don't want to allow them, and only want to allow valid domain names
	 * (<a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>, x.y.z.com, etc),
	 * and specifically only those with at least two levels ("example.com"), then
	 * set this flag to {@code false}.
	 */
	public RFC2822AddressParser allowDomainLiterals(boolean allow) {
		ALLOW_DOMAIN_LITERALS = allow;
		resetPatterns();
		return this;
	}

	/**
	 * Defines if quoted identifiers are allowed.
	 * Using quotes and angle brackets around the raw address may be allowed, e.g.:
	 *
	 * <ul>
	 * <li><code>"John Smith" &lt;john.smith@somewhere.com&gt;</code></li>
	 * </ul>
	 * 
	 * The RFC says this is a valid mailbox. If you don't want to
	 * allow this, because for example, you only want users to enter in
	 * a raw address (<code>john.smith@somewhere.com</code> - no quotes or angle
	 * brackets), then set the flag <code>false</code>.
	 */
	public RFC2822AddressParser allowQuotedIdentifiers(boolean allow) {
		ALLOW_QUOTED_IDENTIFIERS = allow;
		resetPatterns();
		return this;
	}

	/**
	 * Allows &quot;.&quot; to appear in atext (note: only atext which appears
	 * in the 2822 &quot;name-addr&quot; part of the address, not the other instances).
	 * <p>
	 * The addresses:
	 * <ul>
	 * <li><code>Kayaks.org &lt;kayaks@kayaks.org&gt;</code></li>
	 * <li><code>Bob K. Smith&lt;bobksmith@bob.net&gt;</code></li>
	 * </ul>
	 * ...are not valid. They should be:
	 * <ul>
	 * <li><code>&quot;Kayaks.org&quot; &lt;kayaks@kayaks.org&gt;</code></li>
	 * <li><code>&quot;Bob K. Smith&quot; &lt;bobksmith@bob.net&gt;</code></li>
	 * </ul>
	 * If this boolean is set to false, the parser will act per 2822 and will require
	 * the quotes; if set to true, it will allow the use of &quot;.&quot; without quotes.
	 */
	public RFC2822AddressParser allowDotInAtext(boolean allow) {
		ALLOW_DOT_IN_ATEXT = allow;
		resetPatterns();
		return this;
	}

	/**
	 * Controls the behavior of getInternetAddress. If true, allows the real world practice of:
	 * <ul>
	 * <li>&lt;bob@example.com&gt; (Bob Smith)</li>
	 * </ul>
	 *
	 * In this case, &quot;Bob Smith&quot; is not technically the personal name, just a
	 * comment. If this is set to true, the methods will convert this into:
	 * <ul>
	 * <li>Bob Smith &lt;bob@example.com&gt;</li>
	 * </ul>
	 * <p>
	 * This also happens somewhat more often and appropriately with
	 * <code>mailer-daemon@blah.com (Mail Delivery System)</code>.
	 *
	 * <p>
	 * If a personal name appears to the left and CFWS appears to the right of an address,
	 * the methods will favor the personal name to the left. If the methods need to use the
	 * CFWS following the address, they will take the first comment token they find.
	 */
	public RFC2822AddressParser extractCfwsPersonalName(boolean extract) {
		EXTRACT_CFWS_PERSONAL_NAMES = extract;
		resetPatterns();
		return this;
	}

	/**
	 * Allows &quot;[&quot; or &quot;]&quot; to appear in atext.
	 * The address:
	 * <ul><li><code>[Kayaks] &lt;kayaks@kayaks.org&gt;</code></li></ul>
	 *
	 * ...is not valid. It should be:
	 *
	 * <ul><li><code>&quot;[Kayaks]&quot; &lt;kayaks@kayaks.org&gt;</code></li></ul>
	 * <p>
	 * If this boolean is set to false, the parser will act per 2822 and will require
	 * the quotes; if set to true, it will allow them to be missing.
	 * <p>
	 * Use at your own risk. There may be some issue with enabling this feature in conjunction
	 * with {@link #allowDomainLiterals(boolean)}.
	 */
	public RFC2822AddressParser allowSquareBracketsInAtext(boolean allow) {
		ALLOW_SQUARE_BRACKETS_IN_ATEXT = allow;
		resetPatterns();
		return this;
	}

	/**
	 * Allows &quot;)&quot; or &quot;(&quot; to appear in quoted versions of
	 * the localpart (they are never allowed in unquoted versions).
	 * The default (2822) behavior is to allow this, i.e. boolean true.
	 * You can disallow it, but better to leave it true.
	 */
	public RFC2822AddressParser allowParentheseInLocalpart(boolean allow) {
		ALLOW_PARENS_IN_LOCALPART = allow;
		resetPatterns();
		return this;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parsed message address and various information.
	 */
	public static class ParsedAddress {
		private final boolean isValid;
		private final String personalName;
		private final String localPart;
		private final String domain;
		private final InternetAddress internetAddress;
		private final boolean validReturnPath;
		private final String returnPathAddress;

		private ParsedAddress(
				boolean isValid,
				String personalName,
				String localPart,
				String domain,
				InternetAddress internetAddress,
				boolean validReturnPath,
				String returnPathAddress) {

			this.isValid = isValid;
			this.personalName = personalName;
			this.internetAddress = internetAddress;
			this.domain = domain;
			this.localPart = localPart;
			this.validReturnPath = validReturnPath;
			this.returnPathAddress = returnPathAddress;
		}

		/**
		 * Returns <code>true</code> if email is valid.
		 */
		public boolean isValid() {
			return isValid;
		}

		/**
		 * Returns personal name. Returned string does
		 * not reflect any decoding of RFC-2047 encoded personal names.
		 */
		public String getPersonalName() {
			return personalName;
		}

		/**
		 * Returns local part of the email address.
		 */
		public String getLocalPart() {
			return localPart;
		}

		/**
		 * Returns domain part of the email address.
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * Given a 2822-valid single address string, returns an InternetAddress object holding
		 * that address, otherwise returns null. The email address that comes back from the
		 * resulting InternetAddress object's getAddress() call will have comments and unnecessary
		 * quotation marks or whitespace removed.
		 */
		public InternetAddress getInternetAddress() {
			return internetAddress;
		}

		/**
		 * Returns <code>true</code> if the email represents a valid return path.
		 */
		public boolean isValidReturnPath() {
			return validReturnPath;
		}

		/**
		 * Pulls out the cleaned-up return path address. May return an empty string.
		 * Returns null if there are any syntax issues or other weirdness, otherwise
		 * the valid, trimmed return path email address without CFWS, surrounding angle brackets,
		 * with quotes stripped where possible, etc. (may return an empty string).
		 */
		public String getReturnPathAddress() {
			return returnPathAddress;
		}
	}

	/**
	 * Parses email address. Returns {@code null} if parsing fails for some reason.
	 * Returns {@link ParsedAddress parsed address}, that might be valid or note.
	 */
	public ParsedAddress parse(String email) {
		email = email.trim();

		// match all

		Matcher mailboxMatcher = MAILBOX_PATTERN().matcher(email);
		boolean	mailboxMatcherMatches = mailboxMatcher.matches();
		String[] mailboxMatcherParts = mailboxMatcherMatches ? _calcMatcherParts(mailboxMatcher) : null;

		Matcher returnPathMatcher = RETURN_PATH_PATTERN().matcher(email);
		boolean returnPathMatches = returnPathMatcher.matches();

		// extract

		String personalName = null;
		String localPart = null;
		String domain = null;
		InternetAddress internetAddress = null;
		String returnPathAddress = null;

		if (mailboxMatcherMatches) {
			personalName = mailboxMatcherParts[0];
			localPart = mailboxMatcherParts[1];
			domain = mailboxMatcherParts[2];
			internetAddress = pullFromGroups(mailboxMatcher);
		}

		if (returnPathMatches) {
			if (internetAddress != null) {
				returnPathAddress = internetAddress.getAddress();
			}
			else {
				returnPathAddress = StringPool.EMPTY;
			}
		}

		return new ParsedAddress(mailboxMatcherMatches, personalName, localPart, domain, internetAddress, returnPathMatches, returnPathAddress);
	}

	/**
	 * Convenient shortcut of {@link #parse(String)} that returns {@code InternetAddress} or {@code null}.
	 */
	public InternetAddress parseToInternetAddress(String email) {
		ParsedAddress parsedAddress = parse(email);

		if (parsedAddress == null) {
			return null;
		}

		return parsedAddress.getInternetAddress();
	}

	/**
	 * Convenient shortcut of {@link #parse(String)} that returns {@link EmailAddress} or {@code null}.
	 */
	public EmailAddress parseToEmailAddress(String email) {
		ParsedAddress parsedAddress = parse(email);

		if (parsedAddress == null) {
			return null;
		}

		return new EmailAddress(parsedAddress.getPersonalName(), parsedAddress.getLocalPart() + '@' + parsedAddress.getDomain());
	}

	// ---------------------------------------------------------------- regexp

	private Pattern _MAILBOX_PATTERN;
	private Pattern _RETURN_PATH_PATTERN;

	private Pattern _ADDR_SPEC_PATTERN;				// internal
	private Pattern _COMMENT_PATTERN;				// internal
	private Pattern _QUOTED_STRING_WO_CFWS_PATTERN;	// internal

	private Pattern MAILBOX_PATTERN() {
		if (_MAILBOX_PATTERN == null) {
			buildPatterns();
		}
		return _MAILBOX_PATTERN;
	}
	private Pattern RETURN_PATH_PATTERN() {
		if (_RETURN_PATH_PATTERN == null) {
			buildPatterns();
		}
		return _RETURN_PATH_PATTERN;
	}

	/**
	 * Resets patterns so they can be build on next use.
	 */
	private void resetPatterns() {
		_MAILBOX_PATTERN = null;
		_RETURN_PATH_PATTERN = null;
	}

	/**
	 * Builds all regexp patterns.
 	 */
	private void buildPatterns() {

		// http://tools.ietf.org/html/rfc2822

		// RFC 2822 2.2.2 Structured Header Field Bodies

		final String CRLF = "\\r\\n";
		final String WSP = "[ \\t]";
		final String FWSP = "(?:" + WSP + "*" + CRLF + ")?" + WSP + "+";

		// RFC 2822 3.2.1 Primitive tokens

		final String D_QUOTE = "\\\"";
		final String NO_WS_CTL = "\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F";
		final String ASCII_TEXT = "[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F]";

		// RFC 2822 3.2.2 Quoted characters

		final String QUOTED_PAIR = "(?:\\\\" + ASCII_TEXT + ")";

		// RFC 2822 3.2.3 CFWS specification

		final String C_TEXT = "[" + NO_WS_CTL + "\\!-\\'\\*-\\[\\]-\\~]";
		final String C_CONTENT = C_TEXT + "|" + QUOTED_PAIR; // + "|" + comment;
		final String COMMENT = "\\((?:(?:" + FWSP + ")?" + C_CONTENT + ")*(?:" + FWSP + ")?\\)";
		final String CFWS = "(?:(?:" + FWSP + ")?" + COMMENT + ")*(?:(?:(?:" + FWSP + ")?" + COMMENT + ")|(?:" + FWSP + "))";

		// RFC 2822 3.2.4 Atom

		final String A_TEXT =
			"[a-zA-Z0-9\\!\\#-\\'\\*\\+\\-\\/\\=\\?\\^-\\`\\{-\\~"
				+ (ALLOW_DOT_IN_ATEXT ? "\\." : "")
				+ (ALLOW_SQUARE_BRACKETS_IN_ATEXT ? "\\[\\]" : "") + "]";
		final String REGULAR_A_TEXT = "[a-zA-Z0-9\\!\\#-\\'\\*\\+\\-\\/\\=\\?\\^-\\`\\{-\\~]";

		final String ATOM = "(?:" + CFWS + ")?" + A_TEXT + "+" + "(?:" + CFWS + ")?";
		final String DOT_ATOM_TEXT = REGULAR_A_TEXT + "+" + "(?:" + "\\." + REGULAR_A_TEXT + "+)*";
		final String CAP_DOT_ATOM_NO_CFWS = "(?:" + CFWS + ")?(" + DOT_ATOM_TEXT + ")(?:" + CFWS + ")?";
		final String CAP_DOT_ATOM_TRAILING_CFWS = "(?:" + CFWS + ")?(" + DOT_ATOM_TEXT + ")(" + CFWS + ")?";

		// RFC 2822 3.2.5 Quoted strings

		final String Q_TEXT = "[" + NO_WS_CTL + "\\!\\#-\\[\\]-\\~]";
		final String LOCAL_PART_Q_TEXT = "[" + NO_WS_CTL + (ALLOW_PARENS_IN_LOCALPART ? "\\!\\#-\\[\\]-\\~]" : "\\!\\#-\\'\\*-\\[\\]-\\~]");

		final String Q_CONTENT = "(?:" + Q_TEXT + "|" + QUOTED_PAIR + ")";
		final String LOCAL_PART_Q_CONTENT = "(?>" + LOCAL_PART_Q_TEXT + "|" + QUOTED_PAIR + ")";
		final String QUOTED_STRING_WOCFWS = D_QUOTE + "(?>(?:" + FWSP + ")?" + Q_CONTENT + ")*(?:" + FWSP + ")?" + D_QUOTE;
		final String QUOTED_STRING = "(?:" + CFWS + ")?" + QUOTED_STRING_WOCFWS + "(?:" + CFWS + ")?";
		final String LOCAL_PART_QUOTED_STRING = "(?:" + CFWS + ")?(" + D_QUOTE + "(?:(?:" + FWSP + ")?" + LOCAL_PART_Q_CONTENT + ")*(?:" + FWSP + ")?" + D_QUOTE + ")(?:" + CFWS + ")?";

		// RFC 2822 3.2.6 Miscellaneous tokens

		final String WORD = "(?:(?:" + ATOM + ")|(?:" + QUOTED_STRING + "))";

		// by 2822: phrase = 1*word / obs-phrase
		// implemented here as: phrase = word (FWS word)*
		// so that aaaa can't be four words, which can cause tons of recursive backtracking

		final String PHRASE = WORD + "(?:(?:" + FWSP + ")" + WORD + ")*";

		// RFC 1035 tokens for domain names

		final String LETTER = "[a-zA-Z]";
		final String LET_DIG = "[a-zA-Z0-9]";
		final String LET_DIG_HYP = "[a-zA-Z0-9-]";
		final String RFC_LABEL = LET_DIG + "(?:" + LET_DIG_HYP + "{0,61}" + LET_DIG + ")?";
		final String RFC_1035_DOMAIN_NAME = RFC_LABEL + "(?:\\." + RFC_LABEL + ")*\\." + LETTER + "{2,6}";

		// RFC 2822 3.4 Address specification

		final String D_TEXT = "[" + NO_WS_CTL + "\\!-Z\\^-\\~]";

		final String D_CONTENT = D_TEXT + "|" + QUOTED_PAIR;
		final String CAP_DOMAIN_LITERAL_NO_CFWS = "(?:" + CFWS + ")?" + "(\\[" + "(?:(?:" + FWSP + ")?(?:" + D_CONTENT + ")+)*(?:" + FWSP + ")?\\])" + "(?:" + CFWS + ")?";
		final String CAP_DOMAIN_LITERAL_TRAILING_CFWS = "(?:" + CFWS + ")?" + "(\\[" + "(?:(?:" + FWSP + ")?(?:" + D_CONTENT + ")+)*(?:" + FWSP + ")?\\])" + "(" + CFWS + ")?";
		final String RFC_2822_DOMAIN = "(?:" + CAP_DOT_ATOM_NO_CFWS + "|" + CAP_DOMAIN_LITERAL_NO_CFWS + ")";
		final String CAP_CFWSR_FC2822_DOMAIN = "(?:" + CAP_DOT_ATOM_TRAILING_CFWS + "|" + CAP_DOMAIN_LITERAL_TRAILING_CFWS + ")";

		final String DOMAIN = ALLOW_DOMAIN_LITERALS ? RFC_2822_DOMAIN : "(?:" + CFWS + ")?(" + RFC_1035_DOMAIN_NAME + ")(?:" + CFWS + ")?";
		final String CAP_CFWS_DOMAIN = ALLOW_DOMAIN_LITERALS ? CAP_CFWSR_FC2822_DOMAIN : "(?:" + CFWS + ")?(" + RFC_1035_DOMAIN_NAME + ")(" + CFWS + ")?";
		final String LOCAL_PART = "(" + CAP_DOT_ATOM_NO_CFWS + "|" + LOCAL_PART_QUOTED_STRING + ")";

		// uniqueAddrSpec exists so we can have a duplicate tree that has a capturing group
		// instead of a non-capturing group for the trailing CFWS after the domain token
		// that we wouldn't want if it was inside
		// an angleAddr. The matching should be otherwise identical.

		final String ADDR_SPEC = LOCAL_PART + "@" + DOMAIN;
		final String UNIQUE_ADDR_SPEC = LOCAL_PART + "@" + CAP_CFWS_DOMAIN;
		final String ANGLE_ADDR = "(?:" + CFWS + ")?<" + ADDR_SPEC + ">(" + CFWS + ")?";

		final String NAME_ADDR = "(" + PHRASE + ")??(" + ANGLE_ADDR + ")";
		final String MAIL_BOX = (ALLOW_QUOTED_IDENTIFIERS ? "(" + NAME_ADDR + ")|" : "") + "(" + UNIQUE_ADDR_SPEC + ")";

		final String RETURN_PATH = "(?:(?:" + CFWS + ")?<((?:" + CFWS + ")?|" + ADDR_SPEC + ")>(?:" + CFWS + ")?)";

		//private static final String mailboxList = "(?:(?:" + mailbox + ")(?:,(?:" + mailbox + "))*)";
		//private static final String groupPostfix = "(?:" + CFWS + "|(?:" + mailboxList + ")" + ")?;(?:" + CFWS + ")?";
		//private static final String groupPrefix = phrase + ":";
		//private static final String group = groupPrefix + groupPostfix;
		//private static final String address = "(?:(?:" + mailbox + ")|(?:" + group + "))"


		// Java regex pattern for 2822
		_MAILBOX_PATTERN = Pattern.compile(MAIL_BOX);

		_ADDR_SPEC_PATTERN = Pattern.compile(ADDR_SPEC);
		//final Pattern MAILBOX_LIST_PATTERN = Pattern.compile(mailboxList);
		_COMMENT_PATTERN = Pattern.compile(COMMENT);

		_QUOTED_STRING_WO_CFWS_PATTERN = Pattern.compile(QUOTED_STRING_WOCFWS);
		_RETURN_PATH_PATTERN = Pattern.compile(RETURN_PATH);
	}

	private static final Pattern ESCAPED_QUOTE_PATTERN = Pattern.compile("\\\\\"");
	private static final Pattern ESCAPED_BSLASH_PATTERN = Pattern.compile("\\\\\\\\");


	// ---------------------------------------------------------------- utilities

	private InternetAddress pullFromGroups(Matcher m) {
		InternetAddress currentInternetAddress;
		String[] parts = _calcMatcherParts(m);

		if (parts[1] == null || parts[2] == null) {
			return null;
		}

		// if for some reason you want to require that the result be re-parsable by
		// InternetAddress, you
		// could uncomment the appropriate stuff below, but note that not all the utility
		// functions use pullFromGroups; some call getMatcherParts directly.
		try {
			//currentInternetAddress = new InternetAddress(parts[0] + " <" + parts[1] + "@" +
			//                                 parts[2]+ ">", true);
			// so it parses it OK, but since javamail doesn't extract too well
			// we make sure that the consistent parts
			// are correct

			currentInternetAddress = new InternetAddress();
			currentInternetAddress.setPersonal(parts[0]);
			currentInternetAddress.setAddress(parts[1] + "@" + parts[2]);
		}
		catch (UnsupportedEncodingException uee) {
			currentInternetAddress = null;
		}

		return currentInternetAddress;
	}

	private String[] _calcMatcherParts(Matcher m) {
		String currentLocalpart = null;
		String currentDomainpart = null;
		String localPartDa;
		String localPartQs = null;
		String domainPartDa;
		String domainPartDl = null;
		String personalString = null;

		// see the group-ID lists in the grammar comments

		if (ALLOW_QUOTED_IDENTIFIERS) {
			if (ALLOW_DOMAIN_LITERALS) {
				// yes quoted identifiers, yes domain literals

				if (m.group(1) != null) {
					// name-addr form
					localPartDa = m.group(5);
					if (localPartDa == null) {
						localPartQs = m.group(6);
					}

					domainPartDa = m.group(7);
					if (domainPartDa == null) {
						domainPartDl = m.group(8);
					}

					currentLocalpart = (localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart = (domainPartDa == null ? domainPartDl : domainPartDa);

					personalString = m.group(2);
					if (personalString == null && EXTRACT_CFWS_PERSONAL_NAMES) {
						personalString = m.group(9);
						personalString = removeAnyBounding('(', ')', getFirstComment(personalString));
					}
				}
				else if (m.group(10) != null) {
					// addr-spec form

					localPartDa = m.group(12);
					if (localPartDa == null) {
						localPartQs = m.group(13);
					}

					domainPartDa = m.group(14);
					if (domainPartDa == null) {
						domainPartDl = m.group(15);
					}

					currentLocalpart = (localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart = (domainPartDa == null ? domainPartDl : domainPartDa);

					if (EXTRACT_CFWS_PERSONAL_NAMES) {
						personalString = m.group(16);
						personalString = removeAnyBounding('(', ')', getFirstComment(personalString));
					}
				}
			}
			else {
				// yes quoted identifiers, no domain literals

				if (m.group(1) != null) {
					// name-addr form

					localPartDa = m.group(5);
					if (localPartDa == null) {
						localPartQs = m.group(6);
					}

					currentLocalpart = (localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart = m.group(7);

					personalString = m.group(2);
					if (personalString == null && EXTRACT_CFWS_PERSONAL_NAMES) {
						personalString = m.group(8);
						personalString = removeAnyBounding('(', ')', getFirstComment(personalString));
					}
				}
				else if (m.group(9) != null) {
					// addr-spec form

					localPartDa = m.group(11);
					if (localPartDa == null) {
						localPartQs = m.group(12);
					}

					currentLocalpart = (localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart = m.group(13);

					if (EXTRACT_CFWS_PERSONAL_NAMES) {
						personalString = m.group(14);
						personalString = removeAnyBounding('(', ')', getFirstComment(personalString));
					}
				}
			}
		}
		else {
			// no quoted identifiers, yes|no domain literals

			localPartDa = m.group(3);
			if (localPartDa == null) {
				localPartQs = m.group(4);
			}

			domainPartDa = m.group(5);
			if (domainPartDa == null && ALLOW_DOMAIN_LITERALS) {
				domainPartDl = m.group(6);
			}

			currentLocalpart = (localPartDa == null ? localPartQs : localPartDa);

			currentDomainpart = (domainPartDa == null ? domainPartDl : domainPartDa);

			if (EXTRACT_CFWS_PERSONAL_NAMES) {
				personalString = m.group((ALLOW_DOMAIN_LITERALS ? 1 : 0) + 6);
				personalString = removeAnyBounding('(', ')', getFirstComment(personalString));
			}
		}

		if (currentLocalpart != null) {
			currentLocalpart = currentLocalpart.trim();
		}
		if (currentDomainpart != null) {
			currentDomainpart = currentDomainpart.trim();
		}
		if (personalString != null) {
			// trim even though calling cPS which trims, because the latter may return
			// the same thing back without trimming
			personalString = personalString.trim();
			personalString = cleanupPersonalString(personalString);
		}

		// remove any unnecessary bounding quotes from the localpart:

		String testAddr = removeAnyBounding('"', '"', currentLocalpart) + "@" + currentDomainpart;

		if (_ADDR_SPEC_PATTERN.matcher(testAddr).matches()) {
			currentLocalpart = removeAnyBounding('"', '"', currentLocalpart);
		}

		return (new String[] {personalString, currentLocalpart, currentDomainpart});
	}

	/**
	 * Given a string, extract the first matched comment token as defined in 2822, trimmed;
	 * return null on all errors or non-findings.
	 * <p>
	 * Note for future improvement: if COMMENT_PATTERN could handle nested
	 * comments, then this should be able to as well, but if this method were to be used to
	 * find the CFWS personal name (see boolean option) then such a nested comment would
	 * probably not be the one you were looking for?
	 */
	private String getFirstComment(String text) {
		if (text == null) {
			return null; // important
		}

		Matcher m = _COMMENT_PATTERN.matcher(text);

		if (!m.find()) {
			return null;
		}

		return m.group().trim();		// must trim
	}

	private String cleanupPersonalString(String text) {
		if (text == null) {
			return null;
		}
		text = text.trim();

		Matcher m = _QUOTED_STRING_WO_CFWS_PATTERN.matcher(text);

		if (!m.matches()) {
			return text;
		}

		text = removeAnyBounding('"', '"', m.group());

		text = ESCAPED_BSLASH_PATTERN.matcher(text).replaceAll("\\\\");
		text = ESCAPED_QUOTE_PATTERN.matcher(text).replaceAll("\"");

		return text.trim();
	}

	/**
	 * If the string starts and ends with start and end char, remove them,
	 * otherwise return the string as it was passed in.
	 */
	private static String removeAnyBounding(char s, char e, String str) {
		if (str == null || str.length() < 2) {
			return str;
		}

		if (str.startsWith(String.valueOf(s)) && str.endsWith(String.valueOf(e))) {
			return str.substring(1, str.length() - 1);
		}

		return str;
	}

}