package com.softwares.gmailmanager.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.softwares.gmailmanager.entities.Product;
import com.softwares.gmailmanager.entities.srcObj;
import com.softwares.gmailmanager.services.CustomBrowser;
import com.softwares.gmailmanager.services.MessageFunctions;

import jakarta.servlet.http.HttpSession;

class MyRunnable implements Runnable {

    public static MessageFunctions mf;

    public void run() {
        System.out.println("MyThread is running");
        try {
            mf = new MessageFunctions("url");
            System.out.println("connection done");
        } catch (GeneralSecurityException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}

@Controller
@RequestMapping("/")
public class HomeController {

    LinkedList<srcObj> sortedListByCount;

    HashMap<String, LinkedList<String>> mappedSenderToIds;

    String url = "";

    CustomBrowser cb;

    MessageFunctions mf;

    @GetMapping("/")
    public String displayHome() {
        
        return "home";
    }

    @GetMapping("/sendToAuth")
    public String authorization() throws GeneralSecurityException, IOException, InterruptedException {
        Thread t = new Thread(new MyRunnable());
        t.start();
        System.out.println("passsssed");
        Thread.sleep(5000);

        // mf = new MessageFunctions("lol");
        System.out.println("passed this ");
        cb = new CustomBrowser();
        String url = cb.getUrlFromFile();
        return "redirect:" + url;
    }



    @GetMapping("/test")
    public String displayTestScreen(Model model, HttpSession ht) throws IOException, GeneralSecurityException, InterruptedException {
        System.out.println("Fetched user list");
        ht.setMaxInactiveInterval(3);
        Thread.sleep(5000);
        try {
            mf = MyRunnable.mf;
            // this method consumes time
            mappedSenderToIds = mf.getMappedSenderToMessageIDs(mf.getListOfMessagesIds(), 99);
            System.out.println(mappedSenderToIds);
            sortedListByCount = mf.getSenderNamesSortedByCount(mappedSenderToIds);
            model.addAttribute("products", sortedListByCount);
            System.out.println("Came till here");
            return "shoping";
        } catch (Exception e) {
            System.out.println("came here");
            return "redirect:/";
        }

    }

    @PostMapping("/deleteMails")
    public String deleteMail(Model model,@RequestBody srcObj product){
        System.out.println("name recieved is: ");
        System.out.println(product.getName());
        LinkedList<String> mailIdsToBeDeleted = mappedSenderToIds.get(product.getName());
        System.out.println("printing the mail idss");
        System.out.println(mailIdsToBeDeleted);
        mf.batchDeleteByMessageIdList(mailIdsToBeDeleted);
        return "redirect:/test";
    }

}
