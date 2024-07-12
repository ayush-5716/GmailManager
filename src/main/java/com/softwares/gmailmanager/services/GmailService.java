package com.softwares.gmailmanager.services;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.softwares.gmailmanager.entities.srcObj;

/* Class to demonstrate the use of Gmail Send Message API */

class comparer implements Comparator<srcObj> {
    @Override
    public int compare(srcObj o1, srcObj o2) {
        // TODO Auto-generated method stub
        return Integer.compare(o2.count, o1.count);
    }
}

public class GmailService {
    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param fromEmailAddress - Email address to appear in the from: header
     * @param toEmailAddress   - Email address of the recipient
     * @return the sent message, {@code null} otherwise.
     * @throws GeneralSecurityException 
     * @throws MessagingException - if a wrongly formatted address is encountered.
     * @throws IOException        - if service account credentials file not found.
     */

     Gmail service;

     public GmailService() throws GeneralSecurityException, IOException{
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Gmail.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(),
                getCredentials(HTTP_TRANSPORT))
                .setApplicationName("lomror")
                .build();
     }


    String filePaths = "/great.json";
    // ----------------------------------------------------------------------------------------------------

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GmailService.class.getResourceAsStream(filePaths);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + filePaths);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(),
                new InputStreamReader(in));

        LinkedList<String> ls =new LinkedList<>();
        ls.add(GmailScopes.GMAIL_READONLY);

        // Build flow and trigger user authorization request.
        // System.out.println("before");
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), clientSecrets, Set.of(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND, GmailScopes.MAIL_GOOGLE_COM))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("online")
                .build();

        // System.out.println("after");
        
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).setLandingPages("http://localhost:8080/test", "http://localhost:8080/thiserror").build();
        // System.out.println("after after");
        // @SuppressWarnings("unused")
        // AuthorizationCodeInstalledApp auth = new AuthorizationCodeInstalledApp(flow, receiver);
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver,new CustomBrowser()).authorize("user");
        System.out.println( credential.getTokenServerEncodedUrl());
        // System.out.println("after after after");
        // returns an authorized Credential object.
        return credential;
    }

    // -------------------------------------------------------------------------------------------------

    public Message sendEmail()
            throws MessagingException, IOException, GeneralSecurityException {
        /*
         * Load pre-authorized user credentials from the environment.
         * TODO(developer) - See https://developers.google.com/identity for
         * guides on implementing OAuth2 for your application.
         */

        

        String messageSubject = "aagaya kya? Aagaya toh aage padh bhai";
        String bodyText = "Aagaya toh khush ho jana bhai, tujhe khush dekh k achha lagta h";

        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("ayuuush02@gmail.com"));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress("ayushz5716@gmail.com"));
        email.setSubject(messageSubject);
        email.setText(bodyText);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        try {
            // Create send message
            ListMessagesResponse messages = service.users().messages().list("me").execute();
            java.util.List<Message> messageIDs = messages.getMessages();
            messages.getNextPageToken();
            messageIDs.get(8).getId();

            LinkedList<String> listOfSenders = new LinkedList<>();
            // System.out.println(messageIDs.size());

            HashMap<String, LinkedList<String>> hm = new HashMap<>();

            for (int i = 0; i < 30; i++) {

                Message m = service.users().messages().get("me", messageIDs.get(i).getId()).execute();
                String senderDetails = m.getPayload().getHeaders().stream()
                        .filter(header -> header.getName().equals("From"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Sender not found"))
                        .getValue();
                        

                String senderName = senderDetails.split("\\s+")[0];
                String senderId = messageIDs.get(i).getId();
                String[] senders = { senderId};
                hm.merge(senderName, new LinkedList<String>(Arrays.asList(senders)), (a, b) -> {
                    a.add(senderId);
                    return a;
                });

                listOfSenders.add(senderName);
            }

            System.out.println(hm);

            LinkedList<srcObj> sortedAccToCount = giveSortedAccCount(listOfSenders);

            for (int i = 0; i < sortedAccToCount.size(); i++) {
                System.out.println(sortedAccToCount.get(i).name + "-" + sortedAccToCount.get(i).count);
            }

            

            return message;
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }

        return null;
    }

    public Gmail getGmailService() throws GeneralSecurityException, IOException{

        return service;
    }

    private LinkedList<srcObj> giveSortedAccCount(LinkedList<String> listOfSenders) {
        TreeMap<String, Integer> tm = new TreeMap<>();

        listOfSenders.stream()
                .forEach((x) -> {
                    tm.merge(x.split("\\s+")[0], 1, (b, y) -> b + 1);
                });

        LinkedList<srcObj> list2 = new LinkedList<>();
        for (String key : tm.keySet()) {
            list2.add(new srcObj(key, tm.get(key)));
        }

        Collections.sort(list2, new comparer());

        return list2;

    }

    @SuppressWarnings("unused")
    private static String decodeBase64(String encodedBody) {
        byte[] decodedBytes = Base64.decodeBase64(encodedBody);
        return new String(decodedBytes);
    }

}