package com.example.task1.service;

import java.util.List;

public class CommandValidator {
    // VERY SIMPLE whitelist for demo (expand if you want)
    private static final List<String> ALLOWED = List.of(
            "echo", "dir", "whoami", "ping", "date"
    );

    public static void validate(String command) {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("Command must not be empty");
        }
        // Disallow obvious dangerous tokens
        String lower = command.toLowerCase();
        if (lower.contains("&&") || lower.contains("||") || lower.contains(";")
                || lower.contains("|") || lower.contains("rm ") || lower.contains("del ")
                || lower.contains("shutdown") || lower.contains("format")) {
            throw new IllegalArgumentException("Command contains forbidden tokens");
        }
        // Check first token is allowed (whitelist)
        String first = command.trim().split("\\s+")[0];
        boolean ok = ALLOWED.stream().anyMatch(a -> a.equalsIgnoreCase(first));
        if (!ok) {
            throw new IllegalArgumentException("Command not allowed. Allowed commands: " + ALLOWED);
        }
    }
}
