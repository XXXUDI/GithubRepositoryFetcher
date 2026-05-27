package com.socompany.githubrepofetcher.model.dto;

public record ErrorResponse(
        int status,
        String message
) {}
