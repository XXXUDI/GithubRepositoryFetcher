package com.socompany.githubrepofetcher;

public record ErrorResponse(
        int status,
        String message
) {}
