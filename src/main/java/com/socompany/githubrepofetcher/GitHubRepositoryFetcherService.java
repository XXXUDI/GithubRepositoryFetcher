package com.socompany.githubrepofetcher;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@Slf4j
public class GitHubRepositoryFetcherService {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private HttpClient httpClient;

    @Setter
    private String baseUrl = "https://api.github.com";

    public List<GithubRepositoryDto> getRepositories(String user) throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        log.debug("Sending request to GitHub API for user: {}", user);
        var response = httpClient.send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .uri(java.net.URI.create(baseUrl + "/users/" + user + "/repos"))
                .build(), HttpResponse.BodyHandlers.ofString());
        log.info("Received response from GitHub API: {}", response.statusCode());

        if (response.statusCode() == 404) {
            log.error("User not found: {}", user);
            throw new UserNotFoundException("User not found");
        }
        log.debug("Trying to parse response body...");
        List<GithubRepository> repos = objectMapper.readValue(response.body(), new TypeReference<List<GithubRepository>>() {});
        log.debug("Response body parsed successfully. Returning repositories...");
        return repos.stream()
                .filter(repo -> !repo.isFork())
                .map(this::fetchAndMapRepository)
                .collect(Collectors.toList());
    }

    private GithubRepositoryDto fetchAndMapRepository(GithubRepository repo) {
        List<BranchDto> branchDtos = null;
        try {
            var branchResponse = httpClient.send(HttpRequest.newBuilder()
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .uri(java.net.URI.create(baseUrl + "/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/branches"))
                    .build(), HttpResponse.BodyHandlers.ofString());

            if (branchResponse.statusCode() == 200) {
                List<GithubBranch> branches = objectMapper.readValue(branchResponse.body(), new TypeReference<>() {});
                branchDtos = branches.stream()
                        .map(b -> new BranchDto(b.getName(), b.getCommit().getSha()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Failed to fetch branches for {}", repo.getName(), e);
        }
        return new GithubRepositoryDto(repo.getName(), repo.getOwner().getLogin(), branchDtos);
    }
}
