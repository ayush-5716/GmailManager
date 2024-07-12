package com.softwares.gmailmanager.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser;

public class CustomBrowser  implements Browser{

  

    @Override
    public void browse(String url) throws IOException {
        System.out.println(url);
      
        writeUrlTofile(url);
    }


    public void writeUrlTofile(String url) throws IOException{

        FileWriter fw = new FileWriter("url.txt");
        
        fw.write(url);
        System.out.println("Url written to the file");

        fw.close();
    }

    public String getUrlFromFile() throws IOException{

        FileReader fr = new FileReader("url.txt");

        BufferedReader br = new BufferedReader(fr);


        String url = br.readLine();
        br.close();
        return url;
    }

    public void deleteUrlFile(){
        File file = new File("url.txt");

        if( file.exists()){
            boolean deleted = file.delete();
            System.out.println("File deleted " + deleted );
        }else{
            System.out.println("file didnt exist");
        }
    }


    
}
