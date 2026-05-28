package com.socompany.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.socompany.githubrepofetcher.GitHubRepositoryFetcherService;
import com.socompany.githubrepofetcher.UserNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GitHubRepositoriesControllerTest {

    private static WireMockServer wireMockServer;
    private static GitHubRepositoryFetcherService service;

    @BeforeAll
    public static void startWireMock() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor("localhost", 8089);
        service = new GitHubRepositoryFetcherService();
        service.setBaseUrl("http://localhost:8089");
    }

    @AfterAll
    public static void stopWireMock() {
        wireMockServer.stop();
    }

    @AfterEach
    public void resetWireMock() {
        wireMockServer.resetAll();
    }

    @Test
    public void shouldReturnRepositoriesForValidUser() throws Exception {
        // Given
        String username = "testuser";
        String reposResponse = """
                [
                    {
                        "name": "repo1",
                        "fork": false,
                        "owner": {
                            "login": "testuser"
                        }
                    },
                    {
                        "name": "repo2",
                        "fork": false,
                        "owner": {
                            "login": "testuser"
                        }
                    }
                ]
                """;

        String branchesRepo1 = """
                [
                    {
                        "name": "main",
                        "commit": {
                            "sha": "abc123"
                        }
                    },
                    {
                        "name": "dev",
                        "commit": {
                            "sha": "def456"
                        }
                    }
                ]
                """;

        String branchesRepo2 = """
                [
                    {
                        "name": "master",
                        "commit": {
                            "sha": "xyz789"
                        }
                    }
                ]
                """;

        // Mock GitHub API responses
        stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(reposResponse)));

        stubFor(get(urlEqualTo("/repos/" + username + "/repo1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(branchesRepo1)));

        stubFor(get(urlEqualTo("/repos/" + username + "/repo2/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(branchesRepo2)));

        // When
        var repositories = service.getRepositories(username);

        // Then
        assertThat(repositories).hasSize(2);
        assertThat(repositories.getFirst().repositoryName()).isEqualTo("repo1");
        assertThat(repositories.getFirst().ownerLogin()).isEqualTo("testuser");
        assertThat(repositories.getFirst().branches()).hasSize(2);
        assertThat(repositories.getFirst().branches().getFirst().name()).isEqualTo("main");
        assertThat(repositories.getFirst().branches().getFirst().lastCommitSha()).isEqualTo("abc123");
        assertThat(repositories.get(0).branches().get(1).name()).isEqualTo("dev");
        assertThat(repositories.get(0).branches().get(1).lastCommitSha()).isEqualTo("def456");

        assertThat(repositories.get(1).repositoryName()).isEqualTo("repo2");
        assertThat(repositories.get(1).ownerLogin()).isEqualTo("testuser");
        assertThat(repositories.get(1).branches()).hasSize(1);
        assertThat(repositories.get(1).branches().getFirst().name()).isEqualTo("master");
        assertThat(repositories.get(1).branches().getFirst().lastCommitSha()).isEqualTo("xyz789");
    }

    @Test
    public void shouldFilterOutForkedRepositories() throws Exception {
        // Given
        String username = "testuser";
        String reposResponse = """
                [
                    {
                        "name": "original-repo",
                        "fork": false,
                        "owner": {
                            "login": "testuser"
                        }
                    },
                    {
                        "name": "forked-repo",
                        "fork": true,
                        "owner": {
                            "login": "testuser"
                        }
                    }
                ]
                """;

        String branches = """
                [
                    {
                        "name": "main",
                        "commit": {
                            "sha": "abc123"
                        }
                    }
                ]
                """;

        stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(reposResponse)));

        stubFor(get(urlEqualTo("/repos/" + username + "/original-repo/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(branches)));

        // When
        var repositories = service.getRepositories(username);

        // Then
        assertThat(repositories).hasSize(1);
        assertThat(repositories.getFirst().repositoryName()).isEqualTo("original-repo");
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String username = "nonexistentuser";

        stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(404)));

        // When & Then
        assertThrows(UserNotFoundException.class, () -> service.getRepositories(username));
    }
}
