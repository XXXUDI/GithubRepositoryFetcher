package com.socompany.githubrepofetcher.service;

import com.socompany.githubrepofetcher.model.dto.GithubRepositoryDto;
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
import com.socompany.githubrepofetcher.model.GithubRepository;
import com.socompany.githubrepofetcher.model.GithubBranch;
import com.socompany.githubrepofetcher.model.dto.BranchDto;
import com.socompany.githubrepofetcher.exception.UserNotFoundException;

@Service
@Slf4j
public class GitHubRepositoryFetcherService {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private HttpClient httpClient;

    public List<GithubRepositoryDto> getRepositories(String user) throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        var response = httpClient.send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .uri(java.net.URI.create("https://api.github.com/users/" + user + "/repos"))
                .build(), HttpResponse.BodyHandlers.ofString());
        log.info("Response status: {}", response.statusCode());

        if (response.statusCode() == 404) {
            throw new UserNotFoundException("User not found");
        }

        List<GithubRepository> repos = objectMapper.readValue(response.body(), new TypeReference<List<GithubRepository>>() {});

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
                    .uri(java.net.URI.create("https://api.github.com/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/branches"))
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
