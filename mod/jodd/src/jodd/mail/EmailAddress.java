// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import java.util.regex.Pattern;

/**
 * A utility class to parse, clean up, and extract email addresses from messages
 * per RFC2822 syntax. Designed to integrate with Javamail (this class will require that you
 * have a javamail mail.jar in your classpath), but you could easily change
 * the existing methods around to not use Javamail at all. For example, if you're changing
 * the code, see the difference between getInternetAddress and getDomain: the latter doesn't
 * depend on any javamail code. This is all a by-product of what this class was written for,
 * so feel free to modify it to suit your needs.
 * <p>
 * For real-world addresses, this class is roughly 3-4 times slower than parsing with
 * InternetAddress, but
 * it can handle a whole lot more. Because of sensible design tradeoffs made in javamail, if
 * InternetAddress has trouble parsing,
 * it might throw an exception, but often it will silently leave the entire original string
 * in the result of ia.getAddress(). This class can be trusted to only provide authenticated
 * results.
 * <p>
 * This class has been tested on a few thousand real-world addresses, and is live in
 * production environments, but you may want to do some of your own testing to ensure
 * that it works for you. In other words, it's not beta, but it's not guaranteed yet.
 * <p>
 * Comments/Questions/Corrections welcome: java &lt;at&gt; caseyconnor.org
 * <p>
 * Started with code by Les Hazlewood:
 * <a href="http://www.leshazlewood.com">leshazlewood.com</a>.
 * <p>
 * Modified/added: removed some functions, added support for CFWS token,
 * corrected FWSP token, added some boolean flags, added getInternetAddress and
 * extractHeaderAddresses and other methods, some optimization.
 * <p>
 * Where Mr. Hazlewood's version was more for ensuring certain forms that were passed in during
 * registrations, etc, this handles more types of verifying as well a few forms of extracting
 * the data in predictable, cleaned-up chunks.
 * <p>
 * Note: CFWS means the "comment folded whitespace" token from 2822, in other words,
 * whitespace and comment text that is enclosed in ()'s.
 * <p>
 * <b>Limitations</b>: doesn't support nested CFWS (comments within (other) comments), doesn't
 * support mailbox groups except when flat-extracting addresses from headers or when doing
 * verification, doesn't support
 * any of the obs-* tokens. Also: the getInternetAddress and
 * extractHeaderAddresses methods return InternetAddress objects; if the personal name has
 * any quotes or \'s in it at all, the InternetAddress object will always
 * escape the name entirely and put it in quotes, so
 * multiple-token personal names with those characters somewhere in them will always be munged
 * into one big escaped string. This is not really a big deal at all, but I mention it anyway.
 * (And you could get around it by a simple modification to those methods to not use
 * InternetAddress objects.) See the docs of those methods for more info.
 * <p>
 * Note: This does not do any header-length-checking. There are no such limitations on the
 * email address grammar in 2822, though email headers in general do have length restrictions.
 * So if the return path
 * is 40000 unfolded characters long, but otherwise valid under 2822, this class will pass it.
 * <p>
 * Examples of passing (2822-valid) addresses, believe it or not:
 * <p>
 * <tt>bob @example.com</tt>
 * <BR><tt>&quot;bob&quot;  @  example.com</tt>
 * <BR><tt>bob (comment) (other comment) @example.com (personal name)</tt>
 * <BR><tt>&quot;&lt;bob \&quot; (here) &quot; &lt; (hi there) &quot;bob(the man)smith&quot; (hi) @ (there) example.com (hello) &gt; (again)</tt>
 * <p>
 * (none of which are permitted by javamail, incidentally)
 * <p>
 * By using getInternetAddress(), you can retrieve an InternetAddress object that, when
 * toString()'ed, would reveal that the parser had converted the above into:
 * <p>
 * <tt>&lt;bob@example.com&gt;</tt>
 * <BR><tt>&lt;bob@example.com&gt;</tt>
 * <BR><tt>&quot;personal name&quot; &lt;bob@example.com&gt;</tt>
 * <BR><tt>&quot;&lt;bob \&quot; (here)&quot; &lt;&quot;bob(the man)smith&quot;@example.com&gt;</tt>
 * <P>(respectively)
 * <P>If parsing headers, however, you'll probably be calling extractHeaderAddresses().
 * <p>
 * A future improvement may be to use this class to extract info from corrupted
 * addresses, but for now, it does not permit them.
 * <p>
 * <b>Some of the configuration booleans allow a bit of tweaking
 * already. The source code can be compiled with these booleans in various
 * states. They are configured to what is probably the most commonly-useful state.</b>
 *
 * @author Les Hazlewood, Casey Connor, najgor++
 */
