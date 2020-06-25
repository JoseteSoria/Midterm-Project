package com.ironhack.MidtermProject.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtility {
    public static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        System.out.println(passwordEncoder.encode("tercero"));
    }
}
