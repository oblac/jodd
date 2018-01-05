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

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jodd.mail.EmailAttachmentBuilder.DEPRECATED_MSG;

/**
 * Received email.
 */
public class ReceivedEmail extends CommonEmail<ReceivedEmail> {

  public static final ReceivedEmail[] EMPTY_ARRAY = new ReceivedEmail[0];

  @Override
  ReceivedEmail getThis() {
    return this;
  }

  /**
   * Static constructor for fluent interface.
   *
   * @return new {@link ReceivedEmail}.
   * @since 4.0
   */
  public static ReceivedEmail create() {
    return new ReceivedEmail();
  }

  @Override
  public ReceivedEmail clone() {
    return create()
        // flags
        .setFlags(getFlags())

        // message number
        .setMessageNumber(getMessageNumber())

        // from / reply-to
        .setFrom(getFrom())
        .setReplyTo(getReplyTo())

        // recipients
        .setTo(getTo())
        .setCc(getCc())

        // subject
        .setSubject(getSubject(), getSubjectEncoding())

        // dates
        .setReceivedDate(getReceivedDate())
        .setSentDate(getSentDate())

        // headers - includes priority
        .setHeaders(getAllHeaders())

        // content / attachments
        .addMessages(getAllMessages())
        .storeAttachments(getAttachments())
        .addAttachedMessages(getAttachedMessages());
  }

  /**
   * Creates an empty {@link ReceivedEmail}.
   *
   * @since 4.0
   */
  private ReceivedEmail() {
  }

  /**
   * Creates a {@link ReceivedEmail} from a given {@link Message}.
   *
   * @param msg {@link Message}
   */
  public ReceivedEmail(final Message msg) {
    try {
      parseMessage(msg);
    } catch (final Exception ex) {
      throw new MailException("Message parsing failed", ex);
    }
  }

  /**
   * Parses {@link Message} and extracts all data for the received message.
   *
   * @param msg {@link Message} to parse.
   * @throws IOException        if there is an error with the content
   * @throws MessagingException if there is an error.
   */
  protected void parseMessage(final Message msg) throws MessagingException, IOException {
    // flags
    setFlags(msg.getFlags());

    // message number
    setMessageNumber(msg.getMessageNumber());

    // single from
    final Address[] addresses = msg.getFrom();

    if (addresses != null && addresses.length > 0) {
      setFrom(addresses[0]);
    }

    // reply-to
    setReplyTo(msg.getReplyTo());

    // recipients
    setTo(msg.getRecipients(Message.RecipientType.TO));
    setCc(msg.getRecipients(Message.RecipientType.CC));
    // no BCC because this will always be empty

    // subject
    setSubject(msg.getSubject());

    // dates
    setReceivedDate(msg.getReceivedDate());
    setSentDate(msg.getSentDate());

    // headers
    setHeaders(msg.getAllHeaders());

    // content
    processPart(msg);
  }

  /**
   * Process part of the received message. All parts are simply added to the {@link ReceivedEmail},
   * i.e. hierarchy is not saved.
   *
   * @param part {@link Part} of received message
   * @throws IOException        if there is an error with the content.
   * @throws MessagingException if there is an error.
   */
  protected void processPart(final Part part) throws MessagingException, IOException {
    final Object content = part.getContent();

    if (content instanceof String) {
      addStringContent(part, (String) content);
    } else if (content instanceof Multipart) {
      processMultipart((Multipart) content);
    } else if (content instanceof InputStream) {
      addAttachment(part, (InputStream) content);
    } else if (content instanceof MimeMessage) {
      final MimeMessage mimeMessage = (MimeMessage) content;
      addAttachedMessage(new ReceivedEmail(mimeMessage));
    } else {
      addAttachment(part, part.getInputStream());
    }
  }

  /**
   * Process the {@link Multipart}.
   *
   * @param mp {@link Multipart}
   * @throws MessagingException if there is a failure.
   * @throws IOException        if there is an issue with the {@link Multipart}.
   * @since 4.0
   */
  private void processMultipart(final Multipart mp) throws MessagingException, IOException {
    final int count = mp.getCount();
    for (int i = 0; i < count; i++) {
      final Part innerPart = mp.getBodyPart(i);
      processPart(innerPart);
    }
  }

