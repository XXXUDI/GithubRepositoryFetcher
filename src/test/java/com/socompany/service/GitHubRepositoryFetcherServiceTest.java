package com.socompany.service;

import com.socompany.githubrepofetcher.UserNotFoundException;
import com.socompany.githubrepofetcher.GithubRepositoryDto;
import com.socompany.githubrepofetcher.GitHubRepositoryFetcherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GitHubRepositoryFetcherServiceTest {

    private GitHubRepositoryFetcherService service;
    private HttpClient mockHttpClient;

    @BeforeEach
    void setUp() {
        service = new GitHubRepositoryFetcherService();
        mockHttpClient = mock(HttpClient.class);
    }

    @Test
    void testGetRepositoriesWithValidUserReturnsNonForkRepositories() throws IOException, InterruptedException {
        // Arrange
        String reposJson = "[{\"name\":\"repo1\",\"fork\":false,\"owner\":{\"login\":\"testuser\"}},{\"name\":\"repo2\",\"fork\":true,\"owner\":{\"login\":\"testuser\"}}]";
        String branchesJson = "[{\"name\":\"main\",\"commit\":{\"sha\":\"abc123\"}}]";
        HttpResponse repoResponse = mock(HttpResponse.class);
        HttpResponse branchResponse = mock(HttpResponse.class);
        when(repoResponse.statusCode()).thenReturn(200);
        when(repoResponse.body()).thenReturn(reposJson);
        when(branchResponse.statusCode()).thenReturn(200);
        when(branchResponse.body()).thenReturn(branchesJson);

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(repoResponse)
                    .thenReturn(branchResponse);

            // Act
            List<GithubRepositoryDto> result = service.getRepositories("testuser");

            // Assert
            assertEquals(1, result.size());
            assertEquals("repo1", result.getFirst().repositoryName());
            assertEquals("testuser", result.getFirst().ownerLogin());
        }
    }

    @Test
    void testGetRepositoriesWithNonExistentUserThrowsUserNotFoundException() {
        // Arrange
        HttpResponse<String> errorResponse = mock(HttpResponse.class);
        when(errorResponse.statusCode()).thenReturn(404);

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(errorResponse);

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> service.getRepositories("nonexistent"));
        } catch (IOException | InterruptedException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testGetRepositoriesWithBranchFetchFailureReturnsRepositoryWithNullBranches() throws IOException, InterruptedException {
        // Arrange
        String reposJson = "[{\"name\":\"repo1\",\"fork\":false,\"owner\":{\"login\":\"testuser\"}}]";
        HttpResponse<String> repoResponse = mock(HttpResponse.class);
        HttpResponse<String> branchResponse = mock(HttpResponse.class);
        when(repoResponse.statusCode()).thenReturn(200);
        when(repoResponse.body()).thenReturn(reposJson);
        when(branchResponse.statusCode()).thenReturn(500);

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(repoResponse)
                    .thenReturn(branchResponse);

            // Act
            List<GithubRepositoryDto> result = service.getRepositories("testuser");

            // Assert
            assertEquals(1, result.size());
            assertNull(result.getFirst().branches());
        }
    }

    @Test
    void testGetRepositoriesWithMultipleBranchesReturnsBranchDetails() throws IOException, InterruptedException {
        // Arrange
        String reposJson = "[{\"name\":\"repo1\",\"fork\":false,\"owner\":{\"login\":\"testuser\"}}]";
        String branchesJson = "[{\"name\":\"main\",\"commit\":{\"sha\":\"abc123\"}},{\"name\":\"develop\",\"commit\":{\"sha\":\"def456\"}}]";
        HttpResponse repoResponse = mock(HttpResponse.class);
        HttpResponse branchResponse = mock(HttpResponse.class);
        when(repoResponse.statusCode()).thenReturn(200);
        when(repoResponse.body()).thenReturn(reposJson);
        when(branchResponse.statusCode()).thenReturn(200);
        when(branchResponse.body()).thenReturn(branchesJson);

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(repoResponse)
                    .thenReturn(branchResponse);

            // Act
            List<GithubRepositoryDto> result = service.getRepositories("testuser");

            // Assert
            assertEquals(1, result.size());
            assertEquals(2, result.getFirst().branches().size());
            assertEquals("main", result.getFirst().branches().get(0).name());
            assertEquals("abc123", result.getFirst().branches().get(0).lastCommitSha());
            assertEquals("develop", result.getFirst().branches().get(1).name());
            assertEquals("def456", result.getFirst().branches().get(1).lastCommitSha());
        }
    }

    @Test
    void testGetRepositoriesWithOnlyForkedRepositoriesReturnsEmptyList() throws IOException, InterruptedException {
        // Arrange
        String reposJson = "[{\"name\":\"repo1\",\"fork\":true,\"owner\":{\"login\":\"testuser\"}},{\"name\":\"repo2\",\"fork\":true,\"owner\":{\"login\":\"testuser\"}}]";
        HttpResponse repoResponse = mock(HttpResponse.class);
        when(repoResponse.statusCode()).thenReturn(200);
        when(repoResponse.body()).thenReturn(reposJson);

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(repoResponse);

            // Act
            List<GithubRepositoryDto> result = service.getRepositories("testuser");

            // Assert
            assertTrue(result.isEmpty());
        }
    }
}
