package com.devglan.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        SpringApplication.run(Application.class, args);
        
        FileInputStream serviceAccount = new FileInputStream("credencial.json");
        

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://iris-894d0.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        
    }
    
    
}
