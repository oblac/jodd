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
 * EmailAddress - a utility class to parse, clean up, and extract email addresses
 * per RFC2822 syntax. This class can be trusted to only provide authenticated results.
 * <p>
 * This class has been successfully used on many billion real-world addresses, live in
 * production environments, but it's not perfect yet, since the standard is quite complex.
 * <p>
 * Note: Unlike <code>InternetAddress</code>, this class will preserve any RFC-2047-encoding of international
 * characters.
 */
public class EmailAddress {
	/**
	 * This constant changes the behavior of the domain parsing. If true, the parser will
	 * allow 2822 domains, which include single-level domains (e.g. bob@localhost) as well
	 * as domain literals, e.g.:
	 *
	 * <ul>
	 * <li><code>someone@[192.168.1.100]</code> or</li>
	 * <li><code>john.doe@[23:33:A2:22:16:1F]</code> or</li>
	 * <li><code>me@[my computer]</code></li>
	 * </ul>
	 *
	 * The RFC says these are valid email addresses, but most people don't like
	 * allowing them.
	 * If you don't want to allow them, and only want to allow valid domain names
	 * (<a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>, x.y.z.com, etc),
	 * and specifically only those with at least two levels ("example.com"), then
	 * change this constant to <code>false</code>.
	 */
	public static boolean ALLOW_DOMAIN_LITERALS = false;

	/**
	 * This constant states that quoted identifiers are allowed
	 * (using quotes and angle brackets around the raw address) are allowed, e.g.:
	 *
	 * <ul>
	 * <li><code>"John Smith" &lt;john.smith@somewhere.com&gt;</code></li>
	 * </ul>
	 * 
	 * The RFC says this is a valid mailbox. If you don't want to
	 * allow this, because for example, you only want users to enter in
	 * a raw address (<code>john.smith@somewhere.com</code> - no quotes or angle
	 * brackets), then change this constant to <code>false</code>.
	 */
	public static boolean ALLOW_QUOTED_IDENTIFIERS = true;

	/**
	 * This constant allows &quot;.&quot; to appear in atext (note: only atext which appears
	 * in the 2822 &quot;name-addr&quot; part of the address, not the other instances)
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
	public static boolean ALLOW_DOT_IN_ATEXT = false;

	/**
	 * This controls the behavior of getInternetAddress and extractHeaderAddresses. If true,
	 * it allows the real world practice of:
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
	public static boolean EXTRACT_CFWS_PERSONAL_NAMES = true;

	/**
	 * This constant allows &quot;[&quot; or &quot;]&quot; to appear in atext.
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
	 * with ALLOW_DOMAIN_LITERALS.
	 */
	public static boolean ALLOW_SQUARE_BRACKETS_IN_ATEXT = false;

	/**
	 * This constant allows &quot;)&quot; or &quot;(&quot; to appear in quoted versions of
	 * the localpart (they are never allowed in unquoted versions)
	 * The default (2822) behavior is to allow this, i.e. boolean true.
	 * You can disallow it, but better to leave it true.
	 */
	public static boolean ALLOW_PARENS_IN_LOCALPART = true;

	// ---------------------------------------------------------------- ctor

	protected final String email;

	/**
	 * Creates new email address. Nothing is parsed yet.
	 */
	public EmailAddress(String email) {
		this.email = email.trim();
	}

	// ---------------------------------------------------------------- calc

	private Matcher mailboxMatcher;
	private boolean mailboxMatcherMatches;
	private String[] matcherParts;

	private Matcher _mailboxMatcher() {
		if (mailboxMatcher == null) {
			mailboxMatcher = MAILBOX_PATTERN.matcher(email);
			mailboxMatcherMatches = mailboxMatcher.matches();
		}
		return mailboxMatcher;
	}

	private boolean _mailboxMatcherMatches() {
		if (mailboxMatcher == null) {
			_mailboxMatcher();
		}
		return mailboxMatcherMatches;
	}

	private String[] _mailboxMatcherParts() {
		if (matcherParts == null) {
			matcherParts = _calcMatcherParts(_mailboxMatcher());

			if (matcherParts == null) {
				matcherParts = StringPool.EMPTY_ARRAY;
			}
		}

		if (matcherParts.length == 0) {
			return null;
		}
		return matcherParts;
	}

