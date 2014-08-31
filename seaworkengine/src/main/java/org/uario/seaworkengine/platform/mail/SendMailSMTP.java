package org.uario.seaworkengine.platform.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.sun.mail.smtp.SMTPTransport;

public class SendMailSMTP implements IMailWrapper {

	public static void main(final String[] args) throws AddressException, NoSuchProviderException, SendFailedException, MessagingException,
			IOException {

		final SendMailSMTP ss = new SendMailSMTP();

		final java.io.InputStream inputStream = new FileInputStream("/home/francesco/Scrivania/uario_logo.png");

		ss.sendByMailAtachStream("ciao", "francesco.damore@gmail.com", "<h1>Hello</h1>", "myname.png", inputStream);
	}

	private final boolean	auth			= true;

	private final boolean	debug			= false;

	private String			from			= "francesco.damore@gmail.com";

	private final boolean	gmail_and_tls	= true;

	private String			mailer			= "Ciao";

	private String			mailhost		= "smtp.gmail.com";

	private String			password		= "lunanera";

	private String			port			= "587";

	private String			prot			= "smtp";

	private String			user			= "francesco.damore@gmail.com";

	private final boolean	verbose			= false;

	public String getFrom() {
		return this.from;
	}

	public String getMailer() {
		return this.mailer;
	}

	public String getMailhost() {
		return this.mailhost;
	}

	public String getPassword() {
		return this.password;
	}

	public String getPort() {
		return this.port;
	}

	public String getProt() {
		return this.prot;
	}

	public String getUser() {
		return this.user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.uario.db_spider.platform.service.mail.IMailWrapper#sendByMail(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendByMail(final String subject, final String message_to, final String text, final Boolean ishtml, final File... files)
			throws MessagingException, AddressException, NoSuchProviderException, SendFailedException {
		final Properties props = System.getProperties();
		if (this.mailhost != null) {
			props.put("mail." + this.prot + ".host", this.mailhost);
		}
		if (this.auth) {
			props.put("mail." + this.prot + ".auth", "true");
		}

		if (this.port != null) {
			props.put("mail." + this.prot + ".port", this.port);
		}

		// try gmail
		if (this.gmail_and_tls) {
			props.put("mail.smtp.starttls.enable", "true");
		}

		final Session session = Session.getInstance(props, null);
		if (this.debug) {
			session.setDebug(true);
		}

		// construct the message
		final Message msg = new MimeMessage(session);
		if (this.from != null) {
			msg.setFrom(new InternetAddress(this.from));
		} else {
			msg.setFrom();
		}

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message_to, false));

		/*
		 * if (cc != null) { msg.setRecipients(Message.RecipientType.CC,
		 * InternetAddress.parse(cc, false)); } if (bcc != null) {
		 * msg.setRecipients(Message.RecipientType.BCC,
		 * InternetAddress.parse(bcc, false)); }
		 */

		msg.setSubject(subject);

		if (files.length != 0) {

			// create the message part
			final MimeBodyPart messageBodyPart = new MimeBodyPart();

			// fill message
			messageBodyPart.setText(text);

			final Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			for (int i = 0; i < files.length; i++) {
				final File itmf = files[i];

				final BodyPart messageBodyPart_item = new MimeBodyPart();

				final DataSource source = new FileDataSource(itmf);
				messageBodyPart_item.setDataHandler(new DataHandler(source));
				messageBodyPart_item.setFileName(itmf.getName());
				multipart.addBodyPart(messageBodyPart_item);

			}

			// add text
			// final BodyPart bp = new MimeBodyPart();
			// bp.setText(text);
			// multipart.addBodyPart(bp);

			if (ishtml) {
				msg.setContent(multipart, "text/html; charset=utf-8");

			} else {
				msg.setContent(multipart);
			}

		} else {
			if (ishtml) {
				msg.setContent(text, "text/html; charset=utf-8");

			} else {
				msg.setText(text);
			}

		}

		final SMTPTransport t = (SMTPTransport) session.getTransport(this.prot);
		try {
			if (this.auth) {
				t.connect(this.mailhost, this.user, this.password);
			} else {
				t.connect();
			}
			t.sendMessage(msg, msg.getAllRecipients());
		} finally {
			if (this.verbose) {
				System.out.println("Response: " + t.getLastServerResponse());
			}
			t.close();
		}

		msg.setHeader("X-Mailer", this.mailer);
		msg.setSentDate(new Date());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.uario.db_spider.platform.service.mail.IMailWrapper#sendByMail(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendByMailAtachStream(final String subject, final String message_to, final String text, final String filename,
			final InputStream stream) throws AddressException, MessagingException, IOException {
		final Properties props = System.getProperties();
		if (this.mailhost != null) {
			props.put("mail." + this.prot + ".host", this.mailhost);
		}
		if (this.auth) {
			props.put("mail." + this.prot + ".auth", "true");
		}

		if (this.port != null) {
			props.put("mail." + this.prot + ".port", this.port);
		}

		// try gmail
		if (this.gmail_and_tls) {
			props.put("mail.smtp.starttls.enable", "true");
		}

		final Session session = Session.getInstance(props, null);
		if (this.debug) {
			session.setDebug(true);
		}

		// construct the message
		final Message msg = new MimeMessage(session);
		if (this.from != null) {
			msg.setFrom(new InternetAddress(this.from));
		} else {
			msg.setFrom();
		}

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message_to, false));

		/*
		 * if (cc != null) { msg.setRecipients(Message.RecipientType.CC,
		 * InternetAddress.parse(cc, false)); } if (bcc != null) {
		 * msg.setRecipients(Message.RecipientType.BCC,
		 * InternetAddress.parse(bcc, false)); }
		 */

		msg.setSubject(subject);

		final Multipart multipart = new MimeMultipart();

		// create the second message part with the attachment from a
		// OutputStrean
		final MimeBodyPart attachment = new MimeBodyPart();
		final ByteArrayDataSource ds = new ByteArrayDataSource(stream, "application/pdf");
		attachment.setDataHandler(new DataHandler(ds));
		attachment.setFileName(filename);
		multipart.addBodyPart(attachment);

		// text
		final BodyPart bp = new MimeBodyPart();
		bp.setText(text);
		multipart.addBodyPart(bp);

		msg.setContent(multipart);

		final SMTPTransport t = (SMTPTransport) session.getTransport(this.prot);
		try {
			if (this.auth) {
				t.connect(this.mailhost, this.user, this.password);
			} else {
				t.connect();
			}
			t.sendMessage(msg, msg.getAllRecipients());
		} finally {
			if (this.verbose) {
				System.out.println("Response: " + t.getLastServerResponse());
			}
			t.close();
		}

		msg.setHeader("X-Mailer", this.mailer);
		msg.setSentDate(new Date());

	}

	public void setFrom(final String from) {
		this.from = from;
	}

	public void setMailer(final String mailer) {
		this.mailer = mailer;
	}

	public void setMailhost(final String mailhost) {
		this.mailhost = mailhost;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPort(final String port) {
		this.port = port;
	}

	public void setProt(final String prot) {
		this.prot = prot;
	}

	public void setUser(final String user) {
		this.user = user;
	}
}