  /**
   * Adds String content as either {@link EmailAttachment} or as {@link EmailMessage}.
   *
   * @param part    {@link Part}
   * @param content Content as {@link String}
   * @throws MessagingException           if there is a failure.
   * @throws UnsupportedEncodingException if the named charset is not supported.
   * @see #addMessage(String, String, String)
   * @since 4.0
   */
  private void addStringContent(final Part part, final String content) throws MessagingException, UnsupportedEncodingException {
    final String contentType = part.getContentType();
    final String encoding = EmailUtil.extractEncoding(contentType, StringPool.US_ASCII);

    final String disposition = part.getDisposition();

    if (disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
      addAttachment(part, content.getBytes(encoding));
    } else {
      final String mimeType = EmailUtil.extractMimeType(contentType);
      addMessage(content, mimeType, encoding);
    }
  }

  /**
   * Returns the Content-ID of this {@link Part}. Returns {@code null} if none present.
   *
   * @param part {@link Part} the Part to parse.
   * @return String containing content ID.
   * @throws MessagingException if there is a failure.
   * @see MimePart#getContentID()
   */
  protected static String parseContentId(final Part part) throws MessagingException {
    if (part instanceof MimePart) {
      final MimePart mp = (MimePart) part;
      return mp.getContentID();
    } else {
      return null;
    }
  }

  /**
   * Returns {@code true} if the {@link Part} is inline.
   *
   * @param part {@link Part} to parse.
   * @return {@code true} if the {@link Part} is inline.
   * @throws MessagingException if there is a failure.
   */
  protected static boolean parseInline(final Part part) throws MessagingException {
    if (part instanceof MimePart) {
      final String dispositionId = part.getDisposition();
      return dispositionId != null && dispositionId.equalsIgnoreCase("inline");
    }
    return false;
  }

  // ---------------------------------------------------------------- flags

  /**
   * {@link Flags} for this {@link ReceivedEmail}.
   */
  private Flags flags;

  /**
   * @return {@link Flags}
   */
  public Flags getFlags() {
    return flags;
  }

  /**
   * Sets the flags.
   *
   * @param flags {@link Flags} to set.
   */
  public ReceivedEmail setFlags(final Flags flags) {
    this.flags = flags;
    return this;
  }

  /**
   * Returns {@code true} if message is answered.
   *
   * @return {@code true} if message is answered.
   */
  public boolean isAnswered() {
    return flags.contains(Flag.ANSWERED);
  }

  /**
   * Returns {@code true} if message is deleted.
   *
   * @return {@code true} if message is deleted.
   */
  public boolean isDeleted() {
    return flags.contains(Flag.DELETED);
  }

  /**
   * Returns {@code true} if message is draft.
   */
  public boolean isDraft() {
    return flags.contains(Flag.DRAFT);
  }

  /**
   * Returns {@code true} is message is flagged.
   *
   * @return {@code true} is message is flagged.
   */
  public boolean isFlagged() {
    return flags.contains(Flag.FLAGGED);
  }

  /**
   * Returns {@code true} if message is recent.
   *
   * @return {@code true} if message is recent.
   */
  public boolean isRecent() {
    return flags.contains(Flag.RECENT);
  }

  /**
   * Returns {@code true} if message is seen.
   *
   * @return {@code true} if message is seen.
   */
  public boolean isSeen() {
    return flags.contains(Flag.SEEN);
  }

  // ---------------------------------------------------------------- additional properties

  private int messageNumber;

  /**
   * Returns message number.
   *
   * @return message number
   */
  public int getMessageNumber() {
    return messageNumber;
  }

  /**
   * Sets message number.
   *
   * @param messageNumber The message number to set.
   * @return this
   */
  public ReceivedEmail setMessageNumber(final int messageNumber) {
    this.messageNumber = messageNumber;
    return this;
  }

  private Date receivedDate;

  /**
   * Sets email's received {@link Date}.
   *
   * @param date The received {@link Date} to set.
   * @return this
   * @since 4.0
   */
  public ReceivedEmail setReceivedDate(final Date date) {
    receivedDate = date;
    return this;
  }

  /**
   * Returns email's received {@link Date}.
   *
   * @return The email's received {@link Date}.
   * @since 4.0
   */
  public Date getReceivedDate() {
    return receivedDate;
  }

  // ---------------------------------------------------------------- attachments