	// ---------------------------------------------------------------- valid

	/**
	 * Returns <code>true</code>if email is valid.
	 */
	public boolean isValid() {
		return _mailboxMatcherMatches();
	}

	/**
	 * Returns <code>true</code> if the email represents a valid return path.
	 */
	public boolean isValidReturnPath() {
		return RETURN_PATH_PATTERN.matcher(email).matches();
	}

	/**
	 * WARNING: You may want to use getReturnPathAddress() instead if you're
	 * looking for a clean version of the return path without CFWS, etc. See that
	 * documentation first!
	 * <p>
	 * Pull whatever's inside the angle brackets out, without alteration or cleaning.
	 * This is more secure than a simple substring() since paths like:
	 * <P><code>&lt;(my &gt; path) &gt;</code>
	 * <P>...are legal return-paths and may throw a simpler parser off. However
	 * this method will return <b>all</b> CFWS (comments, whitespace) that may be between
	 * the brackets as well. So the example above will return:
	 * <P><code>(my &gt; path)_</code> <br>(where the _ is the trailing space from the original
	 * string)
	 */
	public String getReturnPathBracketContents() {
		Matcher m = RETURN_PATH_PATTERN.matcher(email);

		if (m.matches()) {
			return m.group(1);
		}
		return null;
	}

	/**
	 * Pull out the cleaned-up return path address. May return an empty string.
	 * Will require two parsings due to an inefficiency.
	 *
	 * @return null if there are any syntax issues or other weirdness, otherwise
	 * the valid, trimmed return path email address without CFWS, surrounding angle brackets,
	 * with quotes stripped where possible, etc. (may return an empty string).
	 */
	public String getReturnPathAddress() {
		// inefficient, but there is no parallel grammar tree to extract the return path
		// accurately:

		if (isValidReturnPath()) {
			InternetAddress ia = getInternetAddress();
			if (ia == null) {
				return StringPool.EMPTY;
			}
			return ia.getAddress();
		}
		return null;
	}

	/**
	 * Given a 2822-valid single address string, returns an InternetAddress object holding
	 * that address, otherwise returns null. The email address that comes back from the
	 * resulting InternetAddress object's getAddress() call will have comments and unnecessary
	 * quotation marks or whitespace removed.
	 */
	public InternetAddress getInternetAddress() {
		Matcher m = _mailboxMatcher();

		if (_mailboxMatcherMatches()) {
			return pullFromGroups(m);
		}

		return null;
	}

	/**
	 * Returns personal name. The Strings returned by this method will not reflect any decoding of RFC-2047
	 * encoded personal names.
	 */
	public String getPersonalName() {
		if (_mailboxMatcherMatches()) {
			return _mailboxMatcherParts()[0];
		}
		return null;
	}

	/**
	 * Returns local part of the email address.
	 */
	public String getLocalPart() {
		if (_mailboxMatcherMatches()) {
			return _mailboxMatcherParts()[1];
		}
		return null;
	}

	/**
	 * Returns domain part of the email address.
	 */
	public String getDomain() {
		if (_mailboxMatcherMatches()) {
			return _mailboxMatcherParts()[2];
		}
		return null;
	}

	// ---------------------------------------------------------------- REGEXPs

	// http://tools.ietf.org/html/rfc2822

	// RFC 2822 2.2.2 Structured Header Field Bodies

	private static final String crlf = "\\r\\n";
	private static final String wsp = "[ \\t]";
	private static final String fwsp = "(?:" + wsp + "*" + crlf + ")?" + wsp + "+";

	// RFC 2822 3.2.1 Primitive tokens

	private static final String dquote = "\\\"";
	private static final String noWsCtl = "\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F";
	private static final String asciiText = "[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F]";

	// RFC 2822 3.2.2 Quoted characters:

	private static final String quotedPair = "(?:\\\\" + asciiText + ")";

	// RFC 2822 3.2.3 CFWS specification

	private static final String ctext = "[" + noWsCtl + "\\!-\\'\\*-\\[\\]-\\~]";
	private static final String ccontent = ctext + "|" + quotedPair; // + "|" + comment;
	private static final String comment = "\\((?:(?:" + fwsp + ")?" + ccontent + ")*(?:" + fwsp + ")?\\)";
	private static final String cfws = "(?:(?:" + fwsp + ")?" + comment + ")*(?:(?:(?:" + fwsp + ")?" + comment + ")|(?:" + fwsp + "))";

