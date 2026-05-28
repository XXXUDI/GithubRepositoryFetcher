package com.socompany.githubrepofetcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubRepository {
    private Long id;

    @JsonProperty("node_id")
    private String nodeId;

    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("private")
    private boolean isPrivate;

    private Owner owner;

    @JsonProperty("html_url")
    private String htmlUrl;

    private String description;

    private boolean fork;

    private String url;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("pushed_at")
    private String pushedAt;

    @JsonProperty("stargazers_count")
    private int stargazersCount;

    @JsonProperty("watchers_count")
    private int watchersCount;

    private String language;

    @JsonProperty("forks_count")
    private int forksCount;

    @JsonProperty("open_issues_count")
    private int openIssuesCount;

    @JsonProperty("default_branch")
    private String defaultBranch;

    @Data
    public static class Owner {
        private String login;
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        private String url;

        @JsonProperty("html_url")
        private String htmlUrl;

        private String type;
    }
}

// Example Github response:
// {
//    "id": 1014995833,
//    "node_id": "R_kgDOPH-beQ",
//    "name": "coffee-platform-fullstack",
//    "full_name": "XXXUDI/coffee-platform-fullstack",
//    "private": false,
//    "owner": {
//      "login": "XXXUDI",
//      "id": 124133032,
//      "node_id": "U_kgDOB2YeqA",
//      "avatar_url": "https://avatars.githubusercontent.com/u/124133032?v=4",
//      "gravatar_id": "",
//      "url": "https://api.github.com/users/XXXUDI",
//      "html_url": "https://github.com/XXXUDI",
//      "followers_url": "https://api.github.com/users/XXXUDI/followers",
//      "following_url": "https://api.github.com/users/XXXUDI/following{/other_user}",
//      "gists_url": "https://api.github.com/users/XXXUDI/gists{/gist_id}",
//      "starred_url": "https://api.github.com/users/XXXUDI/starred{/owner}{/repo}",
//      "subscriptions_url": "https://api.github.com/users/XXXUDI/subscriptions",
//      "organizations_url": "https://api.github.com/users/XXXUDI/orgs",
//      "repos_url": "https://api.github.com/users/XXXUDI/repos",
//      "events_url": "https://api.github.com/users/XXXUDI/events{/privacy}",
//      "received_events_url": "https://api.github.com/users/XXXUDI/received_events",
//      "type": "User",
//      "user_view_type": "public",
//      "site_admin": false
//    }