  /**
   * Adds received attachment.
   *
   * @param part    {@link Part}.
   * @param content Content as {@link InputStream}.
   * @return this
   * @see #addAttachment(EmailAttachment)
   * @since 4.0
   */
  private ReceivedEmail addAttachment(final Part part, final InputStream content) throws MessagingException, IOException {
    final EmailAttachmentBuilder builder = addAttachmentInfo(part);
    builder.setContent(content, part.getContentType());
    return storeAttachment(builder.buildByteArrayDataSource());
  }

  /**
   * Adds received attachment.
   *
   * @param part    {@link Part}.
   * @param content Content as byte array.
   * @return this
   * @see #addAttachment(EmailAttachment)
   * @since 4.0
   */
  private ReceivedEmail addAttachment(final Part part, final byte[] content) throws MessagingException {
    final EmailAttachmentBuilder builder = addAttachmentInfo(part);
    builder.setContent(content, part.getContentType());
    final EmailAttachment<ByteArrayDataSource> attachment = builder.buildByteArrayDataSource();
    attachment.setSize(content.length);
    return storeAttachment(attachment);
  }

  /**
   * Creates {@link EmailAttachmentBuilder} from {@link Part} and sets Content ID, inline and name.
   *
   * @param part {@link Part}.
   * @return this
   * @see #addAttachment(EmailAttachment)
   * @since 4.0
   */
  private static EmailAttachmentBuilder addAttachmentInfo(final Part part) throws MessagingException {

    final String fileName = EmailUtil.resolveFileName(part);
    final String contentId = parseContentId(part);
    final boolean isInline = parseInline(part);

    return new EmailAttachmentBuilder()
        .setName(fileName)
        .setContentId(contentId)
        .setInline(isInline)
        ;
  }

  // ---------------------------------------------------------------- inner messages

  /**
   * {@link List} of attached {@link ReceivedEmail}s.
   */
  private final List<ReceivedEmail> attachedMessages = new ArrayList<>();

  /**
   * Adds attached {@link ReceivedEmail}s.
   *
   * @param emails {@link List} of {@link ReceivedEmail}s to attach.
   * @since 4.0
   */
  public ReceivedEmail addAttachedMessages(final List<ReceivedEmail> emails) {
    attachedMessages.addAll(emails);
    return this;
  }

  /**
   * Adds attached {@link ReceivedEmail}.
   *
   * @param email {@link ReceivedEmail} to attach.
   * @return this
   * @since 4.0
   */
  public ReceivedEmail addAttachedMessage(final ReceivedEmail email) {
    attachedMessages.add(email);
    return this;
  }

  /**
   * Returns the {@link List} of attached messages.
   * If no attached message is available, returns an empty {@link List}.
   *
   * @return {@link List} of {@link ReceivedEmail}s.
   */
  public List<ReceivedEmail> getAttachedMessages() {
    return attachedMessages;
  }

  // ---------------------------------------------------------------- deprecated

  /**
   * @deprecated Use {@link #addAttachedMessage(ReceivedEmail)} instead.
   */
  @Deprecated
  public void addAttachmentMessage(final ReceivedEmail email) {
    addAttachedMessage(email);
  }

  /**
   * @deprecated Use {@link #isDraft()}
   */
  @Deprecated
  public boolean isDraf() {
    return isDraft();
  }

  /**
   * @deprecated Use {@link #getReceivedDate()} instead.
   */
  @Deprecated
  protected Date getReceiveDate() {
    return getReceivedDate();
  }

  /**
   * @deprecated Use {@link #setReceivedDate(Date)} instead.
   */
  @Deprecated
  protected void setReceiveDate(final Date date) {
    setReceivedDate(date);
  }

  /**
   * @deprecated Use {@link Message#getReceivedDate()} instead.
   */
  @Deprecated
  protected Date parseReceiveDate(final Message msg) throws MessagingException {
    return msg.getReceivedDate();
  }

  /**
   * @deprecated Use {@link Message#getSentDate()} instead.
   */
  @Deprecated
  protected Date parseSendDate(final Message msg) throws MessagingException {
    return msg.getSentDate();
  }

  /**
   * @deprecated Use {@link #addAttachment(EmailAttachmentBuilder)}
   */
  @Deprecated
  public void addAttachment(final String fileName, final String mimeType, final String contentId, final boolean isInline, final byte[] content) {
    throw new UnsupportedOperationException(String.format(DEPRECATED_MSG, "#addAttachment(EmailAttachmentBuilder)"));
  }
}