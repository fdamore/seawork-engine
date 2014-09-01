package org.uario.seaworkengine.platform.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;

public interface IMailWrapper {

	public abstract void sendByMail(String subject, String message_to, String text, Boolean ishtml, File... file) throws MessagingException, AddressException,
			NoSuchProviderException, SendFailedException;

	public void sendByMailAtachStream(String subject, String message_to, String text, String fileName, InputStream stream) throws AddressException, MessagingException, IOException;

}