	// RFC 2822 3.2.4 Atom:

	private static final String atext =
			"[a-zA-Z0-9\\!\\#-\\'\\*\\+\\-\\/\\=\\?\\^-\\`\\{-\\~"
			+ (ALLOW_DOT_IN_ATEXT ? "\\." : "")
			+ (ALLOW_SQUARE_BRACKETS_IN_ATEXT ? "\\[\\]" : "") + "]";
	private static final String regularAtext = "[a-zA-Z0-9\\!\\#-\\'\\*\\+\\-\\/\\=\\?\\^-\\`\\{-\\~]";

	private static final String atom = "(?:" + cfws + ")?" + atext + "+" + "(?:" + cfws + ")?";
	private static final String dotAtomText = regularAtext + "+" + "(?:" + "\\." + regularAtext + "+)*";
	private static final String dotAtom = "(?:" + cfws + ")?" + dotAtomText + "(?:" + cfws + ")?";
	private static final String capDotAtomNoCFWS = "(?:" + cfws + ")?(" + dotAtomText + ")(?:" + cfws + ")?";
	private static final String capDotAtomTrailingCFWS = "(?:" + cfws + ")?(" + dotAtomText + ")(" + cfws + ")?";

	// RFC 2822 3.2.5 Quoted strings:

	private static final String qtext = "[" + noWsCtl + "\\!\\#-\\[\\]-\\~]";
	private static final String localPartqtext = "[" + noWsCtl + (ALLOW_PARENS_IN_LOCALPART ? "\\!\\#-\\[\\]-\\~]" : "\\!\\#-\\'\\*-\\[\\]-\\~]");

	private static final String qcontent = "(?:" + qtext + "|" + quotedPair + ")";
	private static final String localPartqcontent = "(?>" + localPartqtext + "|" + quotedPair + ")";
	private static final String quotedStringWOCFWS = dquote + "(?>(?:" + fwsp + ")?" + qcontent + ")*(?:" + fwsp + ")?" + dquote;
	private static final String quotedString = "(?:" + cfws + ")?" + quotedStringWOCFWS + "(?:" + cfws + ")?";
	private static final String localPartQuotedString = "(?:" + cfws + ")?(" + dquote + "(?:(?:" + fwsp + ")?" + localPartqcontent + ")*(?:" + fwsp + ")?" + dquote + ")(?:" + cfws + ")?";

	// RFC 2822 3.2.6 Miscellaneous tokens

	private static final String word = "(?:(?:" + atom + ")|(?:" + quotedString + "))";

	// by 2822: phrase = 1*word / obs-phrase
	// implemented here as: phrase = word (FWS word)*
	// so that aaaa can't be four words, which can cause tons of recursive backtracking

	private static final String phrase = word + "(?:(?:" + fwsp + ")" + word + ")*";

	// RFC 1035 tokens for domain names

	private static final String letter = "[a-zA-Z]";
	private static final String letDig = "[a-zA-Z0-9]";
	private static final String letDigHyp = "[a-zA-Z0-9-]";
	private static final String rfcLabel = letDig + "(?:" + letDigHyp + "{0,61}" + letDig + ")?";
	private static final String rfc1035DomainName = rfcLabel + "(?:\\." + rfcLabel + ")*\\." + letter + "{2,6}";

	// RFC 2822 3.4 Address specification

	private static final String dtext = "[" + noWsCtl + "\\!-Z\\^-\\~]";

	private static final String dcontent = dtext + "|" + quotedPair;
	private static final String capDomainLiteralNoCFWS = "(?:" + cfws + ")?" + "(\\[" + "(?:(?:" + fwsp + ")?(?:" + dcontent + ")+)*(?:" + fwsp + ")?\\])" + "(?:" + cfws + ")?";
	private static final String capDomainLiteralTrailingCFWS = "(?:" + cfws + ")?" + "(\\[" + "(?:(?:" + fwsp + ")?(?:" + dcontent + ")+)*(?:" + fwsp + ")?\\])" + "(" + cfws + ")?";
	private static final String rfc2822Domain = "(?:" + capDotAtomNoCFWS + "|" + capDomainLiteralNoCFWS + ")";
	private static final String capCFWSRfc2822Domain = "(?:" + capDotAtomTrailingCFWS + "|" + capDomainLiteralTrailingCFWS + ")";

