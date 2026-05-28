package com.socompany.githubrepofetcher;

public record BranchDto(
        String name,
        String lastCommitSha
) {}
