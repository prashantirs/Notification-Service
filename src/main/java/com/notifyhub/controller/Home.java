package com.notifyhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Welcome to NotifyHub! Please use the /api/notify endpoint to send notifications.");
    }
}
