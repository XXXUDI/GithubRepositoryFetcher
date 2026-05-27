package com.socompany.githubrepofetcher.model.dto;

public record BranchDto(
        String name,
        String lastCommitSha
) {}