public class EmailAddress {
	/**
	 * This constant states that domain literals are allowed in the email address, e.g.:
	 * <p>
	 * <p><tt>someone@[192.168.1.100]</tt> or <br/>
	 * <tt>john.doe@[23:33:A2:22:16:1F]</tt> or <br/>
	 * <tt>me@[my computer]</tt></p>
	 * <p>
	 * <p>The RFC says these are valid email addresses, but most people don't like allowing them.
	 * If you don't want to allow them, and only want to allow valid domain names
	 * (<a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>, x.y.z.com, etc),
	 * change this constant to <tt>false</tt>.
	 * <p>
	 * <p>Its default value is <tt>true</tt> to remain RFC 2822 compliant, but
	 * you should set it depending on what you need for your application.
	 */
	private static final boolean ALLOW_DOMAIN_LITERALS = true;

	/**
	 * This contstant states that quoted identifiers are allowed
	 * (using quotes and angle brackets around the raw address) are allowed, e.g.:
	 * <p>
	 * <p><tt>"John Smith" &lt;john.smith@somewhere.com&gt;</tt>
	 * <p>
	 * <p>The RFC says this is a valid mailbox.  If you don't want to
	 * allow this, because for example, you only want users to enter in
	 * a raw address (<tt>john.smith@somewhere.com</tt> - no quotes or angle
	 * brackets), then change this constant to <tt>false</tt>.
	 * <p>
	 * <p>Its default value is <tt>true</tt> to remain RFC 2822 compliant, but
	 * you should set it depending on what you need for your application.
	 */
	private static final boolean ALLOW_QUOTED_IDENTIFIERS = true;

	// RFC 2822 2.2.2 Structured Header Field Bodies
	private static final String wsp = "[ \\t]"; //space or tab
	private static final String fwsp = wsp + '*';

	//RFC 2822 3.2.1 Primitive tokens
	private static final String dquote = "\\\"";
	//ASCII Control characters excluding white space:
	private static final String noWsCtl = "\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F";
	//all ASCII characters except CR and LF:
	private static final String asciiText = "[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F]";

	// RFC 2822 3.2.2 Quoted characters:
	//single backslash followed by a text char
	private static final String quotedPair = "(\\\\" + asciiText + ')';

	//RFC 2822 3.2.4 Atom:
	private static final String atext = "[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]";
	private static final String atom = fwsp + atext + '+' + fwsp;
	private static final String dotAtomText = atext + '+' + '(' + "\\." + atext + "+)*";
	private static final String dotAtom = fwsp + '(' + dotAtomText + ')' + fwsp;

	//RFC 2822 3.2.5 Quoted strings:
	//noWsCtl and the rest of ASCII except the doublequote and backslash characters:
	private static final String qtext = '[' + noWsCtl + "\\x21\\x23-\\x5B\\x5D-\\x7E]";
	private static final String qcontent = '(' + qtext + '|' + quotedPair + ')';
	private static final String quotedString = dquote + '(' + fwsp + qcontent + ")*" + fwsp + dquote;

	//RFC 2822 3.2.6 Miscellaneous tokens
	private static final String word = "((" + atom + ")|(" + quotedString + "))";
	private static final String phrase = word + '+'; //one or more words.

