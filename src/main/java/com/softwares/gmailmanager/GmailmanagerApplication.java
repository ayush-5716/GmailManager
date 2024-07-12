package com.softwares.gmailmanager;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.mail.MessagingException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GmailmanagerApplication {

	public static void main(String[] args) throws IOException, GeneralSecurityException, MessagingException {
		SpringApplication.run(GmailmanagerApplication.class, args);
		/*
		 * Press fetch
		 * Now these are all your messages sorted by order of counts. Select any one sender  name to delete
		 * Fetch all message IDs linked to this sender name
		 */

		
	}

}
