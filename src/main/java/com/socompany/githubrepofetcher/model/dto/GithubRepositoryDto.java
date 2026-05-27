package com.socompany.githubrepofetcher.model.dto;

import java.util.List;

public record GithubRepositoryDto (
        String repositoryName,
        String ownerLogin,
        List<BranchDto> branches
) {}
