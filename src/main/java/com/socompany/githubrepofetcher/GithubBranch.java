package com.socompany.githubrepofetcher;

import lombok.Data;

@Data
public class GithubBranch {
    private String name;
    private Commit commit;

    @Data
    public static class Commit {
        private String sha;
    }
}