	//RFC 1035 tokens for domain names:
	private static final String letter = "[a-zA-Z]";
	private static final String letDig = "[a-zA-Z0-9]";
	private static final String letDigHyp = "[a-zA-Z0-9-]";
	private static final String rfcLabel = letDig + '(' + letDigHyp + "{0,61}" + letDig + ")?";
	private static final String rfc1035DomainName = rfcLabel + "(\\." + rfcLabel + ")*\\." + letter + "{2,6}";

	//RFC 2822 3.4 Address specification
	//domain text - non white space controls and the rest of ASCII chars not including [, ], or \:
	private static final String dtext = '[' + noWsCtl + "\\x21-\\x5A\\x5E-\\x7E]";
	private static final String dcontent = dtext + '|' + quotedPair;
	private static final String domainLiteral = "\\[" + '(' + fwsp + dcontent + "+)*" + fwsp + "\\]";
	private static final String rfc2822Domain = '(' + dotAtom + '|' + domainLiteral + ')';

	private static final String domain = ALLOW_DOMAIN_LITERALS ? rfc2822Domain : rfc1035DomainName;

	private static final String localPart = "((" + dotAtom + ")|(" + quotedString + "))";
	private static final String addrSpec = localPart + '@' + domain;
	private static final String angleAddr = '<' + addrSpec + '>';
	private static final String nameAddr = '(' + phrase + ")?" + fwsp + angleAddr;
	private static final String mailbox = nameAddr + '|' + addrSpec;

	//now compile a pattern for efficient re-use:
	//if we're allowing quoted identifiers or not:
	private static final String patternString = ALLOW_QUOTED_IDENTIFIERS ? mailbox : addrSpec;
	public static final Pattern VALID_PATTERN = Pattern.compile(patternString);

	//class attributes
	private String text;
	private boolean bouncing = true;
	private boolean verified;
	private String label;

	public EmailAddress() {
		super();
	}

	public EmailAddress(String text) {
		super();
		setText(text);
	}

	/**
	 * Returns the actual email address string, e.g. <tt>someone@somewhere.com</tt>
	 *
	 * @return the actual email address string.
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns whether or not any emails sent to this email address come back as bounced
	 * (undeliverable).
	 * <p>
	 * <p>Default is <tt>false</tt> for convenience's sake - if a bounced message is ever received for this
	 * address, this value should be set to <tt>true</tt> until verification can made.
	 *
	 * @return whether or not any emails sent to this email address come back as bounced
	 *         (undeliverable).
	 */
	public boolean isBouncing() {
		return bouncing;
	}

	public void setBouncing(boolean bouncing) {
		this.bouncing = bouncing;
	}

	/**
	 * Returns whether or not the party associated with this email has verified that it is
	 * their email address.
	 * <p>
	 * <p>Verification is usually done by sending an email to this
	 * address and waiting for the party to respond or click a specific link in the email.
	 * <p>
	 * <p>Default is <tt>false</tt>.
	 *
	 * @return whether or not the party associated with this email has verified that it is
	 *         their email address.
	 */
	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	/**
	 * Party label associated with this address, for example, 'Home', 'Work', etc.
	 *
	 * @return a label associated with this address, for example 'Home', 'Work', etc.
	 */
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns whether or not the text represented by this object instance is valid
	 * according to the <tt>RFC 2822</tt> rules.
	 *
	 * @return true if the text represented by this instance is valid according
	 *         to RFC 2822, false otherwise.
	 */
	public boolean isValid() {
		return isValidText(getText());
	}

	/**
	 * Utility method that checks to see if the specified string is a valid
	 * email address according to the RFC 2822 specification.
	 *
	 * @param email the email address string to test for validity.
	 * @return true if the given text valid according to RFC 2822, false otherwise.
	 */
	public static boolean isValidText(String email) {
		return (email != null) && VALID_PATTERN.matcher(email).matches();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof EmailAddress) {
			EmailAddress ea = (EmailAddress) o;
			return getText().equals(ea.getText());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

	@Override
	public String toString() {
		return getText();
	}
}
