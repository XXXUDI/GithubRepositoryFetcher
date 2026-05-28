package com.socompany.githubrepofetcher;

import java.util.List;

public record GithubRepositoryDto (
        String repositoryName,
        String ownerLogin,
        List<BranchDto> branches
) {}