	private static final String domain = ALLOW_DOMAIN_LITERALS ? rfc2822Domain : "(?:" + cfws + ")?(" + rfc1035DomainName + ")(?:" + cfws + ")?";
	private static final String capCFWSDomain = ALLOW_DOMAIN_LITERALS ? capCFWSRfc2822Domain : "(?:" + cfws + ")?(" + rfc1035DomainName + ")(" + cfws + ")?";
	private static final String localPart = "(" + capDotAtomNoCFWS + "|" + localPartQuotedString + ")";

	// uniqueAddrSpec exists so we can have a duplicate tree that has a capturing group
	// instead of a non-capturing group for the trailing CFWS after the domain token
	// that we wouldn't want if it was inside
	// an angleAddr. The matching should be otherwise identical.

	private static final String addrSpec = localPart + "@" + domain;
	private static final String uniqueAddrSpec = localPart + "@" + capCFWSDomain;
	private static final String angleAddr = "(?:" + cfws + ")?<" + addrSpec + ">(" + cfws + ")?";

	private static final String nameAddr = "(" + phrase + ")??(" + angleAddr + ")";
	private static final String mailbox = (ALLOW_QUOTED_IDENTIFIERS ? "(" + nameAddr + ")|" : "") + "(" + uniqueAddrSpec + ")";

	private static final String returnPath = "(?:(?:" + cfws + ")?<((?:" + cfws + ")?|" + addrSpec + ")>(?:" + cfws + ")?)";

	//private static final String mailboxList = "(?:(?:" + mailbox + ")(?:,(?:" + mailbox + "))*)";
	//private static final String groupPostfix = "(?:" + cfws + "|(?:" + mailboxList + ")" + ")?;(?:" + cfws + ")?";
	//private static final String groupPrefix = phrase + ":";
	//private static final String group = groupPrefix + groupPostfix;
	//private static final String address = "(?:(?:" + mailbox + ")|(?:" + group + "))";

	// ---------------------------------------------------------------- patterns

	/**
	 * Java regex pattern for 2822 &quot;mailbox&quot; token; Not necessarily useful,
	 * but available in case.
	 */
	public static final Pattern MAILBOX_PATTERN = Pattern.compile(mailbox);
	/**
	 * Java regex pattern for 2822 &quot;addr-spec&quot; token; Not necessarily useful,
	 * but available in case.
	 */
	public static final Pattern ADDR_SPEC_PATTERN = Pattern.compile(addrSpec);
	/*
	 * Java regex pattern for 2822 &quot;mailbox-list&quot; token; Not necessarily useful,
	 * but available in case.
	 */
	//public static final Pattern MAILBOX_LIST_PATTERN = Pattern.compile(mailboxList);
	/**
	 * Java regex pattern for 2822 &quot;comment&quot; token; Not necessarily useful,
	 * but available in case.
	 */
	public static final Pattern COMMENT_PATTERN = Pattern.compile(comment);

	private static final Pattern QUOTED_STRING_WO_CFWS_PATTERN = Pattern.compile(quotedStringWOCFWS);
	private static final Pattern RETURN_PATH_PATTERN = Pattern.compile(returnPath);

	private static final Pattern ESCAPED_QUOTE_PATTERN = Pattern.compile("\\\\\"");
	private static final Pattern ESCAPED_BSLASH_PATTERN = Pattern.compile("\\\\\\\\");

	// ---------------------------------------------------------------- utilities

	private InternetAddress pullFromGroups(Matcher m) {
		InternetAddress current_ia;
		String[] parts = _calcMatcherParts(m);

		if (parts[1] == null || parts[2] == null) {
			return null;
		}

		// if for some reason you want to require that the result be re-parsable by
		// InternetAddress, you
		// could uncomment the appropriate stuff below, but note that not all the utility
		// functions use pullFromGroups; some call getMatcherParts directly.
		try {
			//current_ia = new InternetAddress(parts[0] + " <" + parts[1] + "@" +
			//                                 parts[2]+ ">", true);
			// so it parses it OK, but since javamail doesn't extract too well
			// we make sure that the consituent parts
			// are correct

			current_ia = new InternetAddress();
			current_ia.setPersonal(parts[0]);
			current_ia.setAddress(parts[1] + "@" + parts[2]);
		}
		catch (UnsupportedEncodingException uee) {
			current_ia = null;
		}

		return (current_ia);
	}

