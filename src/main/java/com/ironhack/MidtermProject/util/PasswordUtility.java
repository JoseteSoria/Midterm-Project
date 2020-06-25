package com.ironhack.MidtermProject.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtility {
    public static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        String str = "ES";
        for(int i = 0; i<22; i++) {
            str += String.valueOf((int)(Math.random()*10));
        }
        System.out.println(passwordEncoder.encode("tercero-hashkey"));
    }
}
