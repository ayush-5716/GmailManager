package com.softwares.gmailmanager.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.BatchDeleteMessagesRequest;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.softwares.gmailmanager.entities.srcObj;

public class MessageFunctions {

    GmailService sm;
    Gmail service;

    public MessageFunctions(String usage) throws GeneralSecurityException, IOException{
        sm = new GmailService();
        service = sm.getGmailService();
    }

    

    public MessageFunctions() {
        
    }



    public void sendMessage(String subject, String messageToSend, String sendTo)
            throws GeneralSecurityException, IOException, AddressException, MessagingException {

        String messageSubject = subject;
        String bodyText = messageToSend;

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
            message = service.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());

        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }

    }

    
    public List<Message> getListOfMessagesIds() throws IOException{
        java.util.List<Message> messageIDs = new LinkedList<>();
        try {
            
            ListMessagesResponse messages = service.users().messages().list("me").execute();
            messageIDs = messages.getMessages();
            return messageIDs;
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }



        return messageIDs;
    }


    public HashMap<String, LinkedList<String>> getMappedSenderToMessageIDs(List<Message> messageIDs, int amountOfMessagesToFetch) throws IOException{

        HashMap<String, LinkedList<String>> hm = new HashMap<>();

        for (int i = 0; i < amountOfMessagesToFetch; i++) {

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

        }

        return hm;
    }


    public LinkedList<srcObj> getSenderNamesSortedByCount(HashMap<String, LinkedList<String>> mappedSenderToMessageIDs) {

        LinkedList<srcObj> list2 = new LinkedList<>();

        
        for (String key : mappedSenderToMessageIDs.keySet()) {
            list2.add(new srcObj(key, mappedSenderToMessageIDs.get(key).size()));
        }

        Collections.sort(list2, new comparer());

        return list2;

    }


    public void batchDeleteByMessageIdList(LinkedList<String> listOfMessageIds){

        BatchDeleteMessagesRequest bm = new BatchDeleteMessagesRequest();
        bm.setIds(listOfMessageIds);

        try {
            service.users().messages().batchDelete("me", bm).execute();
            System.out.println("Deleted succesfully");
        } catch (IOException e) {
            System.out.println("Delete Karne Mein Dikakt");
            e.printStackTrace();
        }

    }
}