	private String[] _calcMatcherParts(Matcher m) {
		String currentLocalpart = null;
		String currentDomainpart = null;
		String localPartDa = null;
		String localPartQs = null;
		String domainPartDa = null;
		String domainPartDl = null;
		String personal_string = null;

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

					currentLocalpart =
							(localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart =
							(domainPartDa == null ? domainPartDl : domainPartDa);

					personal_string = m.group(2);
					if (personal_string == null && EXTRACT_CFWS_PERSONAL_NAMES) {
						personal_string = m.group(9);
						personal_string = removeAnyBounding('(', ')',
								getFirstComment(personal_string));
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

					currentLocalpart =
							(localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart =
							(domainPartDa == null ? domainPartDl : domainPartDa);

					if (EXTRACT_CFWS_PERSONAL_NAMES) {
						personal_string = m.group(16);
						personal_string = removeAnyBounding('(', ')',
								getFirstComment(personal_string));
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

					currentLocalpart =
							(localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart = m.group(7);

					personal_string = m.group(2);
					if (personal_string == null && EXTRACT_CFWS_PERSONAL_NAMES) {
						personal_string = m.group(8);
						personal_string = removeAnyBounding('(', ')',
								getFirstComment(personal_string));
					}
				}
				else if (m.group(9) != null) {
					// addr-spec form

					localPartDa = m.group(11);
					if (localPartDa == null) {
						localPartQs = m.group(12);
					}

					currentLocalpart =
							(localPartDa == null ? localPartQs : localPartDa);

					currentDomainpart = m.group(13);

					if (EXTRACT_CFWS_PERSONAL_NAMES) {
						personal_string = m.group(14);
						personal_string = removeAnyBounding('(', ')',
								getFirstComment(personal_string));
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
				personal_string = m.group((ALLOW_DOMAIN_LITERALS ? 1 : 0) + 6);
				personal_string = removeAnyBounding('(', ')',
						getFirstComment(personal_string));
			}
		}

		if (currentLocalpart != null) {
			currentLocalpart = currentLocalpart.trim();
		}
		if (currentDomainpart != null) {
			currentDomainpart = currentDomainpart.trim();
		}
		if (personal_string != null) {
			// trim even though calling cPS which trims, because the latter may return
			// the same thing back without trimming
			personal_string = personal_string.trim();
			personal_string = cleanupPersonalString(personal_string);
		}

		// remove any unecessary bounding quotes from the localpart:

		String test_addr = removeAnyBounding('"', '"', currentLocalpart) + "@" + currentDomainpart;

		if (ADDR_SPEC_PATTERN.matcher(test_addr).matches()) {
			currentLocalpart = removeAnyBounding('"', '"', currentLocalpart);
		}

		return (new String[] {personal_string, currentLocalpart, currentDomainpart});
	}

	/**
	 * Given a string, extract the first matched comment token as defined in 2822, trimmed;
	 * return null on all errors or non-findings
	 * <p/>
	 * This is probably not super-useful. Included just in case.
	 * <p/>
	 * Note for future improvement: if COMMENT_PATTERN could handle nested
	 * comments, then this should be able to as well, but if this method were to be used to
	 * find the CFWS personal name (see boolean option) then such a nested comment would
	 * probably not be the one you were looking for?
	 */
	private static String getFirstComment(String text) {
		if (text == null) {
			return null; // important
		}

		Matcher m = COMMENT_PATTERN.matcher(text);

		if (!m.find()) {
			return null;
		}

		return (m.group().trim());		// must trim
	}

	private static String cleanupPersonalString(String text) {
		if (text == null) {
			return null;
		}
		text = text.trim();

		Matcher m = QUOTED_STRING_WO_CFWS_PATTERN.matcher(text);

		if (!m.matches()) {
			return (text);
		}

		text = removeAnyBounding('"', '"', m.group());

		text = ESCAPED_BSLASH_PATTERN.matcher(text).replaceAll("\\\\");
		text = ESCAPED_QUOTE_PATTERN.matcher(text).replaceAll("\"");

		return (text.trim());
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