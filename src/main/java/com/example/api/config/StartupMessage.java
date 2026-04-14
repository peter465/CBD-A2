package com.example.api.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupMessage implements ApplicationRunner {
    
    private final Environment environment;

    public StartupMessage(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        String port = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String activeProfile = String.join(", ", environment.getActiveProfiles());
        
        System.out.println("\n" +
            "╔══════════════════════════════════════════════════════════╗\n" +
            "║                                                          ║\n" +
            "║     🚀  BOOKS API STARTED SUCCESSFULLY!                 ║\n" +
            "║                                                          ║\n" +
            "╠══════════════════════════════════════════════════════════╣\n" +
            "║                                                          ║\n" +
            "║  📍  Base URL: http://localhost:" + port + contextPath + "            ║\n" +
            "║  🔧  Profile: " + (activeProfile.isEmpty() ? "default" : activeProfile) + "\n" +
            "║  📊  Status: RUNNING                                     ║\n" +
            "║                                                          ║\n" +
            "║  📚  Available Endpoints:                                ║\n" +
            "║  ────────────────────────────────────────────────────── ║\n" +
            "║  POST   /authors/{authorId}/books     - Create book     ║\n" +
            "║  GET    /authors/{authorId}/books     - List books      ║\n" +
            "║  GET    /authors/{authorId}/books/{id} - Get book       ║\n" +
            "║  PUT    /authors/{authorId}/books/{id} - Update book    ║\n" +
            "║  DELETE /authors/{authorId}/books/{id} - Delete book    ║\n" +
            "║  Get api/books                         -get all books   ║\n" +
            "╚═════════════════════════════════════════════════════════╝\n");
    }
}